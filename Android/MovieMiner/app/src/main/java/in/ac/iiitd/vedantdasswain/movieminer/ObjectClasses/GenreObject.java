package in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses;

/**
 * Created by vedantdasswain on 23/09/15.
 */
public class GenreObject {
    String name;
    int icon;

    public GenreObject(String name,int icon){
        this.name=name;
        this.icon=icon;
    }

    public String getName(){
        return name;
    }
    public int getIcon(){
        return icon;
    }
}
