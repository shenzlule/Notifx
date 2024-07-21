package com.tonni.notifx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tonni.notifx.R;
import com.tonni.notifx.models.ForexCurrency;
import com.tonni.notifx.frags.ForexFragment;
import com.tonni.notifx.models.PendingPrice;

import java.util.List;

public class ForexCurrencyAdapter extends RecyclerView.Adapter<ForexCurrencyAdapter.ViewHolder> {

    private List<ForexCurrency> forexCurrencyList;
    private ForexFragment forexFragment;
    List<PendingPrice> pendingPrices;

    public ForexCurrencyAdapter(List<ForexCurrency> forexCurrencyList, ForexFragment forexFragment, List<PendingPrice> pendingPrices) {
        this.forexCurrencyList = forexCurrencyList;
        this.forexFragment = forexFragment;
        this.pendingPrices=pendingPrices;
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
        int alertTracker=0;
        for (int i = 0; i < pendingPrices.size(); i++) {
            if (pendingPrices.get(i).getPair().equals(forexCurrency.getBaseCurrency() + "/" + forexCurrency.getQuoteCurrency())){
                alertTracker++;
            }

        }
        holder.currencyPair.setText(forexCurrency.getBaseCurrency() + "/" + forexCurrency.getQuoteCurrency());
        holder.alertNumber.setText(String.valueOf(alertTracker));
        holder.pending_mar.setSelected(true);

        holder.itemView.setOnClickListener(v -> forexFragment.showInputDialog(position,forexCurrency));
        alertTracker=0;
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
