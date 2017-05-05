from django.contrib.auth.models import User

from rest_framework import status
from rest_framework import viewsets
from rest_framework.authentication import TokenAuthentication, SessionAuthentication
from rest_framework.decorators import list_route
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from accounts import serializers


class AccountViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = serializers.AccountSerializer

    @list_route(methods=['get'], authentication_classes=[TokenAuthentication, SessionAuthentication],
                permission_classes=[IsAuthenticated], url_path="user-info")
    def user_info(self, request):
        serializer = self.serializer_class(request.user)
        return Response(serializer.data, status=status.HTTP_200_OK)
