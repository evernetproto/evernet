import cryptography.hazmat.primitives.asymmetric.ed25519

from lib.ed25519 import string_to_public_key
from service import NodeService
from service.remote_node_service import RemoteNodeService


class NodeKeyService:
    def __init__(self, node_service: NodeService, remote_node_service: RemoteNodeService, vertex_endpoint: str):
        self.node_service = node_service
        self.remote_node_service = remote_node_service
        self.vertex_endpoint = vertex_endpoint

    def get_signing_public_key(self, kid: str) -> \
            (str, str, cryptography.hazmat.primitives.asymmetric.ed25519.Ed25519PublicKey):
        components = kid.split("/")

        if len(components) != 2:
            raise Exception("Invalid key id")

        if components[0] == self.vertex_endpoint:
            return components[0], components[1], string_to_public_key(
                self.node_service.get(components[1])["signing_public_key"])
        else:
            return components[0], components[1], string_to_public_key(
                self.remote_node_service.get(components[0], components[1])["signing_public_key"])
