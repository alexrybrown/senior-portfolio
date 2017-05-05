from rest_framework import status
from rest_framework import viewsets
from rest_framework.response import Response

from accounts.models import Student
from assignments.models import Assignment
from classes.models import Class
from classes.serializers import ClassSerializer


class ClassViewSet(viewsets.ModelViewSet):
    queryset = Class.objects.all()
    serializer_class = ClassSerializer

    def get_serializer_context(self):
        return {'request': self.request}

    def update(self, request, *args, **kwargs):
        serializer = ClassSerializer(data=request.data, context={'request': request})
        if serializer.is_valid():
            Class.objects.filter(pk=self.get_object().pk).update(**serializer.validated_data)
            class_obj = self.get_object()
            students = []
            assignments = []
            if len(request.data.get('students', [])) > 0:
                students = request.POST.getlist('students')
            if len(request.data.get('assignments', [])) > 0:
                assignments = request.POST.getlist('assignments')
            if len(students) > 0:
                class_obj.students.clear()
            if len(assignments) > 0:
                class_obj.assignments.clear()
            for student in Student.objects.filter(pk__in=students):
                class_obj.students.add(student)
            for assignment in Assignment.objects.filter(pk__in=assignments):
                class_obj.assignments.add(assignment)
                class_obj.save()
            class_obj.save()
            return Response(status=status.HTTP_200_OK)
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)
