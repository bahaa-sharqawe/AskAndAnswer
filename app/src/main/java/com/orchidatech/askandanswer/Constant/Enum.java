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
        LIKE(0),
        DISLIKE(1),
        NO_ACTIONS(2);
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
    public enum POSTS_FRAGMENTS {
        TIMELINE(0),
        MY_ASKS(1),
        PROFILE(2),
        CATEGORY_POST(3),
        MY_ANSWERS_POSTS(4);
        private int type;
        POSTS_FRAGMENTS(int i) {
            this.type = i;
        }
        public int getNumericType() {
            return type;
        }
    }
    public enum COMMENTS_FRAGMENTS {
        COMMENTS(0);
        private int type;
        COMMENTS_FRAGMENTS(int i) {
            this.type = i;
        }
        public int getNumericType() {
            return type;
        }
    }
    public enum LOGIN_TYPE {
        DEFAULT(0),
        FACEBOOK(1),
        GOOGLE(2);
        private int type;
        LOGIN_TYPE(int i) {
            this.type = i;
        }
        public int getNumericType() {
            return type;
        }
    }


}