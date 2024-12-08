from datetime import datetime
import bcrypt
from pymongo.collection import Collection


class AdminService:
    def __init__(self, mongo: Collection):
        self.mongo = mongo

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
