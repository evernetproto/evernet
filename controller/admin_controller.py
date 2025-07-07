from flask import Flask

from service.admin_service import AdminService
from utils.api import required_param, authenticate_admin, size, page


class AdminController:
    def __init__(self, app: Flask):
        self.app = app

    def register(self):

        @self.app.post("/api/v1/admins/init")
        def init_admin():
            return AdminService.init(
                required_param("identifier"),
                required_param("password"),
                required_param("vertex_endpoint"),
                required_param("vertex_display_name"),
                required_param("vertex_description"),
            )

        @self.app.post("/api/v1/admins/token")
        def get_admin_token():
            return AdminService.get_token(
                required_param("identifier"),
                required_param("password"),
            )

        @self.app.get("/api/v1/admins/current")
        @authenticate_admin
        def get_current_admin(admin):
            return AdminService.get(admin["identifier"])

        @self.app.put("/api/v1/admins/current/password")
        @authenticate_admin
        def change_current_admin_password(admin):
            return AdminService.change_password(admin["identifier"], required_param("password"))

        @self.app.post("/api/v1/admins")
        @authenticate_admin
        def add_admin(admin):
            return AdminService.add(required_param("identifier"), admin["identifier"])

        @self.app.get("/api/v1/admins")
        @authenticate_admin
        def list_admins(admin):
            return AdminService.list(page(), size())

        @self.app.get("/api/v1/admins/<identifier>")
        @authenticate_admin
        def get_admin(admin, identifier):
            return AdminService.get(identifier)

        @self.app.delete("/api/v1/admins/<identifier>")
        @authenticate_admin
        def delete_admin(admin, identifier):
            return AdminService.delete(identifier)

        @self.app.put("/api/v1/admins/<identifier>/password")
        @authenticate_admin
        def reset_admin_password(admin, identifier):
            return AdminService.reset_password(identifier)
