package feed_content.comment;

import java.util.ArrayList;
import java.util.Arrays;

// This class is the database of the comments
public class CommentRepo {
    private ArrayList<Comment> commentsList;
    private static int nextId;

    /**
     * Constructor for the CommentRepo class.
     * @param commentsList Initial comment list.
     */
    public CommentRepo(ArrayList<Comment> commentsList) {
        this.commentsList = commentsList;
    }

    /**
     * Adds a new comment to the list.
     * @param text The comment's text.
     * @param firstN The user's first name.
     * @param lastN The user's last name.
     */
    public void add(String text, String firstN, String lastN) {
        Comment comment = new Comment(nextId, text, firstN + " " + lastN);
        commentsList.add(0, comment); // Add the comment at the beginning of the list
        nextId++; // Increment the id for the next comment
    }

    /**
     * Removes a comment from the list by its id.
     * @param id The id of the comment to be removed.
     */
    public void remove(int id) {
        for (int i = 0; i < commentsList.size(); i++) {
            if (commentsList.get(i).getId() == id) {
                commentsList.remove(commentsList.get(i)); // Remove the comment at index i
                break; // Exit the loop after removing the comment
            }
        }
    }

    /**
     * Edits the text of a comment with the specified id.
     * @param id The id of the comment to be edited.
     * @param newText The new text of the comment.
     */
    public void edit(int id, String newText) {
        for (int i = 0; i < commentsList.size(); i++) {
            if (commentsList.get(i).getId() == id) {
                commentsList.get(i).setText(newText); // Set the new text for the comment
                break; // Exit the loop after updating the comment
            }
        }
    }

    /**
     * Gets the list of comments.
     * @return The list of comments.
     */
    public ArrayList<Comment> getCommentsList() {
        return commentsList;
    }

    /**
     * Sets the list of comments.
     * @param commentsList The list of comments to be set.
     */
    public void setCommentsList(ArrayList<Comment> commentsList) {
        this.commentsList = commentsList;
    }
}
