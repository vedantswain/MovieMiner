import pdb

from django.http import Http404
from django.views.decorators.csrf import csrf_exempt

from rest_framework import status,viewsets
from rest_framework.views import APIView

from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.authtoken.models import Token
from rest_framework.authtoken.serializers import AuthTokenSerializer
from rest_framework.parsers import JSONParser, MultiPartParser, FormParser
from rest_framework.renderers import JSONRenderer

from mine.serializers import UserProfileSerializer
from mine.models import UserProfile
from mine.graph_functions import fetch_movies

from social.apps.django_app.utils import psa


class ObtainAuthToken(APIView):
    throttle_classes = ()
    permission_classes = ()
    authentication_classes = ()
    parser_classes = (FormParser, MultiPartParser, JSONParser,)
    renderer_classes = (JSONRenderer,)
    serializer_class = AuthTokenSerializer
    model = Token

    # Accept backend as a parameter and 'auth' for a login / pass
    def post(self, request, backend):
        if backend == 'auth':
            serializer = self.serializer_class(data=request.DATA)
            if serializer.is_valid():
                token, created = Token.objects.get_or_create(user=serializer.object['user'])
                return Response({'token': token.key})
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        else:
            # Here we call PSA to authenticate like we would if we used PSA on server side.
            print 'calling PSA'
            user = register_by_access_token(request, backend)
            # If user is active we get or create the REST token and send it back with user data
            if user and user.is_active:
                token, created = Token.objects.get_or_create(user=user)
                return Response({'id': user.id, 'name': user.username, 'userRole': 'user', 'token': token.key})


@psa(redirect_uri=None)
def register_by_access_token(request, backend):
    # Split by spaces and get the array
    auth = get_authorization_header(request).split()

    if not auth or auth[0].lower() != b'token':
        msg = 'No token header provided.'
        return msg
    
    if len(auth) == 1:
        msg = 'Invalid token header. No credentials provided.'
        return msg
    
    access_token = auth[1]
    
    # Real authentication takes place here
    user = request.backend.do_auth(access_token)
    return user

def get_authorization_header(request):
    return request.META['HTTP_AUTHORIZATION']


class UserProfileViewSet(APIView):
    queryset = UserProfile.objects.all()
    serializer_class = UserProfileSerializer

    def get(self, request, format=None):
        serializer = UserProfileSerializer(self.queryset, many=True)
        return Response(serializer.data)

    def post(self, request, format=None):
    	serializer = UserProfileSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            userProfile = UserProfile.objects.get(fb_id=request.data.get('fb_id',''))
            save_auth_user(userProfile,request.user)    
            return Response(serializer.data, status=status.HTTP_201_CREATED)    
        else:
            if(UserProfile.objects.filter(fb_id=request.data.get('fb_id','')).exists()):
                userProfile = UserProfile.objects.get(fb_id=request.data.get('fb_id',''))
                save_auth_user(userProfile,request.user)
                return Response(serializer.data, status=status.HTTP_200_OK)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

def save_auth_user(userProfile,authUser):
    if userProfile.auth_user is None:
        userProfile.auth_user=authUser
        userProfile.save()
        print userProfile.auth_user



class MovieViewSet(APIView):
    def get(self, request, format=None):
        user=request.user
        return Response(request.data, status=status.HTTP_200_OK)


    def post(self, request, format=None):
        access_token=request.data.get('access_token','')
        fb_id=request.data.get('fb_id','')
        fetch_movies(access_token,fb_id);
        return Response(request.data, status=status.HTTP_201_CREATED)