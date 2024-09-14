package com.tonni.notifx.frags.stats;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.tonni.notifx.R;

import es.dmoral.toasty.Toasty;


public class PieChartFrag extends SimpleFragment {

    @NonNull
    public static Fragment newInstance() {
        return new PieChartFrag();
    }

    @SuppressWarnings("FieldCanBeLocal")
    private PieChart chart;
    private PieChart chart2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_simple_pie, container, false);

        chart = v.findViewById(R.id.pieChart1);
        chart.getDescription().setEnabled(false);

        Typeface tf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");

        chart.setCenterTextTypeface(tf);
        chart.setCenterText(generateCenterText());
        chart.setCenterTextSize(6f);
        chart.setCenterTextTypeface(tf);

        // radius of the center hole in percent of maximum radius
        chart.setHoleRadius(35f);
        chart.setTransparentCircleRadius(40f);
        chart.getLegend().setEnabled(false);


//        Legend l = chart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);

        chart.setData(generatePieData());


        chart2 = v.findViewById(R.id.pieChart2);
        chart2.getDescription().setEnabled(false);


        chart2.setCenterTextTypeface(tf);
        chart2.setCenterText(generateCenterText_());
        chart2.setCenterTextSize(6f);
        chart2.setCenterTextTypeface(tf);

        // radius of the center hole in percent of maximum radius
        chart2.setHoleRadius(35f);
        chart2.setTransparentCircleRadius(40f);
        chart2.getLegend().setEnabled(false);

//        Legend l2 = chart2.getLegend();
//        l2.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l2.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l2.setDrawInside(false);

        chart2.setData(generatePieData_fails());

        return v;
    }

    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString("0/2000\nFilled");
        s.setSpan(new RelativeSizeSpan(2f), 0, 6, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 6, s.length(), 0);
        return s;
    }
    private SpannableString generateCenterText_() {
        SpannableString s = new SpannableString("0/2000\nFails");
        s.setSpan(new RelativeSizeSpan(2f), 0, 6, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 6, s.length(), 0);
        return s;
    }

    public  void update_stats(){
        chart.setData(generatePieData());
        chart2.setData(generatePieData_fails());
        Toasty.success(getContext(), "Stats Updated!", Toast.LENGTH_SHORT, true).show();
    }
}
