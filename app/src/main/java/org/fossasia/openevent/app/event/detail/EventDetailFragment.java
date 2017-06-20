package org.fossasia.openevent.app.event.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.fossasia.openevent.app.OrgaApplication;
import org.fossasia.openevent.app.common.BaseFragment;
import org.fossasia.openevent.app.data.models.Event;
import org.fossasia.openevent.app.databinding.EventDetailBinding;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailPresenter;
import org.fossasia.openevent.app.event.detail.contract.IEventDetailView;
import org.fossasia.openevent.app.utils.ViewUtils;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends BaseFragment implements IEventDetailView {

    private long initialEventId;
    private EventDetailBinding binding;

    @Inject
    Context context;

    @Inject
    IEventDetailPresenter eventDetailPresenter;

    public EventDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventId Event for which the Fragment is to be created.
     * @return A new instance of fragment EventDetailFragment.
     */
    public static EventDetailFragment newInstance(long eventId) {
        EventDetailFragment fragment = new EventDetailFragment();
        fragment.setInitialEvent(eventId);
        return fragment;
    }

    public void setInitialEvent(long initialEventId) {
        this.initialEventId = initialEventId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OrgaApplication
            .getAppComponent(getActivity())
            .inject(this);

        super.onCreate(savedInstanceState);

        if (initialEventId != -1) {
            eventDetailPresenter.attach(this, initialEventId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventDetailBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventDetailPresenter.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventDetailPresenter.detach();
    }

    @Override
    public void showProgressBar(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void showEvent(Event event) {
        binding.setEvent(event);
        binding.executePendingBindings();
    }

    @Override
    public void showEventLoadError(String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

}
