from django.conf.urls import patterns, include, url
from django.contrib import admin
#from rest_framework import routers
from rest_framework.authtoken import views
from mine import views

#router = routers.DefaultRouter()
#router.register(r'users', views.UsersViewSet.as_view())

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'MovieMiner.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url('', include('social.apps.django_app.urls', namespace='social')),
    url(r'^$', views.index, name='index'),
    url(r'^privacy_policy/', views.privacy_policy, name='privacy_policy'),
    url(r'^saveIMDB250/',views.saveIMDBTop, name='saveIMDBTop'),
    url(r'^browse/(?P<genre>[^/]+)/$', views.BrowseViewSet.as_view()),
    url(r'^search/$', views.SearchViewSet.as_view()),
    # url(r'^test/', views.testing, name='testing'),
    url(r'^admin/', include(admin.site.urls)),
    #url(r'^', include(router.urls)),
    url(r'^api-auth/(?P<backend>[^/]+)/$', views.ObtainAuthToken.as_view()),
    url(r'^api-token-auth/', 'rest_framework.authtoken.views.obtain_auth_token'),
    url(r'^user-profiles/$', views.UserProfileViewSet.as_view()),
    url(r'^movies/(?P<kind>[^/]+)/$', views.MovieViewSet.as_view()),
    url(r'^movie-likes/(?P<action>[^/]+)/$', views.MovieLikeViewSet.as_view()),
)

