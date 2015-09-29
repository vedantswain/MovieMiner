from django.contrib import admin
from mine.models import UserProfile,Movie,MovieLikes,MovieDislikes

class UserProfileAdmin(admin.ModelAdmin):
	list_display=('fb_id','username','location','birthday','auth_user')

class MovieAdmin(admin.ModelAdmin):
	list_display=('fb_id','imdb_id','title','genre','director','actors','image_uri')

class MovieLikesAdmin(admin.ModelAdmin):
	list_display=('get_user','get_movie')

	def get_user(self, obj):
		return '%s'%(obj.user.username)
	get_user.short_description = 'User'
	get_user.admin_order_field = 'user'

	def get_movie(self, obj):
		return '%s'%(obj.movie.title)
	get_movie.short_description = 'Movie'
	get_movie.admin_order_field = 'movie'

class MovieDislikesAdmin(admin.ModelAdmin):
	list_display=('get_user','get_movie')

	def get_user(self, obj):
		return '%s'%(obj.user.username)
	get_user.short_description = 'User'
	get_user.admin_order_field = 'user'

	def get_movie(self, obj):
		return '%s'%(obj.movie.title)
	get_movie.short_description = 'Movie'
	get_movie.admin_order_field = 'movie'

# Register your models here.
admin.site.register(UserProfile,UserProfileAdmin)
admin.site.register(Movie,MovieAdmin)
admin.site.register(MovieLikes,MovieLikesAdmin)
admin.site.register(MovieDislikes,MovieDislikesAdmin)


