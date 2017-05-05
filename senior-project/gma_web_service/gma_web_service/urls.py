from django.conf.urls import url, include

from rest_framework.authtoken import views
from rest_framework.routers import DefaultRouter
from rest_framework.schemas import get_schema_view


schema_view = get_schema_view(title='Pastebin API')
router = DefaultRouter()

urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^', include('accounts.urls')),
    url(r'^', include('goals.urls')),
    url(r'^schema/$', schema_view),
    url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework')),
    url(r'^api-token-auth/', views.obtain_auth_token),
]
