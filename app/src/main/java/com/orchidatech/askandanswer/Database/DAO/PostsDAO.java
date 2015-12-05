package com.orchidatech.askandanswer.Database.DAO;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.User_Categories;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class PostsDAO {

    public static void addPost(Posts newPost){
        if(isExist(newPost.getServerID())){
            updatePost(newPost);
            return;
        }
        Posts post = new Posts();
        post.categoryID = newPost.getCategoryID();
        post.date = newPost.getDate();
        post.image = newPost.getImage();
        post.isHidden = newPost.getIsHidden();
        post.serverID = newPost.getServerID();
        post.userID = newPost.getUserID();
        post.text = newPost.getText();
        if(newPost.getComments_no() != -1)
             post.comments_no = newPost.getComments_no();
        if(newPost.getNum_likes() != -1){
            post.num_likes = newPost.num_dislikes;
            post.num_likes = newPost.num_likes;
        }
        if(newPost.isFavorite != -1)
           post.isFavorite = newPost.getIsFavorite();
        post.save();
    }

    public static void checkRowsCount() {
        ArrayList<Posts> allPosts = new ArrayList<>(getAllPosts());
        int count = allPosts.size();
        Log.i("fdfdf", count + ", " + (count-GNLConstants.MAX_POSTS_ROWS));
        Collections.reverse(allPosts);
            for(int i = 0; i < count-GNLConstants.MAX_POSTS_ROWS; i++) {
                deletePost(allPosts.get(i).getServerID());
                CommentsDAO.deleteCommentByPost(allPosts.get(i).getServerID());
            }
    }

    public static void deletePost(long postServerId){
        new Delete().from(Posts.class).where(Posts.FIELDS.COLUMN_SERVER_ID + " = ?", postServerId).execute();
    }

    public static Posts getPost(long postServerId){
        return new Select().from(Posts.class).where(Posts.FIELDS.COLUMN_SERVER_ID + " = ?", postServerId).executeSingle();
    }

    public static void updatePost(Posts post){
        Posts existPost = getPost(post.getServerID());
        existPost.date = post.getDate();
        existPost.image = post.getImage();
        existPost.isHidden = post.getIsHidden();
        existPost.text = post.getText();
        existPost.userID = post.getUserID();
        existPost.categoryID = post.getCategoryID();
        if(post.getComments_no() != -1)
            existPost.comments_no = post.getComments_no();
        if(post.getNum_likes() != -1){
            existPost.num_likes = post.num_dislikes;
            existPost.num_likes = post.num_likes;
        }
        if(post.isFavorite != -1)
            existPost.isFavorite = post.getIsFavorite();

        existPost.save();
    }

    public static List<Posts> getAllPosts(){
        return new Select().from(Posts.class).orderBy(Posts.FIELDS.COLUMN_DATE + " desc").execute();
    }

    public static List<Posts> getUserPosts(long userId){
        return new Select().from(Posts.class).where(Posts.FIELDS.COLUMN_USER_ID + " = ? ", userId).orderBy(Posts.FIELDS.COLUMN_SERVER_ID + " desc").execute();
    }
    public static List<Posts> getAllPosts(long userId, long categoryId){
        return new Select().from(Posts.class).where(Posts.FIELDS.COLUMN_USER_ID + " = ? and " +
                Posts.FIELDS.COLUMN_CATEGORY_ID + " = ?", userId, categoryId).orderBy(Posts.FIELDS.COLUMN_SERVER_ID + " desc").execute();
    }
    public static List<Posts> getAllPostsInCategory(long categoryId){
        return new Select().from(Posts.class).where(Posts.FIELDS.COLUMN_CATEGORY_ID + " = ?", categoryId).orderBy(Posts.FIELDS.COLUMN_DATE + " desc").execute();
    }

    public static void deleteAllPosts(){
        new Delete().from(Posts.class).execute();
    }

    private static boolean isExist(long serverID) {
        return getPost(serverID) != null ;
    }

    public static void deletePostsInCategory(long uid, long categoryId) {
        new Delete().from(Posts.class).where(Posts.FIELDS.COLUMN_USER_ID + " = ? and " + Posts.FIELDS.COLUMN_CATEGORY_ID + " = ?", uid, categoryId).execute();

    }

    public static List<Posts> getPostsInUserCategories(long user_id) {

        ArrayList<Long> user_categories_id = new ArrayList<>();
        ArrayList<User_Categories> user_categories = new ArrayList<>(User_CategoriesDAO.getAllUserCategories(user_id));
        Long[] ids = new Long[user_categories.size()];
        String s = "(";
        for(int i = 0; i< user_categories.size(); i++){
            ids[i] = user_categories.get(i).getCategoryID();
            s += user_categories.get(i).getCategoryID();
            s += i!=user_categories.size()-1?",":"";
        }
        s+=")";
        Log.i("ghfgh", s);

        return  new Select().from(Posts.class).where(Posts.FIELDS.COLUMN_CATEGORY_ID + " in "+s).orderBy(Posts.FIELDS.COLUMN_DATE + " DESC").execute();
    }

    private static List<Posts> getAllPostsByCategory(long categoryId) {
        return new Select().from(Posts.class).where(
                Posts.FIELDS.COLUMN_CATEGORY_ID + " = ?", categoryId).orderBy(Posts.FIELDS.COLUMN_SERVER_ID + " desc").execute();
    }
}
