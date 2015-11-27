package com.orchidatech.askandanswer.Constant;

/**
 * Created by Bahaa on 13/11/2015.
 */
/*
    This class will contains all webservice links
 */
public class URL {
    public static final String SERVER_URL = "http://softplaystore.com";
    public static final String WEB_SERVICES_PATH = "/askandanswer_ws/public/";
    public static final String DEFAULT_IMAGE = SERVER_URL + WEB_SERVICES_PATH + "users/default_image.jpg";

    public static final String LOGIN = SERVER_URL + WEB_SERVICES_PATH + "user/login";
    public static final String REGISTER = SERVER_URL + WEB_SERVICES_PATH + "user/reg-user";
    public static final String GET_USER_INFO = "user/user_info";
    public static final String GET_CATEGORIES = SERVER_URL + WEB_SERVICES_PATH + "cat/all-categories";
    public static final String UPDATE_PROFILE = SERVER_URL + WEB_SERVICES_PATH + "user/update_profile";
    public static final String SEND_USER_CATEGORIES = SERVER_URL + WEB_SERVICES_PATH + "usercat/add-user-category";
    public static final String GET_USER_POSTS = SERVER_URL + WEB_SERVICES_PATH + "post/userpost";
    public static final String EDIT_POST = "post/edit-post";
    public static final String ADD_POST = "post/add-post";
    public static final String DELETE_POST = "post/delete-post";
    public static final String GET_TIME_LINE = "post/newestpost";
    public static final String ADD_POST_FAVORITE = "postFav/add-post-favorite";
    public static final String GET_USER_FAV_POSTS = "postFav/userfavpost";
    public static final String REMOVE_POST_FAVORITE = "postFav/remove-post-favorite";
    public static final String SEARCH = "post/searchuserpostall";
    public static final String ADD_COMMENT = "comment/add-comment";
    public static String GET_USER_Comments = "";
    public static String GET_POST_Comments = "comment/post-comment";

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
