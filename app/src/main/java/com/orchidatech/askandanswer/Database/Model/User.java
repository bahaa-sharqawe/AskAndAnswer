package com.orchidatech.askandanswer.Database.Model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.orchidatech.askandanswer.Database.DAO.UserDAO;

import static com.orchidatech.askandanswer.Database.DAO.UserDAO.FIELDS.*;

/**
 * Created by Bahaa on 12/11/2015.
 */
@Table(name = UserDAO.TABLE_NAME, id = BaseColumns._ID)
public class User extends Model{

    @Column(name = COLUMN_REMOTE_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long remoteID;

    @Column(name = COLUMN_NAME)
    private String name;

    @Column(name = COLUMN_EMAIL)
    private String email;

    public User() {
        super();
    }

    public User(long remoteID, String name, String email) {
        this.remoteID = remoteID;
        this.name = name;
        this.email = email;
    }

    public long getRemoteID() {
        return remoteID;
    }

    public void setRemoteID(long remoteID) {
        this.remoteID = remoteID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
