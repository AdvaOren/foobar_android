package entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;

/**
 * Entity class representing a member/user.
 */
@Entity(tableName = "members")
public class Member {
    @PrimaryKey
    @NonNull
    private String _id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String img;

    /**
     * Constructor for creating a Member object with Bitmap image.
     *
     * @param Email The email of the member.
     * @param firstName The first name of the member.
     * @param lastName The last name of the member.
     * @param password The password of the member.
     * @param image The profile image of the member as a Bitmap.
     */
    public Member(String Email,String firstName, String lastName, String password,Bitmap image) {
        this.email = Email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        setImgBitmap(image);
        _id = "";
    }

    /**
     * Constructor for creating a Member object with all fields.
     *
     * @param id The ID of the member.
     * @param email The email of the member.
     * @param firstName The first name of the member.
     * @param lastName The last name of the member.
     * @param password The password of the member.
     * @param img The profile image of the member as a Base64 encoded string.
     */
    public Member(@NonNull String id, String email, String firstName, String lastName, String password, String img) {
        this._id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.img = img;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }



    /**
     * Setter for the profile image of the member.
     *
     * @param img The profile image to set as a Bitmap.
     */
    public void setImgBitmap(Bitmap img) {

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        this.img = temp;
    }

    /**
     * Getter for the profile image of the member as a Bitmap.
     *
     * @return The profile image of the member as a Bitmap.
     */
    public Bitmap getImgBitmap() {
        try {
            byte [] encodeByte=Base64.decode(img,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    //Getters and Setters
    @NonNull
    public String get_id() {
        return _id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setId(@NonNull String id) {
        this._id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Method to check if two Member objects are equal.
     *
     * @param member The Member object to compare.
     * @return True if the Member objects are equal, false otherwise.
     */
    public boolean equals(Member member) {
        return this.email.equals(member.getEmail()) &&
                this.img.equals(member.getImg()) &&
                this._id.equals(member.get_id()) &&
                this.firstName.equals(member.getFirstName()) &&
                this.lastName.equals(member.getLastName()) &&
                this.password.equals(member.getPassword());
    }
}
