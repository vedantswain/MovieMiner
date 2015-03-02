import  omdb
from open_facebook import OpenFacebook

def fetch_movies(access_token):
	limit=500
	offset=0
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
		res = omdb.request(t=movie.get('name'))
		content=res.content
	# 	if(content.get('Response')=="True")
	# 		print i, content 