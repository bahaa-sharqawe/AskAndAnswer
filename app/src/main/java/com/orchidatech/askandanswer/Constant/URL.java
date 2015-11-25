package com.orchidatech.askandanswer.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bahaa on 13/11/2015.
 */
/*
    This class will contains all webservice links
 */
public class URL {
    public static final String SERVER_URL = "http://orchidatech.com/";
    public static final String WEB_SERVICES_PATH = "sharearide/web-services/";

    public static final String LOGIN = SERVER_URL + WEB_SERVICES_PATH + "login.php";
    public static final String GET_USER_POSTS = SERVER_URL + WEB_SERVICES_PATH + "userpost.php";
    public static final String GET_CATEGORIES = SERVER_URL + WEB_SERVICES_PATH + "all-categories.php";
    public static final String REGISTER = SERVER_URL + WEB_SERVICES_PATH + "reg-user.php";
    public static final String UPDATE_PROFILE = SERVER_URL + WEB_SERVICES_PATH + "update_profile.php";
    public static final String SEND_USER_CATEGORIES = SERVER_URL + WEB_SERVICES_PATH + "add-user-category.php";
    public static final String DEFAULT_IMAGE = "";
    public static final String ADD_POST_FAVORITE = "add-post-favorite.php";
    public static final String EDIT_POST = "edit-post.php";
    public static final String ADD_POST = "add-post.php";
    public static final String DELETE_POST = "delete-post.php";
    public static final String GET_USER_FAV_POSTS = "userfavpost.php";
    public static final String REMOVE_POST_FAVORITE = "remove-post-favorite.php";
    public static final String GET_USER_INFO = "user_id.php";
    public static final String GET_TIME_LINE = "newestpost.php";
    public static final String SEARCH = "";
    public static String GET_USER_Comments = "";
    public static String GET_POST_Comments = "";

    public class URLParameters {
        public static final String ID = "id";
        public static final String USERNAME = "username";
        public static final String FNAME = "f_name";
        public static final String LNAME = "l_name";
        public static final String PASSWORD = "password";
        public static final String EMAIL = "email";
        public static final String IMAGE = "image";
        public static final String CATEGORIES_ID = "categories_id";
        public static final String ACTIVE = "active";
        public static final String LAST_LOGIN = "last_login";
        public static final String MOBILE = "mobile";
        public static final String IS_PUBLIC = "is_public";
        public static final String USER_ID = "user_id";
        public static final String LIMIT = "limit";
        public static final String OFFSET = "offset";
        public static final String LAST_ID = "last_id";
        public static final String POST_ID = "post_id";
        public static final String TEXT = "text";
        public static final String DATE = "date";
        public static final String IS_HIDDEN = "is_hidden";
    }

}
