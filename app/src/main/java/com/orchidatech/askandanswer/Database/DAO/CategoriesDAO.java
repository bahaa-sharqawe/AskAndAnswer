package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Category;

import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class CategoriesDAO {

    public static void addCategory(Category newCategory){
        if(isExist(newCategory.getServerID())){
            updateCategory(newCategory);
            return;
        }
        Category category = new Category();
        category.name = newCategory.getName();
        category.serverID = newCategory.getServerID();
        category.description = newCategory.getDescription();
        category.save();
    }

    public static void deleteCategory(long categoryServerId){
        new Delete().from(Category.class).where(Category.FIELDS.COLUMN_SERVER_ID + " = ?", categoryServerId).execute();
    }

    public static Category getCategory(long categoryServerId){
        return new Select().from(Category.class).where(Category.FIELDS.COLUMN_SERVER_ID + " = ? "
                , categoryServerId).executeSingle();
    }

    public static void updateCategory(Category category){
        Category existCategory = getCategory(category.getServerID());
        existCategory.name = category.getName();
        existCategory.description = category.getDescription();
        existCategory.save();
    }

    public static List<Category> getAllCategories(){
        return new Select().from(Category.class).orderBy(Category.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static void deleteAllCategories(){
        new Delete().from(Category.class).execute();
    }
    private static boolean isExist(long serverID) {
        return getCategory(serverID) != null ;
    }
}
