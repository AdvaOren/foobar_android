package repositories;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import java.util.LinkedList;
import java.util.List;

import api.PostAPI;
import entities.Member;
import entities.PostInfo;
import entities.Post;
import viewmodels.MemberViewModel;

public class PostRepo {

    private final PostDao postDao;
    private final PostInfoDao postInfoDao;
    private final PostListData posts;
    private final PostAPI postAPI;
    private final MemberViewModel memberVm;


    /**
     * Constructor for the PostRepo class.
     * Loads posts from a JSON file and initializes the postList.
     *
     * @param context The activity context.
     */
    public PostRepo(Activity context, String jwtToken, MemberViewModel memberVM) {
        AppDB db = Room.databaseBuilder(context, AppDB.class, "Foobar_DAT").build();
        postDao = db.postDao();
        postInfoDao = db.likeDao();
        posts = new PostListData();
        postAPI = new PostAPI(posts, postDao, jwtToken);
        this.memberVm = memberVM;
    }


    class PostListData extends MutableLiveData<List<Post>> {
        public PostListData() {
            super();
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();

            new Thread(() -> {
                Member member = memberVm.getCurrentMember().getValue();
                if (member != null)
                    posts.postValue(postDao.getAll(member.get_id()));
            });
        }
    }

    public LiveData<List<Post>> getAll(String userId) {
        postAPI.getLastPosts(userId, postInfoDao,memberVm);
        return posts;
    }

    public void add(String userId, Post post) {
        postAPI.addPost(userId, post);
    }

    public void reload(String userId) {
        postAPI.getLastPosts(userId, postInfoDao,memberVm);
    }

    public void delete(String userId, String postId) {
        postAPI.deletePost(userId, postId);
    }

    public void update(String userId, Post post) {
        if (post.getContent().equals("") || post.getImg() == null)
            postAPI.updatePost(userId, post);
        else
            postAPI.updateAllThePost(userId, post);
    }

    public boolean isLiked(String userId, String postId) {
        return postInfoDao.getIfLike(userId, postId);
    }

    public void addLike(String userId, String postId) {
        PostInfo postInfo = postInfoDao.getPostInfo(userId,postId);
        postInfo.setLiked(true);
        postInfo.setLikeAmount(postInfo.getLikeAmount()+1);
        postInfoDao.update(postInfo);
        postAPI.addLike(userId, postId);
    }

    public void removeLike(String userId, String postId) {
        PostInfo postInfo = postInfoDao.getPostInfo(userId,postId);
        postInfo.setLiked(false);
        postInfo.setLikeAmount(postInfo.getLikeAmount()-1);
        postInfoDao.update(postInfo);
        postAPI.removeLike(userId, postId);
    }


    /*
     * This function return image by path
     * @param context the context screen
     * @param path path to image
     * @return the image
     */
    /*private Bitmap getImageByPath(Activity context ,String path) throws IOException {
        InputStream inputStream = context.getAssets().open(path);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
    }*/

    /*
     * Load JSON data from the assets folder.
     * @param context The activity context.
     * @param filename The name of the JSON file.
     * @return The JSON data as a string.
     */
    /*public String loadJSONFromAsset(Activity context, String filename) {
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
    }*/

    /*
     * Adds a new post to the postList.
     *
     * @param title   The title of the post.
     * @param content The content of the post.
     * @param firstN  The first name of the user.
     * @param lastN   The last name of the user.
     * @param posPic  The picture associated with the post.
     * @param userPic The user's profile picture.
     * @param date    The date of the post.
     */
    /*public void add(String title, String content, String firstN, String lastN, Bitmap posPic, Bitmap userPic, String date) {
        Post post = new Post(nextId, title, firstN, lastN, content, posPic, userPic, date);
        postList.add(0, post); // Add the post at the beginning of the list
        nextId++; // Increment the id for the next post
    }*/

    /*
     * Deletes a post from the postList by its id.
     *
     * @param id The id of the post to be deleted.
     */
    /*public void delete(int id) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.remove(postList.get(i)); // Remove the post at index i
                break; // Exit the loop after removing the post
            }
        }
    }*/

    /*
     * Edits an existing post with the given id by updating its title, content, date, and image.
     *
     * @param id         The id of the post to be edited.
     * @param newTitle   The new title for the post.
     * @param newContent The new content for the post.
     * @param newDate    The new date for the post.
     * @param newImg     The new image for the post.
     */
    /*public void edit(int id, String newTitle, String newContent, String newDate, Bitmap newImg) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.get(i).setTitle(newTitle);
                postList.get(i).setContent(newContent);
                postList.get(i).setDate(newDate);
                postList.get(i).setPostPic(newImg);
                return;
            }
        }
    }*/

    /*
     * Updates the number of likes for a post with the given id.
     *
     * @param id            The id of the post.
     * @param newLikeAmount The new number of likes for the post.
     */
    /*public void updateLike(int id, int newLikeAmount) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.get(i).setLikes(newLikeAmount);
                return;
            }
        }
    }*/

    /*
     * Checks if a post with the given id has been liked.
     *
     * @param id The id of the post.
     * @return True if the post has been liked, false otherwise.
     */
    /*public boolean isLiked(int id) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                return postList.get(i).isLiked();
            }
        }
        return false;
    }*/

    /*
     * Sets the liked status of a post with the given id.
     *
     * @param id The id of the post.
     */
    /*public void setLiked(int id) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.get(i).setLiked(!postList.get(i).isLiked());
            }
        }
    }*/

    /*
     * Updates the comments for a post with the given id.
     *
     * @param id       The id of the post.
     * @param comments The new list of comments for the post.
     */
    /*public void updateComments(int id, ArrayList<Comment> comments) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.get(i).setCommentList(comments);
                return;
            }
        }
    }*/

    /*
     * Sets the share clicked status of a post with the given id.
     *
     * @param id The id of the post.
     */
    /*public void setShareClicked(int id) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == id) {
                postList.get(i).setShareClicked(!postList.get(i).isShareClicked());
                return;
            }
        }
    }*/

    /**
     * Gets the list of posts.
     *
     * @return The list of posts.
     */
    /*public List<Post> getPostList() {
        return postList;
    }*/
}
