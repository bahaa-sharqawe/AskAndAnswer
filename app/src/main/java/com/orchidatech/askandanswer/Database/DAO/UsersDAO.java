package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Users;

import java.util.List;

/**
 * Created by Bahaa on 12/11/2015.
 */
public class UsersDAO {

    public static void addUser(Users newUser){
        newUser.save();
    }

    public static void deleteUser(long userServerId){
        new Delete().from(Users.class).where(Users.FIELDS.COLUMN_SERVER_ID + " = ?", userServerId).execute();
    }

    public static Users getUser(long userServerId){
        return new Select ().from(Users.class).where(Users.FIELDS.COLUMN_SERVER_ID + " = ?", userServerId).executeSingle();
    }

    public static void updateUser(Users user){
        Users existUser = getUser(user.getServerID());
        existUser.setUsername(user.getUsername());
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

    public static List<Users> getAllUsers(){
        return new Select().from(Users.class).orderBy(Users.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static void deleteAllUsers(){
        new Delete().from(Users.class).execute();
    }
}
