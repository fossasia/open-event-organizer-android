package org.fossasia.openevent.app.core.share;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.main.MainActivity;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.databinding.ShareEventLayoutBinding;
import org.fossasia.openevent.app.ui.ViewUtils;

import javax.inject.Inject;

import static org.fossasia.openevent.app.ui.ViewUtils.showView;

public class ShareEventFragment extends BaseFragment implements ShareEventView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private SwipeRefreshLayout refreshLayout;

    private ShareEventViewModel shareEventViewModel;
    private ShareEventLayoutBinding binding;
    private long eventId;

    public static ShareEventFragment newInstance(long eventId) {
        ShareEventFragment fragment = new ShareEventFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.EVENT_KEY, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding =  DataBindingUtil.inflate(localInflater, R.layout.share_event_layout, container, false);
        shareEventViewModel = ViewModelProviders.of(this, viewModelFactory).get(ShareEventViewModel.class);

        eventId = getArguments().getLong(MainActivity.EVENT_KEY);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();

        shareEventViewModel.getProgress().observe(this, this::showProgress);
        shareEventViewModel.getError().observe(this, this::showError);
        loadEvent(false);

        binding.moreLink.setOnClickListener(view -> shareEvent());
        binding.emailLink.setOnClickListener(view -> shareByEmail());
        binding.copyLink.setOnClickListener(view -> copyUrlToClipboard());
    }

    public void shareEvent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareEventViewModel.getShareableInformation());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
    }

    public void shareByEmail() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SENDTO);
        shareIntent.setType("message/rfc822");
        shareIntent.setData(Uri.parse("mailto:"));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareEventViewModel.getEmailSubject());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareEventViewModel.getShareableInformation());
        try {
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
        } catch (android.content.ActivityNotFoundException ex) {
            ViewUtils.showSnackbar(binding.getRoot(), "There are no email clients installed");
        }
    }

    public void copyUrlToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        String eventUrl = shareEventViewModel.getShareableUrl();
        if (eventUrl == null) {
            ViewUtils.showSnackbar(binding.getRoot(), "Event does not have a Public URL");
        } else {
            ClipData clip = ClipData.newPlainText("Event URL", shareEventViewModel.getShareableUrl());
            clipboard.setPrimaryClip(clip);
            ViewUtils.showSnackbar(binding.getRoot(), "Event URL Copied to Clipboard");
        }
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            loadEvent(true);
        });
    }

    private void loadEvent(boolean reload) {
        shareEventViewModel.getEvent(eventId, reload).observe(this, this::showResult);
    }

    @Override
    protected int getTitle() {
        return R.string.share;
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public void showResult(Event event) {
        binding.setEvent(event);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        refreshLayout.setRefreshing(false);
        if (success)
            ViewUtils.showSnackbar(binding.getRoot(), R.string.refresh_complete);
    }

    @Override
    public void onSuccess(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }
}
