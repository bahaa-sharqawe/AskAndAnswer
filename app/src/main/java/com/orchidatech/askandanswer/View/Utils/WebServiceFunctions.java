package com.orchidatech.askandanswer.View.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Constant.URL;
import com.orchidatech.askandanswer.Database.DAO.CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.CommentsDAO;
import com.orchidatech.askandanswer.Database.DAO.Post_FavoriteDAO;
import com.orchidatech.askandanswer.Database.DAO.PostsDAO;
import com.orchidatech.askandanswer.Database.DAO.User_CategoriesDAO;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Category;
import com.orchidatech.askandanswer.Database.Model.Post_Favorite;
import com.orchidatech.askandanswer.Database.Model.Posts;
import com.orchidatech.askandanswer.Database.Model.User_Categories;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnAddPostListener;
import com.orchidatech.askandanswer.View.Interface.OnCategoriesFetchedListener;
import com.orchidatech.askandanswer.View.Interface.OnEditPostListener;
import com.orchidatech.askandanswer.View.Interface.OnLoadFinished;
import com.orchidatech.askandanswer.View.Interface.OnLoginListener;
import com.orchidatech.askandanswer.View.Interface.OnPostDeletedListener;
import com.orchidatech.askandanswer.View.Interface.OnPostFavoriteListener;
import com.orchidatech.askandanswer.View.Interface.OnRegisterListener;
import com.orchidatech.askandanswer.View.Interface.OnSendCategoriesListener;
import com.orchidatech.askandanswer.View.Interface.OnUpdateProfileListener;
import com.orchidatech.askandanswer.View.Interface.OnUploadImageListener;
import com.orchidatech.askandanswer.View.Interface.OnUserFavPostFetched;
import com.orchidatech.askandanswer.View.Interface.OnUserInfoFetched;
import com.orchidatech.askandanswer.View.Interface.OnUserPostFetched;
import com.orchidatech.askandanswer.WebService.Operations;

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

    public static void login(final Context context, String email, String password, final OnLoginListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.EMAIL, email);
        params.put(URL.URLParameters.PASSWORD, encode(password));

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
                        JSONArray data = o.getJSONArray("data");
                        JSONObject user = data.getJSONObject(0);
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
                        ArrayList<Long> user_categories = new ArrayList<Long>();
                        JSONArray user_category = user.getJSONArray("user_category");
                        for (int i = 0; i < user_category.length(); i++)
                            user_categories.add((Long) user_category.get(i));
                        Users _user = new Users(id, f_name, l_name, null, email, null, image, created_at, active, last_login, mobile, is_public, code);
                        UsersDAO.addUser(_user);
                        listener.onSuccess(id, user_categories);
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
                                final String image, final int active, final long creation_date, final long last_login, final String mobile, final int is_public, String password,
                                final OnRegisterListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put(URL.URLParameters.FNAME, encode(fname));
        params.put(URL.URLParameters.LNAME, encode(lname));
        params.put(URL.URLParameters.EMAIL, email);
        params.put(URL.URLParameters.IMAGE, image);
        params.put(URL.URLParameters.ACTIVE, active + "");
        params.put(URL.URLParameters.LAST_LOGIN, last_login + "");
        params.put(URL.URLParameters.MOBILE, mobile);
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
                        JSONArray data = o.getJSONArray("data");
                        JSONObject user = data.getJSONObject(0);
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
            sb.append(selectedCats.get(i).getServerID()).append(i != selectedCats.size() - 1 ? "," : "");
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
                            long category_id = category.getLong("category_id");
                            User_CategoriesDAO.addUserCategory(new User_Categories(id, uid, category_id));
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
        Operations.getInstance(context).getUserInfo(new OnLoadFinished(){

            @Override
            public void onSuccess(JSONObject o) {
                try {
                    int status_code = o.getInt("statusCode");
                    int status = o.getInt("status");
                    if (status == 0) {
                        JSONArray data = o.getJSONArray("data");
                        JSONObject user = data.getJSONObject(0);
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
                        listener.onDataFetched(_user);
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
                        Post_Favorite post_favorite = new Post_Favorite(post_fav.getLong("id"), pid, uid, post_fav.getLong("date"));
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

    public static void addPost(final Context context, final long user_id, long category_id, String postDesc, String picturePath, long date, int is_hidden, final OnAddPostListener listener) {
        Operations.getInstance(context).addPost(context, user_id, category_id, encode(postDesc), picturePath, date, is_hidden, new OnUploadImageListener() {
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
                        long category_id = post.getLong("category_id");
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
                                ArrayList<User_Categories> user_categories = new ArrayList<User_Categories>();
                                JSONArray arr_user_category = user.getJSONArray("user_category");
                                for (int i = 0; i < arr_user_category.length(); i++) {
                                    JSONObject user_category = arr_user_category.getJSONObject(i);
                                    long user_category_id = user_category.getLong("id");
                                    long category_id = user_category.getLong("category_id");
                                    user_categories.add(new User_Categories(user_category_id, uid, category_id));
                                }
                                ArrayList<User_Categories> allCurrentStored = new ArrayList<>(User_CategoriesDAO.getAllUserCategories(uid));
                                for(User_Categories currentUserCategory : allCurrentStored){
                                    boolean deleting = true;
                                    for(int i = 0; i < user_categories.size(); i++){
                                        if(currentUserCategory.getCategoryID() == user_categories.get(i).getCategoryID()){
                                            deleting = true;
                                            break;
                                        }
                                        if(deleting){
                                            User_CategoriesDAO.deleteUserCategory(currentUserCategory.getServerID(), uid);
                                            PostsDAO.deletePostsInCategory(uid, currentUserCategory.getCategoryID());
                                      ////find solution to remove favorite posts also, it is solvd im getAllUserPostFavorite method
                                        }

                                    }
                                }

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
