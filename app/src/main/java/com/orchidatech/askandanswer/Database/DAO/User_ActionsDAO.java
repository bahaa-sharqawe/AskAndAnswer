package com.orchidatech.askandanswer.Database.DAO;

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.User_Actions;

import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class User_ActionsDAO {

    public static void addUserAction(User_Actions newUserAction) {
        if(isExist(newUserAction.getServerID())){
            updateUserAction(newUserAction);
            Log.i("xcxcvc", "exist" + ", " + newUserAction.getActionType());
            return;
        }
        User_Actions user_actions = new User_Actions();
        user_actions.actionType = newUserAction.getActionType();
        user_actions.commentID = newUserAction.getCommentID();
        user_actions.date = newUserAction.getDate();
        user_actions.serverID = newUserAction.getServerID();
        user_actions.userID = newUserAction.getUserID();
        user_actions.save();
    }

    public static User_Actions getUserAction(long userServerId, long commentServerId) {
        return new Select().from(User_Actions.class).where(User_Actions.FIELDS.COLUMN_USER_ID + " = ? and " +
                User_Actions.FIELDS.COLUMN_COMMENT_ID + " = ?", userServerId, commentServerId).executeSingle();
    }

    private static User_Actions getUserAction(long serverID) {
        return new Select().from(User_Actions.class).where(User_Actions.FIELDS.COLUMN_SERVER_ID + " = ?", serverID).executeSingle();
    }

    public static void updateUserAction(User_Actions userAction) {
        User_Actions existUserAction = getUserAction(userAction.getServerID());
        existUserAction.userID = userAction.getUserID();
        existUserAction.date = userAction.getDate();
        existUserAction.actionType = userAction.getActionType();
        existUserAction.commentID = userAction.getCommentID();
        existUserAction.save();
    }

    public static List<User_Actions> getAllUserActions(long userServerId) {
        return new Select().from(User_Actions.class).where(User_Actions.FIELDS.COLUMN_USER_ID + " = ?", userServerId)
                .orderBy(User_Actions.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static List<User_Actions> getAllUserActions(long userServerId, int userActionType) {
        return new Select().from(User_Actions.class).where(User_Actions.FIELDS.COLUMN_USER_ID + " = ? and " +
                User_Actions.FIELDS.ACTION_TYPE + " = ?", userServerId, userActionType)
                .orderBy(User_Actions.FIELDS.COLUMN_SERVER_ID).execute();
    }

    private static boolean isExist(long serverID) {
        return getUserAction(serverID) != null ;
    }


    public static void delteActionByComment(long commentId) {
        new Delete().from(User_Actions.class).where(User_Actions.FIELDS.COLUMN_COMMENT_ID + " = ?", commentId).execute();

    }
}
