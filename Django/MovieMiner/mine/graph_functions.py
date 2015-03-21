import  omdb,json,traceback
from open_facebook import OpenFacebook
from mine.models import UserProfile,Movie,MovieLikes

def fetch_movies(access_token,fb_id):
	limit=500
	offset=0
	user=UserProfile.objects.get(fb_id=fb_id)
	facebook = OpenFacebook(access_token)
	movie_likes=facebook.get('me/movies',limit=limit,offset=offset)
	all_movies=movie_likes.get('data')
	
	while (not(not movie_likes.get('data'))):
		# print movie_likes.get('data')
		offset=offset+limit
		movie_likes=facebook.get('me/movies',limit=limit,offset=offset)
		# print len(all_movies)
		all_movies=all_movies+movie_likes.get('data')

	for i, movie in enumerate(all_movies):
		if(not (Movie.objects.filter(fb_id=movie.get('id')).exists())):
			# print movie.get('name')+" doesn't exist"
			res = omdb.request(t=movie.get('name'),fullplot=True,tomatoes=True,type="movie")
			content=res.content
			content=unicode(content,"utf-8")
			# print content			
			try:
				content_dict=json.loads(content)
				if(content_dict.get('Response')=="True"):
					# save entry in movie model
					movieEntry=Movie(fb_id=movie.get('id'),imdb_id=content_dict.get('imdbID'),title=content_dict.get('Title'),actors=content_dict.get('Actors'),director=content_dict.get('Director'),genre=content_dict.get('Genre'),image_uri=content_dict.get('Poster'))
					if(not (Movie.objects.filter(imdb_id=content_dict.get('imdbID')).exists())):
						movieEntry.save()
						movieEntry=Movie.objects.get(fb_id=movie.get('id'))

						print "Saved film: " +content_dict.get('Title')

						# save relationship
						if(not (MovieLikes.objects.filter(user=user,movie=movieEntry).exists())):
							movieLike=MovieLikes(user=user,movie=movieEntry)
							movieLike.save()
					else:
						movieExists=Movie.objects.get(imdb_id=content_dict.get('imdbID'))
						# print movieExists.title+" saved id: "+str(movieExists.fb_id)+" actual id: "+movie.get('id')

			except Exception as e:
				traceback.print_exc()
		else:
			# print movie.get('name')+" exists"
			movieEntry=Movie.objects.get(fb_id=movie.get('id'))
			# save relationship
			if(not (MovieLikes.objects.filter(user=user,movie=movieEntry).exists())):
				movieLike=MovieLikes(user=user,movie=movieEntry)
				movieLike.save()