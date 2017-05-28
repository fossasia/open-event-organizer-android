package org.fossasia.openevent.app.event.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.EventDataRepository;
import org.fossasia.openevent.app.data.UtilModel;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailPresenter;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailView;
import org.fossasia.openevent.app.events.EventsActivity;
import org.fossasia.openevent.app.utils.ViewUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment implements IEventDetailView {

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

    private IEventDetailPresenter eventDetailPresenter;

    private Context context;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param event Event for which the Fragment is to be created.
     * @return A new instance of fragment EventDetailsFragment.
     */
    public static EventDetailsFragment newInstance(Event event) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(EventsActivity.EVENT_KEY, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        if (getArguments() != null) {
            Event initialEvent = getArguments().getParcelable(EventsActivity.EVENT_KEY);
            eventDetailPresenter = new EventDetailsPresenter(initialEvent, this, new EventDataRepository(new UtilModel(context)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventDetailPresenter.attach();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventDetailPresenter.detach();
    }

    @Override
    public void showProgressBar(boolean show) {
        ViewUtils.showView(progressBar, show);
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
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

}
