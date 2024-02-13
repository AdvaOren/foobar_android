package feed_content.comment;

import java.util.ArrayList;

//This class is the model layer it's get data from the comment repo
public class CommentModel {
    private final CommentRepo commentRepo;

    /**
     * This is a constructor
     */
    public CommentModel() {
        ArrayList<Comment> c = new ArrayList<>();
        commentRepo = new CommentRepo(c);
    }

    /**
     * This function edit comment
     * @param id the comment id
     * @param newText the new text for the comment
     */
    public void edit(int id, String newText) {
        commentRepo.edit(id,newText);
    }

    /**
     * This function delete comment
     * @param id the comment id
     */
    public void remove(int id) {
        commentRepo.remove(id);
    }

    /**
     * This function add a new comment
     * @param text the comment text
     * @param firstN the user first name
     * @param lastN the user last name
     */
    public void add(String text, String firstN, String lastN) {
        commentRepo.add(text,firstN,lastN);
    }

    /**
     * This function return array list of all the comments
     * @return the comments
     */
    public ArrayList<Comment> get() {
        return commentRepo.getCommentsList();
    }

    /**
     * This function set the comment in the comment repo
     * @param comments all the comments
     */
    public void setComments(ArrayList<Comment> comments) {
        commentRepo.setCommentsList(comments);
    }

}
