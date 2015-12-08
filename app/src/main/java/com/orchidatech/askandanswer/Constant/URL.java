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
    public static final String GET_USER_INFO = SERVER_URL + WEB_SERVICES_PATH + "user/user_info";
    public static final String GET_CATEGORIES = SERVER_URL + WEB_SERVICES_PATH + "cat/all-categories";
    public static final String UPDATE_PROFILE = SERVER_URL + WEB_SERVICES_PATH + "user/update-profile";
    public static final String UPDATE_PASSWORD = SERVER_URL + WEB_SERVICES_PATH + "user/forget-password";
    public static final String SEND_USER_CATEGORIES = SERVER_URL + WEB_SERVICES_PATH + "usercat/add-user-category";
    public static final String GET_USER_CATEGORIES = SERVER_URL + WEB_SERVICES_PATH + "usercat/user-categories";
    public static final String UPDATE_USER_CATEGORIES = SERVER_URL + WEB_SERVICES_PATH + "usercat/edit-user-category";

    public static final String GET_USER_POSTS = SERVER_URL + WEB_SERVICES_PATH + "post/userpost";
    public static final String EDIT_POST = SERVER_URL + WEB_SERVICES_PATH + "post/edit-post";
    public static final String ADD_POST = SERVER_URL + WEB_SERVICES_PATH + "post/add-post";
    public static final String DELETE_POST = SERVER_URL + WEB_SERVICES_PATH + "post/delete-post";
    public static final String GET_TIME_LINE = SERVER_URL + WEB_SERVICES_PATH + "post/newestpost";
    public static final String SEARCH = SERVER_URL + WEB_SERVICES_PATH + "post/searchuserpostall";
    ;
    public static String GET_Category_POSTS = SERVER_URL + WEB_SERVICES_PATH + "post/postincategory";
    public static final String ADD_POST_FAVORITE = SERVER_URL + WEB_SERVICES_PATH + "postFav/add-post-favorite";
    public static final String GET_USER_FAV_POSTS = SERVER_URL + WEB_SERVICES_PATH + "postFav/user-post-favorite";
    public static final String REMOVE_POST_FAVORITE = SERVER_URL + WEB_SERVICES_PATH + "postFav/delete-post-favorite";
    public static final String ADD_COMMENT = "http://softplaystore.com/askandanswer_ws/public/comment/add-comment";
    public static String GET_USER_Comments = SERVER_URL + WEB_SERVICES_PATH + "comment/usercomment";
    public static String GET_POST_Comments = SERVER_URL + WEB_SERVICES_PATH + "comment/post-comment";
    public static final String DELETE_COMMENT = SERVER_URL + WEB_SERVICES_PATH + "comment/delete-comment";
    public static String ADD_ACTION = SERVER_URL + WEB_SERVICES_PATH + "commentAction/add-comment-action";
    public static final String CONTACT_US = SERVER_URL + WEB_SERVICES_PATH + "contactus/add-massage";

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
        public static final String CATEGORY_ID = "category_id";
        public static final String FILTER = "filter";
        public static final String COMMENT_ID = "comment_id";
        public static final String ACTION_TYPE = "action_type";
        public static final String MESSAGE = "massage";
        public static final String NEW_PASWORD = "new_password";
        public static final String CONFIRM_NEW_PASWORD = "confirm_password";
    }
}
