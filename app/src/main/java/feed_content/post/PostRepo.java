package feed_content.post;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import feed_content.comment.Comment;

public class PostRepo {
    private List<Post> postList;
    private static int nextId = 0;

    /**
     * Constructor for the PostRepo class.
     * Loads posts from a JSON file and initializes the postList.
     * @param context The activity context.
     */
    public PostRepo(Activity context) {
        postList = new ArrayList<>();
        String jsonString = loadJSONFromAsset(context, "posts.json");

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            //run over all the objects in the json file
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //get the fields that in the json file
                String title = jsonObject.getString("title");
                String content = jsonObject.getString("content");
                String img = jsonObject.getString("img");
                String date = jsonObject.getString("date");
                String firstN = jsonObject.getString("firstN");
                String lastN = jsonObject.getString("lastN");
                String avatar = jsonObject.getString("avatar");
                int likes = jsonObject.getInt("likes");

                //get the image by image path
                Bitmap bitmap = getImageByPath(context,img);
                Bitmap bitmap2 = getImageByPath(context,avatar);

                //create new post and save it in the post repo
                Post post = new Post(nextId, title, firstN, lastN, content, bitmap, bitmap2, date);
                post.setLikes(likes);
                postList.add(post);
                nextId++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This function return image by path
     * @param context the context screen
     * @param path path to image
     * @return the image
     */
    private Bitmap getImageByPath(Activity context ,String path) throws IOException {
        InputStream inputStream = context.getAssets().open(path);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
    }

    /**
     * Load JSON data from the assets folder.
     * @param context The activity context.
     * @param filename The name of the JSON file.
     * @return The JSON data as a string.
     */
    public String loadJSONFromAsset(Activity context, String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("posts.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * Adds a new post to the postList.
     * @param title The title of the post.
     * @param content The content of the post.
     * @param firstN The first name of the user.
     * @param lastN The last name of the user.
     * @param posPic The picture associated with the post.
     * @param userPic The user's profile picture.
     * @param date The date of the post.
     */
    public void add(String title, String content, String firstN, String lastN, Bitmap posPic, Bitmap userPic, String date) {
        Post post = new Post(nextId, title, firstN, lastN, content, posPic, userPic, date);
        postList.add(0, post); // Add the post at the beginning of the list
        nextId++; // Increment the id for the next post
    }

    /**
     * Deletes a post from the postList by its id.
     * @param id The id of the post to be deleted.
     */
    public void delete(int id) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.remove(postList.get(i)); // Remove the post at index i
                break; // Exit the loop after removing the post
            }
        }
    }

    /**
     * Edits an existing post with the given id by updating its title, content, date, and image.
     * @param id The id of the post to be edited.
     * @param newTitle The new title for the post.
     * @param newContent The new content for the post.
     * @param newDate The new date for the post.
     * @param newImg The new image for the post.
     */
    public void edit(int id, String newTitle, String newContent, String newDate, Bitmap newImg) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.get(i).setTitle(newTitle);
                postList.get(i).setContent(newContent);
                postList.get(i).setDate(newDate);
                postList.get(i).setPicBit(newImg);
                return;
            }
        }
    }

    /**
     * Updates the number of likes for a post with the given id.
     * @param id The id of the post.
     * @param newLikeAmount The new number of likes for the post.
     */
    public void updateLike(int id, int newLikeAmount) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.get(i).setLikes(newLikeAmount);
                return;
            }
        }
    }

    /**
     * Checks if a post with the given id has been liked.
     * @param id The id of the post.
     * @return True if the post has been liked, false otherwise.
     */
    public boolean isLiked(int id) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                return postList.get(i).isLiked();
            }
        }
        return false;
    }

    /**
     * Sets the liked status of a post with the given id.
     * @param id The id of the post.
     */
    public void setLiked(int id) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.get(i).setLiked(!postList.get(i).isLiked());
            }
        }
    }

    /**
     * Updates the comments for a post with the given id.
     * @param id The id of the post.
     * @param comments The new list of comments for the post.
     */
    public void updateComments(int id, ArrayList<Comment> comments) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.get(i).setCommentList(comments);
                return;
            }
        }
    }

    /**
     * Sets the share clicked status of a post with the given id.
     * @param id The id of the post.
     */
    public void setShareClicked(int id) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.get(i).setShareClicked(!postList.get(i).isShareClicked());
                return;
            }
        }
    }

    /**
     * Gets the list of posts.
     * @return The list of posts.
     */
    public List<Post> getPostList() {
        return postList;
    }
}
