package com.orchidatech.askandanswer.Constant;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.User_ActionsDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Comments;
import com.orchidatech.askandanswer.Database.Model.Notifications;
import com.orchidatech.askandanswer.Database.Model.Post_Favorite;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.User_Actions;
import com.orchidatech.askandanswer.Database.Model.User_Categories;
import com.orchidatech.askandanswer.Database.Model.Users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bahaa on 7/11/2015.
 */
public class GNLConstants {

    public static final int POST_LIMIT = 5;
    public static final int MAX_POSTS_ROWS = 100;
    public static final int MAX_COMMENTS_ROWS = 100;
    public static final int COMMENTS_LIMIT = 10;
    public static final int MAX_NUMBER_REQUESTS_A_TIME = 10;
    public static final int MAX_IMAGE_LOADER_CACH_SIZE = 10 * 1024 * 1024;
    public static final String SENDER_ID = "602645926253"
            ;


    public final static class Settings_Keys {
        public static String NOTIFICATIONS_SOUND = "notificationAndSound";
    }

    public final static class SharedPreference {
        public static final String SHARED_PREF_NAME = "AskAndAnswerPref";
        public static final String FIRST_TIME_KEY = "FIRST_TIME";
        public static final String ID_KEY = "ID";
        public static final String PASSWORD_KEY = "PASSWORD";
        public static final String IS_LOGGED_KEY = "IS_LOGGED";
        public static final String IS_USER_SELECTED_CATEGORIES_KEY = "IS_CATEGORY_SELECTED";
        public static final String USER_KEY = "USER";
        public static final String LOGIN_TYPE = "LOGIN_TYPE";
        public static final String REG_ID = "REGITRATION_ID";
    }

    public final static class DateConversion {
        final static String MONTHS[] = new String[]{"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

        public static String getDate(long milliSeconds) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            String fullDate = MONTHS[calendar.get(Calendar.MONTH)] + " " + buildValueOf(calendar.get(Calendar.DAY_OF_MONTH)) + ", " + calendar.get(Calendar.YEAR);
            return fullDate;
        }

        private static String buildValueOf(int value) {
            return value >= 10 ? String.valueOf(value) : "0" + String.valueOf(value);
        }
    }

    public static String getStatus(int code){
        String message = "";
        switch (code){
            case 100:
                message = "Please check your internet connection";
                break;
            case 200:
                message = "The operation has successfully";
                break;
            case 300:
                message = "The operation failed";
                break;
            case 400:
                message = "Validator fails";
                break;
            case 500:
                message = "Input Error";
                break;
            case 301:
                message = "Couldn't be saved image";
                break;
            case 303:
                message = "User already exist";
                break;
            case 305:
                message = "User does not exist";
                break;
            case 307:
                message = "No search results";
                break;
            case 401:
                message = "Your category doesn't numeric array !";
                break;
            case 501:
                message = "There is no input category !";
                break;
            case 404:
                message = "There is no comment on this post !";
                break;
            case 402:
                message = "There is no post";
                break;
            case 403:
                message = "There is no favorite posts";
                break;
            case 406:
                message = "Can't found Categories for this user";
                break;
            case 408:
                message = "Can't found comment Action";
                break;
            case 505:
                message = "Can't found this comment";
                break;
            case 503:
                message = "Can't found this post with this id";
                break;
            case 504:
                message = "Can't found  User with this id !";
                break;
            case 700:
                message = "Can't add  this search to search history";
                break;
            case 506:
                message = "Can't found  category with this id !";
                break;
            case 900:
                message = "No comments found!";
                break;
            default:
                message = "An error occurred";
                break;
        }
        return message;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    public static class Parser{
        public static Users getUser(JSONObject user_json){
            try {
                long id = Long.parseLong(user_json.getString("id"));
                String f_name = user_json.getString("f_name");
                String l_name = user_json.getString("l_name");
                String email = user_json.getString("email");
                String image = user_json.getString("image");
                int active = Integer.parseInt(user_json.getString("active"));
                long created_at = user_json.getLong("created_at");
                long last_login = user_json.getLong("last_login");
                String code = user_json.getString("code");
                String mobile = user_json.getString("mobile");
                int is_public = Integer.parseInt(user_json.getString("is_public"));
                JSONObject askandanswer = user_json.getJSONObject("askandanswer");
                int no_asks = askandanswer.getInt("no_ask");
                int no_answers = askandanswer.getInt("no_answer");
                float rating = Float.parseFloat(askandanswer.get("no_of_stars") + "");
                Users user = new Users(id, f_name, l_name, null, email, null, image.equals("null") ? null : image, created_at, active, last_login,
                        mobile, is_public, code, no_answers, no_asks, rating);
                return user;
            } catch (JSONException e) {
                    return null;
            }

        }
        public static Posts getPost(JSONObject post_json){
            try {
                long id = Long.parseLong(post_json.getString("id"));
                long user_id = Long.parseLong(post_json.getString("user_id"));
                String text = post_json.getString("text");
                String image = post_json.getString("image");
                int is_hidden = Integer.parseInt(post_json.getString("is_hidden"));
                long category_id = Long.parseLong(post_json.getString("category_id"));
                int comments_no = post_json.getInt("comment_no");
                long created_at = post_json.getLong("updated_at");
                int isFavorite = post_json.getBoolean("is_postfavorite") ? 1 : 0;
                Posts post = new Posts(id, text, image.equals("null") ? null : image, created_at, user_id, category_id, is_hidden, comments_no, isFavorite, -1, -1);
                return post;
            } catch (JSONException e) {
                return null;
            }

        }
        public static Comments getComment(JSONObject comment_json){
            try {
                long comment_id = Long.parseLong(comment_json.getString("id"));
                String comment_text = comment_json.getString("comment");
                String comment_image = comment_json.getString("image");
                long user_id = Long.parseLong(comment_json.getString("user_id"));
                long post_id = Long.parseLong(comment_json.getString("post_id"));
                long comment_date = comment_json.getLong("updated_at");
                JSONObject actions = comment_json.getJSONObject("action");
                int likes = actions.getInt("like");
                int dislikes = actions.getInt("dislike");
                Comments comment = new Comments(comment_id, comment_text, comment_image.equals("null") ? null : comment_image, comment_date, user_id, post_id, likes, dislikes);
                return comment;
            } catch (JSONException e) {
                return null;
            }

        }
        public static Category getCategory(JSONObject category_json){
            try {
                Category category = new Category(Long.parseLong(category_json.getString("id")), category_json.getString("name"),
                        category_json.getString("description"));
                return category;
            } catch (JSONException e) {
                return null;
            }
        }
        public static Notifications getNotification(JSONObject notification_json){
            try {
                int notification_is_done = Integer.parseInt(notification_json.getString("is_done"));//is_done = 0 ==> false
                long notification_date = notification_json.getLong("created_at");
                long notification_id = Long.parseLong(notification_json.getString("id"));
                String notification_text = notification_json.getString("text");
                int notification_type = Integer.parseInt(notification_json.getString("type"));
                long notification_object_id = Long.parseLong(notification_json.getString("object_id"));
                Notifications notification = new Notifications(notification_id, notification_type, notification_object_id,
                        notification_text, notification_date, notification_is_done);
                return notification;
            } catch (JSONException e) {
                return null;
            }
        }
        public static Post_Favorite getPost_Favorite(JSONObject post_favorite_json){
            try {
                long id = Long.parseLong(post_favorite_json.getString("id"));
                long post_id = Long.parseLong(post_favorite_json.getString("post_id"));
                long user_id = Long.parseLong(post_favorite_json.getString("user_id"));
                long date = post_favorite_json.getLong("updated_at");
                Post_Favorite post_favorite = new Post_Favorite(id, post_id, user_id, date);
                return post_favorite;

            } catch (JSONException e) {
                return null;
            }
        }
        public static User_Actions getUser_Actions(JSONObject user_action_json){
            try {
                long user_action_id = Long.parseLong(user_action_json.getString("id"));
                long user_action_comment = Long.parseLong(user_action_json.getString("comment_id"));
                long user_action_user = Long.parseLong(user_action_json.getString("user_id"));
                int user_action_action = user_action_json.optInt("action_type");
                User_Actions user_actions = new User_Actions(user_action_id, user_action_comment, user_action_user, System.currentTimeMillis(), user_action_action);
                  return user_actions;

            } catch (JSONException e) {
                return null;
            }
        }
        public static User_Categories getUser_Category(JSONObject user_category_json){
            try {
                long user_category_id = user_category_json.getLong("id");
                long user_id = user_category_json.getLong("user_id");
                long category_id = user_category_json.getLong("category_id");
                JSONObject category_info = user_category_json.getJSONObject("category_info");
                CategoriesDAO.addCategory(new Category(category_info.getLong("id"), category_info.getString("name"), category_info.getString("description")));
                User_Categories user_category = new User_Categories(user_category_id, user_id, category_id);
                return user_category;
            } catch (JSONException e) {
                return null;
            }

        }
    }

}
