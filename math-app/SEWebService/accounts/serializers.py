from django.contrib.auth.models import User

from rest_framework import serializers
from rest_framework.authtoken.models import Token

from accounts import models as account_models


class AccountSerializer(serializers.ModelSerializer):
    password = serializers.CharField(allow_blank=False, style={'input_type': 'password'})
    confirm_password = serializers.CharField(allow_blank=False,
                                             style={'input_type': 'password'},
                                             write_only=True)

    class Meta:
        model = User
        fields = '__all__'
        write_only_fields = ('password', 'confirm_password',)
        read_only_fields = ('is_staff', 'is_superuser', 'is_active', 'date_joined', 'user_permissions', 'last_login', 'groups')

    def validate(self, attrs):
        if attrs['password'] != attrs.pop('confirm_password'):
            raise serializers.ValidationError("Passwords do not match")
        return attrs

    def create(self, validated_data):
        # call set_password on user object. Without this
        # the password will be stored in plain text.
        user = User.objects.create(
            username=validated_data['username'],
            first_name=validated_data['first_name'],
            last_name=validated_data['last_name'],
        )
        if validated_data.get('email'):
            user.email = validated_data['email']
        user.set_password(validated_data['password'])
        user.save()
        # After saving the user, create a token for that user.
        Token.objects.create(user=user)
        return user


class StudentSerializer(serializers.ModelSerializer):
    class Meta:
        model = account_models.Student
        fields = '__all__'
        depth = 1

    def validate(self, attrs):
        request = self.context.get('request')
        serializer = AccountSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.save()
            attrs['user'] = user
        else:
            raise serializers.ValidationError("User wasn't created")
        return attrs

    def create(self, validated_data):
        student = super(StudentSerializer, self).create(validated_data)
        request = self.context.get('request')
        teacher = account_models.Teacher.objects.get(user=request.user)
        teacher.students.add(student)
        return student


class TeacherSerializer(serializers.ModelSerializer):
    class Meta:
        model = account_models.Teacher
        fields = '__all__'
        depth = 2
