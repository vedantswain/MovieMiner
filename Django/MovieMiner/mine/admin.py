from django.contrib import admin
from mine.models import UserProfile

class UserProfileAdmin(admin.ModelAdmin):
	list_display=('fb_id','username','location','birthday')

# Register your models here.
admin.site.register(UserProfile,UserProfileAdmin)

