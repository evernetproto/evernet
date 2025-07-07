import datetime

from peewee import *
from .database import db


class Admin(Model):
    identifier = CharField(unique=True)
    password = CharField()
    creator = CharField()
    created_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))
    updated_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))

    class Meta:
        database = db
