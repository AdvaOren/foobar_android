package entities;



import android.graphics.Bitmap;

/**
 * Model class representing a comment on a post.
 */
public class Comment {

    private String _id;
    private String text;
    private String userId;
    private Bitmap img;
    private String firstName;
    private String lastName;
    private String postId;



    /**
     * Constructor to create a new Comment object.
     *
     * @param _id       Unique identifier for the comment
     * @param text      Text content of the comment
     * @param userId    ID of the user who posted the comment
     * @param img       Image attached to the comment
     * @param firstName First name of the user who posted the comment
     * @param lastName  Last name of the user who posted the comment
     * @param postId    ID of the post to which the comment belongs
     */
    public Comment(String _id, String text, String userId, Bitmap img, String firstName, String lastName, String postId) {
        this._id = _id;
        this.text = text;
        this.userId = userId;
        this.img = img;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postId = postId;
    }

    //Getters and Setters
    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
