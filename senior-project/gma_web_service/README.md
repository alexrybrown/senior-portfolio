# PostgreSQL Setup
```
user@user:<path to project>$ sudo su - postgres
postgres@user:~$ psql

postgres=# CREATE DATABASE gma_web_service;
postgres=# CREATE USER <insert user from settings file> WITH PASSWORD <insert password from settings file>;
postgres=# ALTER ROLE <insert user from settings file> SET client_encoding TO 'utf8';
postgres=# ALTER ROLE <insert user from settings file> SET default_transaction_isolation TO 'read commited';
postgres=# ALTER ROLE <insert user from settings file> SET timezone TO 'UTC';
postgres=# GRANT ALL PRIVILEGES ON DATABASE gma_web_service TO <insert user from settings file>;
postgres=# \q

postgres@user:~$ exit
```

# Creating Token for Super User
```python
from rest_framework.authtoken.models import Token
Token.objects.create(user=...)
```
