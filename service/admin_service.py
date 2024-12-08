import time
from datetime import datetime
import bcrypt
import jwt
from pymongo.collection import Collection
from password_generator import PasswordGenerator


class AdminService:
    def __init__(self, mongo: Collection, jwt_signing_key: str, vertex: str):
        self.mongo = mongo
        self.jwt_signing_key = jwt_signing_key
        self.vertex = vertex
        self.password_generator = PasswordGenerator()

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

    def change_password(self, identifier: str, password: str) -> dict:
        fields = {
            "updated_at": datetime.now(),
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
        }

        result = self.mongo.update_one({
            "identifier": identifier,
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise Exception(f"Admin {identifier} not found")

        return {
            "identifier": identifier,
        }

    def add(self, identifier: str, creator: str) -> dict:
        if self.mongo.count_documents({"identifier": identifier}) != 0:
            raise Exception(f"Admin {identifier} already exists")

        new_password = self.password_generator.generate()

        self.mongo.insert_one({
            "identifier": identifier,
            "password": bcrypt.hashpw(new_password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
            "creator": creator,
            "created_at": datetime.now(),
            "updated_at": datetime.now(),
        })

        return {
            "identifier": identifier,
            "password": new_password
        }

    def delete(self, identifier: str) -> dict:
        result = self.mongo.delete_one({"identifier": identifier})
        if result.deleted_count == 0:
            raise Exception(f"Admin {identifier} not found")
        return {
            "identifier": identifier,
        }

    def fetch(self, page=0, size=50) -> list[dict]:
        admins = self.mongo.find({}).skip(page * size).limit(size)
        result = []
        for admin in admins:
            result.append(self.to_dict(admin))
        return result

    def reset_password(self, identifier: str) -> dict:
        new_password = self.password_generator.generate()
        result = self.change_password(identifier, new_password)
        result["password"] = new_password
        return result

    @staticmethod
    def to_dict(self):
        return {
            "identifier": self["identifier"],
            "creator": self["creator"],
            "created_at": self["created_at"],
            "updated_at": self["updated_at"],
        }