from flask import Flask, render_template


class Web:
    def __init__(self, app: Flask):
        self.app = app

    def register(self):

        @self.app.get("/")
        def index():
            return render_template("index.html")

        @self.app.get("/admin")
        def page_admin():
            return render_template("admin.html")

        @self.app.get("/logout")
        def page_logout():
            return render_template("logout.html")

        @self.app.get("/admin/init")
        def page_admin_init():
            return render_template("admin-init.html")