from flask import Flask

from service.user_service import UserService
from utils.api import required_param, optional_param, authenticate_user


class UserController:
    def __init__(self, app: Flask):
        self.app = app

    def register(self):
        @self.app.post("/api/v1/nodes/<node_identifier>/users/signup")
        def user_sign_up(node_identifier):
            return UserService.sign_up(
                node_identifier,
                required_param("identifier"),
                required_param("password"),
                required_param("display_name")
            )

        @self.app.post("/api/v1/nodes/<node_identifier>/users/token")
        def get_user_token(node_identifier):
            return UserService.get_token(
                node_identifier,
                required_param("identifier"),
                required_param("password"),
                optional_param("target_node_address")
            )

        @self.app.get("/api/v1/users/me")
        @authenticate_user
        def get_current_user_details(user):
            if not user["local"]:
                raise Exception("You are not allowed to perform this action")
            return UserService.get(user["identifier"], user["target_node_identifier"])

        @self.app.put("/api/v1/users/me")
        @authenticate_user
        def update_current_user_details(user):
            if not user["local"]:
                raise Exception("You are not allowed to perform this action")
            return UserService.update(
                user["identifier"],
                required_param("display_name"),
                user["target_node_identifier"]
            )

        @self.app.put("/api/v1/users/me/password")
        @authenticate_user
        def update_current_user_password(user):
            if not user["local"]:
                raise Exception("You are not allowed to perform this action")
            return UserService.change_password(
                user["identifier"],
                required_param("password"),
                user["target_node_identifier"]
            )

        @self.app.delete("/api/v1/users/me")
        @authenticate_user
        def delete_current_user(user):
            if not user["local"]:
                raise Exception("You are not allowed to perform this action")
            return UserService.delete(
                user["identifier"],
                user["node_identifier"]
            )
