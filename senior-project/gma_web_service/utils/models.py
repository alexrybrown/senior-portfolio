from django.contrib.auth.models import User
from django.db import models


class BaseModel(models.Model):
    updated_by = models.ForeignKey('auth.User', related_name='base_models')
    created_at = models.DateTimeField(auto_now_add=True)
    last_modified = models.DateTimeField(auto_now=True)
    archived = models.BooleanField(default=False)
