package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Comments;
import com.orchidatech.askandanswer.Database.Model.Settings;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class SettingsDAO {

    public static void addSettings(Settings newSettings){
        if(isExist(newSettings.getServerID())){
            updateSettings(newSettings);
            return;
        }
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

    public static Settings getSetting(long serverId){
        return new Select().from(Settings.class).where(Settings.FIELDS.COLUMN_SERVER_ID + " = ?", serverId).executeSingle();
    }

    public static void updateSettings(long userId, String settingKey,  int settingValue){
        Settings existSetting = getSetting(userId, settingKey);
        existSetting.value = settingValue;
        existSetting.save();
    }

    private static void updateSettings(Settings settings) {
        Settings existSetting = getSetting(settings.getServerID());
        existSetting.value = settings.getValue();
        existSetting.key = settings.getKey();
        existSetting.userID = settings.getUserID();
        existSetting.save();

    }

    private static boolean isExist(long serverID) {
        return getSetting(serverID) != null ;
    }
}
