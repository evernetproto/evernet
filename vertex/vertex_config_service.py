import sqlite3
import time


class VertexConfigService:

    def __init__(self, db: sqlite3.Connection):
        self.db = db
        self.run_migrations()

    def is_initialized(self):
        cursor = self.db.cursor()
        count = cursor.execute('SELECT COUNT(*) FROM vertex_configs').fetchone()[0]
        return count > 0

    def set(self, config_key: str, config_value: str):
        now = int(time.time())
        cursor = self.db.cursor()
        cursor.execute(
            "INSERT INTO vertex_configs (config_key, config_value, created_at, updated_at) VALUES (?, ?, ?, ?) ON CONFLICT(config_key) DO UPDATE SET config_value = ?, updated_at = ?",
            (config_key, config_value, now, now, config_value, now))
        self.db.commit()
        cursor.close()

    def get(self, config_key: str) -> str:
        cursor = self.db.cursor()
        config = cursor.execute("SELECT config_value FROM vertex_configs WHERE config_key = ?", (config_key,)).fetchone()
        cursor.close()

        if not config:
            raise Exception(f"Config {config_key} not found")

        return config[0]

    def run_migrations(self):
        cursor = self.db.cursor()
        query = '''CREATE TABLE IF NOT EXISTS vertex_configs
                   (
                       config_key
                       TEXT
                       NOT
                       NULL
                       PRIMARY
                       KEY,
                       config_value
                       TEXT
                       NOT
                       NULL,
                       created_at
                       INTEGER
                       NOT
                       NULL,
                       updated_at
                       INTEGER
                       NOT
                       NULL
                   )'''
        cursor.execute(query)
        self.db.commit()
        cursor.close()
