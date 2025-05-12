import os
import sqlite3
import traceback

from dotenv import *
from flask import Flask, request, jsonify, g

from actor import ActorService, ActorAPI, ActorAdminAPI
from admin import AdminService, AdminAPI
from node import NodeService, NodeAdminAPI, NodeAPI, RemoteNodeService, NodeKeyService
from vertex import HealthCheckAPI, VertexConfigService, VertexService, VertexAPI
from web import Web

load_dotenv()
data_path = os.getenv("DATA_PATH")

if not os.path.exists(data_path):
    os.makedirs(data_path)

app = Flask(__name__)
db = sqlite3.connect(f"{data_path}/evernet.sqlite", check_same_thread=False)

vertex_config_service = VertexConfigService(db)
vertex_service = VertexService(vertex_config_service)
admin_service = AdminService(db, vertex_service)
node_service = NodeService(db)
remote_node_service = RemoteNodeService(vertex_service)
node_key_service = NodeKeyService(vertex_service, node_service, remote_node_service)
actor_service = ActorService(db, node_service, vertex_service)

HealthCheckAPI(app).register()
VertexAPI(app, vertex_service).register()
AdminAPI(app, admin_service).register()
NodeAdminAPI(app, node_service).register()
NodeAPI(app, node_service).register()
ActorAPI(app, actor_service).register()
ActorAdminAPI(app, actor_service).register()

Web(app).register()


@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)
    g.vertex_service = vertex_service
    g.node_key_service = node_key_service


@app.errorhandler(404)
def handle_404_error(e):
    return jsonify({
        "success": False,
        "message": str(e)
    }), 404


@app.errorhandler(Exception)
def handle_all_errors(e):
    print(traceback.format_exc())
    return jsonify({
        "success": False,
        "message": str(e)
    }), 500


if __name__ == '__main__':
    app.run(host=os.getenv("HOST"), port=int(os.getenv("PORT")), debug=os.getenv("ENV") != "PROD")
