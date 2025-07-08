import datetime

from model.config import Config
from utils import secret


class ConfigService:
    def __init__(self):
        pass

    @staticmethod
    def init():
        ConfigService.upsert(key='jwt_signing_key', value=secret.generate(64))

    @staticmethod
    def upsert(key: str, value: str):
        Config.insert(
            key=key,
            value=value,
            created_at=datetime.datetime.now(tz=datetime.timezone.utc),
            updated_at=datetime.datetime.now(tz=datetime.timezone.utc)
        ).on_conflict(
            "IGNORE"
        ).execute()

    @staticmethod
    def set(key: str, value: str):
        Config.insert(
            key=key,
            value=value,
            created_at=datetime.datetime.now(tz=datetime.timezone.utc),
            updated_at=datetime.datetime.now(tz=datetime.timezone.utc)
        ).on_conflict(
            conflict_target=[Config.key],
            preserve=[Config.created_at],
            update={
                Config.value: value,
                Config.updated_at: datetime.datetime.now(tz=datetime.timezone.utc)
            }
        ).execute()

    @staticmethod
    def get_or_default(key: str, default: str) -> str:
        val = Config.select(Config.value).where(Config.key == key).get_or_none()

        if val is None:
            return default

        return val.value

    @staticmethod
    def get_jwt_signing_key():
        return ConfigService.get_or_default('jwt_signing_key', 'secret')

    @staticmethod
    def set_vertex_endpoint(vertex_endpoint: str):
        ConfigService.set("vertex_endpoint", vertex_endpoint)

    @staticmethod
    def get_vertex_endpoint():
        return ConfigService.get_or_default('vertex_endpoint', "localhost:3000")

    @staticmethod
    def set_vertex_display_name(vertex_display_name: str):
        ConfigService.set('vertex_display_name', vertex_display_name)

    @staticmethod
    def get_vertex_display_name():
        return ConfigService.get_or_default('vertex_display_name', "Vertex")

    @staticmethod
    def set_vertex_description(vertex_description):
        ConfigService.set('vertex_description', vertex_description)

    @staticmethod
    def get_vertex_description():
        return ConfigService.get_or_default('vertex_description', "Vertex")

    @staticmethod
    def get_federation_protocol():
        return ConfigService.get_or_default('federation_protocol', "http")
