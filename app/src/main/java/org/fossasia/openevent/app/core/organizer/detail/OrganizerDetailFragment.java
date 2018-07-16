package org.fossasia.openevent.app.core.organizer.detail;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.mvp.view.BaseFragment;
import org.fossasia.openevent.app.core.organizer.password.ChangePasswordFragment;
import org.fossasia.openevent.app.core.organizer.update.UpdateOrganizerInfoFragment;
import org.fossasia.openevent.app.data.ContextUtils;
import org.fossasia.openevent.app.data.user.User;
import org.fossasia.openevent.app.databinding.OrganizerDetailFragmentBinding;
import org.fossasia.openevent.app.ui.ViewUtils;
import org.fossasia.openevent.app.utils.UploadUtils;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import dagger.Lazy;

public class OrganizerDetailFragment extends BaseFragment<OrganizerDetailPresenter> implements OrganizerDetailView {

    private OrganizerDetailFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    public static final int PICK_IMAGE = 2;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Inject
    ContextUtils utilModel;
    @Inject
    Lazy<OrganizerDetailPresenter> presenterProvider;

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
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();
        getPresenter().attach(this);
        getPresenter().start();

        binding.detail.uploadProfilePic.setOnClickListener(view -> requestRead());
    }

    public void requestRead() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(binding.getRoot(), R.string.storage_access_required,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, view -> ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_READ_EXTERNAL_STORAGE)).show();
        } else {
            uploadImage();
        }
    }

    public void uploadImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Profile Photo");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri targetUri = data.getData();
            ContentResolver contentResolver = getContext().getContentResolver();
            String type = UploadUtils.getMimeTypeFromUri(contentResolver, targetUri);

            try {
                Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(targetUri));
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                String imageString = UploadUtils.convertBitmapToString(resizedBitmap, type);

                getPresenter().uploadImage(imageString);
            } catch (FileNotFoundException e) {
                ViewUtils.showSnackbar(binding.mainContent, R.string.image_upload_error);
            }
        }
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
                    .replace(R.id.fragment, UpdateOrganizerInfoFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openChangePasswordFragment() {
        getFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right)
            .replace(R.id.fragment, new ChangePasswordFragment())
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
    public void onRefreshComplete(boolean success) {
        refreshLayout.setRefreshing(false);
        if (success)
            ViewUtils.showSnackbar(binding.mainContent, R.string.refresh_complete);
    }
}
