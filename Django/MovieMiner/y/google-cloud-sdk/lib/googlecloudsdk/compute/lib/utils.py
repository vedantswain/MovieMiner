# Copyright 2014 Google Inc. All Rights Reserved.
"""Utility functions that don't belong in the other utility modules."""


import cStringIO
import re
import urlparse

from googlecloudapis.compute.alpha import compute_alpha_client
from googlecloudapis.compute.beta import compute_beta_client
from googlecloudapis.compute.v1 import compute_v1_client
from googlecloudsdk.calliope import actions
from googlecloudsdk.calliope import exceptions as calliope_exceptions
from googlecloudsdk.compute.lib import constants
from googlecloudsdk.core import log
from googlecloudsdk.core import properties
from googlecloudsdk.core import resolvers
from googlecloudsdk.core import resources
from googlecloudsdk.core.util import console_io


def ZoneNameToRegionName(zone_name):
  """Converts zone name to region name: 'us-central1-a' -> 'us-central1'."""
  return zone_name.rsplit('-', 1)[0]


def CollectionToResourceType(collection):
  """Converts a collection to a resource type: 'compute.disks' -> 'disks'."""
  return collection.split('.', 1)[1]


def CollectionToApi(collection):
  """Converts a collection to an api: 'compute.disks' -> 'compute'."""
  return collection.split('.', 1)[0]


def NormalizeGoogleStorageUri(uri):
  """Converts gs:// to http:// if uri begins with gs:// else returns uri."""
  if uri and uri.startswith('gs://'):
    return 'http://storage.googleapis.com/' + uri[len('gs://'):]
  else:
    return uri


def CamelCaseToOutputFriendly(string):
  """Converts camel case text into output friendly text.

  Args:
    string: The string to convert.

  Returns:
    The string converted from CamelCase to output friendly text.

  Examples:
    'camelCase' -> 'camel case'
    'CamelCase' -> 'camel case'
    'camelTLA' -> 'camel tla'
  """
  return re.sub('([A-Z]+)', r' \1', string).strip().lower()


def ConstructList(title, items):
  """Returns a string displaying the items and a title."""
  buf = cStringIO.StringIO()
  printer = console_io.ListPrinter(title)
  printer.Print(sorted(set(items)), output_stream=buf)
  return buf.getvalue()


def RaiseToolException(problems, error_message=None):
  """Raises a ToolException with the given list of problems."""
  errors = []
  for _, message in problems:
    errors.append(message)

  raise calliope_exceptions.ToolException(
      ConstructList(
          error_message or 'Some requests did not succeed:',
          errors))


def AddZoneFlag(parser, resource_type, operation_type):
  """Adds a --zone flag to the given parser."""
  short_help = 'The zone of the {0} to {1}.'.format(
      resource_type, operation_type)
  zone = parser.add_argument(
      '--zone',
      help=short_help,
      action=actions.StoreProperty(properties.VALUES.compute.zone))
  zone.detailed_help = '{0} {1}'.format(
      short_help, constants.ZONE_PROPERTY_EXPLANATION)


def AddRegionFlag(parser, resource_type, operation_type):
  """Adds a --region flag to the given parser."""
  short_help = 'The region of the {0} to {1}.'.format(
      resource_type, operation_type)
  region = parser.add_argument(
      '--region',
      help=short_help,
      action=actions.StoreProperty(properties.VALUES.compute.region))
  region.detailed_help = '{0} {1}'.format(
      short_help, constants.REGION_PROPERTY_EXPLANATION)


def PromptForDeletion(refs, scope_name=None, prompt_title=None):
  """Prompts the user to confirm deletion of resources."""
  if not refs:
    return
  resource_type = CollectionToResourceType(refs[0].Collection())
  resource_name = CamelCaseToOutputFriendly(resource_type)
  prompt_list = []
  for ref in refs:
    if scope_name:
      item = '[{0}] in [{1}]'.format(ref.Name(), getattr(ref, scope_name))
    else:
      item = '[{0}]'.format(ref.Name())
    prompt_list.append(item)

  prompt_title = (prompt_title or
                  'The following {0} will be deleted:'.format(resource_name))
  prompt_message = ConstructList(prompt_title, prompt_list)
  if not console_io.PromptContinue(message=prompt_message):
    raise calliope_exceptions.ToolException('Deletion aborted by user.')


def BytesToGb(size):
  """Converts a disk size in bytes to GB."""
  if not size:
    return None

  if size % constants.BYTES_IN_ONE_GB != 0:
    raise calliope_exceptions.ToolException(
        'Disk size must be a multiple of 1 GB. Did you mean [{0}GB]?'
        .format(size / constants.BYTES_IN_ONE_GB + 1))

  return size / constants.BYTES_IN_ONE_GB


def WarnIfDiskSizeIsTooSmall(size_gb, disk_type):
  """Writes a warning message if the given disk size is too small."""
  if not size_gb:
    return

  if disk_type and 'pd-ssd' in disk_type:
    warning_threshold_gb = constants.SSD_DISK_PERFORMANCE_WARNING_GB
  else:
    warning_threshold_gb = constants.STANDARD_DISK_PERFORMANCE_WARNING_GB

  if size_gb < warning_threshold_gb:
    log.warn(
        'You have selected a disk size of under [%sGB]. This may result in '
        'poor I/O performance. For more information, see: '
        'https://developers.google.com/compute/docs/disks#pdperformance.',
        warning_threshold_gb)


def SetResourceParamDefaults():
  """Sets resource parsing default parameters to point to properties."""
  core_values = properties.VALUES.core
  compute_values = properties.VALUES.compute
  for api, param, prop in (
      ('compute', 'project', core_values.project),
      ('resourceviews', 'projectName', core_values.project),
      ('compute', 'zone', compute_values.zone),
      ('resourceviews', 'zone', compute_values.zone),
      ('compute', 'region', compute_values.region),
      ('resourceviews', 'region', compute_values.region)):
    resources.SetParamDefault(
        api=api,
        collection=None,
        param=param,
        resolver=resolvers.FromProperty(prop))


def UpdateContextEndpointEntries(context, http, api_version='v1'):
  """Updates context to set API enpoints; requires context['http'] be set."""

  context['project'] = properties.VALUES.core.project.Get(required=True)
  context['http'] = http

  endpoint = properties.VALUES.endpoints.compute.Get()
  # Turn the endpoint into just the host.
  # eg. https://www.googleapis.com/compute/v1 -> https://www.googleapis.com
  u_endpoint = urlparse.urlparse(endpoint or 'https://www.googleapis.com')
  api_host = '%s://%s' % (u_endpoint.scheme, u_endpoint.netloc)

  known_apis = {
      'alpha': compute_alpha_client.ComputeAlpha,
      'beta': compute_beta_client.ComputeBeta,
      'v1': compute_v1_client.ComputeV1,
      }


  client = known_apis.get(api_version, None)
  if not client:
    raise ValueError('Invalid API version: [{0}]'.format(api_version))

  compute_url = properties.VALUES.endpoints.compute.Get()
  compute = client(url=compute_url, get_credentials=False, http=http)
  context['compute'] = compute
  context['resources'] = resources.REGISTRY.CloneAndSwitchAPIs(compute)
  context['batch-url'] = urlparse.urljoin(api_host, 'batch')
