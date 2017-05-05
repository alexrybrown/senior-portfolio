from rest_framework import serializers

from accounts.models import Student
from grades.models import Grade


class GradeSerializer(serializers.ModelSerializer):
    class Meta:
        model = Grade
        depth = 2
        fields = '__all__'

    def validate(self, attrs):
        request = self.context.get('request')
        user = request.user
        if Student.objects.filter(user=user):
            attrs['student'] = Student.objects.get(user=user)
        else:
            raise serializers.ValidationError("Must be a student to post a grade")
        return attrs


class GradeCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Grade
        fields = ('total_questions', 'correct_answers', 'assignment')

    def create(self, validated_data):
        student = Student.objects.get(user=self.context.get('request').user)
        grade = super(GradeCreateSerializer, self).create(validated_data)
        grade.student = student
        grade.save()
        return grade
