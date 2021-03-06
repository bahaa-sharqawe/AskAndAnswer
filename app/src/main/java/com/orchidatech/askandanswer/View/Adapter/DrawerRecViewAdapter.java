package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.orchidatech.askandanswer.Activity.SplashScreen;
import com.orchidatech.askandanswer.Constant.GNLConstants;
import com.orchidatech.askandanswer.Constant.URL;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.Users;
import com.orchidatech.askandanswer.Entity.DrawerItem;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnMainDrawerItemClickListener;
import com.orchidatech.askandanswer.View.Utils.FontManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Bahaa on 8/11/2015.
 */
public class DrawerRecViewAdapter extends RecyclerView.Adapter<DrawerRecViewAdapter.ItemViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_MENU = 1;
    private final FontManager fontManager;
    private final ColorGenerator generator;
    private SharedPreferences pref;

    ArrayList<DrawerItem> items;
    Context context;
    static OnMainDrawerItemClickListener listener;

    public DrawerRecViewAdapter(Context context, ArrayList<DrawerItem> items, OnMainDrawerItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        pref = context.getSharedPreferences(GNLConstants.SharedPreference.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        fontManager = FontManager.getInstance(context.getAssets());
        generator = ColorGenerator.MATERIAL;


    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//Inflating layouts
        View itemView;
        if (viewType == TYPE_HEADER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);
            return new ItemViewHolder(itemView, TYPE_HEADER);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_drawer_item, parent, false);
            return new ItemViewHolder(itemView, TYPE_MENU);
        }


    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {//Display the data at the specified position
        if (holder.viewType == TYPE_HEADER) {//complete the code here
            final Users user = UsersDAO.getUser(pref.getLong(GNLConstants.SharedPreference.ID_KEY, -1));
            if (user != null) {
                holder.tv_person_name.setText(user.getFname() + " " + user.getLname());
                holder.tv_person_name.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));

                holder.tv_person_email.setText(user.getEmail());
                holder.tv_person_email.setTypeface(fontManager.getFont(FontManager.ROBOTO_LIGHT));

//                holder.load_image_progress.getIndeterminateDrawable().setColorFilter(Color.parseColor("#249885"), android.graphics.PorterDuff.Mode.MULTIPLY);
                String letter = user.getFname().charAt(0) + " " + user.getLname().charAt(0);

                final TextDrawable drawable = TextDrawable.builder().beginConfig().fontSize((int) context.getResources().getDimension(R.dimen.drawer_user_letters_font_size)).endConfig()
                        .buildRound(letter.toUpperCase(), generator.getRandomColor());
                if (user != null && !user.getImage().equals(URL.DEFAULT_IMAGE)) {
                    Picasso.with(context).load(Uri.parse(user.getImage())).skipMemoryCache().into(holder.iv_profile, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.tv_person_photo.setVisibility(View.INVISIBLE);
                            holder.iv_profile.setVisibility(View.VISIBLE);
//                            holder.load_image_progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.iv_profile.setVisibility(View.INVISIBLE);
//                            holder.tv_person_photo.setImageDrawable(drawable);

                            holder.tv_person_photo.setVisibility(View.VISIBLE);
                            holder.tv_person_photo.setText((user.getFname().charAt(0) + " " + user.getLname().charAt(0)).toUpperCase());
//                            holder.load_image_progress.setVisibility(View.GONE);
                        }
                    });
                }
                else{
                    holder.iv_profile.setVisibility(View.INVISIBLE);
//                    holder.tv_person_photo.setImageDrawable(drawable);
                    holder.tv_person_photo.setVisibility(View.VISIBLE);
                    holder.tv_person_photo.setText((user.getFname().charAt(0) + " " + user.getLname().charAt(0)).toUpperCase());
                }
            }
        } else {
            DrawerItem item = items.get(position - 1);//0 is header
            holder.tv_drawer_item.setText(item.getTitle());
            holder.tv_drawer_item.setTypeface(fontManager.getFont(FontManager.ROBOTO_MEDIUM));
            if(items.get(position-1).isSelected()){
                holder.tv_drawer_item.setTextColor(Color.parseColor("#2dbda6"));
                holder.ll_item.setBackgroundColor(Color.parseColor("#e9e9e9"));
                holder.iv_drawer_item.setImageResource(item.getImage_on());

            }else{
                holder.tv_drawer_item.setTextColor(Color.parseColor("#4d4d4d"));
                holder.ll_item.setBackgroundColor(Color.parseColor("#ffffff"));
                holder.iv_drawer_item.setImageResource(item.getImage());
            }
        }
    }

    @Override
    public int getItemCount() {//Returns the total number of items in the data set hold by the adapter
        return items.size() + 1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_person_photo;
        int viewType;
        //header components
        TextView tv_person_name;
        TextView tv_person_email;
        ImageView iv_profile;
//        ProgressBar load_image_progress;
        //menu components
        ImageView iv_drawer_item;
        TextView tv_drawer_item;
        LinearLayout ll_item;

        public ItemViewHolder(final View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            if (viewType == TYPE_HEADER) {
                tv_person_name = (TextView) itemView.findViewById(R.id.tv_person_name);
                tv_person_email = (TextView) itemView.findViewById(R.id.tv_person_email);
                iv_profile = (ImageView) itemView.findViewById(R.id.iv_profile);
//                load_image_progress = (ProgressBar) itemView.findViewById(R.id.load_image_progress);
                tv_person_photo = (TextView) itemView.findViewById(R.id.tv_person_photo);

            } else {
                iv_drawer_item = (ImageView) itemView.findViewById(R.id.iv_drawer_item);
                tv_drawer_item = (TextView) itemView.findViewById(R.id.tv_drawer_item);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(getAdapterPosition() - 1);// -1 beacause the header is in position 0
                    }
                });
                ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
//                ll_item.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        if(event.getAction() == MotionEvent.ACTION_DOWN  || event.getAction() == MotionEvent.ACTION_HOVER_ENTER ){
//                            ll_item.setBackgroundColor(Color.parseColor("#e9e9e9"));
//                        }else if(event.getAction() == MotionEvent.ACTION_HOVER_EXIT){
//                            ll_item.setBackgroundColor(Color.parseColor("#ffffff"));
//                        }
//                        return false;
//                    }
//                });
            }

        }
    }

    @Override
    public int getItemViewType(int position) {//Return the view type of the item at position
        return position == 0 ? TYPE_HEADER : TYPE_MENU;
    }
}
