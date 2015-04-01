# Copyright 2014 Google Inc. All Rights Reserved.
"""Command for listing VPN tunnels."""
from googlecloudsdk.compute.lib import base_classes


class List(base_classes.RegionalLister):
  """List VPN tunnels."""

  # Placeholder to indicate that a detailed_help field exists and should
  # be set outside the class definition.
  detailed_help = None

  @property
  def service(self):
    return self.compute.vpnTunnels

  @property
  def resource_type(self):
    return 'vpnTunnels'


List.detailed_help = {
    'brief': 'List vpn tunnels',
    'DESCRIPTION': """\
        *{command}* lists summary information for the VPN tunnels
        in a project.  The ``--uri'' option can be used to display the
        URIs for the VPN tunnels.
        """,
}
