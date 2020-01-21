package com.eventyay.organizer.core.organizer.detail;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.core.organizer.password.ChangePasswordFragment;
import com.eventyay.organizer.core.organizer.update.UpdateOrganizerInfoFragment;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.image.ImageData;
import com.eventyay.organizer.data.user.User;
import com.eventyay.organizer.databinding.OrganizerDetailFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class OrganizerDetailFragment extends BaseFragment implements OrganizerDetailView {

    private static final String TOKEN_KEY = "token";

    private OrganizerDetailFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;

    private String token;

    @Inject
    ContextUtils utilModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private OrganizerDetailViewModel organizerDetailViewModel;

    public static final String INFO_FRAGMENT_TAG = "info";

    private static final int IMAGE_CHOOSER_REQUEST_CODE = 3;

    public static OrganizerDetailFragment newInstance(String token) {
        OrganizerDetailFragment fragment = new OrganizerDetailFragment();
        Bundle args = new Bundle();
        args.putString(TOKEN_KEY, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            token = getArguments().getString(TOKEN_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.organizer_detail_fragment, container, false);
        organizerDetailViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrganizerDetailViewModel.class);

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

        binding.detail.deleteProfilePicture.setOnClickListener(v -> {
            organizerDetailViewModel.deleteProfilePicture();
        });

        binding.detail.organizerEmail.setOnClickListener(view -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", binding.detail.organizerEmail.getText().toString(), null));
            startActivity(Intent.createChooser(emailIntent, "Send email"));
        });

        binding.detail.organizerContact.setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_VIEW);
            callIntent.setData(Uri.parse("tel:" + binding.detail.organizerContact.getText().toString()));
            startActivity(callIntent);
        });

        binding.detail.organizerFacebook.setOnClickListener(view -> {
            Intent facebookIntent = openFacebook(getContext().getPackageManager(),
                binding.detail.organizerFacebook.getText().toString());
            startActivity(facebookIntent);
        });

        binding.detail.organizerInstagram.setOnClickListener(view -> {
            String instagramUrl = binding.detail.organizerInstagram.getText().toString();

            if (instagramUrl.endsWith("/")) {
                instagramUrl = instagramUrl.substring(0, instagramUrl.length() - 1);
            }

            String instagramUsername = instagramUrl.substring(instagramUrl.lastIndexOf("/") + 1);

            Intent instagramIntent = openInstagram(getContext().getPackageManager(), instagramUrl, instagramUsername);
            startActivity(instagramIntent);
        });

        binding.detail.organizerTwitter.setOnClickListener(view -> {
            String twitterUrl = binding.detail.organizerTwitter.getText().toString();

            if (twitterUrl.endsWith("/")) {
                twitterUrl = twitterUrl.substring(0, twitterUrl.length() - 1);
            }

            String twitterUsername = twitterUrl.substring(twitterUrl.lastIndexOf("/") + 1);

            Intent twitterIntent = openTwitter(getContext().getPackageManager(), twitterUrl, twitterUsername);
            startActivity(twitterIntent);
        });

        binding.detail.organizerGooglePlus.setOnClickListener(view -> {
            Intent googlePlusIntent = openGooglePlus(getContext().getPackageManager(),
                binding.detail.organizerGooglePlus.getText().toString());
            startActivity(googlePlusIntent);
        });

        binding.detail.resendVerificationMail.setOnClickListener(view ->
            organizerDetailViewModel.resendVerificationMail());

        binding.detail.btnChangePassword.setOnClickListener(view -> openChangePasswordFragment());

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRefreshListener();
        organizerDetailViewModel.loadOrganizer(true);
        organizerDetailViewModel.getProgress().observe(this, this::showProgress);
        organizerDetailViewModel.getSuccess().observe(this, this::onSuccess);
        organizerDetailViewModel.getError().observe(this, this::showError);
        organizerDetailViewModel.getUserLiveData().observe(this, this::setUser);
        binding.setUser(organizerDetailViewModel.getUser());

        if (token != null) {
            organizerDetailViewModel.setToken(token);
            organizerDetailViewModel.verifyMail();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        User user = organizerDetailViewModel.getUser();

        if (user != null) {
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            String email = user.getEmail();

            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

            if (firstName != null && lastName != null)
                actionBar.setTitle(firstName + " " + lastName);
            else if (firstName != null)
                actionBar.setTitle(firstName);
            else
                actionBar.setTitle(email);
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
                organizerDetailViewModel.uploadImage(imageData);
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
            organizerDetailViewModel.loadOrganizer(true);
        });
    }

    public static Intent openFacebook(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException exception) {
            Logger.logError(exception);
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    public static Intent openInstagram(PackageManager pm, String url, String username) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (pm.getPackageInfo("com.instagram.android", 0) != null) {
                intent.setData(Uri.parse("http://instagram.com/_u/" + username));
                intent.setPackage("com.instagram.android");
                return intent;
            }
        } catch (PackageManager.NameNotFoundException exception) {
            Logger.logError(exception);
        }
        intent.setData(Uri.parse(url));
        return intent;
    }

    public static Intent openTwitter(PackageManager pm, String url, String username) {
        Intent intent = null;

        try {
            // Open in the Twitter app if possible
            pm.getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + username));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (PackageManager.NameNotFoundException exception) {
            // Twitter app not installed, open in browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }

        return intent;
    }

    public static Intent openGooglePlus(PackageManager pm, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            if (pm.getPackageInfo("com.google.android.apps.plus", 0) != null) {
                intent.setPackage("com.google.android.apps.plus");
            }
        } catch (PackageManager.NameNotFoundException exception) {
            Logger.logError(exception);
        }
        return intent;
    }

    @Override
    protected int getTitle() {
        return R.string.title_activity_organizer_detail;
    }

    @Override
    public void setUser(User user) {
        binding.setUser(user);
    }

    @Override
    public void onSuccess(String message) {
        ViewUtils.showSnackbar(binding.mainContent, message);
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
    }
}
