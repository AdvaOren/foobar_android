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

    public PostsViewModel() {
    }

    public void initializePostViewModel(Activity context, String jwtToken, MemberViewModel memberVM, String userId) {
        postRepo = new PostRepo(context, jwtToken, memberVM);
        posts = postRepo.getAll(userId);
    }

    public LiveData<List<Post>> getAll() {
        return posts;
    }

    public void addPost(String userId, Post post) {
        postRepo.add(userId, post);
    }

    public void reload(String userId) {
        postRepo.reload(userId);
    }

    public void delete(String userId, String postId) {
        postRepo.delete(userId, postId);
    }

    public void update(String userId, Post post) {
        postRepo.update(userId, post);
    }

    public boolean isLiked(String userId, String postId) {
        return postRepo.isLiked(userId, postId);
    }

    public void addLike(String userId, String postId) {
        postRepo.addLike(userId, postId);
    }

    public void removeLike(String userId, String postId) {
        postRepo.removeLike(userId, postId);
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
