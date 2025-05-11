from flask import Flask

from .vertex_service import VertexService


class VertexAPI:
    def __init__(self, app: Flask, vertex_service: VertexService):
        self.app = app
        self.vertex_service = vertex_service

    def register(self):
        @self.app.get("/info")
        def get_vertex_info():
            return self.vertex_service.info()
