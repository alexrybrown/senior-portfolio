from django.db import models


class Class(models.Model):
    teacher = models.ForeignKey('accounts.Teacher')
    students = models.ManyToManyField('accounts.Student')
    assignments = models.ManyToManyField('assignments.Assignment')
    name = models.CharField(max_length=100)

    def __str__(self):
        return self.name
