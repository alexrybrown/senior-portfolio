from rest_framework import serializers
from rest_framework.authtoken.models import Token

from accounts.models import Teacher
from classes.models import Class


class ClassSerializer(serializers.ModelSerializer):
    class Meta:
        model = Class
        depth = 3
        fields = '__all__'

    def validate(self, attrs):
        request = self.context.get('request')
        attrs['teacher'] = Teacher.objects.get(user=request.user)
        return attrs
