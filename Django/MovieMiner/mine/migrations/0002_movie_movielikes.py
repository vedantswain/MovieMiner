# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('mine', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Movie',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('fb_id', models.CharField(unique=True, max_length=200)),
                ('imdb_id', models.CharField(unique=True, max_length=200)),
                ('title', models.CharField(max_length=200)),
                ('image_uri', models.CharField(max_length=200)),
                ('actors', models.CharField(max_length=200)),
                ('director', models.CharField(max_length=200)),
                ('genre', models.CharField(max_length=200)),
            ],
            options={
                'db_table': 'movie',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='MovieLikes',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('movie', models.ForeignKey(to='mine.Movie')),
                ('user', models.ForeignKey(to='mine.UserProfile')),
            ],
            options={
                'db_table': 'movie_likes',
            },
            bases=(models.Model,),
        ),
    ]
