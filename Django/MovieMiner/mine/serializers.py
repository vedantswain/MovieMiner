from mine.models import UserProfile
from rest_framework import serializers

class UserProfileSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
    	model = UserProfile
        fields = ('fb_id','username','location','birthday')

