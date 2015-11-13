package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.User_Actions;

import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class User_ActionsDAO {

    public static void addUserAction(User_Actions newUserAction){
        newUserAction.save();
    }

    public static User_Actions getUserAction(long userServerId, long commentServerId){
        return new Select().from(User_Actions.class).where(User_Actions.FIELDS.COLUMN_USER_ID + " = ? and " +
                User_Actions.FIELDS.COLUMN_COMMENT_ID + " = ?", userServerId, commentServerId).executeSingle();
    }

    public static void updateUserAction(User_Actions userAction){
        User_Actions existUserAction = getUserAction(userAction.getUserID(), userAction.getCommentID());

        existUserAction.setDate(userAction.getDate());
        existUserAction.setActionType(userAction.getActionType());
        existUserAction.save();
    }

    public static List<User_Actions> getAllUserActions(long userServerId){
        return new Select().from(User_Actions.class).where(User_Actions.FIELDS.COLUMN_USER_ID + " = ?", userServerId)
                .orderBy(User_Actions.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static List<User_Actions> getAllUserActions(long userServerId, int userActionType){
        return new Select().from(User_Actions.class).where(User_Actions.FIELDS.COLUMN_USER_ID + " = ? and " +
                User_Actions.FIELDS.ACTION_TYPE + " = ?", userServerId, userActionType)
                .orderBy(User_Actions.FIELDS.COLUMN_SERVER_ID).execute();
    }

}
