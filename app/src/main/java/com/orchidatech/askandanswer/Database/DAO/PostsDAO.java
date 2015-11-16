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
        Posts post = new Posts();
        post.categoryID = newPost.getCategoryID();
        post.date = newPost.getDate();
        post.image = newPost.getImage();
        post.isHidden = newPost.getIsHidden();
        post.serverID = newPost.getServerID();
        post.userID = newPost.getUserID();
        post.text = newPost.getText();
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
        post.date = post.getDate();
        post.image = post.getImage();
        post.isHidden = post.getIsHidden();
        post.text = post.getText();
        existPost.save();
    }

    public static List<Posts> getAllPosts(){
        return new Select().from(Posts.class).orderBy(Posts.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static List<Posts> getAllPosts(long categoryId){
        return new Select().from(Posts.class).where(Posts.FIELDS.COLUMN_CATEGORY_ID + " = ?", categoryId).orderBy(Posts.FIELDS.COLUMN_SERVER_ID).execute();
    }
    public static List<Posts> getAllPosts(long userId, long categoryId){
        return new Select().from(Posts.class).where(Posts.FIELDS.COLUMN_USER_ID + " = ? and " +
                Posts.FIELDS.COLUMN_CATEGORY_ID + " = ?", userId, categoryId).orderBy(Posts.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static void deleteAllPosts(){
        new Delete().from(Posts.class).execute();
    }
}
