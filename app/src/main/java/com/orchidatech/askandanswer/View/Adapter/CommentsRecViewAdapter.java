package com.orchidatech.askandanswer.View.Adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orchidatech.askandanswer.Entity.Post;
import com.orchidatech.askandanswer.R;

import java.util.ArrayList;

/**
 * Created by Bahaa on 15/11/2015.
 */
public class CommentsRecViewAdapter extends RecyclerView.Adapter<CommentsRecViewAdapter.CommentsViewHolder>  {
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_FOOTER = 1;

    private int tempNum;
    private ArrayList<Post> posts;
    private Context context;

    public CommentsRecViewAdapter(Context context, ArrayList<Post> posts, int tempNum) {
        this.tempNum = tempNum;
        this.posts = posts;
        this.context = context;
    }


    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        //if (viewType == TYPE_HEADER)
        if(viewType == TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false);
            return new CommentsViewHolder(itemView, TYPE_FOOTER);
        }else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_profile, parent, false);
            return new CommentsViewHolder(itemView, TYPE_HEADER);
        }

    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        if(holder.viewType == TYPE_FOOTER ){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                   listener.onReached();
                    tempNum += 10;
                    notifyDataSetChanged();

                }
            }, 2000);
//            Toast.makeText(context, "last reached", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return tempNum+1;
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        int viewType;
        public CommentsViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == tempNum)
            return TYPE_FOOTER;
        return TYPE_HEADER;
    }
}
