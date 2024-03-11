package entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;

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
    public Member(String Email,String firstName, String lastName, String password,Bitmap image) {
        this.email = Email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        setImgBitmap(image);
        _id = "";
    }

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

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    public void setImgBitmap(Bitmap img) {

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        this.img = temp;
    }

    @NonNull
    public String get_id() {
        return _id;
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

    public boolean equals(Member member) {
        return this.email.equals(member.getEmail()) &&
                this.img.equals(member.getImg()) &&
                this._id.equals(member.get_id()) &&
                this.firstName.equals(member.getFirstName()) &&
                this.lastName.equals(member.getLastName()) &&
                this.password.equals(member.getPassword());
    }
}
