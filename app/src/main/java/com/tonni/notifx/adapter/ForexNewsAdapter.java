package com.tonni.notifx.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;


import com.tonni.notifx.R;
import com.tonni.notifx.models.ForexNewsItem;

import java.util.List;

public class ForexNewsAdapter extends RecyclerView.Adapter<ForexNewsAdapter.ForexNewsViewHolder> {
    private List<ForexNewsItem> forexNewsItems;
    private Context context;

    public ForexNewsAdapter(List<ForexNewsItem> forexNewsItems, Context context) {
        this.forexNewsItems = forexNewsItems;
        this.context=context;
    }

    @NonNull
    @Override
    public ForexNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forex_news, parent, false);
        return new ForexNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForexNewsViewHolder holder, int position) {
        ForexNewsItem newsItem = forexNewsItems.get(position);
        holder.timeTextView.setText(newsItem.getTime());
        holder.nameTextView.setText(newsItem.getCurrency());
        holder.eventTextView.setText(newsItem.getName()+" "+newsItem.getName()+" "+newsItem.getName()+" "+newsItem.getName()+" "+newsItem.getName()+" "+newsItem.getName()+" "+newsItem.getName()+" "+newsItem.getName());
        holder.eventTextView.setSelected(true);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, String.valueOf(newsItem.getName()), Toast.LENGTH_SHORT).show();
            }
        });


        if(newsItem.getStatus().equals("red")){
            // Load the image using Glide
            Glide.with(context)
                    .load(R.drawable.ic_news_red_24)
                    .placeholder(R.drawable.ic_news_red_24) // Optional placeholder
                    .into(holder.img);
        }
        if(newsItem.getStatus().equals("ora")){
            // Load the image using Glide
            Glide.with(context)
                    .load(R.drawable.ic_news_orange_24)
                    .placeholder(R.drawable.ic_news_orange_24) // Optional placeholder
                    .into(holder.img);
        }
        if(newsItem.getStatus().equals("yel")){
            // Load the image using Glide
            Glide.with(context)
                    .load(R.drawable.ic_baseline_circle_24)
                    .placeholder(R.drawable.ic_baseline_circle_24) // Optional placeholder
                    .into(holder.img);
        }



    }

    public void addAll(List<ForexNewsItem> list){
        forexNewsItems.clear();
        forexNewsItems.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return forexNewsItems.size();
    }

    public static class ForexNewsViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView, nameTextView,eventTextView;
        ImageView img;

        public ForexNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.textViewTime);
            nameTextView = itemView.findViewById(R.id.textViewName);
            eventTextView = itemView.findViewById(R.id.event);
            img=itemView.findViewById(R.id.status);
        }
    }
}
