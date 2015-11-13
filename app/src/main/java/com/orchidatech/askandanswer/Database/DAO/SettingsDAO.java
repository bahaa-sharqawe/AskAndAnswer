package com.orchidatech.askandanswer.Database.DAO;

import com.activeandroid.query.Select;
import com.orchidatech.askandanswer.Database.Model.Comments;
import com.orchidatech.askandanswer.Database.Model.Settings;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class SettingsDAO {

    public static void addSettings(Settings newSettings){
        newSettings.save();
    }
    public static Settings getSetting(String settingKey){
        return new Select().from(Settings.class).where(Settings.FIELDS.COLUMN_SETTING_KEY + " = ?",
                settingKey).executeSingle();

    }
    public static void updateSettings(Settings settings){
        Settings existSetting = getSetting(settings.getKey());
        existSetting.setValue(settings.getValue());
        existSetting.save();
    }
}
