import sqlite3
import time

import bcrypt

from vertex import VertexConfigService, VertexService


class AdminService:
    def __init__(self, db: sqlite3.Connection, vertex_service: VertexService, vertex_config_service: VertexConfigService):
        self.db = db
        self.vertex_service = vertex_service
        self.vertex_config_service = vertex_config_service
        self.run_migrations()

    def init(self, identifier: str, password: str, vertex_endpoint: str) -> dict:
        cursor = self.db.cursor()
        count = cursor.execute("SELECT COUNT(*) FROM admins").fetchone()[0]
        if count > 0:
            raise Exception("You are not allowed to perform this action")

        self.vertex_service.init(vertex_endpoint)

        hashed_password = bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")
        now = int(time.time())

        cursor.execute("INSERT INTO admins (identifier, password, creator, created_at, updated_at) VALUES (?, ?, ?, ?, ?)", (identifier, hashed_password, identifier, now, now))
        self.db.commit()
        cursor.close()

        return {
            "identifier": identifier,
        }

    def run_migrations(self):
        cursor = self.db.cursor()
        cursor.execute("CREATE TABLE IF NOT EXISTS admins (identifier TEXT NOT NULL PRIMARY KEY, password TEXT NOT NULL, creator TEXT NOT NULL, created_at INTEGER NOT NULL, updated_at INTEGER NOT NULL)")
        self.db.commit()
        cursor.close()
