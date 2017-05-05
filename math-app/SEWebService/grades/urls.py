from rest_framework.routers import SimpleRouter

from grades import views


# Create a router and register our viewsets with it
router = SimpleRouter()
router.register(r'grades', views.GradeViewSet)
urlpatterns = router.urls
