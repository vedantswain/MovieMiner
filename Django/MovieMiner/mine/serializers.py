from mine.models import UserProfile,Movie
from rest_framework import serializers

class UserProfileSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
    	model = UserProfile
        fields = ('fb_id','username','location','birthday')

class MovieSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
    	model = Movie
        fields = ('fb_id','imdb_id','title','genre','director','actors','image_uri')

