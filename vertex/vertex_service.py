from password_generator import PasswordGenerator

from vertex import VertexConfigService


class VertexService:
    def __init__(self, vertex_config_service: VertexConfigService):
        self.vertex_config_service = vertex_config_service
        self.password_generator = PasswordGenerator()
        self.password_generator.minlen = 64
        self.password_generator.maxlen = 64

    def init(self, vertex_endpoint: str):
        if self.vertex_config_service.is_initialized():
            return

        self.vertex_config_service.set("VERTEX_ENDPOINT", vertex_endpoint)

        jwt_signing_key = self.password_generator.generate()
        self.vertex_config_service.set("JWT_SIGNING_KEY", jwt_signing_key)

    def info(self) -> dict:
        return {
            "endpoint": self.vertex_config_service.get("VERTEX_ENDPOINT"),
        }

    def get_vertex_endpoint(self) -> str:
        return self.vertex_config_service.get("VERTEX_ENDPOINT")

    def get_jwt_signing_key(self) -> str:
        return self.vertex_config_service.get("JWT_SIGNING_KEY")
