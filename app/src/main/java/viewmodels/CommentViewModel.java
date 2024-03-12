package viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import entities.Comment;
import repositories.CommentRepo;

/**
 * ViewModel class responsible for managing comments data and interactions.
 */
public class CommentViewModel extends ViewModel {
    private CommentRepo commentRepo;
    private LiveData<List<Comment>> comments;

    /**
     * This is a constructor
     */
    public CommentViewModel() {

    }

    /**
     * Initializes the CommentViewModel with the specified post ID and JWT token.
     *
     * @param postId The ID of the post.
     * @param jwt    The JWT token.
     */
    public void initializeCommentViewModel (String postId,String jwt) {
        commentRepo = new CommentRepo(postId,jwt);
        comments = commentRepo.get();
    }

    /**
     * Retrieves all comments.
     */
    public void getAll() {
        commentRepo.getAll();
    }

    /**
     * Retrieves the LiveData object containing the list of comments.
     *
     * @return A LiveData object containing a list of comments.
     */
    public LiveData<List<Comment>> get(){
        return comments;
    }

    /**
     * Adds a new comment.
     *
     * @param c The comment to add.
     */
    public void addComment(Comment c) {
        commentRepo.addComment(c);
    }

    /**
     * Deletes a comment.
     *
     * @param c The comment to delete.
     */
    public void deleteComment(Comment c) {
        commentRepo.deleteComment(c.getUserId(), c.getPostId(), c.get_id());
    }

    /**
     * Updates a comment.
     *
     * @param c The updated comment.
     */
    public void updateComment(Comment c) {
        commentRepo.updateComment(c);
    }
}
