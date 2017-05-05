from rest_framework.routers import SimpleRouter

from assignments import views


# Create a router and register our viewsets with it
router = SimpleRouter()
router.register(r'assignments', views.AssignmentViewSet)
router.register(r'questions', views.QuestionViewSet)
urlpatterns = router.urls
