import os
from dotenv import *
from peewee import *

load_dotenv()

data_path = os.getenv("DATA_PATH")

if not os.path.exists(data_path):
    os.makedirs(data_path)

db = SqliteDatabase(f'{data_path}/evernet.db')
