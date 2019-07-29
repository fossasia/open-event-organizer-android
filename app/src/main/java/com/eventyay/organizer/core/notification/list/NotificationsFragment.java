package com.eventyay.organizer.core.notification.list;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.data.notification.Notification;
import com.eventyay.organizer.databinding.NotificationsFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

public class NotificationsFragment extends BaseFragment implements NotificationsView {

    private Context context;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private NotificationsAdapter notificationsAdapter;
    private NotificationsFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    private NotificationsViewModel notificationsViewModel;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.notifications_fragment, container, false);
        notificationsViewModel = ViewModelProviders.of(this, viewModelFactory).get(NotificationsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();
        notificationsViewModel.getProgress().observe(this, this::showProgress);
        notificationsViewModel.getSuccess().observe(this, this::showMessage);
        notificationsViewModel.getError().observe(this, this::showError);
        notificationsViewModel.getNotificationsLiveData().observe(this, this::showResults);
        notificationsViewModel.loadNotifications(false);
        notificationsViewModel.listenChanges();
    }

    @Override
    protected int getTitle() {
        return R.string.notifications;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        notificationsViewModel.getNotificationsChangeListener().stopListening();
    }

    private void setupRecyclerView() {

        notificationsAdapter = new NotificationsAdapter(notificationsViewModel);

        RecyclerView recyclerView = binding.notificationsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(notificationsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            notificationsViewModel.loadNotifications(true);
        });
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        refreshLayout.setRefreshing(false);
    }

    private void showMessage(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }

    @Override
    public void showResults(List<Notification> notifications) {
        if(notifications.isEmpty()) {
            showEmptyView(true);
            return;
        } else {
            showEmptyView(false);
        }
        notificationsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }
}
