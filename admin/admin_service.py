import sqlite3
import time

import bcrypt
import jwt

from vertex import VertexService


class AdminService:
    def __init__(self, db: sqlite3.Connection, vertex_service: VertexService):
        self.db = db
        self.vertex_service = vertex_service
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

    def get_token(self, identifier: str, password: str) -> dict:
        cursor = self.db.cursor()
        admin = cursor.execute("SELECT identifier, password FROM admins WHERE identifier=?", (identifier,)).fetchone()

        if not bcrypt.checkpw(password.encode("utf-8"), admin[1].encode("utf-8")):
            raise Exception("Invalid identifier and password combination")

        vertex_endpoint = self.vertex_service.get_vertex_endpoint()
        jwt_signing_key = self.vertex_service.get_jwt_signing_key()

        return {
            "token": jwt.encode({
                "sub": admin[0],
                "type": "admin",
                "iat": int(time.time()),
                "iss": vertex_endpoint,
                "aud": vertex_endpoint
            }, algorithm="HS256", key=jwt_signing_key),
        }

    def run_migrations(self):
        cursor = self.db.cursor()
        cursor.execute("CREATE TABLE IF NOT EXISTS admins (identifier TEXT NOT NULL PRIMARY KEY, password TEXT NOT NULL, creator TEXT NOT NULL, created_at INTEGER NOT NULL, updated_at INTEGER NOT NULL)")
        self.db.commit()
        cursor.close()
