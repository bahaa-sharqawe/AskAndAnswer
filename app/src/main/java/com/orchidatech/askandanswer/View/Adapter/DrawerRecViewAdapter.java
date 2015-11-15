package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orchidatech.askandanswer.Database.DAO.UsersDAO;
import com.orchidatech.askandanswer.Database.Model.User;
import com.orchidatech.askandanswer.Entity.DrawerItem;
import com.orchidatech.askandanswer.R;
import com.orchidatech.askandanswer.View.Interface.OnDrawerItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Bahaa on 8/11/2015.
 */
public class DrawerRecViewAdapter extends RecyclerView.Adapter<DrawerRecViewAdapter.ItemViewHolder> {

        private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
        private static final int TYPE_MENU = 1;

        ArrayList<DrawerItem> items;
        Context context;
        static OnDrawerItemClickListener listener;

    public DrawerRecViewAdapter(Context context, ArrayList<DrawerItem> items, OnDrawerItemClickListener listener){
        this.context = context;
        this.items = items;
        this.listener = listener;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//Inflating layouts
        View itemView;
        if (viewType == TYPE_HEADER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);
            return new ItemViewHolder(itemView, TYPE_HEADER);
        }else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_drawer_item, parent, false);
            return new ItemViewHolder(itemView, TYPE_MENU);
        }


    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {//Display the data at the specified position
        if(holder.viewType == TYPE_HEADER){//complete the code here
           User user =  UsersDAO.getUser(1);
            if(user != null) {
                holder.tv_person_name.setText(user.getUsername());
                holder.tv_person_email.setText(user.getEmail());
                Picasso.with(context).load(Uri.parse(user.getImage())).into(holder.iv_profile);
            }
        }else{
            DrawerItem item = items.get(position - 1);//0 is header
            holder.tv_drawer_item.setText(item.getTitle());
            holder.iv_drawer_item.setImageResource(item.getImage());
        }
    }

    @Override
    public int getItemCount() {//Returns the total number of items in the data set hold by the adapter
        return items.size()+1;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        int viewType;
       //header components
        TextView tv_person_name;
        TextView tv_person_email;
        ImageView iv_profile;
        //menu components
        ImageView iv_drawer_item;
        TextView tv_drawer_item;

        public ItemViewHolder(final View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            if(viewType == TYPE_HEADER){
                tv_person_name = (TextView) itemView.findViewById(R.id.tv_person_name);
                tv_person_email = (TextView) itemView.findViewById(R.id.tv_person_email);
                iv_profile = (ImageView) itemView.findViewById(R.id.iv_profile);
            }else{
                iv_drawer_item = (ImageView) itemView.findViewById(R.id.iv_drawer_item);
                tv_drawer_item = (TextView) itemView.findViewById(R.id.tv_drawer_item);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(getAdapterPosition());// -1 beacause the header is in position 0
                    }
                });
            }

        }
    }

    @Override
    public int getItemViewType(int position) {//Return the view type of the item at position
        return position==0?TYPE_HEADER:TYPE_MENU;
    }
}
