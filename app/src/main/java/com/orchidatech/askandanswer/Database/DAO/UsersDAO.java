package com.orchidatech.askandanswer.Database.DAO;

import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.User;

import java.util.List;

/**
 * Created by Bahaa on 12/11/2015.
 */
public class UsersDAO {

    public static void addUser(User newUser){
        User user = new User();
        user.fname = newUser.getFname();
        user.lname = newUser.getLname();
        user.image = newUser.getImage();
        user.email = newUser.getEmail();
        user.username = newUser.getUsername();
        user.serverID = newUser.getServerID();

        Log.i("fd", user.save()+"");
    }

    public static void deleteUser(long UsererverId){
        new Delete().from(User.class).where(User.FIELDS.COLUMN_SERVER_ID + " = ?", UsererverId).execute();
    }

    public static User getUser(long UsererverId){
        return new Select().from(User.class).where(User.FIELDS.COLUMN_SERVER_ID + " = ?", UsererverId).executeSingle();
    }

    public static void updateUser(User user){
        User existUser = getUser(user.getServerID());
        existUser.setFname(user.getFname());
        existUser.setLname(user.getLname());
        existUser.setEmail(user.getEmail());
        existUser.setPassword(user.getPassword());
        existUser.setImage(user.getImage());
        existUser.setCreationDate(user.getCreationDate());
        existUser.setActive(user.getActive());
        existUser.setLastLogin(user.getLastLogin());
        existUser.setMobile(user.getMobile());
        existUser.setIsPublicProfile(user.getIsPublicProfile());
        existUser.setCode(user.getCode());
        existUser.save();
    }

    public static List<User> getAllUser(){
        return new Select().from(User.class).orderBy(User.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static void deleteAllUser(){
        new Delete().from(User.class).execute();
    }
}
