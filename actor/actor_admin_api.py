from flask import Flask

from utils.api import authenticate_admin, required_param, optional_param, size, page
from .actor_service import ActorService


class ActorAdminAPI:
    def __init__(self, app: Flask, actor_service: ActorService):
        self.app = app
        self.actor_service = actor_service

    def register(self):

        @self.app.post("/api/v1/admins/nodes/<node_identifier>/actors")
        @authenticate_admin
        def admin_add_actor(admin, node_identifier):
            return self.actor_service.add(
                node_identifier,
                required_param("identifier"),
                required_param("display_name"),
                required_param("type"),
                optional_param("description"),
                admin["identifier"],
            )

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/actors")
        @authenticate_admin
        def admin_fetch_actors(_, node_identifier):
            return self.actor_service.fetch(node_identifier, page(), size())

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/actors/<actor_identifier>")
        @authenticate_admin
        def admin_get_actor(_, node_identifier, actor_identifier):
            return self.actor_service.get(node_identifier, actor_identifier)

        @self.app.put("/api/v1/admins/nodes/<node_identifier>/actors/<actor_identifier>")
        @authenticate_admin
        def admin_update_actor(_, node_identifier, actor_identifier):
            return self.actor_service.update(
                node_identifier,
                actor_identifier,
                required_param("display_name"),
                required_param("type"),
                optional_param("description")
            )

        @self.app.put("/api/v1/admins/nodes/<node_identifier>/actors/<actor_identifier>/password")
        @authenticate_admin
        def admin_reset_actor_password(_, node_identifier, actor_identifier):
            return self.actor_service.reset_password(
                node_identifier,
                actor_identifier,
            )

        @self.app.delete("/api/v1/admins/nodes/<node_identifier>/actors/<actor_identifier>")
        @authenticate_admin
        def admin_delete_actor(_, node_identifier, actor_identifier):
            return self.actor_service.delete(node_identifier, actor_identifier)
