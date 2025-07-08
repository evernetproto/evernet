import requests

from service.config_service import ConfigService


class RemoteNodeService:
    def __init__(self):
        pass

    @staticmethod
    def get(vertex_endpoint: str, identifier: str):
        protocol = ConfigService.get_federation_protocol()
        response = requests.get(f"{protocol}://{vertex_endpoint}/api/v1/nodes/{identifier}")

        if response.ok:
            return response.json()
        else:
            raise Exception(f"Error getting remote node {identifier} from {vertex_endpoint}: {response.text}")
