from rest_framework import status
from rest_framework import viewsets
from rest_framework.authentication import TokenAuthentication
from rest_framework.decorators import detail_route
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from accounts.models import Teacher
from assignments.models import Assignment, Question
from assignments.serializers import AssignmentSerializer, QuestionSerializer
from grades.models import Grade
from grades.serializers import GradeSerializer


class AssignmentViewSet(viewsets.ModelViewSet):
    queryset = Assignment.objects.all()
    serializer_class = AssignmentSerializer
    authentication_classes = [TokenAuthentication]

    def get_serializer_context(self):
        return {'request': self.request}

    @detail_route(methods=['get'], authentication_classes=[TokenAuthentication],
                  permission_classes=[IsAuthenticated], url_path="student-grades")
    def student_grades(self, request, pk=None):
        if Teacher.objects.filter(user=request.user):
            assignment = self.get_object()
            grades = Grade.objects.filter(assignment=assignment)
            serializer = GradeSerializer(grades, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class QuestionViewSet(viewsets.ModelViewSet):
    queryset = Question.objects.all()
    serializer_class = QuestionSerializer
    authentication_classes = [TokenAuthentication]

    def get_serializer_context(self):
        return {'request': self.request}
