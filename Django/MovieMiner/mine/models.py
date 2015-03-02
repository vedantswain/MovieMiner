from django.db import models
from django.conf import settings
from django.contrib.auth.models import AbstractBaseUser,BaseUserManager, User
from django.db.models.signals import post_save
from django.dispatch import receiver
from rest_framework.authtoken.models import Token

# Create your models here.
class UserProfile(models.Model):
    username = models.CharField(max_length=200)
    fb_id = models.CharField(max_length=200, unique=True)
    location = models.CharField(max_length=200)
    birthday = models.DateField(blank=False)

    class Meta:
        db_table = u'user_profile'