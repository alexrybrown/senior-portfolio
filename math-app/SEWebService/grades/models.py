from django.contrib.auth.models import User
from django.db import models


class Grade(models.Model):
    total_questions = models.IntegerField()
    correct_answers = models.IntegerField()
    student = models.ForeignKey('accounts.Student', null=True, blank=True)
    assignment = models.ForeignKey('assignments.Assignment')

    def __str__(self):
        return "{} {} {}".format(self.student.user.get_full_name(), self.assignment,
                                 (self.correct_answers / self.total_questions))
