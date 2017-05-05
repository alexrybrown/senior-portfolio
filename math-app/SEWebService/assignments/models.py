from django.db import models

from assignments.constants import GAME_CHOICES


class Assignment(models.Model):
    teacher = models.ForeignKey('accounts.Teacher', null=True, blank=True)
    questions = models.ManyToManyField('assignments.Question', blank=True)
    math_type = models.CharField(max_length=15, choices=GAME_CHOICES)
    name = models.CharField(max_length=100)

    def __str__(self):
        return self.name


class Question(models.Model):
    question = models.CharField(max_length=100)
    answer = models.CharField(max_length=100)

    def __str__(self):
        return "{}={}".format(self.question, self.answer)
