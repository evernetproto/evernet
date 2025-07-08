import datetime
import time
import uuid

import bcrypt
import jwt
from certifi import where

from model.user import User
from service.config_service import ConfigService
from service.node_service import NodeService
from utils import secret


class UserService:
    def __init__(self):
        pass

    @staticmethod
    def sign_up(node_identifier: str, identifier: str, password: str, display_name: str):
        node = NodeService.get(node_identifier)

        if not node["open"]:
            raise Exception(f"Node {node_identifier} not found")

        if User.select().where(User.node_identifier == node_identifier, User.identifier == identifier).exists():
            raise Exception(f"User {identifier} already exists on node {node_identifier}")

        user = User(
            node_identifier=node["identifier"],
            identifier=identifier,
            display_name=display_name,
            password=bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            creator=None
        )

        user.save()

        return {
            "id": user.get_id(),
            "identifier": user.identifier,
            "node_identifier": user.node_identifier
        }

    @staticmethod
    def get_token(node_identifier: str, identifier: str, password: str, target_node_address: str = None) -> dict:
        user = User.select().where(User.node_identifier == node_identifier, User.identifier == identifier).get_or_none()

        if not user:
            raise Exception("Invalid identifier and password combination")

        if not bcrypt.checkpw(password.encode(), user.password.encode()):
            raise Exception("Invalid identifier and password combination")

        vertex_endpoint = ConfigService.get_vertex_endpoint()
        issuer = f"{vertex_endpoint}/{node_identifier}"
        if not target_node_address:
            target_node_address = issuer

        jwt_token = jwt.encode(
            payload={
                "sub": user.identifier,
                "exp": int(time.time() + 3600 * 24),
                "iat": int(time.time()),
                "iss": issuer,
                "aud": target_node_address,
                "jti": str(uuid.uuid4()),
                "type": "user"
            },
            headers={
                "kid": issuer
            },
            key=NodeService.get_signing_private_key(node_identifier),
            algorithm="EdDSA"
        )

        return {
            "token": jwt_token
        }

    @staticmethod
    def get(identifier: str, node_identifier: str) -> dict:
        user = User.select().where(User.node_identifier == node_identifier, User.identifier == identifier).get_or_none()
        if not user:
            raise Exception(f"User {identifier} not found on node {node_identifier}")
        return UserService.to_dict(user)

    @staticmethod
    def update(identifier: str, display_name: str, node_identifier: str) -> dict:
        count = User.update(
            display_name=display_name,
            updated_at=datetime.datetime.now(tz=datetime.timezone.utc)
        ).where(User.identifier == identifier, User.node_identifier == node_identifier).execute()

        if count == 0:
            raise Exception(f"User {identifier} not found on node {node_identifier}")

        return {
            "identifier": identifier,
            "node_identifier": node_identifier
        }

    @staticmethod
    def change_password(identifier: str, password: str, node_identifier: str) -> dict:
        count = User.update(
            password=bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            updated_at=datetime.datetime.now(tz=datetime.timezone.utc)
        ).where(User.identifier == identifier, User.node_identifier == node_identifier).execute()

        if count == 0:
            raise Exception(f"User {identifier} not found on node {node_identifier}")

        return {
            "identifier": identifier,
            "node_identifier": node_identifier
        }

    @staticmethod
    def delete(identifier: str, node_identifier: str) -> dict:
        count = User.delete().where(User.identifier == identifier, User.node_identifier == node_identifier).execute()

        if count == 0:
            raise Exception(f"User {identifier} not found on node {node_identifier}")

        return {
            "identifier": identifier,
            "node_identifier": node_identifier
        }

    @staticmethod
    def add(identifier: str, display_name: str, node_identifier: str, creator: str) -> dict:
        node = NodeService.get(node_identifier)
        if User.select().where(User.node_identifier == node_identifier, User.identifier == identifier).exists():
            raise Exception(f"User {identifier} already exists on node {node_identifier}")

        password = secret.generate(16)

        user = User(
            node_identifier=node["identifier"],
            identifier=identifier,
            display_name=display_name,
            password=bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            creator=creator
        )

        user.save()

        return {
            "id": user.get_id(),
            "identifier": identifier,
            "node_identifier": node_identifier,
            "password": password,
        }

    @staticmethod
    def fetch(node_identifier: str, page: int = 1, size: int = 50):
        users = User.select().where(User.node_identifier == node_identifier).paginate(page, size)

        result = []
        for user in users:
            result.append(UserService.to_dict(user))

        return result

    @staticmethod
    def reset_password(identifier: str, node_identifier: str) -> dict:
        password = secret.generate(16)

        result = UserService.change_password(identifier, password, node_identifier)
        result["password"] = password
        return result

    @staticmethod
    def to_dict(user: User) -> dict:
        return {
            "id": user.get_id(),
            "identifier": user.identifier,
            "node_identifier": user.node_identifier,
            "display_name": user.display_name,
            "creator": user.creator,
            "created_at": user.created_at,
            "updated_at": user.updated_at,
        }
