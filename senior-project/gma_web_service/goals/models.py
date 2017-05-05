from django.contrib.auth.models import User
from django.db import models

from utils.models import BaseModel


class Goal(BaseModel):
    user = models.ForeignKey(User)
    future_goal = models.ForeignKey('goals.Goal', null=True)
    title = models.TextField()
    description = models.TextField(null=True, blank=True)
    comment = models.TextField(null=True, blank=True)
    expected_completion = models.DateTimeField()
    finished_at = models.DateTimeField(null=True)

    def __str__(self):
        return self.title
