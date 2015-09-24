# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('mine', '0003_userprofile_auth_user'),
    ]

    operations = [
        migrations.CreateModel(
            name='MovieDislikes',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('movie', models.ForeignKey(to='mine.Movie')),
                ('user', models.ForeignKey(to='mine.UserProfile')),
            ],
            options={
                'db_table': 'movie_dislikes',
            },
            bases=(models.Model,),
        ),
    ]
