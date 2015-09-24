from django.db import models
from django.conf import settings
from django.contrib.auth.models import AbstractBaseUser,BaseUserManager, User
from django.db.models.signals import post_save
from django.dispatch import receiver
from rest_framework.authtoken.models import Token

# Create your models here.

class Movie(models.Model):
	fb_id = models.CharField(max_length=200, unique=True)
	imdb_id = models.CharField(max_length=200, unique=True)
	title = models.CharField(max_length=200)
	image_uri = models.CharField(max_length=200)
	actors = models.CharField(max_length=200)
	director = models.CharField(max_length=200)
	genre = models.CharField(max_length=200)

	class Meta:
		db_table = u'movie'

class UserProfile(models.Model):
    username = models.CharField(max_length=200)
    fb_id = models.CharField(max_length=200, unique=True)
    location = models.CharField(max_length=200)
    birthday = models.DateField(blank=False)
    auth_user=models.ForeignKey(User,null=True)

    class Meta:
        db_table = u'user_profile'

class MovieLikes(models.Model):
	user=models.ForeignKey(UserProfile)
	movie=models.ForeignKey(Movie)

	class Meta:
		db_table = u'movie_likes'

class MovieDislikes(models.Model):
	user=models.ForeignKey(UserProfile)
	movie=models.ForeignKey(Movie)

	class Meta:
		db_table = u'movie_dislikes'