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

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;

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

        String DOW_str="";
        try {
            Calendar calendar = newsItem.getCalendar();
            int DOW = calendar.get(Calendar.DAY_OF_WEEK);

            if(DOW==1){
                DOW_str="SUNDAY";
            }else  if(DOW==2){
                DOW_str="MONDAY";
            }else  if(DOW==3){
                DOW_str="TUESDAY";
            }else  if(DOW==4){
                DOW_str="WEDNESDAY";
            }else  if(DOW==5){
                DOW_str="THURSDAY";
            }else  if(DOW==6){
                DOW_str="FRIDAY";
            }else  if(DOW==7){
                DOW_str="SATURDAY";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String final_DOW_str = DOW_str;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(context,  String.valueOf(newsItem.getName())+" "+"["+ final_DOW_str+"]", Toast.LENGTH_SHORT, true).show();
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
