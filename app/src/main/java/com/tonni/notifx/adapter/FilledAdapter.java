package com.tonni.notifx.adapter;

import android.content.Context;
import android.content.DialogInterface;
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
import com.tonni.notifx.Utils.TimeConverter;
import com.tonni.notifx.frags.FilledFragment;
import com.tonni.notifx.models.PendingPrice;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class FilledAdapter extends RecyclerView.Adapter<FilledAdapter.ViewHolder> {


    List<PendingPrice> filledPrices_list;
    Context context;
    FilledFragment filledFragment;
    private static  final DecimalFormat decfor = new DecimalFormat("0.000");

    public FilledAdapter(FilledFragment filledFragment,List<PendingPrice> pendingPrices, Context context) {
        this.filledPrices_list =pendingPrices;
        this.context=context;
        this.filledFragment=filledFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filled_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingPrice pendingPrice = filledPrices_list.get(position);




        if (pendingPrice.getFilled().equals("Yes")) {
            // Assuming pendingPrice.getDate() returns the date as a string in milliseconds
            // Assuming pendingPrice.getDate() returns the date as a string in milliseconds
            double dateMillis = Long.parseLong(pendingPrice.getDate_filled());
            double currentMillis = System.currentTimeMillis();
            double elapsedMillis = currentMillis - dateMillis;
            double elapsedHours = elapsedMillis / ((1000 * 60 * 60));
            decfor.setRoundingMode(RoundingMode.UP);

            holder.currencyPair.setText(pendingPrice.getPair());
            holder.price.setText("Price: " + String.valueOf(pendingPrice.getPrice()));
            holder.pending_mar_notes.setText(String.valueOf(pendingPrice.getNote()));
            holder.date.setText(String.valueOf("Date: " + convertMillisToDateString(Long.parseLong(pendingPrice.getDate_filled()))));
            holder.pending_mar_notes.setSelected(true);
            holder.elp.setText(String.valueOf("ET: " + TimeConverter.convertHours(Double.parseDouble(decfor.format(elapsedHours)))));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toasty.info(context, String.valueOf(pendingPrice.getNote().toString()), Toast.LENGTH_SHORT, true).show();
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Filled Item")
                            .setMessage("Are you sure you want to delete this Filled item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        filledFragment.deleteFilledPos( holder.getAdapterPosition());
                                    }
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return filledPrices_list.size();
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
            currencyPair = itemView.findViewById(R.id.currency_pair_filled);
            price = itemView.findViewById(R.id.filled_price);
            pending_mar_notes = itemView.findViewById(R.id.filled_mar_notes);
            date = itemView.findViewById(R.id.filled_date);
            elp = itemView.findViewById(R.id.filled_elp);
//            plusIcon = itemView.findViewById(R.id.plus_icon);
        }
    }
}
