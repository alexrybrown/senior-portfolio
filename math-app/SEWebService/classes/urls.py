from rest_framework.routers import SimpleRouter

from classes import views


# Create a router and register our viewsets with it
router = SimpleRouter()
router.register(r'classes', views.ClassViewSet)
urlpatterns = router.urls
