import sqlite3
import time

import bcrypt
import jwt
from password_generator import PasswordGenerator

from vertex import VertexService


class AdminService:
    def __init__(self, db: sqlite3.Connection, vertex_service: VertexService):
        self.db = db
        self.vertex_service = vertex_service
        self.password_generator = PasswordGenerator()
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

    def get(self, identifier: str) -> dict:
        cursor = self.db.cursor()
        admin = cursor.execute("SELECT identifier, creator, created_at, updated_at FROM admins WHERE identifier=?", (identifier,)).fetchone()

        if not admin:
            raise Exception(f"Admin {identifier} not found")

        return {
            "identifier": admin[0],
            "creator": admin[1],
            "created_at": admin[2],
            "updated_at": admin[3],
        }

    def change_password(self, identifier: str, password: str) -> dict:
        hashed_password = bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")
        now = int(time.time())

        cursor = self.db.cursor()
        result = cursor.execute("UPDATE admins SET password=?, updated_at = ? WHERE identifier=?", (hashed_password, now, identifier))
        self.db.commit()
        cursor.close()

        if result.rowcount == 0:
            raise Exception(f"Admin {identifier} not found")

        return {
            "identifier": identifier,
        }

    def add(self, identifier: str, creator: str) -> dict:
        password = self.password_generator.generate()
        hashed_password = bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")

        now = int(time.time())
        cursor = self.db.cursor()

        count = cursor.execute("SELECT COUNT(*) FROM admins WHERE identifier = ?", (identifier,)).fetchone()[0]

        if count > 0:
            raise Exception(f"Admin identifier {identifier} is already taken")

        cursor.execute("INSERT INTO admins (identifier, password, creator, created_at, updated_at) VALUES (?, ?, ?, ?, ?)", (identifier, hashed_password, creator, now, now))
        self.db.commit()
        cursor.close()

        return {
            "identifier": identifier,
            "password": password
        }

    def fetch(self, page: int = 0, size: int = 50) -> list[dict]:
        cursor = self.db.cursor()
        users = cursor.execute("SELECT identifier, creator, created_at, updated_at FROM admins LIMIT ? OFFSET ?", (size, page * size)).fetchall()
        cursor.close()
        result = []
        for user in users:
            result.append({
                "identifier": user[0],
                "creator": user[1],
                "created_at": user[2],
                "updated_at": user[3],
            })
        return result

    def delete(self, identifier: str) -> dict:
        cursor = self.db.cursor()
        result = cursor.execute("DELETE FROM admins WHERE identifier = ?", (identifier,))
        self.db.commit()
        cursor.close()

        if result.rowcount == 0:
            raise Exception(f"Admin {identifier} not found")

        return {
            "identifier": identifier,
        }

    def reset_password(self, identifier: str) -> dict:
        password = self.password_generator.generate()
        hashed_password = bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")
        now = int(time.time())
        cursor = self.db.cursor()
        result = cursor.execute("UPDATE admins SET password=?, updated_at = ? WHERE identifier=?", (hashed_password, now, identifier))
        self.db.commit()
        cursor.close()

        if result.rowcount == 0:
            raise Exception(f"Admin {identifier} not found")

        return {
            "identifier": identifier,
            "password": password
        }

    def run_migrations(self):
        cursor = self.db.cursor()
        cursor.execute("CREATE TABLE IF NOT EXISTS admins (identifier TEXT NOT NULL PRIMARY KEY, password TEXT NOT NULL, creator TEXT NOT NULL, created_at INTEGER NOT NULL, updated_at INTEGER NOT NULL)")
        self.db.commit()
        cursor.close()
