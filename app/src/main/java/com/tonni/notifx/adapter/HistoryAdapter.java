package com.tonni.notifx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tonni.notifx.R;
import com.tonni.notifx.frags.HistoryFragment;
import com.tonni.notifx.models.ForexCurrency;
import com.tonni.notifx.models.PendingPrice;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<ForexCurrency> forexCurrencyList;
    private HistoryFragment historyFragment;


    public HistoryAdapter(List<ForexCurrency> forexCurrencyList, HistoryFragment forexFragment) {
        this.forexCurrencyList = forexCurrencyList;
        this.historyFragment = forexFragment;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forex_currency_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForexCurrency forexCurrency = forexCurrencyList.get(position);



    }

    @Override
    public int getItemCount() {
        return forexCurrencyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView currencyPair, alertNumber,pending_mar;
        ImageView plusIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyPair = itemView.findViewById(R.id.currency_pair);
            alertNumber = itemView.findViewById(R.id.alertNumber);
            pending_mar = itemView.findViewById(R.id.pending_mar);
//            plusIcon = itemView.findViewById(R.id.plus_icon);
        }
    }
}
