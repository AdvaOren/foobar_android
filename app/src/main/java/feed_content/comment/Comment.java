package feed_content.comment;

import android.os.Parcel;
import android.os.Parcelable;

// This class is present a comment of post
public class Comment implements Parcelable {
    private String text;
    private String name;
    private int id;

    /**
     * This is the constructor to the class
     * @param id the id
     * @param text the comment's text
     * @param name the user name
     */
    public Comment(int id, String text, String name) {
        this.id = id;
        this.text = text;
        this.name = name;

    }

    // Parcelable implementation

    /**
     * This constructor create Comment object from Parcel
     * @param in the parcel
     */
    protected Comment(Parcel in) {
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

    /**
     * This function convert the object to the parcel
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
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
    }
}
