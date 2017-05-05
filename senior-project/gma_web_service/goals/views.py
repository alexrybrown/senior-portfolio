from django.utils import timezone
from rest_framework import status
from rest_framework import viewsets
from rest_framework.authentication import TokenAuthentication, SessionAuthentication
from rest_framework.decorators import detail_route, list_route
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from goals.serializers import GoalSerializer

from goals.models import Goal


class GoalViewSet(viewsets.ModelViewSet):
    authentication_classes = (TokenAuthentication, SessionAuthentication,)
    permission_classes = (IsAuthenticated,)
    serializer_class = GoalSerializer

    def get_queryset(self):
        return Goal.objects.filter(user=self.request.user)

    def get_serializer_context(self):
        return {'request': self.request}

    @list_route(['get'], url_path='future-goals')
    def future_goals(self, request):
        date = timezone.now()
        goals = request.user.goal_set.filter(
            future_goal=None, archived=False, finished_at__isnull=True,
            expected_completion__gte=date).order_by('expected_completion')
        serializer = GoalSerializer(goals, many=True)
        return Response(serializer.data)

    @list_route(['get'], url_path='overdue-goals')
    def overdue_goals(self, request):
        date = timezone.now()
        goals = request.user.goal_set.filter(
            goal__isnull=True, archived=False, finished_at__isnull=True,
            expected_completion__lte=date).order_by('expected_completion')
        serializer = GoalSerializer(goals, many=True)
        return Response(serializer.data)

    @list_route(['get'], url_path='upcoming-goals')
    def upcoming_goals(self, request):
        date = timezone.now()
        next_week = date + timezone.timedelta(days=7)
        goals = request.user.goal_set.filter(
            goal__isnull=True, archived=False, finished_at__isnull=True,
            expected_completion__gte=date, expected_completion__lte=next_week).order_by('expected_completion')
        serializer = GoalSerializer(goals, many=True)
        return Response(serializer.data)

    @detail_route(['get', 'post'], url_path='sub-goals')
    def sub_goals(self, request, pk=None):
        goal = self.get_object()
        if request.method == 'POST':
            serializer = GoalSerializer(data=request.data, context={'request': request})
            if serializer.is_valid():
                serializer.save(future_goal=goal)
                return Response(status=status.HTTP_201_CREATED)
            else:
                return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        else:
            sub_goals = goal.goal_set.filter(
                archived=False, finished_at__isnull=True).order_by('expected_completion')
            serializer = GoalSerializer(sub_goals, many=True)
            return Response(serializer.data)

    @detail_route(['post'], url_path='archive')
    def archive(self, request, pk=None):
        goal = self.get_object()
        if goal:
            self._archive_helper(goal, request)
            return Response(status=status.HTTP_200_OK)
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)

    def _archive_helper(self, goal, request):
        goal.archived = True
        goal.updated_by = request.user
        goal.last_modified = timezone.now()
        goal.save()
        if goal.goal_set.all():
            for sub_goal in goal.goal_set.all():
                self._archive_helper(sub_goal, request)

    @detail_route(['post'], url_path='complete')
    def complete(self, request, pk=None):
        goal = self.get_object()
        if goal:
            goal.finished_at = timezone.now()
            goal.updated_by = request.user
            goal.last_modified = timezone.now()
            goal.save()
            return Response(status=status.HTTP_200_OK)
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)
