package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Categories;

import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class CategoriesDAO {

    public static void addCategory(Categories newCategory){
        newCategory.save();
    }

    public static void deleteCategory(long categoryServerId){
        new Delete().from(Categories.class).where(Categories.FIELDS.COLUMN_SERVER_ID + " = ?", categoryServerId).execute();
    }

    public static Categories getCategory(long categoryServerId){
        return new Select().from(Categories.class).where(Categories.FIELDS.COLUMN_SERVER_ID + " = ? "
                , categoryServerId).executeSingle();
    }

    public static void updateCategory(Categories category){
        Categories existCategory = getCategory(category.getServerID());
        existCategory.setDescription(category.getDescription());
        existCategory.setName(category.getName());
        existCategory.save();
    }

    public static List<Categories> getAllCategories(){
        return new Select().from(Categories.class).orderBy(Categories.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static void deleteAllCategories(){
        new Delete().from(Categories.class).execute();
    }
}
