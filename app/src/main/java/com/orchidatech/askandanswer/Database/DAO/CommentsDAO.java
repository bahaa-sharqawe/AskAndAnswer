package com.orchidatech.askandanswer.Database.DAO;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Database.Model.Comments;
import com.orchidatech.askandanswer.Database.Model.Posts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class CommentsDAO {

    public static void addComment(Comments newComment) {
        if (isExist(newComment.getServerID())) {
            updateComment(newComment);
            return;
        }
        Comments comment = new Comments();
        comment.date = newComment.getDate();
        comment.image = newComment.getImage();
        comment.postID = newComment.getPostID();
        comment.serverID = newComment.getServerID();
        comment.text = newComment.getText();
        comment.userID = newComment.getUserID();
        comment.likes = newComment.getLikes();
        comment.disLikes = newComment.getDisLikes();
        comment.save();
    }

    public static void deleteComment(long commentServerId) {
        new Delete().from(Comments.class).where(Comments.FIELDS.COLUMN_SERVER_ID + " = ?", commentServerId).execute();
    }

    public static void deleteCommentByPost(long postServerId) {//when post is deleted
        new Delete().from(Comments.class).where(Comments.FIELDS.COLUMN_POST_ID + " = ?", postServerId).execute();
    }

    public static Comments getComment(long commentServerId) {
        return new Select().from(Comments.class).where(Comments.FIELDS.COLUMN_SERVER_ID + " = ?",
                commentServerId).executeSingle();
    }

    public static void updateComment(Comments comment) {
        Comments existComment = getComment(comment.getServerID());
        existComment.date = comment.getDate();
        existComment.image = comment.getImage();
        existComment.text = comment.getText();
        existComment.userID = comment.getUserID();
        existComment.postID = comment.getPostID();
        existComment.likes = comment.getLikes();
        existComment.disLikes = comment.getDisLikes();
        existComment.save();
    }

    public static List<Comments> getAllComments(long userServerId, long postServerId) {
        return new Select().from(Comments.class).where(Comments.FIELDS.COLUMN_USER_ID + " = ?" + " and "
                + Comments.FIELDS.COLUMN_POST_ID + " = ?", userServerId, postServerId).execute();
    }

    public static List<Comments> getAllComments(long userServerId) {
        return new Select().from(Comments.class).where(Comments.FIELDS.COLUMN_USER_ID + " = ?", userServerId).execute();
    }

    public static List<Comments> getAllCommentsByPost(long postServerId) {
        return new Select().from(Comments.class).where(Comments.FIELDS.COLUMN_POST_ID + " = ?", postServerId).orderBy(Comments.FIELDS.COLUMN_DATE + " desc").execute();
    }

    private static boolean isExist(long serverID) {
        return getComment(serverID) != null;
    }

    public static void checkRowsCount() {
        ArrayList<Comments> allComments = new ArrayList<>(getAllComments());
        int count = allComments.size();
        Log.i("fdfdf", count + ", " + (count - GNLConstants.MAX_COMMENTS_ROWS));
        Collections.reverse(allComments);
        for(int i = 0; i < count-GNLConstants.MAX_COMMENTS_ROWS; i++) {
            deleteComment(allComments.get(i).getServerID());
            User_ActionsDAO.delteActionByComment(allComments.get(i).getServerID());
        }
    }

    private static List<Comments> getAllComments() {
        return new Select().from(Comments.class).orderBy(Comments.FIELDS.COLUMN_DATE + " desc").execute();

    }

    public static void deleteAllComments() {
        new Delete().from(Comments.class).execute();
    }
}
