package viewmodels;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import entities.Post;
import repositories.PostRepo;

public class PostsViewModel extends ViewModel {
    private PostRepo postRepo;
    private LiveData<List<Post>> posts;
    private LiveData<List<Post>> postsByUser;

    public PostsViewModel() {
    }

    /**
     * Initializes the PostViewModel with the provided context, JWT token, and MemberViewModel.
     *
     * @param context   The activity context.
     * @param jwtToken  The JWT token.
     * @param memberVM  The MemberViewModel.
     */
    public void initializePostViewModel(Activity context, String jwtToken, MemberViewModel memberVM) {
        postRepo = new PostRepo(context, jwtToken, memberVM);
    }

    /**
     * Retrieves all posts for the specified user.
     *
     * @param userId The user ID.
     * @return LiveData containing the list of posts.
     */
    public LiveData<List<Post>> getAll(String userId) {
        posts = postRepo.getAll(userId);
        return posts;
    }

    /**
     * Retrieves posts by the specified user.
     *
     * @param userId    The user ID.
     * @param requester The requester ID.
     * @return LiveData containing the list of posts.
     */
    public LiveData<List<Post>> getPostsByUser(String userId,String requester) {
        postsByUser = postRepo.getPostsByUser(userId,requester);
        return postsByUser;
    }

    /**
     * Adds a new post for the specified user.
     *
     * @param userId The user ID.
     * @param post   The post to add.
     */
    public void addPost(String userId, Post post) {
        postRepo.add(userId, post);
    }

    /**
     * Reloads the posts for the specified user.
     *
     * @param userId The user ID.
     */
    public void reload(String userId) {
        postRepo.reload(userId);
    }

    /**
     * Deletes the specified post for the user.
     *
     * @param userId The user ID.
     * @param post   The post to delete.
     */
    public void delete(String userId, Post post, int whereAmI) {
        postRepo.delete(userId, post,whereAmI);
    }


    /**
     * Updates the specified post for the user.
     *
     * @param userId The user ID.
     * @param post   The post to update.
     */
    public void update(String userId, Post post,int whereAmI) {
        postRepo.update(userId, post,whereAmI);
    }

    public void updateAdapter(List<Post> posts, Post post, Bitmap bmp) {
        String postId = post.get_id();
        for (Post curr : posts) {
            if (curr.get_id().equals(postId)) {
                if (bmp != null) {
                    curr.setImgBitmap(bmp);
                }
                if (!post.getContent().equals("")) {
                    curr.setContent(post.getContent());
                }
                break;
            }
        }
    }

    /**
     * Adds a like to the specified post for the user.
     *
     * @param userId The user ID.
     * @param postId The post ID.
     */
    public void addLike(String userId, String postId) {
        postRepo.addLike(userId, postId);
    }

    /**
     * Removes a like from the specified post for the user.
     *
     * @param userId The user ID.
     * @param postId The post ID.
     */
    public void removeLike(String userId, String postId) {
        postRepo.removeLike(userId, postId);
    }

    /**
     * Updates the number of comments for the specified post.
     *
     * @param userId      The user ID.
     * @param postId      The post ID.
     * @param numComments The number of comments.
     */
    public void updateNumComments(String userId,String postId,int numComments, int whereAmI) {
        postRepo.updateNumComments(userId,postId,numComments,whereAmI);
    }

    /**
     * Reloads the posts by the specified user.
     *
     * @param requested The requested user ID.
     * @param requester The requester user ID.
     */
    public void reloadByUser(String requested,String requester) {
        postRepo.reloadByUser(requested,requester);
    }

    /**
     * Deletes all posts for the specified user.
     *
     * @param userId The user ID.
     */
    public void deleteUser(String userId) {
        postRepo.deleteUser(userId);
    }


}
