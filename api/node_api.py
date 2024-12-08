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
