package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.User_Categories;

import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class User_CategoriesDAO {

    public static void addUserCategory(User_Categories newUserCategory) {
        if(isExist(newUserCategory.getServerID())){
            updateUserCategory(newUserCategory);
            return;
        }

        User_Categories user_categories = new User_Categories();
        user_categories.categoryID = newUserCategory.getCategoryID();
        user_categories.serverID = newUserCategory.getServerID();
        user_categories.userID = newUserCategory.getUserID();
        user_categories.save();
    }

    private static void updateUserCategory(User_Categories newUserCategory) {
        User_Categories exUser_Categories = getUserCategory(newUserCategory.getServerID());
        exUser_Categories.userID = newUserCategory.getUserID();
        exUser_Categories.categoryID = newUserCategory.getCategoryID();
        exUser_Categories.save();
    }

    public static void deleteUserCategory(long userCategoryServerId, long userServerId) {
        new Delete().from(User_Categories.class).where(User_Categories.FIELDS.COLUMN_SERVER_ID +
                " = ? and " + User_Categories.FIELDS.COLUMN_USER_ID + " = ?", userCategoryServerId, userServerId).execute();
    }

    public static User_Categories getUserCategory(long userCategoryServerId) {
        return new Select().from(User_Categories.class).where(User_Categories.FIELDS.COLUMN_SERVER_ID + " = ?", userCategoryServerId)
                .executeSingle();
    }

    public static User_Categories getUserCategory(long userId, long categoryId) {
        return new Select().from(User_Categories.class).where(User_Categories.FIELDS.COLUMN_CATEGORY_ID + " = ? and " +
                User_Categories.FIELDS.COLUMN_USER_ID + " = ?",categoryId, userId)
                .executeSingle();
    }



    public static List<User_Categories> getAllUserCategories(long userServerId) {
        return new Select().from(User_Categories.class).where(User_Categories.FIELDS.COLUMN_USER_ID + " = ?", userServerId).execute();
    }

    public static void deleteAllUserCategories(long userServerId) {
        new Delete().from(User_Categories.class).where(User_Categories.FIELDS.COLUMN_USER_ID + " = ?", userServerId).execute();
    }

    private static boolean isExist(long serverID) {
        return (getUserCategory(serverID)!=null) ;
    }

}
