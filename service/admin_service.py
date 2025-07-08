import datetime
from time import time

import bcrypt
import jwt

from model.admin import Admin
from service.config_service import ConfigService
from utils import secret


class AdminService:
    def __init__(self):
        pass

    @staticmethod
    def init(identifier: str, password: str, vertex_endpoint: str, vertex_display_name: str, vertex_description: str) -> dict:
        if Admin.select().exists():
            raise Exception("You are not allowed to perform this action")

        admin = Admin(
            identifier=identifier,
            password=bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            creator=identifier
        )

        admin.save()

        ConfigService.set_vertex_endpoint(vertex_endpoint)
        ConfigService.set_vertex_display_name(vertex_display_name)
        ConfigService.set_vertex_description(vertex_description)

        return {
            "id": admin.get_id(),
            "identifier": admin.identifier,
            "creator": admin.creator,
            "created_at": admin.created_at,
            "updated_at": admin.updated_at
        }

    @staticmethod
    def get_token(identifier: str, password: str) -> dict:
        admin = Admin.select().where(Admin.identifier == identifier).get_or_none()

        if not admin:
            raise Exception("Invalid identifier and password combination")

        if not bcrypt.checkpw(password.encode(), admin.password.encode()):
            raise Exception("Invalid identifier and password combination")

        vertex_endpoint = ConfigService.get_vertex_endpoint()
        jwt_signing_key = ConfigService.get_jwt_signing_key()

        return {
            "token": jwt.encode({
                "sub": admin.identifier,
                "type": "admin",
                "iat": int(time()),
                "iss": vertex_endpoint,
                "aud": vertex_endpoint,
            }, algorithm="HS256", key=jwt_signing_key),
        }

    @staticmethod
    def get(identifier):
        admin = Admin.select().where(Admin.identifier == identifier).get_or_none()

        if not admin:
            raise Exception(f"Admin {identifier} not found")

        return {
            "identifier": admin.identifier,
            "creator": admin.creator,
            "created_at": admin.created_at,
            "updated_at": admin.updated_at
        }

    @staticmethod
    def change_password(identifier: str, password: str) -> dict:
        result = Admin.update(
            password = bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            updated_at=datetime.datetime.now(tz=datetime.timezone.utc)
        ).where(Admin.identifier == identifier).execute()

        if result == 0:
            raise Exception(f"Admin {identifier} not found")

        return {
            "identifier": identifier
        }

    @staticmethod
    def add(identifier: str, creator: str) -> dict:
        if Admin.select().where(Admin.identifier == identifier).exists():
            raise Exception(f"Admin {identifier} already exists")

        password = secret.generate(16)

        admin = Admin(
            identifier=identifier,
            password=bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            creator=creator,
        )

        admin.save()

        return {
            "id": admin.get_id(),
            "identifier": admin.identifier,
            "password": password,
            "creator": admin.creator,
            "created_at": admin.created_at,
            "updated_at": admin.updated_at
        }

    @staticmethod
    def fetch(page=0, size=50) -> list[dict]:
        result = []

        for admin in Admin.select().paginate(page, size):
            result.append({
                "id": admin.get_id(),
                "identifier": admin.identifier,
                "creator": admin.creator,
                "created_at": admin.created_at,
                "updated_at": admin.updated_at
            })

        return result

    @staticmethod
    def delete(identifier: str) -> dict:
        count = Admin.delete().where(Admin.identifier == identifier).execute()

        if count == 0:
            raise Exception(f"Admin {identifier} not found")

        return {
            "identifier": identifier,
        }

    @staticmethod
    def reset_password(identifier: str) -> dict:
        password = secret.generate(16)
        AdminService.change_password(identifier, password)
        return {
            "identifier": identifier,
            "password": password
        }
