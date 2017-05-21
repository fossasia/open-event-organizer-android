package org.fossasia.openevent.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.contract.view.IEventDetailView;
import org.fossasia.openevent.app.data.AndroidDataUtils;
import org.fossasia.openevent.app.data.EventDataRepository;
import org.fossasia.openevent.app.ui.presenter.EventDetailActivityPresenter;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventDetailsActivity extends AppCompatActivity implements IEventDetailView {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tvEventTitle)
    TextView tvEventTitle;
    @BindView(R.id.tvStartDate)
    TextView tvStartDate;
    @BindView(R.id.tvEndDate)
    TextView tvEndDate;
    @BindView(R.id.tvHour)
    TextView tvTime;
    @BindView(R.id.tvTicketSold)
    TextView tvTicketSold;
    @BindView(R.id.tvAttendance)
    TextView tvAttendees;
    @BindView(R.id.tvTicketTotal)
    TextView tvTicketTotal;
    @BindView(R.id.progressTicketSold)
    ProgressBar pbTickets;
    @BindView(R.id.progressAttendance)
    ProgressBar pbAttendees;
    @BindView(R.id.btnCheckIn)
    Button btnCheckIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        ButterKnife.bind(this);

        long id = getIntent().getLongExtra("id", 0);

        AndroidDataUtils androidDataUtils = new AndroidDataUtils(this);

        EventDetailActivityPresenter eventDetailActivityPresenter = new EventDetailActivityPresenter(id, this,
            new EventDataRepository(androidDataUtils));

        eventDetailActivityPresenter.attach();

        btnCheckIn.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivity.this , AttendeeListActivity.class);
            intent.putExtra("id" , id);
            startActivity(intent);
        });

    }

    private static void showView(View view, boolean show) {
        int mode = View.GONE;

        if(show)
            mode = View.VISIBLE;

        view.setVisibility(mode);
    }

    @Override
    public void showProgressBar(boolean show) {
        showView(progressBar, show);
    }

    @Override
    public void showEventName(String name) {
        tvEventTitle.setText(name);
    }

    @Override
    public void showDates(String start, String end) {
        tvStartDate.setText(start);
        tvEndDate.setText(end);
    }

    @Override
    public void showTime(String time) {
        tvTime.setText(time);
    }

    @Override
    public void showQuantityInfo(long quantity, long total) {
        tvTicketTotal.setText(String.valueOf(quantity));

        if (quantity != 0) {
            pbTickets.setProgress((int) ((total / quantity) * pbTickets.getMax()));
        }
    }

    @Override
    public void showAttendeeInfo(long checkedIn, long total) {
        tvAttendees.setText(String.format(Locale.getDefault(), "%d/%d", checkedIn, total));
        tvTicketSold.setText(String.valueOf(total));

        if(total != 0)
            pbAttendees.setProgress((int) (checkedIn / total));
    }

    @Override
    public void showEventLoadError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
