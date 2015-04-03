import  omdb,json,traceback
from open_facebook import OpenFacebook
from mine.models import UserProfile,Movie,MovieLikes
from mine.serializers import MovieSerializer
from django.http import HttpResponse,HttpResponseNotFound
from django.core.paginator import Paginator
from django.core import serializers
from rest_framework.renderers import JSONRenderer

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

						# print "Saved film: " +content_dict.get('Title')

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

def get_movies(user_profile,page_number):
	movie_list=[]
	json_list=[]
	dict_json={}
	page_size=20
	next_page_number=-1

	q=MovieLikes.objects.filter(user=user_profile)
	for movie_like in q:
		if(Movie.objects.filter(id=movie_like.movie_id).exists()):
			movie=Movie.objects.get(id=movie_like.movie_id)
			movie_list.append(movie)
			# print movie.title
		else:
			MovieLikes.objects.filter(movie_id=movie_like.movie_id).delete()

	pgntr=Paginator(movie_list,page_size)

	page_number=int(page_number)
	if(page_number<1):
		return HttpResponseNotFound('Page not found')
	elif(page_number>pgntr.num_pages):
		return HttpResponseNotFound('Page not found')
	else:
		page=pgntr.page(page_number)
		# print page.object_list

		if(page.has_next()):
			next_page_number=page.next_page_number()

		page_movie_list=page.object_list
		page_movie_list_json = serializers.serialize('json', page_movie_list)

		page_movie_list_json = json.dumps({"movies":[{'title': o.title,'fb_id':o.fb_id,'imdb_id':o.imdb_id,'title':o.title,
			'genre':o.genre,'director':o.director,'actors':o.actors,'image_uri':o.image_uri} 
			for o in page_movie_list],
			"next_page_number":next_page_number})

		return HttpResponse(page_movie_list_json,content_type='application/json')