from django.core.management import execute_from_command_line

from rest_framework.authtoken.models import Token

from accounts import models as account_models


def fixtures_setup():
    execute_from_command_line(["manage.py", "makemigrations"])
    execute_from_command_line(["manage.py", "migrate"])
    execute_from_command_line(["manage.py", "loaddata", "users.json"])
    execute_from_command_line(["manage.py", "loaddata", "students.json"])
    execute_from_command_line(["manage.py", "loaddata", "teachers.json"])
    execute_from_command_line(["manage.py", "loaddata", "questions.json"])
    execute_from_command_line(["manage.py", "loaddata", "assignments.json"])
    execute_from_command_line(["manage.py", "loaddata", "classes.json"])

    for teacher in account_models.Teacher.objects.all():
        Token.objects.create(user=teacher.user)

    for student in account_models.Student.objects.all():
        Token.objects.create(user=student.user)
