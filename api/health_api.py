from flask import Flask


class HealthApi:
    def __init__(self, app: Flask, vertex: str) -> None:
        self.app = app
        self.vertex = vertex

    def register(self):

        @self.app.get("/health")
        def health_check():
            return {
                "status": "ok",
            }

        @self.app.get("/vertex")
        def info_check():
            return {
                "vertex": self.vertex,
            }
