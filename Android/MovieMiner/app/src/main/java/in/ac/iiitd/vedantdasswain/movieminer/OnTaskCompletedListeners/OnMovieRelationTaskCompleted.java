package in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners;

import in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses.MovieObject;

/**
 * Created by vedantdasswain on 24/09/15.
 */
public interface OnMovieRelationTaskCompleted {
    void OnTaskCompleted(String msg,MovieObject mo);
}
