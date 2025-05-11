from flask import Flask

from .node_service import NodeService
from utils.api import authenticate_admin, required_param, optional_param, size, page


class NodeAdminAPI:
    def __init__(self, app: Flask, node_service: NodeService):
        self.app = app
        self.node_service = node_service

    def register(self):
        @self.app.post("/api/v1/admins/nodes")
        @authenticate_admin
        def create_node(admin):
            return self.node_service.create(
                required_param("identifier"),
                required_param("display_name"),
                optional_param("description"),
                required_param("open", bool),
                admin["identifier"]
            )

        @self.app.get("/api/v1/admins/nodes")
        @authenticate_admin
        def fetch_nodes(_):
            return self.node_service.fetch(page(), size())

        @self.app.get("/api/v1/admins/nodes/<identifier>")
        @authenticate_admin
        def get_node(_, identifier):
            return self.node_service.get(identifier)

        @self.app.put("/api/v1/admins/nodes/<identifier>")
        @authenticate_admin
        def update_node(_, identifier):
            return self.node_service.update(
                identifier,
                required_param("display_name"),
                optional_param("description"),
                required_param("open", bool)
            )

        @self.app.delete("/api/v1/admins/nodes/<identifier>")
        @authenticate_admin
        def delete_node(_, identifier):
            return self.node_service.delete(identifier)

        @self.app.put("/api/v1/admins/nodes/<identifier>/signing-keys")
        @authenticate_admin
        def reset_node_signing_keys(_, identifier):
            return self.node_service.reset_signing_keys(identifier)
