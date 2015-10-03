package in.ac.iiitd.vedantdasswain.movieminer;

/**
 * Created by vedantdasswain on 21/03/15.
 */
public class Common {
//    public static final String SERVER_URL="http://192.168.1.3:8000/";
//    public static final String SERVER_URL="http://192.168.48.103:8000/";
    public static final String SERVER_URL="http://facebook-movies-908.appspot.com/";
    public static final String USER_API= SERVER_URL +"user-profiles/";
    public static final String AUTH_API= SERVER_URL +"api-auth/facebook/";
    public static final String MOVIES_API= SERVER_URL +"movies/";
    public static final String MOVIE_REL_API= SERVER_URL +"movie-likes/";
    public static final String BROWSE_API= SERVER_URL +"browse/";
    public static final String SEARCH_API= SERVER_URL +"search/";
    public static final String[] genres = new String[] {"Action","Adventure","Animation","Biography","Comedy","Crime","Documentary","Drama",
            "Family","Fantasy","Film-Noir","History","Horror","Music","Musical","Mystery","Romance","Sci-Fi","Sport","Thriller",
            "War","Western"};
    public static final int[] icons = new int[]{R.mipmap.ic_genre_action,R.mipmap.ic_genre_adventure,R.mipmap.ic_genre_animated,
            R.mipmap.ic_genre_bipoic,R.mipmap.ic_genre_comedy,R.mipmap.ic_genre_crime,R.mipmap.ic_genre_documentary,
            R.mipmap.ic_genre_drama,R.mipmap.ic_genre_family,R.mipmap.ic_genre_fantasy,R.mipmap.ic_genre_noir,
            R.mipmap.ic_genre_history,R.mipmap.ic_genre_horror,R.mipmap.ic_genre_music,R.mipmap.ic_genre_musical,
            R.mipmap.ic_genre_mystery,R.mipmap.ic_genre_romance,R.mipmap.ic_genre_scifi,R.mipmap.ic_genre_sports,
            R.mipmap.ic_genre_thriller,R.mipmap.ic_genre_war,R.mipmap.ic_genre_western};
    public static final int upvote=R.mipmap.ic_upvote;
    public static final int upvoted=R.mipmap.ic_upvoted;
    public static final int downvote=R.mipmap.ic_downvote;
    public static final int downvoted=R.mipmap.ic_downvoted;
}
