from functools import wraps

import jwt
from flask import g, request, Flask, jsonify


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
    return request.args.get("page", 0, int)


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

        try:
            data = jwt.decode(
                token,
                g.jwt_signing_key,
                algorithms=['HS256'],
                issuer=g.vertex,
                audience=g.vertex
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


def register_api_handlers(app: Flask, jwt_signing_key: str, vertex: str):
    @app.before_request
    def before_request():
        g.request_body = request.get_json(force=True, silent=True)
        g.jwt_signing_key = jwt_signing_key
        g.vertex = vertex

    @app.errorhandler(404)
    def handle_404_error(e):
        return jsonify({
            "success": False,
            "message": str(e)
        }), 404

    @app.errorhandler(Exception)
    def handle_all_errors(e):
        return jsonify({
            "success": False,
            "message": str(e)
        }), 500