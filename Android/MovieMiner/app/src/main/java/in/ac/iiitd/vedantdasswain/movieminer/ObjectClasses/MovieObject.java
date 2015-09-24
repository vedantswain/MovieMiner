package in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses;

/**
 * Created by vedantdasswain on 26/03/15.
 */
public class MovieObject {
    String fb_id,imdb_id,title,director,actors,genre,imageUri,rel;

    public MovieObject(String fb_id, String imdb_id, String title,
                    String director, String actors,String genre, String imageUri,String rel){
        this.fb_id=fb_id;
        this.imdb_id=imdb_id;
        this.title=title;
        this.director=director;
        this.actors=actors;
        this.genre=genre;
        this.imageUri=imageUri;
        this.rel=rel;
    }

    public String getRel(){return this.rel;}

    public void putRel(String rel){this.rel=rel;}

    public String getFb_id(){
        return this.fb_id;
    }


    public String getImdb_id(){
        return this.imdb_id;
    }


    public String getTitle(){
        return this.title;
    }


    public String getDirector(){
        return this.director;
    }


    public String getActors(){
        return this.actors;
    }


    public String getGenre(){
        return this.genre;
    }

    public String getImageUri(){
        return this.imageUri;
    }

}
