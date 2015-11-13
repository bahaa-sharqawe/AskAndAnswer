package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Users;

import java.util.List;

/**
 * Created by Bahaa on 12/11/2015.
 */
public class UserDAO {

//    public static void addUser(Users newUsers){
//        newUsers.save();
//    }
//    public static void deleteUser(long remoteUserId){
//        new Delete().from(Users.class).where(FIELDS.COLUMN_REMOTE_ID + " = ?", remoteUserId).execute();
//    }
//    public static Users getUser(long remoteUserId){
//        return new Select ().from(Users.class).where(FIELDS.COLUMN_REMOTE_ID + " = ?", remoteUserId).executeSingle();
//    }
//    public static void updateUser(Users users){
//        Users existUsers = getUser(users.getRemoteID());
//        existUsers.setName(users.getName());
//        existUsers.setEmail(users.getEmail());
//        existUsers.save();
//    }
//    public static List<Users> getAllUsers(){
//        return new Select().from(Users.class).orderBy(FIELDS.COLUMN_REMOTE_ID).execute();
//    }
//    public static void deleteAllUsers(){
//        new Delete().from(Users.class).execute();
//    }
}
