import sqlite3
import time
from utils.ed25519 import *


class NodeService:
    def __init__(self, db: sqlite3.Connection):
        self.db = db
        self.run_migrations()

    def create(self, identifier: str, display_name: str, description: str, open: bool, creator: str) -> dict:
        if self.identifier_exists(identifier):
            raise Exception(f"Node {identifier} already exists")

        now = int(time.time())
        private_key, public_key = generate_ed25519_keys()
        signing_private_key = private_key_to_string(private_key)
        signing_public_key = public_key_to_string(public_key)

        cursor = self.db.cursor()
        cursor.execute("INSERT INTO nodes (identifier, display_name, description, open, signing_private_key, signing_public_key, creator, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                       (identifier, display_name, description, open, signing_private_key, signing_public_key, creator, now, now))
        self.db.commit()
        cursor.close()

        return {
            "identifier": identifier
        }

    def fetch(self, page: int = 0, size: int = 50) -> list[dict]:
        cursor = self.db.cursor()
        nodes = cursor.execute("SELECT identifier, display_name, description, open, signing_public_key, creator, created_at, updated_at FROM  nodes LIMIT ? OFFSET ?", (size, page * size)).fetchall()
        cursor.close()
        result = []

        for node in nodes:
            result.append({
                "identifier": node[0],
                "display_name": node[1],
                "description": node[2],
                "open": node[3],
                "signing_public_key": node[4],
                "creator": node[5],
                "created_at": node[6],
                "updated_at": node[7],
            })

        return result

    def get(self):
        pass

    def update(self):
        pass

    def delete(self):
        pass

    def reset_signing_key(self):
        pass

    def identifier_exists(self, identifier: str) -> bool:
        cursor = self.db.cursor()
        count = cursor.execute("SELECT COUNT(*) FROM nodes WHERE identifier=?", (identifier,)).fetchone()[0]
        return count > 0

    def run_migrations(self):
        cursor = self.db.cursor()
        cursor.execute("""
                       CREATE TABLE IF NOT EXISTS nodes
                       (
                           identifier          TEXT    NOT NULL PRIMARY KEY,
                           display_name        TEXT    NOT NULL,
                           description         TEXT,
                           open                BOOLEAN NOT NULL,
                           signing_private_key TEXT    NOT NULL,
                           signing_public_key  TEXT    NOT NULL,
                           creator             TEXT    NOT NULL,
                           created_at          INTEGER NOT NULL,
                           updated_at          INTEGER NOT NULL
                       )
                       """)
        self.db.commit()
        cursor.close()
