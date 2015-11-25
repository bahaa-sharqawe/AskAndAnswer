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
    public static final String SERVER_URL = "http://softplaystore.com";
    public static final String WEB_SERVICES_PATH = "/askandanswer_ws/public/";
    public static final String DEFAULT_IMAGE = "";

    public static final String LOGIN = SERVER_URL + WEB_SERVICES_PATH + "user/login.php";
    public static final String REGISTER = SERVER_URL + WEB_SERVICES_PATH + "user/reg-user.php";
    public static final String GET_USER_INFO = "user/user_info.php";
    public static final String GET_CATEGORIES = SERVER_URL + WEB_SERVICES_PATH + "cat/all-categories.php";
    public static final String UPDATE_PROFILE = SERVER_URL + WEB_SERVICES_PATH + "user/update_profile.php";
    public static final String SEND_USER_CATEGORIES = SERVER_URL + WEB_SERVICES_PATH + "usercat/add-user-category.php";
    public static final String GET_USER_POSTS = SERVER_URL + WEB_SERVICES_PATH + "post/userpost.php";
    public static final String EDIT_POST = "post/edit-post.php";
    public static final String ADD_POST = "post/add-post.php";
    public static final String DELETE_POST = "post/delete-post.php";
    public static final String GET_TIME_LINE = "post/newestpost.php";
    public static final String ADD_POST_FAVORITE = "postFav/add-post-favorite.php";
    public static final String GET_USER_FAV_POSTS = "postFav/userfavpost.php";
    public static final String REMOVE_POST_FAVORITE = "postFav/remove-post-favorite.php";
    public static final String SEARCH = "post/searchuserpostall.php";
    public static final String ADD_COMMENT = "comment/add-comment.php";
    public static String GET_USER_Comments = "";
    public static String GET_POST_Comments = "comment/post-comment.php";

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
        public static final String COMMENT = "comment";
    }
}
