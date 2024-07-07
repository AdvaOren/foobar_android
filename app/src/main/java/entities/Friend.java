package entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Entity class representing a Friend in the application.
 */
public class Friend {
    private String requester;
    private String requested;
    private String requesterName;
    private String status;
    private String img;
    // Constants for different friend status
    public static final String NOT_FRIENDS = "Add Friend";
    public static final String REQUEST_SENT = "Request Sent";
    public static final String FRIENDS = "We are friends :)";
    public static final String REQUEST_SENT_HIS_SIDE = "Request is sent to you";


    /**
     * Constructor to create a Friend object with specified parameters.
     * @param requester The ID of the user who sent the friend request.
     * @param requested The ID of the user to whom the friend request is sent.
     * @param status The status of the friend request.
     * @param requesterName The name of the user who sent the friend request.
     * @param img The image of the user who sent the friend request.
     */
    public Friend(String requester, String requested, String status, String requesterName,String img) {
        this.requester = requester;
        this.requested = requested;
        this.status = status;
        this.requesterName = requesterName;
        this.img = img;
    }

    /**
     * Constructor to create a Friend object with only requester and requested parameters.
     * @param requester The ID of the user who sent the friend request.
     * @param requested The ID of the user to whom the friend request is sent.
     */
    public Friend(String requester, String requested) {
        this.requester = requester;
        this.requested = requested;
        this.status = "wait";
        this.requesterName = "";
        this.img = "";
    }

    /**
     * Sets the image of the Friend object as a Bitmap.
     * @param img The image Bitmap to set.
     */
    public void setImgBitmap(Bitmap img) {

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        this.img = temp;
    }

    /**
     * Gets the image of the Friend object as a Bitmap.
     * @return The image Bitmap.
     */
    public Bitmap getImgBitmap() {
        try {
            byte [] encodeByte= Base64.decode(img,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    //Getters and Setters
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getRequested() {
        return requested;
    }

    public void setRequested(String requested) {
        this.requested = requested;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
