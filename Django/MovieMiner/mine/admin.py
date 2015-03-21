from django.contrib import admin
from mine.models import UserProfile,Movie,MovieLikes

class UserProfileAdmin(admin.ModelAdmin):
	list_display=('fb_id','username','location','birthday')

class MovieAdmin(admin.ModelAdmin):
	list_display=('fb_id','imdb_id','title','genre','director','actors','image_uri')

class MovieLikesAdmin(admin.ModelAdmin):
	list_display=('user','movie')	

# Register your models here.
admin.site.register(UserProfile,UserProfileAdmin)
admin.site.register(Movie,MovieAdmin)
admin.site.register(MovieLikes,MovieLikesAdmin)


