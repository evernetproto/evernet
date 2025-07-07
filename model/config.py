import datetime

from peewee import *

from model.database import db


class Config(Model):
    key = CharField(unique=True)
    value = CharField()
    created_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))
    updated_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))

    class Meta:
        database = db
