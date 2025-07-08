from flask import g, request
from functools import wraps
import jwt

from service.config_service import ConfigService
from service.node_key_service import NodeKeyService


def required_param(key: str, data_type=str):
    if not g.request_body:
        raise Exception("Request body is missing")
    if key not in g.request_body:
        raise Exception(f"{key} is required")
    val = g.request_body[key]
    if not isinstance(val, data_type):
        raise Exception(f"Invalid data type for value of {key}")
    return val


def optional_param(key: str, data_type=str):
    if not g.request_body:
        return None
    if key not in g.request_body:
        return None
    val = g.request_body[key]
    if not isinstance(val, data_type):
        raise Exception(f"Invalid data type for value of {key}")
    return val


def page():
    return request.args.get("page", 1, int)


def size():
    return request.args.get("size", 50, int)


def authenticate_admin(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None

        if 'Authorization' in request.headers:
            auth_header = request.headers['Authorization']
            if auth_header.startswith('Bearer '):
                token = auth_header.split(' ')[1]

        if not token:
            raise Exception("Invalid access token")

        jwt_signing_key = ConfigService.get_jwt_signing_key()
        vertex_endpoint = ConfigService.get_vertex_endpoint()

        try:
            data = jwt.decode(
                token,
                jwt_signing_key,
                algorithms=['HS256'],
                issuer=vertex_endpoint,
                audience=vertex_endpoint
            )

            if data["type"] != "admin":
                raise Exception("Invalid access token")

            current_admin = {
                "identifier": data["sub"]
            }
        except Exception as _:
            raise Exception("Invalid access token")

        return f(current_admin, *args, **kwargs)

    return decorated


def authenticate_user(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None

        if 'Authorization' in request.headers:
            auth_header = request.headers['Authorization']
            if auth_header.startswith('Bearer '):
                token = auth_header.split(' ')[1]

        if not token:
            raise Exception("Invalid access token")

        try:
            kid = jwt.get_unverified_header(token)["kid"]
            issuer_vertex_endpoint, issuer_node_identifier, issuer_signing_public_key = NodeKeyService.get_signing_public_key(kid)

            data = jwt.decode(
                token,
                issuer_signing_public_key,
                algorithms=['EdDSA'],
                issuer="%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier),
                options={"verify_aud": False}
            )

            if data["type"] != "user":
                raise Exception("Invalid access token")

            audience = data["aud"]
            audience_components = str(audience).split("/")

            if len(audience_components) != 2:
                raise Exception("Invalid audience in access token")

            audience_vertex_endpoint = audience_components[0]
            audience_node_identifier = audience_components[1]

            current_vertex_endpoint = ConfigService.get_vertex_endpoint()

            if audience_vertex_endpoint != current_vertex_endpoint:
                raise Exception("Invalid audience in access token")

            is_local = audience_node_identifier == issuer_node_identifier and audience_vertex_endpoint == issuer_vertex_endpoint

            current_user = {
                "identifier": data["sub"],
                "source_vertex_endpoint": issuer_vertex_endpoint,
                "source_node_identifier": issuer_node_identifier,
                "source_node_address": "%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier),
                "address": "%s/%s/%s" % (issuer_vertex_endpoint, issuer_node_identifier, data["sub"]),
                "target_vertex_endpoint": audience_vertex_endpoint,
                "target_node_identifier": audience_node_identifier,
                "target_node_address": "%s/%s" % (audience_vertex_endpoint, audience_node_identifier),
                "local": is_local
            }
        except Exception as e:
            print(e)
            raise Exception("Invalid access token")

        return f(current_user, *args, **kwargs)

    return decorated
