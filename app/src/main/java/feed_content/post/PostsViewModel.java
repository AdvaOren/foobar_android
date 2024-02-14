package feed_content.post;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import feed_content.comment.Comment;

public class PostsViewModel extends ViewModel {
    private PostRepo postRepo;

    public PostsViewModel(){

    }

    public void initializePostViewModel(Activity context){
        postRepo = new PostRepo(context);

    }

    public List<Post> get() {
        return postRepo.getPostList();
    }

    public void add(String title, String content, Bitmap posPic,Bitmap userPic,String firstN,String lastN, String date) {
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


    public void setLiked(int id) {
        postRepo.setLiked(id);
    }

    public void setShareClicked(int id) {
        postRepo.setShareClicked(id);
    }

    public void updateComments(int id, ArrayList<Comment> comments) {
        postRepo.updateComments(id,comments);
    }

}
