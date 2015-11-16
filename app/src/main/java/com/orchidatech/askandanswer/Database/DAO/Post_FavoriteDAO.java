package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Post_Favorite;

import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class Post_FavoriteDAO {

    public static void addPostFavorite(Post_Favorite newPostFavorite){
        Post_Favorite post_favorite = new Post_Favorite();
        post_favorite.date = newPostFavorite.getDate();
        post_favorite.postID = newPostFavorite.getPostID();
        post_favorite.userID = newPostFavorite.getUserID();
        post_favorite.serverID = newPostFavorite.getServerID();
        post_favorite.save();
    }

    public static void deletePostFavorite(long postFavoriteServerId, long userServerId){
        new Delete().from(Post_Favorite.class).where(Post_Favorite.FIELDS.COLUMN_SERVER_ID +
                " = ? and " + Post_Favorite.FIELDS.COLUMN_USER_ID + " = ?", postFavoriteServerId, userServerId).execute();
    }

    public static Post_Favorite getSearchHistory(long postFavoriteServerId){
        return new Select().from(Post_Favorite.class).where(Post_Favorite.FIELDS.COLUMN_SERVER_ID + " = ?", postFavoriteServerId)
                .executeSingle();
    }


    public static List<Post_Favorite> getAllUserPostFavorite(long userServerId){
        return new Select().from(Post_Favorite.class).where(Post_Favorite.FIELDS.COLUMN_USER_ID + " = ?", userServerId)
                .orderBy(Post_Favorite.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static void deleteAllUserPostFavorite(long userServerId){
        new Delete().from(Post_Favorite.class).where(Post_Favorite.FIELDS.COLUMN_USER_ID + " = ?", userServerId).execute();
    }
}
