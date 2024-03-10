package viewmodels;

import android.app.Activity;

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

    public void initializePostViewModel(Activity context, String jwtToken, MemberViewModel memberVM) {
        postRepo = new PostRepo(context, jwtToken, memberVM);
    }

    public LiveData<List<Post>> getAll(String userId) {
        posts = postRepo.getAll(userId);
        return posts;
    }

    public LiveData<List<Post>> getPostsByUser(String userId,String requester) {
        postsByUser = postRepo.getPostsByUser(userId,requester);
        return postsByUser;
    }

    public void addPost(String userId, Post post) {
        postRepo.add(userId, post);
    }

    public void reload(String userId) {
        postRepo.reload(userId);
    }

    public void delete(String userId, Post post) {
        postRepo.delete(userId, post);
    }

    public void update(String userId, Post post) {
        postRepo.update(userId, post);
    }



    public void addLike(String userId, String postId) {
        postRepo.addLike(userId, postId);
    }

    public void removeLike(String userId, String postId) {
        postRepo.removeLike(userId, postId);
    }

    public void updateNumComments(String userId,String postId,int numComments) {
        postRepo.updateNumComments(userId,postId,numComments);
    }

    public void reloadByUser(String requested,String requester) {
        postRepo.reloadByUser(requested,requester);
    }



    /*public void setLiked(int id) {
        postRepo.setLiked(id);
    }

    public void setShareClicked(int id) {
        postRepo.setShareClicked(id);
    }*/

    /*public void add(String title, String content, Bitmap posPic,Bitmap userPic,String firstN,String lastN, String date) {
        postRepo.add(title,content,firstN,lastN,posPic,userPic,date);
    }

    public void delete(int id) {
        postRepo.delete(id);
    }

    public void edit(int id, String newTitle, String newContent,String date,Bitmap img) {
        postRepo.edit(id,newTitle,newContent,date,img);
    }

    public void updateLike(int id, int newLikeAmount) {
        postRepo.updateLike(id,newLikeAmount);
    }

    public void updateComments(int id, ArrayList<Comment> comments) {
        postRepo.updateComments(id,comments);
    }*/

}
