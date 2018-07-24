package org.fossasia.openevent.app.core.event.list.sales;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseDialogFragment;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.databinding.FragmentSalesSummaryBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import dagger.Lazy;

import static org.fossasia.openevent.app.core.event.dashboard.EventDashboardFragment.EVENT_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesSummaryFragment extends BaseDialogFragment<SalesSummaryPresenter> implements SalesSummaryView {

    private long eventId;

    @Inject
    Lazy<SalesSummaryPresenter> presenterProvider;

    FragmentSalesSummaryBinding binding;

    public SalesSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public Lazy<SalesSummaryPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().attach(eventId, this);
        getPresenter().start();
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
