from django.contrib.auth.models import User

from rest_framework import status
from rest_framework import viewsets
from rest_framework.authentication import TokenAuthentication
from rest_framework.decorators import list_route
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from accounts import models as account_models
from accounts import serializers as account_serializers
from assignments import serializers as assignment_serializers
from classes import serializers as class_serializers


class AccountViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = account_serializers.AccountSerializer

    @list_route(['post'], url_path="teachers")
    def teachers(self, request):
        serializer = account_serializers.AccountSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.save()
            account_models.Teacher.objects.create(user=user)
            return Response(status=status.HTTP_201_CREATED)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    @list_route(['post'], url_path="students")
    def students(self, request):
        serializer = account_serializers.AccountSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.save()
            account_models.Student.objects.create(user=user)
            return Response(status=status.HTTP_201_CREATED)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class StudentViewSet(viewsets.ModelViewSet):
    queryset = account_models.Student.objects.all()
    serializer_class = account_serializers.StudentSerializer

    def get_serializer_context(self):
        return {'request': self.request}

    @list_route(methods=['get'], authentication_classes=[TokenAuthentication],
                permission_classes=[IsAuthenticated], url_path="user-info")
    def user_info(self, request):
        if request.user.student_set.all():
            serializer = account_serializers.AccountSerializer(request.user)
            return Response(serializer.data, status=status.HTTP_200_OK)
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)

    @list_route(['get'], authentication_classes=[TokenAuthentication],
                permission_classes=[IsAuthenticated], url_path="classes")
    def classes(self, request):
        if account_models.Student.objects.filter(user=request.user):
            student = account_models.Student.objects.get(user=request.user)
            classes = student.class_set.all()
            serializer = class_serializers.ClassSerializer(classes, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class TeacherViewSet(viewsets.ModelViewSet):
    queryset = account_models.Teacher.objects.all()
    serializer_class = account_serializers.TeacherSerializer

    def get_queryset(self):
        return self.request.user.teacher_set.all()

    @list_route(methods=['get'], authentication_classes=[TokenAuthentication],
                permission_classes=[IsAuthenticated], url_path="user-info")
    def user_info(self, request):
        if request.user.teacher_set.all():
            serializer = account_serializers.AccountSerializer(request.user)
            return Response(serializer.data, status=status.HTTP_200_OK)
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)

    @list_route(['get'], authentication_classes=[TokenAuthentication],
                permission_classes=[IsAuthenticated], url_path="classes")
    def classes(self, request):
        if account_models.Teacher.objects.filter(user=request.user):
            teacher = account_models.Teacher.objects.get(user=request.user)
            classes = teacher.class_set.all()
            serializer = class_serializers.ClassSerializer(classes, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)

    @list_route(['get'], authentication_classes=[TokenAuthentication],
                permission_classes=[IsAuthenticated], url_path="assignments")
    def assignments(self, request):
        if account_models.Teacher.objects.filter(user=request.user):
            teacher = account_models.Teacher.objects.get(user=request.user)
            assignment = teacher.assignment_set.all()
            serializer = assignment_serializers.AssignmentSerializer(assignment, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)
