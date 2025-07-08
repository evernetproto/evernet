from cryptography.hazmat.primitives.asymmetric.ed25519 import Ed25519PublicKey

from service.config_service import ConfigService
from service.node_service import NodeService
from utils.ed25519 import string_to_public_key


class NodeKeyService:

    @staticmethod
    def get_signing_public_key(kid: str) -> (str, str, Ed25519PublicKey):
        kid_components = kid.split("/")

        if len(kid_components) != 2:
            raise Exception("Invalid kid in jwt token")

        current_vertex_endpoint = ConfigService.get_vertex_endpoint()
        kid_vertex_endpoint = kid_components[0]
        kid_node_identifier = kid_components[1]

        if kid_vertex_endpoint == current_vertex_endpoint:
            node = NodeService.get(kid_node_identifier)
            return string_to_public_key(node["signing_public_key"])
        else:
            # TODO
            return None
