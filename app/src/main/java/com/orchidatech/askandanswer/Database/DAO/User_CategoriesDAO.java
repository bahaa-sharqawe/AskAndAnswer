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
        newUserCategory.save();
    }

    public static void deleteUserCategory(long userCategoryServerId, long userServerId) {
        new Delete().from(User_Categories.class).where(User_Categories.FIELDS.COLUMN_SERVER_ID +
                " = ? and " + User_Categories.FIELDS.COLUMN_USER_ID + " = ?", userCategoryServerId, userServerId).execute();
    }

    public static User_Categories getUserCategory(long userCategoryServerId) {
        return new Select().from(User_Categories.class).where(User_Categories.FIELDS.COLUMN_SERVER_ID + " = ?", userCategoryServerId)
                .executeSingle();
    }


    public static List<User_Categories> getAllUserCategories(long userServerId) {
        return new Select().from(User_Categories.class).where(User_Categories.FIELDS.COLUMN_USER_ID + " = ?", userServerId)
                .orderBy(User_Categories.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static void deleteAllUserCategories(long userServerId) {
        new Delete().from(User_Categories.class).where(User_Categories.FIELDS.COLUMN_USER_ID + " = ?", userServerId).execute();
    }
}
