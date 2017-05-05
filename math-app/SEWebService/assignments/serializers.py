from rest_framework import serializers

from accounts.models import Teacher
from assignments.models import Assignment, Question


class AssignmentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Assignment
        fields = '__all__'

    def validate(self, attrs):
        request = self.context.get('request')
        user = request.user
        if Teacher.objects.filter(user=user):
            attrs['teacher'] = Teacher.objects.get(user=user)
        else:
            raise serializers.ValidationError("Must be a teacher to create an assignment")
        return attrs


class QuestionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Question
        fields = '__all__'

    def validate(self, attrs):
        request = self.context.get('request')
        user = request.user
        if not Teacher.objects.filter(user=user):
            raise serializers.ValidationError("Must be a teacher to create an assignment")
        return attrs
