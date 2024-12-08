import os
import pymongo
from dotenv import *
from flask import Flask, request, jsonify, g

from api import HealthApi
from lib.api import register_api_handlers

app = Flask(__name__)
load_dotenv()

jwt_signing_key = os.getenv("JWT_SIGNING_KEY")
vertex = os.getenv("VERTEX_ENDPOINT")
federation_protocol = os.getenv("FEDERATION_PROTOCOL")

db = pymongo.MongoClient(os.getenv("DB_HOST"), int(os.getenv("DB_PORT"))).evernet

HealthApi(app, vertex).register()

register_api_handlers(app, jwt_signing_key, vertex)

if __name__ == '__main__':
    app.run(
        host=os.getenv("HOST"),
        port=int(os.getenv("PORT")),
        debug=os.getenv("ENV") != "PROD"
    )
