import sqlite3
import time
import uuid

import bcrypt
import jwt

from node import NodeService
from utils.ed25519 import string_to_private_key
from vertex import VertexService


class ActorService:
    def __init__(self, db: sqlite3.Connection, node_service: NodeService, vertex_service: VertexService):
        self.db = db
        self.vertex_service = vertex_service
        self.node_service = node_service
        self.run_migrations()

    def sign_up(self, node_identifier: str, identifier: str, password: str, display_name: str, actor_type: str,
                description: str) -> dict:
        node = self.node_service.get_open(node_identifier)
        if self.identifier_exists(identifier, node_identifier):
            raise Exception(f"Actor {identifier} already exists on node {node["identifier"]}")

        hashed_password = bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")
        now = int(time.time())

        cursor = self.db.cursor()
        cursor.execute(
            "INSERT INTO actors (identifier, password, node_identifier, display_name, type, description, creator, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            (identifier, hashed_password, node_identifier, display_name, actor_type, description, identifier, now, now))
        self.db.commit()
        cursor.close()

        return {
            "identifier": identifier,
            "node_identifier": node_identifier,
        }

    def get_token(self, node_identifier: str, identifier: str, password: str, target_node_address: str=None) -> dict:
        node = self.node_service.get_private_key(node_identifier)

        cursor = self.db.cursor()
        user = cursor.execute("SELECT identifier, password FROM actors WHERE node_identifier = ? AND identifier = ?",
                       (node["identifier"], identifier)).fetchone()
        cursor.close()

        if not user:
            raise Exception("Invalid identifier and password combination")

        if not bcrypt.checkpw(password.encode("utf-8"), user[1].encode("utf-8")):
            raise Exception("Invalid identifier and password combination")

        vertex_endpoint = self.vertex_service.get_vertex_endpoint()
        if not target_node_address:
            target_node_address = f"{vertex_endpoint}/{node_identifier}"

        token = jwt.encode({
            "sub": user[0],
            "iss": f"{vertex_endpoint}/{node_identifier}",
            "aud": target_node_address,
            "iat": int(time.time()),
            "type": "actor"
        }, headers={
            "kid": f"{vertex_endpoint}/{node_identifier}"
        }, algorithm="EdDSA", key=string_to_private_key(node["signing_private_key"]))

        return {
            "token": token
        }

    def identifier_exists(self, identifier: str, node_identifier: str) -> bool:
        cursor = self.db.cursor()
        count = cursor.execute("SELECT COUNT(*) FROM actors WHERE identifier=? AND node_identifier=?",
                               (identifier, node_identifier)).fetchone()[0]
        return count > 0

    def run_migrations(self):
        query = """
                CREATE TABLE IF NOT EXISTS actors
                (
                    id              INTEGER PRIMARY KEY AUTOINCREMENT,
                    identifier      TEXT    NOT NULL,
                    password        TEXT    NOT NULL,
                    node_identifier TEXT    NOT NULL,
                    display_name    TEXT    NOT NULL,
                    type            TEXT    NOT NULL,
                    description     TEXT,
                    creator         TEXT    NOT NULL,
                    created_at      INTEGER NOT NULL,
                    updated_at      INTEGER NOT NULL,
                    UNIQUE (identifier, node_identifier)
                )
                """

        cursor = self.db.cursor()
        cursor.execute(query)
        self.db.commit()
        cursor.close()
