from flask import Flask

from admin import AdminService
from utils.api import required_param, authenticate_admin, size, page


class AdminAPI:

    def __init__(self, app: Flask, admin_service: AdminService):
        self.app = app
        self.admin_service = admin_service

    def register(self):

        @self.app.post("/api/v1/admins/init")
        def init_admin():
            return self.admin_service.init(
                required_param("identifier"),
                required_param("password"),
                required_param("vertex_endpoint")
            )

        @self.app.post("/api/v1/admins/token")
        def get_admin_token():
            return self.admin_service.get_token(
                required_param("identifier"),
                required_param("password")
            )

        @self.app.get("/api/v1/admins/current")
        @authenticate_admin
        def get_admin(admin):
            return self.admin_service.get(admin["identifier"])

        @self.app.put("/api/v1/admins/current/password")
        @authenticate_admin
        def change_admin_password(admin):
            return self.admin_service.change_password(
                admin["identifier"],
                required_param("password")
            )

        @self.app.post("/api/v1/admins")
        @authenticate_admin
        def add_admin(admin):
            return self.admin_service.add(
                required_param("identifier"),
                admin["identifier"],
            )

        @self.app.get("/api/v1/admins")
        @authenticate_admin
        def fetch_admins(_):
            return self.admin_service.fetch(page(), size())
