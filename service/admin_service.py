import time
from datetime import datetime
import bcrypt
import jwt
from pymongo.collection import Collection


class AdminService:
    def __init__(self, mongo: Collection, jwt_signing_key: str, vertex: str):
        self.mongo = mongo
        self.jwt_signing_key = jwt_signing_key
        self.vertex = vertex

    def init(self, identifier: str, password: str) -> dict:
        if self.mongo.count_documents({}) != 0:
            raise Exception("You are not allowed to perform this action")

        self.mongo.insert_one({
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
            "created_at": datetime.now(),
            "creator": identifier,
            "updated_at": datetime.now(),
        })

        return {
            "identifier": identifier,
        }

    def get_token(self, identifier: str, password: str) -> dict:
        admin = self.mongo.find_one({"identifier": identifier})
        if not admin:
            raise Exception("Invalid identifier and password combination")
        if not bcrypt.checkpw(password.encode("utf-8"), admin["password"].encode("utf-8")):
            raise Exception("Invalid identifier and password combination")

        return {
            "token": jwt.encode({
                "sub": admin["identifier"],
                "iat": int(time.time()),
                "exp": int(time.time()) + 3600,
                "iss": self.vertex,
                "aud": self.vertex,
                "type": "admin"
            },
            algorithm="HS256",
            key=self.jwt_signing_key),
        }

    def get(self, identifier: str) -> dict:
        admin = self.mongo.find_one({"identifier": identifier})
        if not admin:
            raise Exception(f"Admin {identifier} not found")
        return self.to_dict(admin)

    @staticmethod
    def to_dict(self):
        return {
            "identifier": self["identifier"],
            "creator": self["creator"],
            "created_at": self["created_at"],
            "updated_at": self["updated_at"],
        }