import os
import traceback

from flask import Flask, request, jsonify, g

from controller.admin_controller import AdminController
from controller.health_check_controller import HealthCheckController
from model.database import db
from model.admin import Admin
from model.config import Config

from service.config_service import ConfigService

db.create_tables([Admin, Config])

app = Flask(__name__)

ConfigService.init()

HealthCheckController(app).register()
AdminController(app).register()


@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)


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
