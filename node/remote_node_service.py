from vertex import VertexService
import requests


class RemoteNodeService:
    def __init__(self, vertex_service: VertexService):
        self.vertex_service = vertex_service

    def get(self, vertex_endpoint: str, identifier: str) -> dict:
        response = requests.get(f"{self.vertex_service.get_federation_protocol()}://{vertex_endpoint}/api/v1/nodes/{identifier}").json()

        return {
            "identifier": response["identifier"],
            "display_name": response["display_name"],
            "signing_public_key": response["signing_public_key"],
            "description": response["description"],
            "creator": response["creator"],
            "created_at": response["created_at"],
            "updated_at": response["updated_at"]
        }
