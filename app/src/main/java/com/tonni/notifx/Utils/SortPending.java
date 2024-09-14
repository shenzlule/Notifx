package com.tonni.notifx.Utils;

import android.util.Log;

import com.tonni.notifx.models.PendingPrice;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortPending {

    public static ArrayList<PendingPrice> sort(ArrayList<PendingPrice> pendingPrices){

        ArrayList<PendingPrice> pendingPricesList= new ArrayList<>();

        pendingPricesList.addAll(pendingPrices);

        Collections.sort(pendingPricesList, new Comparator<PendingPrice>() {
            @Override
            public int compare(PendingPrice o1, PendingPrice o2) {
                return o1.getPair_visible().compareTo(o2.getPair_visible());
            }
        });

        for (int i = 0; i < pendingPricesList.size(); i++) {
            Log.d("Sorted", pendingPricesList.get(i).getPair_visible());

        }


        return pendingPricesList;
    }
}
