package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Comments;
import com.orchidatech.askandanswer.Database.Model.Settings;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class SettingsDAO {

    public static void addSettings(Settings newSettings){
        Settings settings = new Settings();
        settings.key = newSettings.getKey();
        settings.value = newSettings.getValue();
        settings.serverID = newSettings.getServerID();
        settings.save();
    }
    public static Settings getSetting(long userId, String settingKey){
        return new Select().from(Settings.class).where(Settings.FIELDS.COLUMN_SETTING_KEY + " = ? and " + Settings.FIELDS.COLUMN_USER_ID + " = ?",
                userId,settingKey).executeSingle();

    }
    public static void updateSettings(Settings settings){
        Settings existSetting = getSetting(settings.getUserID(), settings.getKey());
        existSetting.value = settings.getValue();
        existSetting.save();
    }
}
