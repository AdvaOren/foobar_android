package entities;


// This class is present a comment of post

import android.graphics.Bitmap;

public class Comment {

    private String _id;
    private String text;
    private String userId;
    private Bitmap img;
    private String firstName;
    private String lastName;
    private String postId;


    public Comment(String _id, String text, String userId, Bitmap img, String firstName, String lastName, String postId) {
        this._id = _id;
        this.text = text;
        this.userId = userId;
        this.img = img;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postId = postId;
    }

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

    // Parcelable implementation

    /*
     * This constructor create Comment object from Parcel
     * @param in the parcel
     */
    /*protected Comment(Parcel in) {
        text = in.readString();
        name = in.readString();
        id = in.readInt();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }
*/
    /*
     * This function convert the object to the parcel
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    /*@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(name);
        dest.writeInt(id);
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }*/
}
