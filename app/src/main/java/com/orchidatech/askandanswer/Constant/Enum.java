package com.orchidatech.askandanswer.Constant;

/**
 * Created by Bahaa on 13/11/2015.
 */
public final class Enum {

    public enum NOTIFICATIONS {//Enum.NOTIFICATIONS.NEW_POST_ADDED.getNumericType();
        NEW_POST_ADDED(0),
        NEW_MENTION(1),
        NEW_COMMENT(2);
        private int type;

        NOTIFICATIONS(int i) {
            this.type = i;
        }

        public int getNumericType() {
            return type;
        }
    }
    public enum USER_ACTIONS {//Enum.USER_ACTIONS.LIKE.getNumericType();
        NO_ACTIONS(0),
        LIKE(1),
        DISLIKE(2);
        private int type;
        USER_ACTIONS(int i) {
            this.type = i;
        }

        public int getNumericType() {
            return type;
        }
    }
    public enum NOTIFICATION_SETTINGS{
        ON(0),
        OFF(1);
        private int type;
        NOTIFICATION_SETTINGS(int i){
            this.type = i;
        }
        public int getNumericType() {
            return type;
        }

    }

}