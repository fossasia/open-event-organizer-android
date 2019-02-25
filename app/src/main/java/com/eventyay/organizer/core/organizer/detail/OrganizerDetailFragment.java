package com.eventyay.organizer.core.organizer.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.organizer.password.ChangePasswordFragment;
import com.eventyay.organizer.core.organizer.update.UpdateOrganizerInfoFragment;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.image.ImageData;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.databinding.OrganizerDetailFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.Utils;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import dagger.Lazy;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class OrganizerDetailFragment extends BaseFragment<OrganizerDetailPresenter> implements OrganizerDetailView {

    private OrganizerDetailFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    @Inject
    ContextUtils utilModel;
    @Inject
    Lazy<OrganizerDetailPresenter> presenterProvider;
    public static final String INFO_FRAGMENT_TAG = "info";

    private static final int IMAGE_CHOOSER_REQUEST_CODE = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.organizer_detail_fragment, container, false);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(binding.toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setHasOptionsMenu(true);

        binding.detail.profilePicture.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_CHOOSER_REQUEST_CODE);
        });

        binding.detail.resendVerificationMail.setOnClickListener(view -> {
            getPresenter().resendVerificationMail();
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();
        getPresenter().attach(this);
        getPresenter().start();
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_organizer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_change_password:
                openChangePasswordFragment();
                break;
            case R.id.update_organizer:
                getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, UpdateOrganizerInfoFragment.newInstance(), INFO_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                String encodedImage = Utils.encodeImage(getActivity(), bitmap, selectedImageUri);
                ImageData imageData = new ImageData(encodedImage);
                getPresenter().uploadImage(imageData);
                Glide.with(getContext()).load(bitmap)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.detail.profilePicture);
            } catch (FileNotFoundException e) {
                Timber.e(e, "File not found");
                Toast.makeText(getActivity(), "File not found. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openChangePasswordFragment() {
        getFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment, new ChangePasswordFragment())
            .addToBackStack(null)
            .commit();
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setColorSchemeColors(utilModel.getResourceColor(R.color.color_accent));

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            getPresenter().loadOrganizer(true);
        });
    }

    @Override
    public Lazy<OrganizerDetailPresenter> getPresenterProvider() {
        return presenterProvider;
    }

    @Override
    protected int getTitle() {
        return R.string.title_activity_organizer_detail;
    }

    @Override
    public void showResult(User item) {
        binding.setUser(item);
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.mainContent, error);
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void showSnackbar(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefreshComplete(boolean success) {
        refreshLayout.setRefreshing(false);
        if (success)
            ViewUtils.showSnackbar(binding.mainContent, R.string.refresh_complete);
    }
}
