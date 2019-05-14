package com.eventyay.organizer.core.event.list.sales;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseDialogFragment;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.databinding.FragmentSalesSummaryBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import static com.eventyay.organizer.core.event.dashboard.EventDashboardFragment.EVENT_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesSummaryFragment extends BaseDialogFragment implements SalesSummaryView {

    private long eventId;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    FragmentSalesSummaryBinding binding;

    private SalesSummaryViewModel salesSummaryViewModel;

    public SalesSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        salesSummaryViewModel.getProgress().observe(this, this::showProgress);
        salesSummaryViewModel.getEventLiveData().observe(this, this::showResult);
        salesSummaryViewModel.getError().observe(this, this::showError);
        salesSummaryViewModel.loadDetails(eventId, false);
    }

    public static SalesSummaryFragment newInstance(long eventId) {
        SalesSummaryFragment fragment = new SalesSummaryFragment();
        Bundle args = new Bundle();
        args.putLong(EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            eventId = arguments.getLong(EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_summary, container, false);
        salesSummaryViewModel = ViewModelProviders.of(this, viewModelFactory).get(SalesSummaryViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void showError(String error) {
        binding.content.setVisibility(View.GONE);
        ViewUtils.showView(binding.emptyView, true);
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showResult(Event event) {
        binding.setEvent(event);
        binding.executePendingBindings();
    }
}
