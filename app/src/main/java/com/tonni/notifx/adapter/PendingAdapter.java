package com.tonni.notifx.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.tonni.notifx.R;
import com.tonni.notifx.frags.PendingFragment;
import com.tonni.notifx.models.PendingPrice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.ViewHolder> {

    private PendingFragment pendingFragment;
    List<PendingPrice> pendingPrices_list;
    Context context;

    public PendingAdapter(PendingFragment pendingFragment, List<PendingPrice> pendingPrices, Context context) {
        this.pendingFragment = pendingFragment;
        this.pendingPrices_list =pendingPrices;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingPrice pendingPrice = pendingPrices_list.get(position);

        if (pendingPrice.getFilled().equals("Not")) {
            // Assuming pendingPrice.getDate() returns the date as a string in milliseconds
            long dateMillis = Long.parseLong(pendingPrice.getDate());
            long currentMillis = System.currentTimeMillis();
            long elapsedMillis = currentMillis - dateMillis;
            long elapsedHours = elapsedMillis / (1000 * 60 * 60);

            holder.currencyPair.setText(pendingPrice.getPair());
            holder.price.setText("Price: " + String.valueOf(pendingPrice.getPrice()));
            holder.pending_mar_notes.setText(String.valueOf(pendingPrice.getNote()));
            holder.date.setText(String.valueOf("Date: " + convertMillisToDateString(Long.parseLong(pendingPrice.getDate()))));
            holder.elp.setText(String.valueOf("Elapsed hours: " + elapsedHours));
            holder.pending_mar_notes.setSelected(true);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "On watch for price to move "+pendingPrice.getDirection()+" "+pendingPrice.getPrice(), Toast.LENGTH_SHORT).show();
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Pending Item")
                            .setMessage("Are you sure you want to delete this pending item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            pendingFragment.deleteVideo(pendingPrice, holder.getAdapterPosition());
                                        }
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    return true;
                }
            });
        }

//        holder.itemView.setOnClickListener(v -> forexFragment.showInputDialog(position,forexCurrency));

    }

    @Override
    public int getItemCount() {
        return pendingPrices_list.size();
    }

    public static String convertMillisToDateString(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date(millis);
        return sdf.format(date);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView currencyPair, price, pending_mar_notes,date,elp;
        ImageView plusIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyPair = itemView.findViewById(R.id.currency_pair_pending);
            price = itemView.findViewById(R.id.pending_price);
            pending_mar_notes = itemView.findViewById(R.id.pending_mar_notes);
            date = itemView.findViewById(R.id.pending_date);
            elp = itemView.findViewById(R.id.pending_elp);
//            plusIcon = itemView.findViewById(R.id.plus_icon);
        }
    }
}
