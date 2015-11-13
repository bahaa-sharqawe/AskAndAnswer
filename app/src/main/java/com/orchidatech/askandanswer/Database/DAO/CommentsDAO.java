package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Comments;

import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class CommentsDAO {

    public static void addComment(Comments newComment){
        newComment.save();
    }

    public static void deleteComment(long commentServerId){
        new Delete().from(Comments.class).where(Comments.FIELDS.COLUMN_SERVER_ID + " = ?", commentServerId).execute();
    }

    public static Comments getComment(long commentServerId){
        return new Select().from(Comments.class).where(Comments.FIELDS.COLUMN_SERVER_ID + " = ?",
                commentServerId).executeSingle();
    }

    public static void updateComment(Comments comment){
        Comments existComment = getComment(comment.getServerID());
        existComment.setText(comment.getText());
        existComment.setImage(comment.getImage());
        existComment.setDate(comment.getDate());
        existComment.setIsHidden(comment.getIsHidden());
        existComment.save();
    }

    public static List<Comments> getAllComments(long userServerId, long postServerId){
        return new Select().from(Comments.class).where(Comments.FIELDS.COLUMN_USER_ID + " = ?" + " and "
                + Comments.FIELDS.COLUMN_POST_ID + " = ?", userServerId, postServerId).execute();
    }
}
