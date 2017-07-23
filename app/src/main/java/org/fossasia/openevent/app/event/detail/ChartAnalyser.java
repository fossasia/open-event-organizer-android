package org.fossasia.openevent.app.event.detail;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.contract.IUtilModel;
import org.fossasia.openevent.app.data.repository.AttendeeRepository;
import org.fossasia.openevent.app.utils.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Completable;

import static org.fossasia.openevent.app.event.detail.TicketAnalyser.TICKET_DONATION;
import static org.fossasia.openevent.app.event.detail.TicketAnalyser.TICKET_FREE;
import static org.fossasia.openevent.app.event.detail.TicketAnalyser.TICKET_PAID;

public class ChartAnalyser {
    private final IUtilModel utilModel;
    private final AttendeeRepository attendeeRepository;

    private LineDataSet freeSet;
    private LineDataSet paidSet;
    private LineDataSet donationSet;
    private LineData lineData = new LineData();

    private long maxTicketSale;

    private Map<String, Long> freeMap = new HashMap<>();
    private Map<String, Long> paidMap = new HashMap<>();
    private Map<String, Long> donationMap = new HashMap<>();

    @Inject
    protected ChartAnalyser(IUtilModel utilModel, AttendeeRepository attendeeRepository) {
        this.utilModel = utilModel;
        this.attendeeRepository = attendeeRepository;

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#992ecc71"));
        gridPaint.setStrokeWidth(5);
    }

    private void clearData() {
        freeMap.clear();
        paidMap.clear();
        donationMap.clear();

        lineData.clearValues();
        maxTicketSale = 0;
    }

    public Completable loadData(long eventId) {
        clearData();
        return attendeeRepository.getAttendees(eventId, false)
            .doOnNext(attendee -> {
                String date = attendee.getOrder().getCompletedAt();
                switch (attendee.getTicket().getType()) {
                    case TICKET_FREE:
                        addDataPoint(freeMap, date);
                        break;
                    case TICKET_DONATION:
                        addDataPoint(donationMap, date);
                        break;
                    case TICKET_PAID:
                        addDataPoint(paidMap, date);
                        break;
                    default:
                        // No action
                }
            })
            .toList()
            .toCompletable()
            .doOnComplete(() -> {
                normalizeDataSet();
                freeSet = setData(freeMap, "Free");
                paidSet = setData(paidMap, "Paid");
                donationSet = setData(donationMap, "Donation");
                prepare();
            });
    }

    private void putIfNotPresent(Map<String, Long> map, String key) {
        if (!map.containsKey(key))
            map.put(key, 0L);
    }

    private void normalizeDataSet(Map<String, Long> source, Map<String, Long> other1, Map<String, Long> other2) {
        for (Map.Entry<String, Long> entry : source.entrySet()) {
            putIfNotPresent(other1, entry.getKey());
            putIfNotPresent(other2, entry.getKey());
        }
    }

    private void normalizeDataSet() {
        normalizeDataSet(freeMap, paidMap, donationMap);
        normalizeDataSet(paidMap, freeMap, donationMap);
        normalizeDataSet(donationMap, paidMap, freeMap);
    }

    private LineDataSet setData(Map<String, Long> map, String label) throws ParseException {
        List<Entry> entries = new ArrayList<>();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            String date = DateUtils.formatDateWithDefault(DateUtils.FORMAT_DAY_COMPLETE, entry.getKey());
            float time = DateUtils.getDate(entry.getKey()).getTime();

            entries.add(new Entry(time, entry.getValue(), date));
        }
        Collections.sort(entries, new EntryXComparator());

        // Add a starting date point ine day ago
        entries.add(0, new Entry(entries.get(0).getX() - 60*60*24*1000, 0));
        return new LineDataSet(entries, label);
    }

    private void addDataPoint(Map<String, Long> map, String dateString) {
        Long amount = map.get(dateString);
        if (amount == null)
            amount = 0L;
        ++amount;

        if (amount > maxTicketSale)
            maxTicketSale = amount;
        map.put(dateString, amount);
    }

    @ColorInt
    private int getColor(@ColorRes int colorId) {
        return utilModel.getResourceColor(colorId);
    }

    private void initializeLineSet(LineDataSet lineSet, @ColorRes int color, @ColorRes int fill) {
        lineSet.setLineWidth(4);
        lineSet.setColor(getColor(color));
        lineSet.setCircleColor(getColor(color));
        lineSet.setCircleColorHole(getColor(fill));
        lineSet.setCircleRadius(8);
        lineSet.setCircleHoleRadius(3);
    }

    private void prepare() {
        initializeLineSet(freeSet, R.color.light_blue_500, R.color.light_blue_100);
        initializeLineSet(paidSet, R.color.purple_500, R.color.purple_100);
        initializeLineSet(donationSet, R.color.red_500, R.color.red_100);

        lineData.addDataSet(freeSet);
        lineData.addDataSet(paidSet);
        lineData.addDataSet(donationSet);
        lineData.setDrawValues(false);
    }

    public void showChart (LineChart lineChart) {
        lineChart.setData(lineData);
        lineChart.getXAxis().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setGridLineWidth(1);
        yAxis.setGridColor(Color.parseColor("#992ecc71"));
        if (maxTicketSale > 5)
            yAxis.setGranularity(maxTicketSale/5);

        lineChart.animateY(1000);
    }
}
