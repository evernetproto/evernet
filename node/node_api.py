from flask import Flask

from utils.api import size, page
from .node_service import NodeService


class NodeAPI:
    def __init__(self, app: Flask, node_service: NodeService):
        self.app = app
        self.node_service = node_service

    def register(self):

        @self.app.get("/api/v1/nodes")
        def fetch_open_nodes():
            return self.node_service.fetch_open(page(), size())

        @self.app.get("/api/v1/nodes/<identifier>")
        def get_node_details(identifier):
            return self.node_service.get(identifier)
