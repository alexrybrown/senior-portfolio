from rest_framework.routers import SimpleRouter

from goals import views


# Create a router and register our viewsets with it
router = SimpleRouter()
router.register(r'goals', views.GoalViewSet, base_name='goals')
urlpatterns = router.urls
