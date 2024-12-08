from pymongo.collection import Collection
from datetime import datetime

from lib.ed25519 import generate_ed25519_keys, private_key_to_string, public_key_to_string


class NodeService:
    def __init__(self, mongo: Collection):
        self.mongo = mongo

    def create(self, identifier: str, display_name: str, description: str, creator: str) -> dict:
        if self.mongo.count_documents({
            "identifier": identifier,
        }) != 0:
            raise Exception(f"Node {identifier} already exists")

        signing_private_key, signing_public_key = generate_ed25519_keys()

        self.mongo.insert_one({
            "identifier": identifier,
            "display_name": display_name,
            "description": description,
            "signing_private_key": private_key_to_string(signing_private_key),
            "signing_public_key": public_key_to_string(signing_public_key),
            "creator": creator,
            "created_at": datetime.now(),
            "updated_at": datetime.now(),
        })

        return {
            "identifier": identifier,
        }

    def fetch(self, page=0, size=50) -> list[dict]:
        nodes = self.mongo.find({}).skip(page * size).limit(size)
        result = []
        for node in nodes:
            result.append(self.to_dict(node))
        return result

    def get(self, identifier: str) -> dict:
        node = self.mongo.find_one({"identifier": identifier})
        if not node:
            raise Exception(f"Node {identifier} not found")
        return self.to_dict(node)

    def update(self):
        pass

    def delete(self):
        pass

    @staticmethod
    def to_dict(self):
        return {
            "identifier": self["identifier"],
            "display_name": self["display_name"],
            "description": self["description"],
            "signing_public_key": self["signing_public_key"],
            "creator": self["creator"],
            "created_at": self["created_at"],
            "updated_at": self["updated_at"],
        }
