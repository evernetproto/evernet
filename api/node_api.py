from flask import Flask

from lib.api import authenticate_admin, required_param, optional_param, size, page
from service import NodeService


class NodeApi:
    def __init__(self, app: Flask, node_service: NodeService):
        self.app = app
        self.node_service = node_service

    def register(self):

        @self.app.post("/api/v1/nodes")
        @authenticate_admin
        def create_node(admin):
            return self.node_service.create(
                required_param("identifier"),
                required_param("display_name"),
                optional_param("description"),
                admin["identifier"],
            )

        @self.app.get("/api/v1/nodes")
        def fetch_nodes():
            return self.node_service.fetch(page(), size())

        @self.app.get("/api/v1/nodes/<identifier>")
        def get_node(identifier):
            return self.node_service.get(identifier)

        @self.app.put("/api/v1/nodes/<identifier>")
        @authenticate_admin
        def update_node(admin, identifier):
            return self.node_service.update(
                identifier,
                optional_param("display_name"),
                optional_param("description"),
            )

        @self.app.delete("/api/v1/nodes/<identifier>")
        @authenticate_admin
        def delete_node(_, identifier):
            return self.node_service.delete(identifier)

        @self.app.put("/api/v1/nodes/<identifier>/signing-key")
        @authenticate_admin
        def reset_node_signing_key(_, identifier):
            return self.node_service.reset_signing_key(identifier)
