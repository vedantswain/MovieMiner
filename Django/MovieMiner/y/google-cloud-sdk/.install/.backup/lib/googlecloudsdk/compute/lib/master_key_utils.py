# Copyright 2014 Google Inc. All Rights Reserved.
"""Utility functions for managing customer supplied master keys."""

import argparse
import base64
import json

from googlecloudsdk.calliope import exceptions


EXPECTED_RECORD_KEY_KEYS = set(['uri', 'key'])
BASE64_KEY_LENGTH_IN_CHARS = 44


SUPPRESS_MASTER_KEY_UTILS = True


class MissingMasterKeyException(exceptions.ToolException):

  def __init__(self, resource):
    super(MissingMasterKeyException, self).__init__(
        'Key required for resource [{0}], but none found.'.format(resource))


class InvalidKeyFileException(exceptions.ToolException):

  def __init__(self, base_message):
    super(InvalidKeyFileException, self).__init__(
        '{0}'.format(base_message))


class BadPatternException(InvalidKeyFileException):

  def __init__(self, pattern_type, pattern):
    self.pattern_type = pattern_type
    self.pattern = pattern
    super(BadPatternException, self).__init__(
        'Invalid value for [{0}] pattern: [{1}]'.format(
            self.pattern_type,
            self.pattern))


class InvalidKeyException(InvalidKeyFileException):
  pass


def ValidateKey(base64_encoded_string):
  """ValidateKey(s) return none or raises InvalidKeyException."""
  try:
    base64.standard_b64decode(base64_encoded_string)
  except TypeError as t:
    raise InvalidKeyException(
        'Provided key [{0}] is not valid base64: [{1}]'.format(
            base64_encoded_string,
            t.message))

  if len(base64_encoded_string) != 44:
    raise InvalidKeyFileException(
        'Provided key [{0}] should contain {1} characters (including padding), '
        'but is [{2}] characters long.'.format(
            base64_encoded_string,
            BASE64_KEY_LENGTH_IN_CHARS,
            len(base64_encoded_string)))


def AddMasterKeyArgs(parser, flags_about_creation=True):
  """Adds arguments related to master keys."""

  master_key_file = parser.add_argument(
      '--master-key-file',
      help=(argparse.SUPPRESS if SUPPRESS_MASTER_KEY_UTILS
            else 'Path to a master key file'),
      metavar='FILE')
  master_key_file.detailed_help = (
      'Path to a master key file, mapping GCE resources to user managed '
      'keys to be used when creating, mounting, or snapshotting disks. ')
  # TODO(user)
  # Argument - indicates the key file should be read from stdin.'

  if flags_about_creation:
    no_require_master_key_create = parser.add_argument(
        '--no-require-master-key-create',
        help=(argparse.SUPPRESS if SUPPRESS_MASTER_KEY_UTILS
              else 'Allow creating of resources not protected by master key.'),
        action='store_true')
    no_require_master_key_create.detailed_help = (
        'When invoked with --master-key-file gcloud will refuse to create '
        'resources not protected by a user managed key in the key file.  This '
        'is intended to prevent incorrect gcloud invocations from accidentally '
        'creating resources with no user managed key.  This flag disables the '
        'check and allows creation of resources without master keys.')


class UriPattern(object):
  """A uri-based pattern that maybe be matched against resource objects."""

  def __init__(self, path_as_string):
    if not path_as_string.startswith('http'):
      raise BadPatternException('uri', path_as_string)
    self._path_as_string = path_as_string

  def Matches(self, resource):
    """Tests if its argument matches the pattern."""
    return self._path_as_string == resource.SelfLink()

  def __str__(self):
    return 'Uri Pattern: ' + self._path_as_string


class MasterKeyStore(object):
  """Represents a map from resource patterns to keys."""

  # Members
  # self._state: dictionary from UriPattern to a valid, base64-encoded key

  @staticmethod
  def FromFile(fname):
    """FromFile loads a MasterKeyStore from a file.

    Args:
      fname: str, the name of a file intended to contain a well-formed key file

    Returns:
      A MaterKeyStore, if found

    Raises:
      exceptions.BadFileException: there's a problem reading fname
      exceptions.InvalidKeyFileException: the key file failed to parse
        or was otherwise invalid
    """

    with open(fname) as infile:
      content = infile.read()

    return MasterKeyStore(content)

  @staticmethod
  def FromArgs(args):
    """FromFile attempts to load a MasterKeyStore from a command's args.

    Args:
      args: CLI args with a master_key_file field set

    Returns:
      A MasterKeyStore, if a valid key file name is provided as master_key_file
      None, if args.master_key_file is None

    Raises:
      exceptions.BadFileException: there's a problem reading fname
      exceptions.InvalidKeyFileException: the key file failed to parse
        or was otherwise invalid
    """
    assert hasattr(args, 'master_key_file')

    if args.master_key_file is None:
      return None

    return MasterKeyStore.FromFile(args.master_key_file)

  @staticmethod
  def _ParseAndValidate(s):
    """_ParseAndValidate(s) inteprets s as a master key file.

    Args:
      s: str, an input to parse

    Returns:
      a valid state object

    Raises:
      InvalidKeyFileException: if the input doesn't parse or is not well-formed.
    """

    assert type(s) is str
    state = {}

    try:
      records = json.loads(s)

      if type(records) is not list:
        raise InvalidKeyFileException(
            "Key file's top-level element must be a JSON list.")

      for key_record in records:
        if type(key_record) is not dict:
          raise InvalidKeyFileException(
              'Key file records must be JSON objects, but [{0}] found.'.format(
                  json.dumps(key_record)))

        if set(key_record.keys()) != EXPECTED_RECORD_KEY_KEYS:
          raise InvalidKeyFileException(
              'Record [{0}] has incorrect keys; [{1}] expected'.format(
                  json.dumps(key_record),
                  ','.join(EXPECTED_RECORD_KEY_KEYS)))

        pattern = UriPattern(key_record['uri'])
        ValidateKey(key_record['key'])

        state[pattern] = key_record['key']

    except ValueError:
      raise InvalidKeyFileException.FromCurrent()

    assert type(state) is dict
    return state

  def __len__(self):
    return len(self.state)

  def LookupKey(self, resource, raise_if_missing=False):
    """Search for the unique key corresponding to a given resource.

    Args:
      resource: the resource to find a key for.
      raise_if_missing: bool, raise an exception if the resource is not found.

    Returns:
      The base64 encoded string corresponding to the resource,
        or none if not found and not raise_if_missing.

    Raises:
      InvalidKeyFileException: if there are two records matching the resource.
      MissingMasterKeyException: if raise_if_missing and no key is found
        for the provided resoure.
    """

    assert type(self.state) is dict
    search_state = (None, None)

    for pat, key in self.state.iteritems():
      if pat.Matches(resource):
        # TODO(user) what's the best thing to do if there are multiple
        # matches?
        if search_state[0]:
          raise exceptions.InvalidKeyFileException(
              'Uri patterns [{0}] and [{1}] both match '
              'resource [{2}].  Bailing out.'.format(
                  search_state[0], pat, str(resource)))

        search_state = (pat, key)

    if raise_if_missing and (search_state[1] is None):
      raise MissingMasterKeyException(resource)

    return search_state[1]

  def __init__(self, json_string):
    self.state = MasterKeyStore._ParseAndValidate(json_string)


def MaybeLookupKey(master_keys_or_none, resource):
  if master_keys_or_none and resource:
    return master_keys_or_none.LookupKey(resource)

  return None


def MaybeLookupKeys(master_keys_or_none, resources):
  return [MaybeLookupKey(master_keys_or_none, r) for r in resources]


def MaybeLookupKeysByUri(master_keys_or_none, parser, uris):
  return MaybeLookupKeys(
      master_keys_or_none,
      [(parser.Parse(u) if u else None) for u in uris])
