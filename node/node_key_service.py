import cryptography

from utils.ed25519 import string_to_public_key
from .node_service import NodeService
from .remote_node_service import RemoteNodeService
from vertex import VertexService


class NodeKeyService:
    def __init__(self, vertex_service: VertexService, node_service: NodeService, remote_node_service: RemoteNodeService):
        self.node_service = node_service
        self.remote_node_service = remote_node_service
        self.vertex_service = vertex_service

    def get_signing_public_key(self, kid: str) -> \
            (str, str, cryptography.hazmat.primitives.asymmetric.ed25519.Ed25519PublicKey):
        components = kid.split("/")

        if len(components) != 2:
            raise Exception("Invalid key id")

        if components[0] == self.vertex_service.get_vertex_endpoint():
            return components[0], components[1], string_to_public_key(
                self.node_service.get(components[1])["signing_public_key"])
        else:
            return components[0], components[1], string_to_public_key(
                self.remote_node_service.get(components[0], components[1])["signing_public_key"])
