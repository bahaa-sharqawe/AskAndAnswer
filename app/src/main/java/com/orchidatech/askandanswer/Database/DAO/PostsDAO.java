package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Posts;


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
        post.comments_no = newPost.getComments_no();
        post.save();
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
        existPost.comments_no = post.getComments_no();
        existPost.save();
    }

    public static List<Posts> getAllPosts(){
        return new Select().from(Posts.class).orderBy(Posts.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static List<Posts> getUserPosts(long userId){
        return new Select().from(Posts.class).where(Posts.FIELDS.COLUMN_USER_ID + " = ? ", userId).orderBy(Posts.FIELDS.COLUMN_SERVER_ID).execute();
    }
    public static List<Posts> getAllPosts(long userId, long categoryId){
        return new Select().from(Posts.class).where(Posts.FIELDS.COLUMN_USER_ID + " = ? and " +
                Posts.FIELDS.COLUMN_CATEGORY_ID + " = ?", userId, categoryId).orderBy(Posts.FIELDS.COLUMN_SERVER_ID).execute();
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
}
