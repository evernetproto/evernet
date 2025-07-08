import datetime

from peewee import *
from .database import db


class User(Model):
    identifier = CharField(unique=True)
    password = CharField()
    display_name = CharField()
    node_identifier = CharField()
    creator =  CharField(null=True)
    created_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))
    updated_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))

    class Meta:
        database = db
