package com.orchidatech.askandanswer.WebService;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Constant.URL;
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
import com.orchidatech.askandanswer.View.Interface.OnCommentActionListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentAddListener;
import com.orchidatech.askandanswer.View.Interface.OnCommentFetchListener;
import com.orchidatech.askandanswer.View.Interface.OnDeleteCommentListener;
import com.orchidatech.askandanswer.View.Interface.OnEditPostListener;
import com.orchidatech.askandanswer.View.Interface.OnForgetPasswordListener;
import com.orchidatech.askandanswer.View.Interface.OnLoadFinished;
import com.orchidatech.askandanswer.View.Interface.OnLoginListener;
import com.orchidatech.askandanswer.View.Interface.OnPostDeletedListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Interface.OnRegisterListener;
import com.orchidatech.askandanswer.View.Interface.OnSearchCompleted;
import com.orchidatech.askandanswer.View.Interface.OnSendCategoriesListener;
import com.orchidatech.askandanswer.View.Interface.OnSendMessageListener;
import com.orchidatech.askandanswer.View.Interface.OnUpdateProfileListener;
import com.orchidatech.askandanswer.View.Interface.OnUserCategoriesFetched;
import com.orchidatech.askandanswer.View.Interface.OnUserFavPostFetched;
import com.orchidatech.askandanswer.View.Interface.OnUserInfoFetched;
import com.orchidatech.askandanswer.View.Interface.OnUserPostFetched;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bahaa on 13/11/2015.
 */
public class WebServiceFunctions {

    public static void login(final Context context, String email, String password, long last_login, final OnLoginListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.EMAIL, email);
        params.put(URL.URLParameters.PASSWORD, password);
        params.put(URL.URLParameters.LAST_LOGIN, last_login + "");
        Operations.getInstance(context).sendPostRequest(URL.LOGIN, params, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                try {

                    Log.i("gfgfdgfdgfdgfdgfdgfdg", response);
                    //fill user info from o then store it and pass it to onSuccess
                    ///Check o content then decide success or fail..
                    //if user not found then listener.onFail(context.getString(R.string.BR_LOGIN_004))
                    //if username or password incorrect then listener.onFail(context.getString(R.string.BR_LOGIN_003))
                    //else listener.onSuccess()
                    //store in local DB
                    JSONObject data = new JSONObject(response);
                    int status_code = data.getInt("statusCode");
                    int status = data.getInt("status");
                    if (status == 0) {
                        JSONObject user = data.getJSONObject("data");
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
                        int no_aks = user.getInt("no_ask");
                        int no_answers = user.getInt("no_answer");
                        float user_rating = Float.parseFloat(user.get("no_of_stars") + "");


                        Users _user = new Users(user_id, f_name, l_name, null, email, null, image.equals("null") ? null : image, created_at, active, last_login, mobile, is_public, code, no_answers, no_aks, user_rating);
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
                    listener.onFail(GNLConstants.getStatus(100));
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
        params.put(URL.URLParameters.FNAME, fname);
        params.put(URL.URLParameters.LNAME, lname);
        params.put(URL.URLParameters.EMAIL, email);
        params.put(URL.URLParameters.PASSWORD, password);
        params.put(URL.URLParameters.IMAGE, image);
        params.put(URL.URLParameters.LAST_LOGIN, last_login + "");
//        params.put(URL.URLParameters.MOBILE, mobile);
        params.put(URL.URLParameters.IS_PUBLIC, is_public + "");
        Operations.getInstance(context).sendPostRequest(URL.REGISTER, params, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                ///Check o content then decide success or fail..
                //if user already exists then listener.onFail(context.getString(R.string.BR_SIGN_004))
                //else listener.onSuccess()
                //store in local DB
                try {
                    Log.i("gfgfdgfdgfdgfdgfdgfdg", response);

                    JSONObject data = new JSONObject(response);
                    int status_code = data.getInt("statusCode");
                    int status = data.getInt("status");
                    if (status == 0) {
                        JSONObject user = data.getJSONObject("data");
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
                        Users _user = new Users(id, f_name, l_name, null, email, null, image.equals("null") ? null : image, created_at, active, last_login,
                                mobile, is_public, code, 0, 0, 0f);
                        UsersDAO.addUser(_user);
                        listener.onSuccess(id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100));
                }

            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });
    }

    public static void getCategories(final Context context, final OnCategoriesFetchedListener listener) {
        Operations.getInstance(context).sendGetRequest(URL.GET_CATEGORIES, new OnLoadFinished() {

            @Override
            public void onSuccess(String response) {
                ///parsing o to fetch all categories then
                //store in localDB
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONArray data = dataObj.getJSONArray("data");
                        ArrayList<Category> allCategories = new ArrayList<Category>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject category = data.getJSONObject(i);
                            Category newCategory = new Category(category.getLong("id"), category.getString("name"),
                                    category.getString("description"));
                            CategoriesDAO.addCategory(newCategory);
                            allCategories.add(newCategory);
                        }
                        listener.onSuccess(allCategories);
                    } else {
                        listener.onFail(GNLConstants.getStatus(status_code));
                    }
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100));
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
            sb.append(selectedCats.get(i).getServerID()).append(i != selectedCats.size() - 1 ? "," : "");
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.CATEGORIES_ID, sb.toString());
        params.put(URL.URLParameters.USER_ID, uid + "");
        Log.i("gfgfdgfdgfdgfdgfdgfdg", uid + ", " + sb.toString());

        Operations.getInstance(context).sendPostRequest(URL.SEND_USER_CATEGORIES, params, new OnLoadFinished() {

            @Override
            public void onSuccess(String response) {
                //store in local DB
                try {
                    Log.i("gfgfdgfdgfdgfdgfdgfdg", uid + ", " + response);
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONArray data = dataObj.getJSONArray("data");
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
                    listener.onSendingFail(GNLConstants.getStatus(100));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onSendingFail(error);
            }
        });
    }

    public static void updateUserCategories(final Context context, final long uid, ArrayList<Category> selectedCats, final OnSendCategoriesListener listener) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedCats.size(); i++)
            sb.append(selectedCats.get(i).getServerID()).append(i != selectedCats.size() - 1 ? "," : "");
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.CATEGORIES_ID, sb.toString());
        params.put(URL.URLParameters.USER_ID, uid + "");
        String url = URL.UPDATE_USER_CATEGORIES + "?" + URL.URLParameters.CATEGORIES_ID + "=" + sb.toString() +
                "&" + URL.URLParameters.USER_ID + "=" + uid;
        Operations.getInstance(context).sendPostRequest(URL.UPDATE_USER_CATEGORIES, params, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        User_CategoriesDAO.deleteAllUserCategories(uid);
                        JSONArray data = dataObj.getJSONArray("data");
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
                    listener.onSendingFail(GNLConstants.getStatus(100));
                }


            }

            @Override
            public void onFail(String error) {
                listener.onSendingFail(error);
            }
        });
    }

    public static void getUserCategories(final Context context, final long mId, final OnUserCategoriesFetched listener) {
        String url = URL.GET_USER_CATEGORIES + "?" + URL.URLParameters.USER_ID + "=" + mId;
        Log.i("sdsdsds", url);
        Operations.getInstance(context).sendGetRequest(url, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        User_CategoriesDAO.deleteAllUserCategories(mId);
                        ArrayList<User_Categories> allUserCategories = new ArrayList<>();
                        JSONArray data = dataObj.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject userCategory_obj = data.getJSONObject(i);
                            long user_category_id = userCategory_obj.getLong("id");
                            long user_id = userCategory_obj.getLong("user_id");
                            long category_id = userCategory_obj.getLong("category_id");
                            JSONObject category_info = userCategory_obj.getJSONObject("category_info");
                            CategoriesDAO.addCategory(new Category(category_info.getLong("id"), category_info.getString("name"), category_info.getString("description")));
                            User_Categories user_categories = new User_Categories(user_category_id, user_id, category_id);
                            User_CategoriesDAO.addUserCategory(user_categories);
                            allUserCategories.add(user_categories);
                        }
                        listener.onSuccess(allUserCategories);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);

            }
        });

    }

    public static void getUserInfo(final Context context, long user_id, final OnUserInfoFetched listener) {
        String url = URL.GET_USER_INFO + "?" + URL.URLParameters.USER_ID + "=" + user_id;
        Log.i("sdsdsd", url);
        Operations.getInstance(context).sendGetRequest(url, new OnLoadFinished() {

            @Override
            public void onSuccess(String response) {
                Log.i("dsdsd", response);
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONObject user = dataObj.getJSONObject("data");
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
                        float user_rating = Float.parseFloat(user.get("no_of_stars") + "");


                        Users _user = new Users(id, f_name, l_name, null, email, null, image.equals("null") ? null : image, created_at, active, last_login,
                                mobile, is_public, code, no_answer, no_ask, user_rating);
                        UsersDAO.addUser(_user);
                        listener.onDataFetched(_user, no_answer, no_ask);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));

                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });
    }

    public static void getUserFavPosts(final Context context, final long uid, int limit, int offset, long last_id, final OnUserFavPostFetched listener) {
        String url = URL.GET_USER_FAV_POSTS + "?" + URL.URLParameters.USER_ID + "=" + uid +
                "&" + URL.URLParameters.LIMIT + "=" + limit + "&" + URL.URLParameters.OFFSET + "=" + offset +
                "&" + URL.URLParameters.LAST_ID + "=" + last_id;
        Log.i("sdsdsdsdsd", url);
        Operations.getInstance(context).sendGetRequest(url, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                Log.i("dfddggg", response);

                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONArray data = dataObj.getJSONArray("data");
                        ArrayList<Post_Favorite> fetchedPosts = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject favPost = data.getJSONObject(i);
                            long id = Long.parseLong(favPost.getString("id"));
                            long post_id = Long.parseLong(favPost.getString("post_id"));
                            long user_id = Long.parseLong(favPost.getString("user_id"));
                            long date = favPost.getLong("updated_at");
                            Post_Favorite favPostItem = new Post_Favorite(id, post_id, user_id, date);
                            Post_FavoriteDAO.addPostFavorite(favPostItem);

                            JSONObject post_obj = favPost.getJSONObject("data_post");
                            String text = post_obj.getString("text");
                            String image = post_obj.getString("image");
                            int is_hidden = Integer.parseInt(post_obj.getString("is_hidden"));
                            long category_id = Long.parseLong(post_obj.getString("category_id"));
                            long post_user_id = Long.parseLong(post_obj.getString("user_id"));
//                            int comments_no = post_obj.getInt("comment_no");
                            long created_at = post_obj.getLong("updated_at");
                            Posts postItem = new Posts(post_id, text, image, created_at, post_user_id, category_id, is_hidden, 0, 1, -1, -1);
                            PostsDAO.addPost(postItem);

                            fetchedPosts.add(favPostItem);
                        }
                        long last_id = Long.parseLong(dataObj.getString("last_id"));
                        listener.onSuccess(fetchedPosts, last_id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code), status_code);

                } catch (JSONException e) {
                    Log.i("Cxcxc", "exception");
                    listener.onFail(GNLConstants.getStatus(100), 100);
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error, 100);
            }
        });
    }

    public static void getCategoryPosts(final Context context, final long userId, long categoryId, int limit, int offset, long last_id, final OnUserPostFetched listener) {
        String url = URL.GET_Category_POSTS + "?" + URL.URLParameters.LIMIT + "=" + limit +
                "&" + URL.URLParameters.CATEGORY_ID + "=" + categoryId +
                "&" + URL.URLParameters.OFFSET + "=" + offset +
                "&" + URL.URLParameters.LAST_ID + "=" + last_id +
                "&" + URL.URLParameters.USER_ID + "=" + userId;
        Log.i("dfddv", url);
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.CATEGORIES_ID, categoryId + "");
        params.put(URL.URLParameters.USER_ID, userId + "");
        params.put(URL.URLParameters.LIMIT, limit + "");
        params.put(URL.URLParameters.OFFSET, offset + "");
        params.put(URL.URLParameters.LAST_ID, last_id + "");

        Operations.getInstance(context).sendGetRequest(url, new OnLoadFinished() {

            @Override
            public void onSuccess(String response) {
                Log.i("dfddv", response);
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONArray data = dataObj.getJSONArray("data");
                        ArrayList<Posts> fetchedPosts = new ArrayList<Posts>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject post = data.getJSONObject(i);
                            long id = Long.parseLong(post.getString("id"));
                            String text = post.getString("text");
                            String image = post.getString("image");
                            int is_hidden = Integer.parseInt(post.getString("is_hidden"));
                            long category_id = Long.parseLong(post.getString("category_id"));
                            int comments_no = post.getInt("comment_no");
                            long created_at = post.getLong("updated_at");
                            int isFavorite = post.getBoolean("is_postfavorite") ? 1 : 0;
                            Posts postItem = new Posts(id, text, image.equals("null") ? null : image, created_at, userId, category_id, is_hidden, comments_no, isFavorite, -1, -1);
                            JSONObject user = post.getJSONArray("user").getJSONObject(0);
                            String f_name = user.getString("f_name");
                            String l_name = user.getString("l_name");
                            String email = user.getString("email");
                            String user_image = user.getString("image");
                            int active = user.getInt("active");
                            long user_created_at = user.getLong("created_at");
                            long last_login = user.getLong("last_login");
                            String code = user.getString("code");
                            String mobile = user.getString("mobile");
                            int is_public = Integer.parseInt(user.getString("is_public"));
                            JSONObject askandanswer = user.getJSONObject("askandanswer");
                            int no_answer = askandanswer.getInt("no_answer");
                            int no_ask = askandanswer.getInt("no_ask");
                            float user_rating = Float.parseFloat(askandanswer.get("no_of_stars") + "");
                            Users _user = new Users(id, f_name, l_name, null, email, null, user_image.equals("null") ? null : user_image, user_created_at, active, last_login,
                                    mobile, is_public, code, no_answer, no_ask, user_rating);
                            UsersDAO.addUser(_user);
                            PostsDAO.addPost(postItem);
                            fetchedPosts.add(postItem);
                        }
                        long last_id = dataObj.getLong("last_id");
                        listener.onSuccess(fetchedPosts, last_id);

                    } else
                        listener.onFail(GNLConstants.getStatus(status_code), status_code);
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100), 100);
                }

            }

            @Override
            public void onFail(String error) {
                listener.onFail(error, 100);
            }
        });

    }

    public static void getUserPosts(final Context context, final long uid, int limit, int offset, long last_id, final OnUserPostFetched listener) {
        String url = URL.GET_USER_POSTS + "?" + URL.URLParameters.USER_ID + "=" + uid +
                "&" + URL.URLParameters.LIMIT + "=" + limit +
                "&" + URL.URLParameters.OFFSET + "=" + offset +
                "&" + URL.URLParameters.LAST_ID + "=" + last_id;
        Log.i("fdfdfd0", url);
        Operations.getInstance(context).sendGetRequest(url, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONArray data = dataObj.getJSONArray("data");
                        ArrayList<Posts> fetchedPosts = new ArrayList<Posts>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject post = data.getJSONObject(i);
                            long id = post.getLong("id");
                            String text = post.getString("text");
                            String image = post.getString("image");
                            int is_hidden = post.getInt("is_hidden");
                            long category_id = post.getLong("category_id");
                            int comments_no = post.getInt("comment_no");
                            long created_at = post.getLong("updated_at");
                            int isFavorite = post.getBoolean("is_postfavorite") ? 1 : 0;
                            Posts postItem = new Posts(id, text, image.equals("null") ? null : image, created_at, uid, category_id, is_hidden, comments_no, isFavorite, -1, -1);
                            PostsDAO.addPost(postItem);
                            fetchedPosts.add(postItem);
                        }
                        long last_id = dataObj.getLong("last_id");
                        listener.onSuccess(fetchedPosts, last_id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code), status_code);
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100), 100);
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error, 100);
            }
        });
    }

    public static void getUserComments(final Context context, final long uid, int limit, int offset, long last_id, final OnCommentFetchListener listener) {
        String url = URL.GET_USER_Comments + "?" + URL.URLParameters.USER_ID + "=" + uid +
                "&" + URL.URLParameters.LIMIT + "=" + limit +
                "&" + URL.URLParameters.OFFSET + "=" + offset +
                "&" + URL.URLParameters.LAST_ID + "=" + last_id;
        Log.i("sdsddsdsd", url);
        Operations.getInstance(context).sendGetRequest(url, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                try {
                    Log.i("sdsddsdsd", response);

                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONArray data = dataObj.getJSONArray("data");
                        ArrayList<Comments> fetchedComments = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject comment = data.getJSONObject(i);
                            long comment_id = Long.parseLong(comment.getString("id"));
                            String comment_text = comment.getString("comment");
                            String comment_image = comment.getString("image");
                            long user_id = Long.parseLong(comment.getString("user_id"));
                            long post_id = Long.parseLong(comment.getString("post_id"));
                            long comment_date = comment.getLong("updated_at");
                            JSONObject actions = comment.getJSONObject("action");
                            int likes = actions.getInt("like");
                            int dislikes = actions.getInt("dislike");
                            JSONArray user_action_arr = comment.optJSONArray("user_action");
                            if (user_action_arr != null && user_action_arr.length() > 0) {
                                JSONObject user_actionObj = user_action_arr.getJSONObject(0);
                                long user_action_id = user_actionObj.getLong("id");
                                long user_action_comment = user_actionObj.getLong("comment_id");
                                long user_action_user = user_actionObj.getLong("user_id");
                                int user_action_action = user_actionObj.getInt("action_type");
                                User_ActionsDAO.addUserAction(new User_Actions(user_action_id, user_action_comment, user_action_user, System.currentTimeMillis(), user_action_action));
                            }
                            JSONObject pos_obj = comment.getJSONObject("post");
                            PostsDAO.addPost(new Posts(Long.parseLong(pos_obj.getString("id")), pos_obj.getString("text"), pos_obj.getString("image"),
                                    pos_obj.getLong("updated_at"), Long.parseLong(pos_obj.getString("user_id")), Long.parseLong(pos_obj.getString("category_id")),
                                    Integer.parseInt(pos_obj.getString("is_hidden")), -1, -1, -1, -1));
                            Comments comment_item = new Comments(comment_id, comment_text, comment_image.equals("null") ? null : comment_image, comment_date, user_id, post_id, likes, dislikes);
                            CommentsDAO.addComment(comment_item);
                            fetchedComments.add(comment_item);
                        }
                        long last_id = dataObj.getLong("last_id");
                        listener.onSuccess(fetchedComments, last_id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code), status_code);
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100), 100);
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error, 100);
            }
        });
    }

    public static void getPostComments(final Context context, long user_id, final long pid, int limit, int offset, long last_id, final OnCommentFetchListener listener) {
        String url = URL.GET_POST_Comments + "?" +
                URL.URLParameters.POST_ID + "=" + pid +
                "&" + URL.URLParameters.USER_ID + "=" + user_id +
                "&" + URL.URLParameters.LIMIT + "=" + limit +
                "&" + URL.URLParameters.OFFSET + "=" + offset +
                "&" + URL.URLParameters.LAST_ID + "=" + last_id;
        Operations.getInstance(context).sendGetRequest(url, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                Log.i("sds", response);
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONArray data = dataObj.getJSONArray("data");
                        ArrayList<Comments> fetchedComments = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject comment = data.getJSONObject(i);
                            long comment_id = Long.parseLong(comment.getString("id"));
                            String comment_text = comment.getString("comment");
                            String comment_image = comment.getString("image");
                            long user_id = Long.parseLong(comment.getString("user_id"));
                            long post_id = Long.parseLong(comment.getString("post_id"));
                            long comment_date = comment.getLong("updated_at");
                            JSONObject actions = comment.getJSONObject("action");
                            int likes = actions.getInt("like");
                            int dislikes = actions.getInt("dislike");
                            Comments comment_item = new Comments(comment_id, comment_text, comment_image.equals("null") ? null : comment_image, comment_date, user_id, post_id, likes, dislikes);
                            CommentsDAO.addComment(comment_item);
                            fetchedComments.add(comment_item);
                            ///////////////////////////////////////////////
                            ///comment's user data
                            JSONObject user_info = comment.getJSONArray("user_info").getJSONObject(0);
                            String f_name = user_info.getString("f_name");
                            String l_name = user_info.getString("l_name");
                            String email = user_info.getString("email");
                            String user_image = user_info.getString("image");
                            int active = Integer.parseInt(user_info.getString("active"));
                            long created_at = user_info.getLong("created_at");
                            long last_login = user_info.getLong("last_login");
                            String code = user_info.getString("code");
                            String mobile = user_info.getString("mobile");
                            int is_public = Integer.parseInt(user_info.getString("is_public"));
                            JSONObject askandanswer = user_info.getJSONObject("askandanswer");
                            int no_answer = askandanswer.getInt("no_answer");
                            int no_ask = askandanswer.getInt("no_ask");
                            float user_rating = Float.parseFloat(askandanswer.get("no_of_stars") + "");

                            Users _user = new Users(user_id, f_name, l_name, null, email, null, user_image.equals("null") ? null : user_image, created_at, active, last_login, mobile, is_public, code, no_answer, no_ask, user_rating);
                            UsersDAO.addUser(_user);
                            /////////////////////////s/////////////////////
                            JSONArray user_action_arr = comment.optJSONArray("user_action");
                            if (user_action_arr != null && user_action_arr.length() > 0) {
                                JSONObject user_action = user_action_arr.getJSONObject(0);
                                User_ActionsDAO.addUserAction(new User_Actions(Long.parseLong(user_action.getString("id"))
                                        , Long.parseLong(user_action.getString("comment_id")), Long.parseLong(user_action.getString("user_id")), System.currentTimeMillis(), Integer.parseInt(user_action.getString("action_type"))));
                            }
                        }
                        long last_id = Long.parseLong(dataObj.getString("last_id"));
                        listener.onSuccess(fetchedComments, last_id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code), status_code);
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100), 100);
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error, 100);
            }
        });
    }

    public static void search(final Context context, String textFilter, final long user_id, final OnSearchCompleted listener) {
        String url = URL.SEARCH + "?" + URL.URLParameters.FILTER + "=" + encode(textFilter) + "&" + URL.URLParameters.USER_ID + "=" + user_id;
        Log.i("dsds", url);
        Operations.getInstance(context).sendGetRequest(url, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONArray data = dataObj.getJSONArray("data");
                        ArrayList<Posts> matchedPosts = new ArrayList<Posts>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject post_obj = data.getJSONObject(i);
                            JSONObject comment_action = post_obj.getJSONObject("comment_action");
                            int num_likes = comment_action.getInt("like");
                            int num_dislikes = comment_action.getInt("dislike");
                            int isFavorite = post_obj.getBoolean("is_postfavorite")?1:0;
                            Posts post = new Posts(Long.parseLong(post_obj.getString("id")), post_obj.getString("text"), post_obj.getString("image").equals("null") ? null : post_obj.getString("image"),
                                    post_obj.getLong("updated_at"), Long.parseLong(post_obj.getString("user_id")), Long.parseLong(post_obj.getString("category_id")), Integer.parseInt(post_obj.getString("is_hidden")), post_obj.getInt("comment_no"), isFavorite, num_likes, num_dislikes);
                            JSONObject userObj = post_obj.getJSONObject("user");
                            Users user = new Users(Long.parseLong(userObj.getString("id")), userObj.getString("f_name"), userObj.getString("l_name"), null, userObj.getString("email"),
                                    null, userObj.getString("image").equals("null") ? null : userObj.getString("image"), userObj.getLong("updated_at"), Integer.parseInt(userObj.getString("active")), userObj.getLong("last_login"),
                                    userObj.getString("mobile"), Integer.parseInt(userObj.getString("is_public")), userObj.getString("code"), userObj.getJSONObject("askandanswer").getInt("no_answer"), userObj.getJSONObject("askandanswer").getInt("no_ask"), Float.parseFloat(userObj.getJSONObject("askandanswer").getInt("no_of_stars")+""));
                            Log.i("cxcdvcv", userObj.getString("mobile").length() + "");
                            PostsDAO.addPost(post);
                            UsersDAO.addUser(user);
                            matchedPosts.add(post);
                        }
                        listener.onSuccess(matchedPosts);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });

    }

    public static void geTimeLine(final Context context, final long uid, int limit, int offset, long last_id, final OnUserPostFetched listener) {
        String url = URL.GET_TIME_LINE + "?" + URL.URLParameters.USER_ID + "=" + uid +
                "&" + URL.URLParameters.LIMIT + "=" + limit +
                "&" + URL.URLParameters.OFFSET + "=" + offset +
                "&" + URL.URLParameters.LAST_ID + "=" + last_id;
        Log.i("sdsadsadsds", url);
        Operations.getInstance(context).sendGetRequest(url, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                try {
                    Log.i("sdsadsadsds", response);

                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONArray data = dataObj.getJSONArray("data");
                        ArrayList<Posts> fetchedPosts = new ArrayList<Posts>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject post = data.getJSONObject(i);
                            /////data for post
                            long id = Long.parseLong(post.getString("id"));
                            String text = post.getString("text");
                            String image = post.getString("image");
                            long user_id = Long.parseLong(post.getString("user_id"));
                            int is_hidden = Integer.parseInt(post.getString("is_hidden"));
                            long category_id = Long.parseLong(post.getString("category_id"));
                            int comments_no = post.getInt("comment_no");
                            long post_created_at = post.getLong("updated_at");
                            /////////////////////////////////////////////////////////////
                            ///data for post's user to store him/her if does not stored in local db
                            JSONObject user = post.getJSONObject("user");
                            String f_name = user.getString("f_name");
                            String l_name = user.getString("l_name");
                            String email = user.getString("email");
                            String user_image = user.getString("image");
                            int active = Integer.parseInt(user.getString("active"));
                            long user_created_at = user.getLong("created_at");
                            long last_login = user.getLong("last_login");
                            String code = user.getString("code");
                            String mobile = user.getString("mobile");
                            int is_public = Integer.parseInt(user.getString("is_public"));

                            JSONObject askandanswer = user.getJSONObject("askandanswer");
                            int no_asks = askandanswer.getInt("no_ask");
                            int no_answers = askandanswer.getInt("no_answer");
                            float user_rating = Float.parseFloat(askandanswer.getInt("no_of_stars") + "");
                            /////////////////////////////////////////////////////////////
                            Users _user = new Users(user_id, f_name, l_name, null, email, null, user_image.equals("null") ? null : user_image, user_created_at, active, last_login, mobile, is_public, code, no_answers, no_asks, user_rating);
                            UsersDAO.addUser(_user);
                            Posts postItem = new Posts(id, text, image.equals("null") ? null : image, post_created_at, user_id, category_id, is_hidden, comments_no, post.getBoolean("is_postfavorite") ? 1 : 0, -1, -1);
                            PostsDAO.addPost(postItem);
                            fetchedPosts.add(postItem);
                        }
                        long last_id = dataObj.getLong("last_id");

                        listener.onSuccess(fetchedPosts, last_id);
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code), status_code);
                } catch (JSONException e) {
                    Log.i("DCDF", e.getMessage());
                    listener.onFail(GNLConstants.getStatus(100), 100);
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error, 100);
            }
        });
    }

    public static void addPostFavorite(final Context context, final long pid, final long uid, final OnPostFavoriteListener listener) {
        String url = URL.ADD_POST_FAVORITE + "?" + URL.URLParameters.USER_ID + "=" + uid + "&"
                + URL.URLParameters.POST_ID + "=" + pid;
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.USER_ID, uid + "");
        params.put(URL.URLParameters.POST_ID, pid + "");
        Log.i("dsds", url);
        Operations.getInstance(context).sendPostRequest(URL.ADD_POST_FAVORITE, params, new OnLoadFinished() {

            @Override
            public void onSuccess(String response) {
                Log.i("dsds", response);
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONObject post_fav = dataObj.getJSONObject("data");
                        Post_Favorite post_favorite = new Post_Favorite(post_fav.getLong("id"), post_fav.getLong("post_id"), post_fav.getLong("user_id"), System.currentTimeMillis());
                        Post_FavoriteDAO.addPostFavorite(post_favorite);
                        listener.onSuccess();
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });

    }

    public static void forget_password(final Context context, String email, String newPassword, String reNewPassword, final OnForgetPasswordListener listener) {
        String url = URL.UPDATE_PASSWORD + "?" + URL.URLParameters.EMAIL + "=" + email + "&"
                + URL.URLParameters.NEW_PASWORD + "=" + newPassword + "&"
                + URL.URLParameters.CONFIRM_NEW_PASWORD + "=" + reNewPassword;
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.EMAIL, email + "");
        params.put(URL.URLParameters.NEW_PASWORD, newPassword);
        params.put(URL.URLParameters.CONFIRM_NEW_PASWORD, reNewPassword);
        Log.i("vcvbbvcb", url);
        Operations.getInstance(context).sendGetRequest(url, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                Log.i("vcvbbvcb", response);
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        listener.success(GNLConstants.getStatus(status_code));
                    } else {
                        listener.fail(GNLConstants.getStatus(status_code));
                    }
                } catch (JSONException e) {
                    listener.fail(GNLConstants.getStatus(100));
                }

            }

            @Override
            public void onFail(String error) {
                listener.fail(error);
            }
        });

    }

    public static void removePostFavorite(final Context context, final long postId, final long uid, final OnPostFavoriteListener listener) {
        String url = URL.REMOVE_POST_FAVORITE + "?" + URL.URLParameters.USER_ID + "=" + uid + "&"
                + URL.URLParameters.POST_ID + "=" + postId;
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.USER_ID, uid + "");
        params.put(URL.URLParameters.POST_ID, postId + "");
        Log.i("dfdffdd", url);
        Operations.getInstance(context).sendPostRequest(URL.REMOVE_POST_FAVORITE, params, new OnLoadFinished() {

            @Override
            public void onSuccess(String response) {
                Log.i("dfdffdd", response);

                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        Post_FavoriteDAO.deletePostFavorite(postId, uid);
                        listener.onSuccess();
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });
    }

    public static void deletePost(final Context context, final long user_id, final long postId, final OnPostDeletedListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.POST_ID, postId + "");
        Operations.getInstance(context).sendPostRequest(URL.DELETE_POST, params, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        PostsDAO.deletePost(postId);
                        //CASCADE
                        CommentsDAO.deleteCommentByPost(postId);
                        Post_FavoriteDAO.deletePostFavoriteByPost(postId);
                        Users user = UsersDAO.getUser(user_id);
                        user.asks = user.asks - 1;
                        user.save();

                        listener.onDeleted();
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100));
                }
            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);
            }
        });

    }

    public static void sendMessage(final Context context, long user_id, String message, final OnSendMessageListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.USER_ID, user_id + "");
        params.put(URL.URLParameters.MESSAGE, message);
        Operations.getInstance(context).sendPostRequest(URL.CONTACT_US, params, new OnLoadFinished() {

            @Override
            public void onSuccess(String response) {
                Log.i("cxx", response);
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        listener.onSuccess();
                    } else {
                        listener.onFail(GNLConstants.getStatus(status_code));
                    }

                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100));
                }

            }

            @Override
            public void onFail(String error) {
                listener.onFail(error);

            }
        });

    }

    public static void addCommentAction(final Context context, final long commentId, long userId, final int action, final OnCommentActionListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.USER_ID, userId + "");
        params.put(URL.URLParameters.COMMENT_ID, commentId + "");
        params.put(URL.URLParameters.ACTION_TYPE, action + "");
        Log.i("cxcxcx", commentId + ", " + URL.ADD_ACTION);

        Operations.getInstance(context).sendPostRequest(URL.ADD_ACTION, params, new OnLoadFinished() {

            @Override
            public void onSuccess(String response) {
                Log.i("cxcxcx", response);
                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONObject data = dataObj.getJSONObject("data");
                        long id = Long.parseLong(data.getString("id"));
                        long comment_id = Long.parseLong(data.getString("comment_id"));
                        long user_id = Long.parseLong(data.getString("user_id"));
                        int action_type = Integer.parseInt(data.getString("action_type"));
//                        User_ActionsDAO.addUserAction();
                        listener.onActionSent(new User_Actions(id, comment_id, user_id, System.currentTimeMillis(), action_type));
                    } else
                        listener.onFail(GNLConstants.getStatus(status_code));
                } catch (JSONException e) {
                    listener.onFail(GNLConstants.getStatus(100));
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
            public void onSuccess(String response) {
                try {
                    Log.i("dfdcxcx", response);
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONObject post = dataObj.getJSONObject("data");
                        long id = post.getLong("id");
                        String text = post.getString("text");
                        String image = post.getString("image");
                        int is_hidden = post.getInt("is_hidden");
                        long category_id = post.getLong("category_id");
                        long user_id = post.getLong("user_id");
//                        int comments_no = post.getInt("comment_no");
                        long created_at = post.getLong("updated_at");
                        Posts postItem = new Posts(id, text, image.equals("null") ? null : image, created_at, user_id, category_id, is_hidden, 0, 0, 0, 0);
                        PostsDAO.addPost(postItem);
                        Users user = UsersDAO.getUser(user_id);
                        user.asks = user.asks + 1;
                        user.save();

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
        uploadImage.addStringProperty(URL.URLParameters.CATEGORY_ID, category_id + "");
        uploadImage.addStringProperty(URL.URLParameters.TEXT, text);
//        uploadImage.addStringProperty(URL.URLParameters.DATE, date + "");
        uploadImage.addStringProperty(URL.URLParameters.IS_HIDDEN, is_hidden + "");
        if (!TextUtils.isEmpty(picturePath))
            uploadImage.addFileProperty(URL.URLParameters.IMAGE, picturePath);
        uploadImage.sendRequest();
    }


    public static void editPost(final Context context, long post_id, long user_id, long category_id, String postDesc, int imageState, String picturePath, long date, int isHidden, final OnEditPostListener listener) {

        UploadImage uploadImage = new UploadImage(context, URL.EDIT_POST, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                try {
                    Log.i("dfdcxcx", response);
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONObject post = dataObj.getJSONArray("data").optJSONObject(0);
                        long id = post.getLong("id");
                        String text = post.getString("text");
                        String image = post.getString("image");
                        int is_hidden = post.getInt("is_hidden");
                        long category_id = post.getLong("category_id");
                        long user_id = post.getLong("user_id");
//                        int comments_no = post.getInt("comment_no");
                        long created_at = post.getLong("updated_at");
                        Posts postItem = new Posts(id, text, image.equals("null") ? null : image, created_at, user_id, category_id, is_hidden, -1, -1, -1, -1);
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
        uploadImage.addStringProperty(URL.URLParameters.ID, post_id + "");
        uploadImage.addStringProperty(URL.URLParameters.USER_ID, user_id + "");
        uploadImage.addStringProperty(URL.URLParameters.CATEGORY_ID, category_id + "");
        uploadImage.addStringProperty(URL.URLParameters.TEXT, postDesc);
//        uploadImage.addStringProperty(URL.URLParameters.DATE, date + "");
        uploadImage.addStringProperty(URL.URLParameters.IS_HIDDEN, isHidden + "");
//        Log.i("cxcxcvc", picturePath);
        if (imageState == 2)
            uploadImage.addFileProperty(URL.URLParameters.IMAGE, picturePath);
        else
            uploadImage.addStringProperty(URL.URLParameters.IMAGE, picturePath == null ? "null" : picturePath);
        uploadImage.sendRequest();
    }

    public static void addComment(final Context context, final String comment, String picturePath, long postId, long user_id, final OnCommentAddListener listener) {
        UploadImage uploadImage = new UploadImage(context, URL.ADD_COMMENT, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                Log.i("sdsdsd", response);
                Log.i("sdsdsd", URL.ADD_COMMENT);

                try {
                    JSONObject dataObj = new JSONObject(response);
                    int status_code = dataObj.getInt("statusCode");
                    int status = dataObj.getInt("status");
                    if (status == 0) {
                        JSONObject comment = dataObj.getJSONObject("data");
                        long comment_id = comment.getLong("id");
                        String comment_text = comment.getString("comment");
                        String comment_image = comment.getString("image");
                        long user_id = comment.getLong("user_id");
                        long post_id = comment.getLong("post_id");
                        long comment_date = comment.getLong("updated_at");
                        Comments newComment = new Comments(comment_id, comment_text, comment_image.equals("null") ? null : comment_image, comment_date, user_id, post_id, 0, 0);
                        CommentsDAO.addComment(newComment);
                        Users user = UsersDAO.getUser(user_id);
                        user.answers = user.answers + 1;
                        user.save();
                        listener.onAdded(newComment);
                    } else
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
        uploadImage.addStringProperty(URL.URLParameters.COMMENT, comment);
        uploadImage.addStringProperty(URL.URLParameters.POST_ID, postId + "");
        uploadImage.addStringProperty(URL.URLParameters.USER_ID, user_id + "");
        if (!TextUtils.isEmpty(picturePath))
            uploadImage.addFileProperty(URL.URLParameters.IMAGE, picturePath);
        uploadImage.sendRequest();
    }

    public static void updateProfile(final Context context, long id, String fname, String lname, final String password, int is_public, String picturePath, ArrayList<Category> selectedCategories, final OnUpdateProfileListener listener) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < selectedCategories.size(); i++)
//            sb.append(selectedCategories.get(i).getServerID()).append(i != selectedCategories.size() - 1 ? "," : "");
//        Log.i("sdsddsfdf", URL.UPDATE_PROFILE);
        UploadImage uploadImage = new UploadImage(context, URL.UPDATE_PROFILE, new OnLoadFinished() {
            @Override
            public void onSuccess(String response) {
                Log.i("sdssds", response);
                try {
                    JSONObject data_obbj = new JSONObject(response);
                    int status_code = data_obbj.getInt("statusCode");
                    int status = data_obbj.getInt("status");
                    if (status == 0) {
                        JSONObject user = data_obbj.getJSONObject("data");
                        long uid = user.getLong("id");
                        String f_name = user.getString("f_name");
                        String l_name = user.getString("l_name");
                        String email = user.getString("email");
                        String image = user.getString("image");
                        int active = Integer.parseInt(user.getString("active"));
                        long created_at = user.getLong("created_at");
                        long last_login = user.getLong("last_login");
                        String code = user.getString("code");
                        String mobile = user.getString("mobile");
                        int is_public = Integer.parseInt(user.getString("is_public"));
                        int no_tasks = user.getInt("no_ask");
                        int no_answers = user.getInt("no_answer");
                        UsersDAO.addUser(new Users(uid, f_name, l_name, null, email, null, image.equals("null") ? null : image, created_at, active, last_login, mobile, is_public, code, no_answers, no_tasks, -1f));
                        context.getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                                .edit().putString(GNLConstants.SharedPreference.PASSWORD_KEY, password).commit();

                        listener.onSuccess();
                    } else
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
        uploadImage.addStringProperty(URL.URLParameters.ID, id + "");
        uploadImage.addStringProperty(URL.URLParameters.FNAME, fname);
        uploadImage.addStringProperty(URL.URLParameters.LNAME, lname);
        uploadImage.addStringProperty(URL.URLParameters.PASSWORD, password);
        uploadImage.addStringProperty(URL.URLParameters.IS_PUBLIC, is_public + "");
//        uploadImage.addStringProperty(URL.URLParameters.CATEGORIES_ID, sb.toString());
//        uploadImage.addStringProperty(URL.URLParameters.IS_PUBLIC, 0+"");
        uploadImage.addStringProperty(URL.URLParameters.LAST_LOGIN, UsersDAO.getUser(id).getLastLogin() + "");
        if (!TextUtils.isEmpty(picturePath))
            uploadImage.addFileProperty(URL.URLParameters.IMAGE, picturePath);
        uploadImage.sendRequest();
    }


    private static String encode(String s) {
        return Uri.encode(s, "utf-8");
    }

    public static void deletComment(final Context context, final long user_id, final long commentId, final OnDeleteCommentListener listener) {
        String url = URL.DELETE_COMMENT + "?" + URL.URLParameters.COMMENT_ID + "=" + commentId;
        Operations.getInstance(context).sendGetRequest(url, new OnLoadFinished() {

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject data_obbj = new JSONObject(response);
                    int status_code = data_obbj.getInt("statusCode");
                    int status = data_obbj.getInt("status");
                    if (status == 0) {
                        CommentsDAO.deleteComment(commentId);
                        User_ActionsDAO.delteActionByComment(commentId);
                        Users user = UsersDAO.getUser(user_id);
                        user.answers = user.answers - 1;
                        user.save();

                        listener.onDeleted();
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

}
