package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Post_Favorite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class Post_FavoriteDAO {

    public static void addPostFavorite(Post_Favorite newPostFavorite){
        if(isExist(newPostFavorite.getServerID())){
            updatePost_Favorite(newPostFavorite);
            return;
        }
        Post_Favorite post_favorite = new Post_Favorite();
        post_favorite.date = newPostFavorite.getDate();
        post_favorite.postID = newPostFavorite.getPostID();
        post_favorite.userID = newPostFavorite.getUserID();
        post_favorite.serverID = newPostFavorite.getServerID();
        post_favorite.save();
    }

    public static void updatePost_Favorite(Post_Favorite post_favorite){
        Post_Favorite exPost_Favorite = getPost_Favorite(post_favorite.getServerID());
        exPost_Favorite.userID = post_favorite.getUserID();
        exPost_Favorite.postID = post_favorite.getPostID();
        exPost_Favorite.date = post_favorite.getDate();
        exPost_Favorite.save();
    }
    public static void deletePostFavorite(long postServerId, long userServerId){
        new Delete().from(Post_Favorite.class).where(Post_Favorite.FIELDS.COLUMN_POST_ID +
                " = ? and " + Post_Favorite.FIELDS.COLUMN_USER_ID + " = ?", postServerId, userServerId).execute();
    }
    public static void deletePostFavoriteByPost(long postServerId){//when post deleted
        new Delete().from(Post_Favorite.class).where(Post_Favorite.FIELDS.COLUMN_POST_ID +
                " = ? ", postServerId).execute();
    }

    public static Post_Favorite getPost_Favorite(long postFavoriteServerId){
        return new Select().from(Post_Favorite.class).where(Post_Favorite.FIELDS.COLUMN_SERVER_ID + " = ?", postFavoriteServerId)
                .executeSingle();
    }


    public static Post_Favorite getPost_FavoriteByPostId(long postId, long userId) {
        return new Select().from(Post_Favorite.class).where(Post_Favorite.FIELDS.COLUMN_POST_ID + " = ? and " + Post_Favorite.FIELDS.COLUMN_USER_ID + " = ?", postId, userId)
                .executeSingle();
    }
    public static ArrayList<Post_Favorite> getAllUserPostFavorite(long userServerId){
        List<Post_Favorite> post_favorites = new Select().from(Post_Favorite.class).where(Post_Favorite.FIELDS.COLUMN_USER_ID + " = ?", userServerId)
                .orderBy(Post_Favorite.FIELDS.COLUMN_SERVER_ID).execute();
//        ArrayList<Post_Favorite> net_post_favorites = new ArrayList<>();
//        //to remove all favorite posts in deleted category for this user
//        for(Post_Favorite post_favorite : post_favorites){
//            if(User_CategoriesDAO.getUserCategory(post_favorite.getUserID(),
//                    PostsDAO.getPost(post_favorite.getPostID()).getCategoryID())!=null)
//                net_post_favorites.add(post_favorite);
//            else
//                deletePostFavorite(post_favorite.getServerID(), userServerId);
//        }
        return new ArrayList<>(post_favorites);
    }

    public static void deleteAllUserPostFavorite(long userServerId){
        new Delete().from(Post_Favorite.class).where(Post_Favorite.FIELDS.COLUMN_USER_ID + " = ?", userServerId).execute();
    }

    private static boolean isExist(long serverID) {
        return getPost_Favorite(serverID) != null ;
    }

}
