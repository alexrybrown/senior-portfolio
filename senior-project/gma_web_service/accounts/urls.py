from rest_framework.routers import SimpleRouter

from accounts import views


# Create a router and register our viewsets with it
router = SimpleRouter()
router.register(r'accounts', views.AccountViewSet)
urlpatterns = router.urls
