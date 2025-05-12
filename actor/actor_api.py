from flask import Flask

from utils.api import required_param, optional_param, authenticate_actor
from .actor_service import ActorService

class ActorAPI:
    def __init__(self, app: Flask, actor_service: ActorService):
        self.app = app
        self.actor_service = actor_service

    def register(self):

        @self.app.post("/api/v1/nodes/<node_identifier>/actors/signup")
        def actor_sign_up(node_identifier):
            return self.actor_service.sign_up(
                node_identifier,
                required_param("identifier"),
                required_param("password"),
                required_param("display_name"),
                required_param("type"),
                optional_param("description")
            )

        @self.app.post("/api/v1/nodes/<node_identifier>/actors/token")
        def get_actor_token(node_identifier):
            return self.actor_service.get_token(
                node_identifier,
                required_param("identifier"),
                required_param("password"),
                optional_param("target_node_address")
            )

        @self.app.get("/api/v1/nodes/<node_identifier>/actors/current")
        @authenticate_actor(should_be_local=True)
        def get_actor_details(actor, node_identifier):
            return self.actor_service.get(node_identifier, actor["identifier"])
