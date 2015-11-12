package com.orchidatech.askandanswer.Database.DAO;

import android.provider.BaseColumns;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.User;

import java.util.List;

/**
 * Created by Bahaa on 12/11/2015.
 */
public class UserDAO {
    public static final String TABLE_NAME = "Users";
    public static class FIELDS{
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_EMAIL = "Email";
        public static final String COLUMN_REMOTE_ID = "RemoteId";
    }
    public static void addUser(User newUser){
        newUser.save();
    }
    public static void deleteUser(long remoteUserId){
        new Delete().from(User.class).where(FIELDS.COLUMN_REMOTE_ID + " = ?", remoteUserId).execute();
    }
    public static User getUser(long remoteUserId){
        return new Select ().from(User.class).where(FIELDS.COLUMN_REMOTE_ID + " = ?", remoteUserId).executeSingle();
    }
    public static void updateUser(User user){
        User existUser = getUser(user.getRemoteID());
        existUser.setName(user.getName());
        existUser.setEmail(user.getEmail());
        existUser.save();
    }
    public static List<User> getAllUsers(){
        return new Select().from(User.class).orderBy(FIELDS.COLUMN_REMOTE_ID).execute();
    }
    public static void deleteAllUsers(){
        new Delete().from(User.class).execute();
    }
}
