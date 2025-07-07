import secrets
import string

def generate(n):
    charset = string.ascii_letters + string.digits
    return ''.join(secrets.choice(charset) for _ in range(n))
