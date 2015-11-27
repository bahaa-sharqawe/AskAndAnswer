package com.orchidatech.askandanswer.WebService;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.orchidatech.askandanswer.Constant.*;
import com.orchidatech.askandanswer.Constant.Enum;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.Database.DAO.Post_FavoriteDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.User_ActionsDAO;
import com.orchidatech.askandanswer.Database.DAO.User_CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Comments;
import com.orchidatech.askandanswer.Database.Model.Post_Favorite;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.User_Actions;
import com.orchidatech.askandanswer.Database.Model.User_Categories;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnAddPostListener;
import com.orchidatech.askandanswer.View.Interface.OnCategoriesFetchedListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentAddListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentFetchListener;
import com.orchidatech.askandanswer.View.Interface.OnEditPostListener;
import com.orchidatech.askandanswer.View.Interface.OnLoadFinished;
import com.orchidatech.askandanswer.View.Interface.OnLoginListener;
import com.orchidatech.askandanswer.View.Interface.OnPostDeletedListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Interface.OnRegisterListener;
import com.orchidatech.askandanswer.View.Interface.OnSearchCompleted;
import com.orchidatech.askandanswer.View.Interface.OnSendCategoriesListener;
import com.orchidatech.askandanswer.View.Interface.OnUpdateProfileListener;
import com.orchidatech.askandanswer.View.Interface.OnUploadImageListener;
import com.orchidatech.askandanswer.View.Interface.OnUserFavPostFetched;
import com.orchidatech.askandanswer.View.Interface.OnUserInfoFetched;
import com.orchidatech.askandanswer.View.Interface.OnUserPostFetched;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import com.orchidatech.askandanswer.WebService.UploadImage;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class WebServiceFunctions {

    public static void login(final Context context, String email, String password, long last_login, final OnLoginListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.EMAIL, email);
        params.put(URL.URLParameters.PASSWORD, encode(password));
        params.put(URL.URLParameters.LAST_LOGIN, last_login+"");

        Operations.getInstance(context).login(params, new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    //fill user info from o then store it and pass it to onSuccess
                    ///Check o content then decide success or fail..
                    //if user not found then listener.onFail(context.getString(R.string.BR_LOGIN_004))
                    //if username or password incorrect then listener.onFail(context.getString(R.string.BR_LOGIN_003))
                    //else listener.onSuccess()
                    //store in local DB
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONObject user = o.getJSONObject("data");
                        long user_id = user.getLong("id");
                        String f_name = user.getString("f_name");
                        String l_name = user.getString("l_name");
                        String email = user.getString("email");
                        String image = user.getString("image");
                        int active = user.getInt("active");
                        long created_at = user.getLong("created_at");
                        long last_login = user.getLong("last_login");
                        String code = user.getString("code");
                        String mobile = user.getString("mobile");
                        int is_public = user.getInt("is_public");
                        Users _user = new Users(user_id, f_name, l_name, null, email, null, image, created_at, active, last_login, mobile, is_public, code);
                        UsersDAO.addUser(_user);
                        ArrayList<Long> user_categories_id = new ArrayList<Long>();
                        JSONArray user_category_arr = user.getJSONArray("user_category");
                        for (int i = 0; i < user_category_arr.length(); i++) {
                            JSONObject category_obj = user_category_arr.getJSONObject(i);
                            User_CategoriesDAO.addUserCategory(new User_Categories(category_obj.getLong("id"), category_obj.getLong("user_id"), category_obj.getLong("category_id")));
                            user_categories_id.add(category_obj.getLong("id"));
                            JSONObject category_info = category_obj.getJSONObject("Category_info");
                            CategoriesDAO.addCategory(new Category(category_info.getLong("id"), category_info.getString("name"), category_info.getString("description")));
                        }
                        listener.onSuccess(user_id, user_categories_id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });
    }

    public static void register(final Context context, final String fname, final String lname, final String email,
                                String password, final String image, final long last_login, final int is_public,
                                final OnRegisterListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.FNAME, encode(fname));
        params.put(URL.URLParameters.LNAME, encode(lname));
        params.put(URL.URLParameters.EMAIL, email);
        params.put(URL.URLParameters.IMAGE, image);
        params.put(URL.URLParameters.LAST_LOGIN, last_login + "");
//        params.put(URL.URLParameters.MOBILE, mobile);
        params.put(URL.URLParameters.IS_PUBLIC, is_public + "");
        params.put(URL.URLParameters.PASSWORD, encode(password));

        Operations.getInstance(context).register(params, new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                ///Check o content then decide success or fail..
                //if user already exists then listener.onFail(context.getString(R.string.BR_SIGN_004))
                //else listener.onSuccess()
                //store in local DB
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONObject user = o.getJSONObject("data");
                        long id = user.getLong("id");
                        String f_name = user.getString("f_name");
                        String l_name = user.getString("l_name");
                        String email = user.getString("email");
                        String image = user.getString("image");
                        int active = user.getInt("active");
                        long created_at = user.getLong("created_at");
                        long last_login = user.getLong("last_login");
                        String code = user.getString("code");
                        String mobile = user.getString("mobile");
                        int is_public = user.getInt("is_public");
                        Users _user = new Users(id, f_name, l_name, null, email, null, image, created_at, active, last_login, mobile, is_public, code);
                        UsersDAO.addUser(_user);
                        listener.onSuccess(id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001));
                }

            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });
    }

    public static void getCategories(final Context context, final OnCategoriesFetchedListener listener) {
        Operations.getInstance(context).getCategories(new OnLoadFinished() {

            @Override
            public void onSuccess(JSONObject o) {
                ///parsing o to fetch all categories then
                //store in localDB
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONArray data = o.getJSONArray("data");
                        ArrayList<Category> allCategories = new ArrayList<Category>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject category = data.getJSONObject(i);
                            Category newCategory = new Category(category.getLong("id"), category.getString("name"), category.getString("description"));
                            CategoriesDAO.addCategory(newCategory);
                            newCategory.setIsChecked(i == 0 ? true : false);
                            allCategories.add(newCategory);
                        }
                        listener.onSuccess(allCategories);
                    } else {
                        listener.onFail(GNLConstants.getStatus(status_code));
                    }
                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });

    }

    public static void sendUserCategories(final Context context, final long uid, final ArrayList<Category> selectedCats, final OnSendCategoriesListener listener) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedCats.size(); i++)
            sb.append(selectedCats.get(i).getServerID()).append(i != selectedCats.size()-1 ? "," : "");
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.CATEGORIES_ID, sb.toString());
        params.put(URL.URLParameters.USER_ID, uid + "");

        Operations.getInstance(context).sendUserCategories(params, new OnLoadFinished() {

            @Override
            public void onSuccess(JSONObject o) {
                //store in local DB
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONArray data = o.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject category = data.getJSONObject(i);
                            long id = category.getLong("id");
                            long user_id = category.getLong("user_id");
                            long category_id = category.getLong("category_id");
                            User_CategoriesDAO.addUserCategory(new User_Categories(id, user_id, category_id));
                        }
                        listener.onSendingSuccess();
                    } else
                        listener.onSendingFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onSendingFail(context.getString(R.string.BR_GNL_001));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onSendingFail(error);
            }
        });
    }

    public static void getUserInfo(final Context context, long user_id, final OnUserInfoFetched listener) {
        String url = URL.GET_USER_INFO + "?" + URL.URLParameters.USER_ID + "=" + user_id;
        Operations.getInstance(context).getUserInfo(new OnLoadFinished() {

            @Override
            public void onSuccess(JSONObject o) {
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONObject user = o.getJSONObject("data");
                        long id = user.getLong("id");
                        String f_name = user.getString("f_name");
                        String l_name = user.getString("l_name");
                        String email = user.getString("email");
                        String image = user.getString("image");
                        int active = user.getInt("active");
                        long created_at = user.getLong("created_at");
                        long last_login = user.getLong("last_login");
                        String code = user.getString("code");
                        String mobile = user.getString("mobile");
                        int is_public = user.getInt("is_public");
                        int no_answer = user.getInt("no_answer");
                        int no_ask = user.getInt("no_ask");
                        Users _user = new Users(id, f_name, l_name, null, email, null, image, created_at, active, last_login, mobile, is_public, code);
                        UsersDAO.addUser(_user);
                        listener.onDataFetched(_user, no_answer, no_ask);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));

                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        }, url);
    }

    public static void getUserFavPosts(final Context context, final long uid, int limit, int offset, long last_id, final OnUserFavPostFetched listener) {
        String url = URL.GET_USER_FAV_POSTS + "?" + URL.URLParameters.USER_ID + "=" + uid +
                "&" + URL.URLParameters.LIMIT + "=" + limit + "&" + URL.URLParameters.OFFSET + "=" + offset +
                "&" + URL.URLParameters.LAST_ID + "=" + last_id;
        Operations.getInstance(context).getUserFavPosts(new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONArray data = o.getJSONArray("data");
                        ArrayList<Post_Favorite> fetchedPosts = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject post = data.getJSONObject(i);
                            long id = post.getLong("id");
                            long post_id = post.getLong("post_id");
                            long date = post.getLong("date");
                            Post_Favorite postItem = new Post_Favorite(id, post_id, uid, date);
                            Post_FavoriteDAO.addPostFavorite(postItem);
                            fetchedPosts.add(postItem);
                        }
                        long last_id = o.getLong("last_id");
                        listener.onSuccess(fetchedPosts, last_id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code), status_code);

                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001), 100);
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error, 100);
            }
        }, url);
    }

    public static void getUserPosts(final Context context, final long uid, int limit, int offset, long last_id, final OnUserPostFetched listener) {
        String url = URL.GET_USER_POSTS + "?" + URL.URLParameters.USER_ID + "=" + uid +
                "&" + URL.URLParameters.LIMIT + "=" + limit +
                "&" + URL.URLParameters.OFFSET + "=" + offset +
                "&" + URL.URLParameters.LAST_ID + "=" + last_id;
        Operations.getInstance(context).getUserPosts(new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONArray data = o.getJSONArray("data");
                        ArrayList<Posts> fetchedPosts = new ArrayList<Posts>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject post = data.getJSONObject(i);
                            long id = post.getLong("id");
                            String text = post.getString("text");
                            String image = post.getString("image");
                            int is_hidden = post.getInt("is_hidden");
                            long category_id = post.getLong("category_id");
                            int comments_no = post.getInt("comment_no");
                            long created_at = post.getLong("created_at");
                            Posts postItem = new Posts(id, text, image, created_at, uid, category_id, is_hidden, comments_no);
                            PostsDAO.addPost(postItem);
                            fetchedPosts.add(postItem);
                        }
                        long last_id = o.getLong("last_id");
                        listener.onSuccess(fetchedPosts, last_id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code), status_code);
                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001), 100);
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error, 100);
            }
        }, url);
    }

    public static void getUserComments(final Context context, final long uid, int limit, int offset, long last_id, final OnCommentFetchListener listener) {
        String url = URL.GET_USER_Comments + "?" + URL.URLParameters.USER_ID + "=" + uid +
                "&" + URL.URLParameters.LIMIT + "=" + limit +
                "&" + URL.URLParameters.OFFSET + "=" + offset +
                "&" + URL.URLParameters.LAST_ID + "=" + last_id;
        Operations.getInstance(context).getUserComments(new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONArray data = o.getJSONArray("data");
                        ArrayList<Comments> fetchedComments = new ArrayList<>();
                        long last_id = o.getLong("last_id");
                        listener.onSuccess(fetchedComments, last_id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code), status_code);
                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001), 100);
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error, 100);
            }
        }, url);
    }

    public static void getPostComments(final Context context, final long pid, int limit, int offset, long last_id, final OnCommentFetchListener listener) {
        String url = URL.GET_POST_Comments + "?" + URL.URLParameters.POST_ID + "=" + pid +
                "&" + URL.URLParameters.LIMIT + "=" + limit +
                "&" + URL.URLParameters.OFFSET + "=" + offset +
                "&" + URL.URLParameters.LAST_ID + "=" + last_id;
        Operations.getInstance(context).getPostComments(new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONArray data = o.getJSONArray("data");
                        ArrayList<Comments> fetchedComments = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject comment = data.getJSONObject(i);
                            long comment_id = comment.getLong("id");
                            String comment_text = comment.getString("comment");
                            String comment_image = comment.getString("image");
                            long user_id = comment.getLong("user_id");
                            long post_id = comment.getLong("post_id");
                            long comment_date = comment.getLong("created_at");
                            JSONObject actions = comment.getJSONObject("action");
                            int likes = actions.getInt("like");
                            int dislikes = actions.getInt("dislike");
                            CommentsDAO.addComment(new Comments(comment_id, comment_text, comment_image, comment_date, user_id, post_id, likes, dislikes));
                            ///////////////////////////////////////////////
                            ///comment's user data
                            JSONObject user_info = comment.getJSONArray("user_info").getJSONObject(0);
                            String f_name = user_info.getString("f_name");
                            String l_name = user_info.getString("l_name");
                            String email = user_info.getString("email");
                            String user_image = user_info.getString("image");
                            int active = user_info.getInt("active");
                            long created_at = user_info.getLong("created_at");
                            long last_login = user_info.getLong("last_login");
                            String code = user_info.getString("code");
                            String mobile = user_info.getString("mobile");
                            int is_public = user_info.getInt("is_public");
                            Users _user = new Users(user_id, f_name, l_name, null, email, null, user_image, created_at, active, last_login, mobile, is_public, code);
                            UsersDAO.addUser(_user);
                            //////////////////////////////////////////////
                            JSONObject user_action = comment.getJSONArray("data").getJSONObject(0);
                            User_ActionsDAO.addUserAction(new User_Actions(user_action.getLong("id"), user_action.getLong("comment_id"), user_action.getLong("user_id"), System.currentTimeMillis(), user_action.getInt("action_type")));
                        }
                        long last_id = o.getLong("last_id");
                        listener.onSuccess(fetchedComments, last_id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code), status_code);
                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001), 100);
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error, 100);
            }
        }, url);
    }

    public static void search(Context context, String textFilter, OnSearchCompleted listener) {
        String url = URL.SEARCH + "?filter=" + encode(textFilter);
        Operations.getInstance(context).search(new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {

            }

            @Override
            public void onFail(String error) {

            }
        }, url);

    }

    public static void geTimeLine(final Context context, final long uid, int limit, int offset, long last_id, final OnUserPostFetched listener) {
        String url = URL.GET_TIME_LINE + "?" + URL.URLParameters.USER_ID + "=" + uid +
                "&" + URL.URLParameters.LIMIT + "=" + limit +
                "&" + URL.URLParameters.OFFSET + "=" + offset +
                "&" + URL.URLParameters.LAST_ID + "=" + last_id;
        Operations.getInstance(context).getTimeLine(new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONArray data = o.getJSONArray("data");
                        ArrayList<Posts> fetchedPosts = new ArrayList<Posts>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject post = data.getJSONObject(i);
                            /////data for post
                            long id = post.getLong("id");
                            String text = post.getString("text");
                            String image = post.getString("image");
                            long user_id = post.getLong("user_id");
                            int is_hidden = post.getInt("is_hidden");
                            long category_id = post.getLong("category_id");
                            int comments_no = post.getInt("comment_no");
                            long post_created_at = post.getLong("created_at");
                            /////////////////////////////////////////////////////////////
                            ///data for post's user to store him/her if does not stored in local db
                            JSONObject user = post.getJSONObject("user");
                            String f_name = user.getString("f_name");
                            String l_name = user.getString("l_name");
                            String email = user.getString("email");
                            String user_image = user.getString("image");
                            int active = user.getInt("active");
                            long user_created_at = user.getLong("created_at");
                            long last_login = user.getLong("last_login");
                            String code = user.getString("code");
                            String mobile = user.getString("mobile");
                            int is_public = user.getInt("is_public");
                            /////////////////////////////////////////////////////////////
                            Users _user = new Users(id, f_name, l_name, null, email, null, user_image, user_created_at, active, last_login, mobile, is_public, code);
                            UsersDAO.addUser(_user);
                            Posts postItem = new Posts(id, text, image, post_created_at, user_id, category_id, is_hidden, comments_no);
                            PostsDAO.addPost(postItem);
                            fetchedPosts.add(postItem);
                        }
                        long last_id = o.getLong("last_id");
                        listener.onSuccess(fetchedPosts, last_id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code), status_code);
                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001), 100);
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error, 100);
            }
        }, url);
    }

    public static void addPostFavorite(final Context context, final long pid, final long uid, final OnPostFavoriteListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.USER_ID, uid + "");
        params.put(URL.URLParameters.POST_ID, pid + "");
        Operations.getInstance(context).addPostFavorite(params, new OnLoadFinished() {

            @Override
            public void onSuccess(JSONObject o) {
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONArray data = o.getJSONArray("data");
                        JSONObject post_fav = data.getJSONObject(0);
                        Post_Favorite post_favorite = new Post_Favorite(post_fav.getLong("id"), post_fav.getLong("post_id"), post_fav.getLong("user_id"), System.currentTimeMillis());
                        Post_FavoriteDAO.addPostFavorite(post_favorite);
                        listener.onSuccess();
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });

    }


    public static void removePostFavorite(final Context context, final long postFavId, final long uid, final OnPostFavoriteListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.USER_ID, uid + "");
        params.put(URL.URLParameters.POST_ID, postFavId + "");
        Operations.getInstance(context).removePostFavorite(params, new OnLoadFinished() {

            @Override
            public void onSuccess(JSONObject o) {
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        Post_FavoriteDAO.deletePostFavorite(postFavId, uid);
                        listener.onSuccess();
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });
    }

    public static void deletePost(final Context context, final long postId, final OnPostDeletedListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.POST_ID, postId + "");
        Operations.getInstance(context).deletePost(params, new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        PostsDAO.deletePost(postId);
                        //CASCADE
                        CommentsDAO.deleteCommentByPost(postId);
                        Post_FavoriteDAO.deletePostFavoriteByPost(postId);
                        listener.onDeleted();
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_001));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });

    }

    public static void addPost(final Context context, final long user_id, long category_id, String text, String picturePath, long date, int is_hidden, final OnAddPostListener listener) {


        UploadImage uploadImage = new UploadImage(context, URL.ADD_POST, new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int status_code = response.getInt("statusCode");
                    int status = response.getInt("status");
                    if (status == 0) {
                        JSONObject post = response.getJSONObject("data");
                        long id = post.getLong("id");
                        String text = post.getString("text");
                        String image = post.getString("image");
                        int is_hidden = post.getInt("is_hidden");
                        long category_id = post.getLong("category_id");
                        long user_id = post.getLong("user_id");
                        int comments_no = post.getInt("comment_no");
                        long created_at = post.getLong("created_at");
                        Posts postItem = new Posts(id, text, image, created_at, user_id, category_id, is_hidden, comments_no);
                        PostsDAO.addPost(postItem);
                        listener.onSuccess(context.getResources().getString(R.string.saved));
                    } else {
                        listener.onFail(GNLConstants.getStatus(status_code));
                    }

                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_006));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);

            }
        });
        uploadImage.addStringProperty(URL.URLParameters.USER_ID, user_id + "");
        uploadImage.addStringProperty(URL.URLParameters.CATEGORIES_ID, category_id + "");
        uploadImage.addStringProperty(URL.URLParameters.TEXT, text);
//        uploadImage.addStringProperty(URL.URLParameters.DATE, date + "");
        uploadImage.addStringProperty(URL.URLParameters.IS_HIDDEN, is_hidden + "");
        if (!TextUtils.isEmpty(picturePath))
            uploadImage.addFileProperty(URL.URLParameters.IMAGE, picturePath);
        uploadImage.sendRequest();

//        Operations.getInstance(context).addPost(context, user_id, category_id, encode(postDesc), picturePath, date, is_hidden, new OnUploadImageListener() {
//            @Override
//            public void onSuccess(String serverResponseMessage) {
//                try {
//                    JSONObject response = new JSONObject(serverResponseMessage);
//                    int status_code = response.getInt("statusCode");
//                    int status = response.getInt("status");
//                    if (status == 0) {
//                        JSONArray data = response.getJSONArray("data");
//                        JSONObject post = data.getJSONObject(0);
//                        long id = post.getLong("id");
//                        String text = post.getString("text");
//                        String image = post.getString("image");
//                        int is_hidden = post.getInt("is_hidden");
//                        long category_id = post.getLong("category_id");
//                        int comments_no = post.getInt("comment_no");
//                        long created_at = post.getLong("created_at");
//                        Posts postItem = new Posts(id, text, image, created_at, user_id, category_id, is_hidden, comments_no);
//                        PostsDAO.addPost(postItem);
//                        listener.onSuccess(context.getResources().getString(R.string.saved));
//                    } else {
//                        listener.onFail(GNLConstants.getStatus(status_code));
//                    }
//
//                } catch (JSONException e) {
//                    listener.onFail(context.getString(R.string.BR_GNL_006));
//                }
//            }
//
//            @Override
//            public void onFail(String error) {
//                listener.onFail(error);
//
//            }
//        });
    }


    public static void editPost(final Context context, long post_id, long user_id, long category_id, String postDesc, String picturePath, long date, int isHidden, final OnEditPostListener listener) {
        Operations.getInstance(context).editPost(context, post_id, user_id, category_id, encode(postDesc), picturePath, date, isHidden, new OnUploadImageListener() {
            @Override
            public void onSuccess(String serverResponseMessage) {
                try {
                    JSONObject response = new JSONObject(serverResponseMessage);
                    int status_code = response.getInt("statusCode");
                    int status = response.getInt("status");
                    if (status == 0) {
                        JSONArray data = response.getJSONArray("data");
                        JSONObject post = data.getJSONObject(0);
                        long id = post.getLong("id");
                        String text = post.getString("text");
                        String image = post.getString("image");
                        int is_hidden = post.getInt("is_hidden");
                        long user_id = post.getLong("user_id");
                        long category_id = post.getLong("category_id");
                        int comments_no = post.getInt("comment_no");
                        long created_at = post.getLong("created_at");
                        Posts postItem = new Posts(id, text, image, created_at, user_id, category_id, is_hidden, comments_no);
                        PostsDAO.updatePost(postItem);
                        listener.onSuccess(context.getResources().getString(R.string.saved));
                        ;
                    } else {
                        listener.onFail(GNLConstants.getStatus(status_code));
                    }

                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_006));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });

    }

    public static void addComment(final Context context, final String comment, String picturePath, long postId, long user_id, final OnCommentAddListener listener) {
        UploadImage uploadImage = new UploadImage(context, URL.ADD_COMMENT, new OnLoadFinished() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if(status == 0){
                        JSONArray data = o.getJSONArray("data");
                        JSONObject comment = data.getJSONObject(0);
                        long comment_id = comment.getLong("id");
                        String comment_text = comment.getString("comment");
                        String comment_image = comment.getString("image");
                        long user_id = comment.getLong("user_id");
                        long post_id = comment.getLong("post_id");
                        long comment_date = comment.getLong("created_at");
                        Comments newComment = new Comments(comment_id, comment_text, comment_image, comment_date, user_id, post_id, 0, 0);
                        CommentsDAO.addComment(newComment);
                        listener.onAdded(newComment);
                    }else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(context.getString(R.string.BR_GNL_006));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });
        uploadImage.addStringProperty(URL.URLParameters.COMMENT, encode(comment));
        uploadImage.addStringProperty(URL.URLParameters.POST_ID, postId+"");
        uploadImage.addStringProperty(URL.URLParameters.USER_ID, user_id+"");
        if(!TextUtils.isEmpty(picturePath))
            uploadImage.addFileProperty(URL.URLParameters.IMAGE, picturePath);
        uploadImage.sendRequest();

//            String url = URL.ADD_COMMENT + "?" + URL.URLParameters.COMMENT + "=" + encode(comment) +
//               "&" + URL.URLParameters.POST_ID + "=" + postId + "&" + URL.URLParameters.USER_ID + "=" + user_id;
//        Operations.getInstance(context).addComment(new OnLoadFinished(){
//
//            @Override
//            public void onSuccess(JSONObject o) {
//                try {
//                    int status_code = o.getInt("statusCode");
//                    int status = o.getInt("status");
//                    if(status == 0){
//                        JSONArray data = o.getJSONArray("data");
//                        JSONObject comment = data.getJSONObject(0);
//                        long comment_id = comment.getLong("id");
//                        String comment_text = comment.getString("comment");
//                        String comment_image = comment.getString("image");
//                        long user_id = comment.getLong("user_id");
//                        long post_id = comment.getLong("post_id");
//                        long comment_date = comment.getLong("created_at");
//                        Comments newComment = new Comments(comment_id, comment_text, comment_image, comment_date, user_id, post_id, 0, 0);
//                        CommentsDAO.addComment(newComment);
//                        listener.onAdded(newComment);
//                    }else
//                        listener.onFail(GNLConstants.getStatus(status_code));
//                } catch (JSONException e) {
//                    listener.onFail(context.getString(R.string.BR_GNL_006));
//                }
//            }
//
//            @Override
//            public void onFail(String error) {
//                listener.onFail(error);
//            }
//        }, url);
    }
    public static void updateProfile(final Context context, long id, String fname, String lname, String password, String picturePath, ArrayList<Category> selectedCategories, final OnUpdateProfileListener listener) {
        Operations.getInstance(context).updateProfile(id, encode(fname), encode(lname),
                encode(password), picturePath, selectedCategories, new OnUploadImageListener() {
                    @Override
                    public void onSuccess(String serverResponseMessage) {
                        try {
                            JSONObject response = new JSONObject(serverResponseMessage);
                            int status_code = response.getInt("statusCode");
                            int status = response.getInt("status");
                            if (status == 0) {
                                JSONArray data = response.getJSONArray("data");
                                JSONObject user = data.getJSONObject(0);
                                long uid = user.getLong("id");
                                String f_name = user.getString("f_name");
                                String l_name = user.getString("l_name");
                                String email = user.getString("email");
                                String image = user.getString("image");
                                int active = user.getInt("active");
                                long created_at = user.getLong("created_at");
                                long last_login = user.getLong("last_login");
                                String code = user.getString("code");
                                String mobile = user.getString("mobile");
                                int is_public = user.getInt("is_public");
//                                ArrayList<User_Categories> user_categories = new ArrayList<User_Categories>();
//                                JSONArray arr_user_category = user.getJSONArray("user_category");
//                                for (int i = 0; i < arr_user_category.length(); i++) {
//                                    JSONObject user_category = arr_user_category.getJSONObject(i);
//                                    long user_category_id = user_category.getLong("id");
//                                    long category_id = user_category.getLong("category_id");
//                                    user_categories.add(new User_Categories(user_category_id, uid, category_id));
//                                }
//                                ArrayList<User_Categories> allCurrentStored = new ArrayList<>(User_CategoriesDAO.getAllUserCategories(uid));
//                                boolean deleting;
//                                for (User_Categories currentUserCategory : allCurrentStored) {
//                                    deleting = false;
//                                    for (int i = 0; i < user_categories.size(); i++) {
//                                        if (currentUserCategory.getCategoryID() == user_categories.get(i).getCategoryID()) {
//                                            deleting = true;
//                                            break;
//                                        }
//                                        if (deleting) {
//                                            User_CategoriesDAO.deleteUserCategory(currentUserCategory.getServerID(), uid);
//                                            PostsDAO.deletePostsInCategory(uid, currentUserCategory.getCategoryID());
//                                            ////find solution to remove favorite posts also, it is solved in getAllUserPostFavorite method
//                                        }
//
//                                    }
//                                }

                                Users _user = new Users(uid, f_name, l_name, null, email, null, image, created_at, active, last_login, mobile, is_public, code);
                                UsersDAO.addUser(_user);

                                listener.onSuccess();
                                ;
                            } else {
                                listener.onFail(GNLConstants.getStatus(status_code));
                            }

                        } catch (JSONException e) {
                            listener.onFail(context.getString(R.string.BR_GNL_006));
                        }
                    }

                    @Override
                    public void onFail(String error) {

                    }
                });

//        UploadImage uploadImage = new UploadImage(context, URL.UPDATE_PROFILE, new OnLoadFinished() {
//            @Override
//            public void onSuccess(JSONObject o) {
//                listener.onSuccess();
//            }
//
//            @Override
//            public void onFail(String error) {
//                listener.onFail(error);
//            }
//        });
//        if(picturePath != null)
//            uploadImage.addFileProperty(URL.URLParameters.IMAGE, encode(picturePath));
//        uploadImage.addStringProperty(URL.URLParameters.FNAME, encode(fname));
//        uploadImage.addStringProperty(URL.URLParameters.LNAME, encode(lname));
//        uploadImage.addStringProperty(URL.URLParameters.EMAIL, encode(email));
//        uploadImage.addStringProperty(URL.URLParameters.PASSWORD, encode(password));
//        uploadImage.addStringProperty(URL.URLParameters.ID, id+"");
//        uploadImage.sendRequest();
    }


    private static String encode(String s) {
        return Uri.encode(s, "utf-8");
    }

}
