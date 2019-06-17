package com.eventyay.organizer.core.role.list;

import android.content.Context;
import android.os.Bundle;

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
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.core.role.invite.RoleInviteFragment;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.role.RoleInvite;
import com.eventyay.organizer.databinding.RolesFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

public class RoleListFragment extends BaseFragment implements RoleListView {

    private Context context;
    private long eventId;

    @Inject
    ContextUtils utilModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private RoleListAdapter rolesAdapter;
    private RolesFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    private RoleListViewModel roleListViewModel;

    public static RoleListFragment newInstance(long eventId) {
        RoleListFragment fragment = new RoleListFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        if (getArguments() != null)
            eventId = getArguments().getLong(MainActivity.EVENT_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.roles_fragment, container, false);
        roleListViewModel = ViewModelProviders.of(this, viewModelFactory).get(RoleListViewModel.class);

        binding.createRoleFab.setOnClickListener(v -> openRoleInviteFragment());

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();
        roleListViewModel.getProgress().observe(this, this::showProgress);
        roleListViewModel.getSuccess().observe(this, this::showMessage);
        roleListViewModel.getError().observe(this, this::showError);
        roleListViewModel.getRolesLiveData().observe(this, this::showResults);
        roleListViewModel.loadRoles(false);
        roleListViewModel.listenChanges();
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
        roleListViewModel.getRoleListChangeListener().stopListening();
    }

    private void setupRecyclerView() {
        rolesAdapter = new RoleListAdapter(roleListViewModel);

        RecyclerView recyclerView = binding.rolesRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(rolesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_accent));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            roleListViewModel.loadRoles(true);
        });
    }

    public void openRoleInviteFragment() {

        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, RoleInviteFragment.newInstance(eventId))
            .addToBackStack(null)
            .commit();
    }

    @Override
    protected int getTitle() {
        return R.string.roles;
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
        if (success)
            ViewUtils.showSnackbar(binding.rolesRecyclerView, R.string.refresh_complete);
    }

    private void showMessage(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }

    @Override
    public void showResults(List<RoleInvite> items) {
        showEmptyView(items.isEmpty());
        rolesAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }
}
