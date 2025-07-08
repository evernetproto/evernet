from flask import Flask

from service.node_service import NodeService
from utils.api import authenticate_admin, required_param, optional_param, size, page


class NodeController:
    def __init__(self, app: Flask):
        self.app = app

    def register(self):

        @self.app.post("/api/v1/admins/nodes")
        @authenticate_admin
        def create_node(admin):
            return NodeService.create(
                required_param("identifier"),
                required_param("display_name"),
                optional_param("description"),
                required_param("open", bool),
                admin["identifier"]
            )

        @self.app.get("/api/v1/admins/nodes")
        @authenticate_admin
        def list_nodes(admin):
            return NodeService.fetch(page(), size())

        @self.app.get("/api/v1/admins/nodes/<identifier>")
        @authenticate_admin
        def get_node(admin, identifier):
            return NodeService.get(identifier)

        @self.app.put("/api/v1/admins/nodes/<identifier>")
        @authenticate_admin
        def update_node(admin, identifier):
            return NodeService.update(
                identifier,
                required_param("display_name"),
                optional_param("description"),
            )

        @self.app.put("/api/v1/admins/nodes/<identifier>/open")
        @authenticate_admin
        def update_node_open_flag(admin, identifier):
            return NodeService.update_open(
                identifier,
                required_param("open", bool)
            )

        @self.app.delete("/api/v1/admins/nodes/<identifier>")
        @authenticate_admin
        def delete_node(admin, identifier):
            return NodeService.delete(identifier)

        @self.app.put("/api/v1/admins/nodes/<identifier>/signing-keys")
        @authenticate_admin
        def reset_node_signing_keys(admin, identifier):
            return NodeService.reset_signing_keys(identifier)

        @self.app.get("/api/v1/nodes")
        def fetch_open_nodes():
            return NodeService.fetch_open(page(), size())

        @self.app.get("/api/v1/nodes/<identifier>")
        def get_node_details(identifier):
            return NodeService.get(identifier)