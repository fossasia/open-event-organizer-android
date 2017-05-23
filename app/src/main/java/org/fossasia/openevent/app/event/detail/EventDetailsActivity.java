package org.fossasia.openevent.app.event.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.EventDataRepository;
import org.fossasia.openevent.app.data.UtilModel;
import org.fossasia.openevent.app.event.attendees.AttendeesActivity;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailView;

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
    @BindView(R.id.tvAttendance)
    TextView tvAttendees;
    @BindView(R.id.tvTickets)
    TextView tvTickets;
    @BindView(R.id.progressTicketSold)
    ProgressBar pbTickets;
    @BindView(R.id.progressAttendance)
    ProgressBar pbAttendees;
    @BindView(R.id.btnCheckIn)
    Button btnCheckIn;

    private EventDetailActivityPresenter eventDetailActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        ButterKnife.bind(this);

        long id = getIntent().getLongExtra("id", 0);

        UtilModel utilModel = new UtilModel(this);

        eventDetailActivityPresenter = new EventDetailActivityPresenter(id, this,
            new EventDataRepository(utilModel));

        eventDetailActivityPresenter.attach();

        btnCheckIn.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivity.this , AttendeesActivity.class);
            intent.putExtra("id" , id);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        eventDetailActivityPresenter.attach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventDetailActivityPresenter.detach();
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
    public void showTicketStats(long sold, long totalTickets) {
        tvTickets.setText(String.format(Locale.getDefault(), "%d/%d", sold, totalTickets));

        if (totalTickets != 0)
            pbTickets.setProgress((int) (sold * pbAttendees.getMax() / totalTickets));
    }

    @Override
    public void showAttendeeStats(long checkedIn, long total) {
        tvAttendees.setText(String.format(Locale.getDefault(), "%d/%d", checkedIn, total));

        if(total != 0)
            pbAttendees.setProgress((int) (checkedIn * pbAttendees.getMax() / total));
    }

    @Override
    public void showEventLoadError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
