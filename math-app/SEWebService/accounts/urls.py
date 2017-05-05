from rest_framework.routers import SimpleRouter

from accounts import views


# Create a router and register our viewsets with it
router = SimpleRouter()
router.register(r'accounts', views.AccountViewSet)
router.register(r'students', views.StudentViewSet)
router.register(r'teachers', views.TeacherViewSet)
urlpatterns = router.urls
