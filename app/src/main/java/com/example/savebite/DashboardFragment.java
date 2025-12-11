package com.example.savebite;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private PieChart pieChart;
    private TextView txtTotal, statGood, statExpired, statConsumed;
    private DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        pieChart = view.findViewById(R.id.pieChart);
        txtTotal = view.findViewById(R.id.txtTotal);
        statGood = view.findViewById(R.id.statGood);
        statExpired = view.findViewById(R.id.statExpired);
        statConsumed = view.findViewById(R.id.statConsumed);

        db = new DatabaseHelper(getContext());

        setupPieChart();
        loadChartData();
        return view;
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleRadius(58f);
        pieChart.setDrawCenterText(false);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadChartData() {
        List<PantryItem> list = db.getAllItems();
        int good = 0, warning = 0, expired = 0, consumed = 0;

        for(PantryItem item : list) {
            // Priority Check: Is it consumed?
            if (item.getIsConsumed() == 1) {
                consumed++;
                continue; // Skip other status checks if consumed
            }

            String status = item.getStatus();
            if(status.equals("good")) good++;
            else if(status.equals("warning")) warning++; // Treat warning as good for simplicity in cards
            else expired++;
        }

        // Update Text Views
        txtTotal.setText(String.valueOf(list.size()));
        statGood.setText(String.valueOf(good + warning)); // Combine Good & Warning for "Fresh" card
        statExpired.setText(String.valueOf(expired));
        statConsumed.setText(String.valueOf(consumed));

        // Update Pie Chart Data
        ArrayList<PieEntry> entries = new ArrayList<>();
        if(good > 0) entries.add(new PieEntry(good, "Fresh"));
        if(warning > 0) entries.add(new PieEntry(warning, "Expiring"));
        if(expired > 0) entries.add(new PieEntry(expired, "Expired"));
        if(consumed > 0) entries.add(new PieEntry(consumed, "Consumed"));

        if (entries.isEmpty()) {
            pieChart.setData(null);
            pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();
        if(good > 0) colors.add(ContextCompat.getColor(getContext(), R.color.chart_good));
        if(warning > 0) colors.add(ContextCompat.getColor(getContext(), R.color.chart_warning));
        if(expired > 0) colors.add(ContextCompat.getColor(getContext(), R.color.chart_expired));
        if(consumed > 0) colors.add(ContextCompat.getColor(getContext(), R.color.accent_green));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);

        pieChart.setData(data);
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        pieChart.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChartData(); // Refresh data when fragment resumes
    }
}
