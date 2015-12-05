package com.orchidatech.askandanswer.Database.DAO;

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Users;

import java.util.List;

/**
 * Created by Bahaa on 12/11/2015.
 */
public class UsersDAO {

    public static void addUser(Users newUser){
        if(isExist(newUser.getServerID())){
            updateUser(newUser);
            return;
        }
        Users user = new Users();
        user.fname = newUser.getFname();
        user.lname = newUser.getLname();
        user.image = newUser.getImage();
        user.email = newUser.getEmail();
        user.username = newUser.getUsername();
        user.serverID = newUser.getServerID();
        user.active = newUser.getActive();
        user.code = newUser.getCode();
        user.creationDate = newUser.getCreationDate();
        user.isPublicProfile = newUser.getIsPublicProfile();
        user.lastLogin = newUser.getLastLogin();
        user.mobile = newUser.getMobile();
        user.password = newUser.getPassword();
        if(newUser.answers != -1){
            user.answers = newUser.getAnswers();
            user.asks = newUser.getAsks();
        }
        user.save();
    }

    public static void deleteUser(long UsererverId){
        new Delete().from(Users.class).where(Users.FIELDS.COLUMN_SERVER_ID + " = ?", UsererverId).execute();
    }

    public static Users getUser(long UsererverId){
        return new Select().from(Users.class).where(Users.FIELDS.COLUMN_SERVER_ID + " = ?", UsererverId).executeSingle();
    }

    public static void updateUser(Users user){
        Users existUser = getUser(user.getServerID());
        existUser.fname = user.getFname();
        existUser.lname = user.getLname();
        existUser.image = user.getImage();
        existUser.email = user.getEmail();
        existUser.username = user.getUsername();
        existUser.active = user.getActive();
        existUser.code = user.getCode();
        existUser.creationDate = user.getCreationDate();
        existUser.isPublicProfile = user.getIsPublicProfile();
        existUser.lastLogin = user.getLastLogin();
        existUser.mobile = user.getMobile();
        existUser.password = user.getPassword();
        if(user.answers != -1){
            existUser.answers = user.getAnswers();
            existUser.asks = user.getAsks();
        }
        existUser.save();
    }

    public static List<Users> getAllUser(){
        return new Select().from(Users.class).orderBy(Users.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static void deleteAllUser(){
        new Delete().from(Users.class).execute();
    }

    private static boolean isExist(long serverID) {
        return getUser(serverID) != null ;
    }

}
