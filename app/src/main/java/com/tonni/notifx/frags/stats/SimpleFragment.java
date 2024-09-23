package com.tonni.notifx.frags.stats;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FileUtils;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.models.ApiCount;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
public abstract class SimpleFragment extends Fragment {

    private Typeface tf;
    protected Context context;
    private static final String FILE_NAME_API_COUNT = "api_count.json";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public SimpleFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected BarData generateBarData(int dataSets, float range, int count) {

        ArrayList<IBarDataSet> sets = new ArrayList<>();

        for(int i = 0; i < dataSets; i++) {

            ArrayList<BarEntry> entries = new ArrayList<>();

            for(int j = 0; j < count; j++) {
                entries.add(new BarEntry(j, (float) (Math.random() * range) + range / 4));
            }

            BarDataSet ds = new BarDataSet(entries, getLabel(i));
            ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
            sets.add(ds);
        }

        BarData d = new BarData(sets);
        d.setValueTypeface(tf);
        return d;
    }

    protected ScatterData generateScatterData(int dataSets, float range, int count) {

        ArrayList<IScatterDataSet> sets = new ArrayList<>();

        ScatterChart.ScatterShape[] shapes = ScatterChart.ScatterShape.getAllDefaultShapes();

        for(int i = 0; i < dataSets; i++) {

            ArrayList<Entry> entries = new ArrayList<>();

            for(int j = 0; j < count; j++) {
                entries.add(new Entry(j, (float) (Math.random() * range) + range / 4));
            }

            ScatterDataSet ds = new ScatterDataSet(entries, getLabel(i));
            ds.setScatterShapeSize(12f);
            ds.setScatterShape(shapes[i % shapes.length]);
            ds.setColors(ColorTemplate.COLORFUL_COLORS);
            ds.setScatterShapeSize(9f);
            sets.add(ds);
        }

        ScatterData d = new ScatterData(sets);
        d.setValueTypeface(tf);
        return d;
    }

    /**
     * generates less data (1 DataSet, 4 values)
     * @return PieData
     */
    protected PieData generatePieData() {

        int count = 3; // Number of slices in the pie chart
        ArrayList<PieEntry> entries = new ArrayList<>(); // Holds the Pie chart entries

        // Read API count data from JSON file
        String readJsonData_Api_Count = StorageUtils.readJsonFromFile(context, FILE_NAME_API_COUNT);
        Type listType_api_count = new TypeToken<List<ApiCount>>() {}.getType();
        ArrayList<ApiCount> api_count_list = new Gson().fromJson(readJsonData_Api_Count, listType_api_count);

//        api_count_list=null;
        // Fallback in case there's no data
        if (api_count_list == null || api_count_list.isEmpty()) {
            api_count_list = new ArrayList<>();
            api_count_list.add(new ApiCount(500, 500, 500, 500)); // Add default values if no data found
        }

        // Loop through and populate entries for the Pie chart
        for (int i = 0; i < count; i++) {
            ApiCount apiCount = api_count_list.get(0); // Assuming we're using the first entry

            // Calculate the percentage of each API success
            if (i == 0) {
                entries.add(new PieEntry((float) (apiCount.getApi_count_success_1() / 2000.0 * 360), "API 1"));
            } else if (i == 1) {
                entries.add(new PieEntry((float) (apiCount.getApi_count_success_2() / 2000.0 * 360), "API 2"));
            } else {
                // Calculate the remaining count for other APIs (or failures)
                float remaining = 2000 - (apiCount.getApi_count_success_1() + apiCount.getApi_count_success_2());
                entries.add(new PieEntry((float) (remaining / 2000.0 * 360), "Other APIs"));
            }
        }

        // Create PieDataSet and configure the appearance
        PieDataSet dataSet = new PieDataSet(entries, "API Stats");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Use predefined color templates
        dataSet.setSliceSpace(2f); // Space between slices
        dataSet.setValueTextColor(Color.BLACK); // Text color inside pie chart
        dataSet.setValueTextSize(12f); // Size of the value labels

        // Create the PieData object
        PieData data = new PieData(dataSet);
        data.setValueTypeface(tf); // Assuming 'tf' is a custom Typeface

        return data;
    }


    protected PieData generatePieData_fails() {

        int count = 3;

        ArrayList<PieEntry> entries1 = new ArrayList<>();

        entries1.clear();

        String readJsonData_Api_Count = StorageUtils.readJsonFromFile(context, FILE_NAME_API_COUNT);

        Type listType_api_count = new TypeToken<List<ApiCount>>() {
        }.getType();
        ArrayList<ApiCount> api_count_list = new Gson().fromJson(readJsonData_Api_Count, listType_api_count);

        if (true){
            api_count_list=new ArrayList<>();
            api_count_list.add(0,new ApiCount(500,500,500,500));
            api_count_list.set(0,new ApiCount(500,500,500,500));

        }


        for(int i = 0; i < count; i++) {

            if (i==1){
                entries1.add(new PieEntry((float) ((api_count_list.get(0).getApi_count_fail_1()/2000)*360), "Api " + (i+1)));
            }else if(i==0){
                entries1.add(new PieEntry((float) ((api_count_list.get(0).getApi_count_fail_2()/2000)*360), "Api " + (i+1)));
            }else {
                entries1.add(new PieEntry((float) (((2000-(api_count_list.get(0).getApi_count_fail_1()+api_count_list.get(0).getApi_count_fail_2()))/2000)*360), "Api " + (i+1)));

            }
        }

        PieDataSet ds1 = new PieDataSet(entries1, "Stats");
        ds1.setColors(ColorTemplate.MATERIAL_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.BLACK);
        ds1.setValueTextSize(12f);

        PieData d = new PieData(ds1);
        d.setValueTypeface(tf);

        return d;
    }

    protected LineData generateLineData() {

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        LineDataSet ds1 = new LineDataSet(FileUtils.loadEntriesFromAssets(context.getAssets(), "sine.txt"), "Sine function");
        LineDataSet ds2 = new LineDataSet(FileUtils.loadEntriesFromAssets(context.getAssets(), "cosine.txt"), "Cosine function");

        ds1.setLineWidth(2f);
        ds2.setLineWidth(2f);

        ds1.setDrawCircles(false);
        ds2.setDrawCircles(false);

        ds1.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        ds2.setColor(ColorTemplate.VORDIPLOM_COLORS[1]);

        // load DataSets from files in assets folder
        sets.add(ds1);
        sets.add(ds2);

        LineData d = new LineData(sets);
        d.setValueTypeface(tf);
        return d;
    }

    protected LineData getComplexity() {

        ArrayList<ILineDataSet> sets = new ArrayList<>();

        LineDataSet ds1 = new LineDataSet(FileUtils.loadEntriesFromAssets(context.getAssets(), "n.txt"), "O(n)");
        LineDataSet ds2 = new LineDataSet(FileUtils.loadEntriesFromAssets(context.getAssets(), "nlogn.txt"), "O(nlogn)");
        LineDataSet ds3 = new LineDataSet(FileUtils.loadEntriesFromAssets(context.getAssets(), "square.txt"), "O(n\u00B2)");
        LineDataSet ds4 = new LineDataSet(FileUtils.loadEntriesFromAssets(context.getAssets(), "three.txt"), "O(n\u00B3)");

        ds1.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        ds2.setColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        ds3.setColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        ds4.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);

        ds1.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        ds2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        ds3.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        ds4.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[3]);

        ds1.setLineWidth(2.5f);
        ds1.setCircleRadius(3f);
        ds2.setLineWidth(2.5f);
        ds2.setCircleRadius(3f);
        ds3.setLineWidth(2.5f);
        ds3.setCircleRadius(3f);
        ds4.setLineWidth(2.5f);
        ds4.setCircleRadius(3f);


        // load DataSets from files in assets folder
        sets.add(ds1);
        sets.add(ds2);
        sets.add(ds3);
        sets.add(ds4);

        LineData d = new LineData(sets);
        d.setValueTypeface(tf);
        return d;
    }

    private final String[] mLabels = new String[] { "Company A", "Company B", "Company C", "Company D", "Company E", "Company F" };

    private String getLabel(int i) {
        return mLabels[i];
    }
}
