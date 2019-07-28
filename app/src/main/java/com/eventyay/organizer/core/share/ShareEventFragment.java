package com.eventyay.organizer.core.share;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.databinding.ShareEventLayoutBinding;
import com.eventyay.organizer.ui.ViewUtils;

import javax.inject.Inject;

import static com.eventyay.organizer.ui.ViewUtils.showView;

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
        String eventUrl = shareEventViewModel.getShareableUrl();
        if (eventUrl == null) {
            ViewUtils.showSnackbar(binding.getRoot(), "Event does not have a Public URL");
        } else {
            ClipData clip = ClipData.newPlainText("Event URL", shareEventViewModel.getShareableUrl());
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
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
    }

    @Override
    public void onSuccess(String message) {
        ViewUtils.showSnackbar(binding.getRoot(), message);
    }
}
