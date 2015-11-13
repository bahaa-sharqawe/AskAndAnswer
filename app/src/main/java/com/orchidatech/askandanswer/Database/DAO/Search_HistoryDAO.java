package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Search_History;

import java.util.List;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class Search_HistoryDAO {

    public static void addSearchHistory(Search_History newSearchHistory){
        newSearchHistory.save();
    }

    public static void deleteSearchHistory(long searchHistoryServerId, long userServerId){
        new Delete().from(Search_History.class).where(Search_History.FIELDS.COLUMN_SERVER_ID +
                " = ? and " + Search_History.FIELDS.COLUMN_USER_ID + " = ?", searchHistoryServerId, userServerId).execute();
    }

    public static Search_History getSearchHistory(long searchHistoryServerId){
        return new Select().from(Search_History.class).where(Search_History.FIELDS.COLUMN_SERVER_ID + " = ?", searchHistoryServerId)
                .executeSingle();
    }


    public static List<Search_History> getAllUserSearchHistory(long userServerId){
        return new Select().from(Search_History.class).where(Search_History.FIELDS.COLUMN_USER_ID + " = ?", userServerId)
                .orderBy(Search_History.FIELDS.COLUMN_SERVER_ID).execute();
    }

    public static void deleteAllUserSearch_History(long userServerId){
        new Delete().from(Search_History.class).where(Search_History.FIELDS.COLUMN_USER_ID + " = ?", userServerId).execute();
    }
}
