package com.eventyay.organizer.core.orders.onsite;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.core.orders.create.CreateOrderFragment;
import com.eventyay.organizer.core.orders.create.adapter.CreateOrderTicketsAdapter;
import com.eventyay.organizer.core.orders.onsite.adapter.CreateAttendeesAdapter;
import com.eventyay.organizer.core.orders.onsite.viewholder.CreateAttendeesViewHolder;
import com.eventyay.organizer.databinding.FragmentCreateAttendeesBinding;
import com.eventyay.organizer.databinding.OrderCreateLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

public class CreateAttendeesFragment extends BaseFragment implements CreateAttendeesView {

    private long eventId;
    private Context context;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CreateAttendeesViewModel createAttendeesViewModel;
    private CreateAttendeesAdapter createAttendeesAdapter;

    private FragmentCreateAttendeesBinding binding;
    private SwipeRefreshLayout refreshLayout;

    public static CreateAttendeesFragment newInstance(long eventId) {
        CreateAttendeesFragment fragment = new CreateAttendeesFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        context = getContext();

        if (getArguments() != null) {
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_attendees, container, false);

        createAttendeesViewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateAttendeesViewModel.class);
        binding.submit.setOnClickListener(view -> createAttendeesViewModel.createOnSiteOrder(eventId));

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();

        createAttendeesViewModel.getProgress().observe(this, this::showProgress);
        createAttendeesViewModel.getError().observe(this, this::showError);
        createAttendeesViewModel.getSuccess().observe(this, this::onSuccess);
        //createAttendeesViewModel.getOrderAmount().observe(this, this::showOrderAmount);
    }

    @Override
    protected int getTitle() {
        return R.string.create_order;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        createAttendeesAdapter = new CreateAttendeesAdapter(createAttendeesViewModel);

        RecyclerView attendeesRecyclerView = binding.attendeeRecyclerView;
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        attendeesRecyclerView.setAdapter(createAttendeesAdapter);
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showProgress(boolean show) {
        //ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onSuccess(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }

}
