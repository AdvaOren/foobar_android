package repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.LinkedList;
import java.util.List;

import api.CommentAPI;
import entities.Comment;

// This class is the database of the comments
public class CommentRepo {
    private CommentListData comments;
    private String postId;
    private CommentAPI commentAPI;

    public CommentRepo(String postId,String jwtToken) {
        comments = new CommentListData();
        this.postId = postId;
        commentAPI = new CommentAPI(comments,jwtToken);
    }

    public class CommentListData extends MutableLiveData<List<Comment>> {

        public CommentListData() {
            super();
            setValue(new LinkedList<>());

        }

        @Override
        protected void onActive() {
            super.onActive();

        }

        public void addComments(List<Comment> newComments) {
            List<Comment> temp = comments.getValue();
            if (temp == null)
                return;
            temp.addAll(newComments);
            comments.postValue(temp);
        }

        public void addComment(Comment newComment) {
            List<Comment> comments1 = getValue();
            if (comments1 == null)
                return;
            comments1.add(0,newComment);
            comments.postValue(comments1);
        }

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

    public LiveData<List<Comment>> get() {
        return comments;
    }

    public void getAll() {
        commentAPI.getAll(postId);
    }

    public void addComment(Comment c) {
        commentAPI.addComment(c);
    }

    public void deleteComment(String userId, String postId, String id) {
        commentAPI.deleteComment(userId, postId, id);
    }

    public void updateComment(Comment c) {
        commentAPI.updateComment(c);
    }


    /*//private ArrayList<Comment> commentsList;
    //private static int nextId;


     *Constructor for the CommentRepo class.
     * @param commentsList Initial comment list.
     */
    /*public CommentRepo(ArrayList<Comment> commentsList) {
        this.commentsList = commentsList;
    }*/

    /*
     * Adds a new comment to the list.
     * @param text The comment's text.
     * @param firstN The user's first name.
     * @param lastN The user's last name.
     */
    /*public void add(String text, String firstN, String lastN) {
        Comment comment = new Comment(nextId, text, firstN + " " + lastN);
        commentsList.add(0, comment); // Add the comment at the beginning of the list
        nextId++; // Increment the id for the next comment
    }*/

    /*
     * Removes a comment from the list by its id.
     * @param id The id of the comment to be removed.
     */
    /*public void remove(int id) {
        for (int i = 0; i < commentsList.size(); i++) {
            if (commentsList.get(i).getId() == id) {
                commentsList.remove(commentsList.get(i)); // Remove the comment at index i
                break; // Exit the loop after removing the comment
            }
        }
    }*/

    /*
     * Edits the text of a comment with the specified id.
     * @param id The id of the comment to be edited.
     * @param newText The new text of the comment.
     */
    /*public void edit(int id, String newText) {
        for (int i = 0; i < commentsList.size(); i++) {
            if (commentsList.get(i).getId() == id) {
                commentsList.get(i).setText(newText); // Set the new text for the comment
                break; // Exit the loop after updating the comment
            }
        }
    }*/

    /*
     * Gets the list of comments.
     * @return The list of comments.
     */
    /*public ArrayList<Comment> getCommentsList() {
        return commentsList;
    }*/

    /*
     * Sets the list of comments.
     * @param commentsList The list of comments to be set.
     */
    /*public void setCommentsList(ArrayList<Comment> commentsList) {
        this.commentsList = commentsList;
    }*/
}
