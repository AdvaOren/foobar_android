package repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.LinkedList;
import java.util.List;

import api.CommentAPI;
import entities.Comment;

/**
 * Repository class for managing comments.
 */
public class CommentRepo {
    private CommentListData comments;
    private String postId;
    private CommentAPI commentAPI;

    /**
     * Constructor for initializing CommentRepo.
     *
     * @param postId The ID of the post associated with the comments.
     * @param jwtToken The JWT token for authorization.
     */
    public CommentRepo(String postId,String jwtToken) {
        comments = new CommentListData();
        this.postId = postId;
        commentAPI = new CommentAPI(comments,jwtToken);
    }

    /**
     * Inner class extending MutableLiveData to hold a list of comments.
     */
    public class CommentListData extends MutableLiveData<List<Comment>> {

        /**
         * Constructor for initializing CommentListData.
         */
        public CommentListData() {
            super();
            setValue(new LinkedList<>());

        }


        /**
         * Called when the LiveData becomes active.
         */
        @Override
        protected void onActive() {
            super.onActive();

        }

        /**
         * Adds a new comment to the list of comments.
         *
         * @param newComment The comment to add.
         */
        public void addComment(Comment newComment) {
            List<Comment> comments1 = getValue();
            if (comments1 == null)
                return;
            comments1.add(0,newComment);
            comments.postValue(comments1);
        }

        /**
         * Removes a comment from the list of comments.
         *
         * @param id The ID of the comment to remove.
         */
        public void removeComment(String id) {
            List<Comment> comments1 = getValue();
            if (comments1 == null)
                return;
            for (Comment comment : comments1) {
                if (comment.get_id().equals(id)) {
                    comments1.remove(comment);
                    comments.postValue(comments1);
                    break;
                }
            }
        }


        /**
         * Updates a comment in the list of comments.
         *
         * @param comment The updated comment.
         */
        public void updatePost(Comment comment) {
            List<Comment> comments1 = getValue();
            if (comments1 == null)
                return;
            String commentId = comment.get_id();
            for (Comment curr : comments1) {
                if (curr.get_id().equals(commentId)) {
                    curr.setText(comment.getText());
                    comments.postValue(comments1);
                    break;
                }
            }
        }
    }

    /**
     * Returns LiveData containing the list of comments.
     *
     * @return LiveData holding the list of comments.
     */
    public LiveData<List<Comment>> get() {
        return comments;
    }

    /**
     * Retrieves all comments associated with the post.
     */
    public void getAll() {
        commentAPI.getAll(postId);
    }

    /**
     * Adds a new comment.
     *
     * @param c The comment to add.
     */
    public void addComment(Comment c) {
        commentAPI.addComment(c);
    }


    /**
     * Deletes a comment.
     *
     * @param userId The ID of the user who owns the comment.
     * @param postId The ID of the post associated with the comment.
     * @param id The ID of the comment to delete.
     */
    public void deleteComment(String userId, String postId, String id) {
        commentAPI.deleteComment(userId, postId, id);
    }

    /**
     * Updates a comment.
     *
     * @param c The updated comment.
     */
    public void updateComment(Comment c) {
        commentAPI.updateComment(c);
    }
}
