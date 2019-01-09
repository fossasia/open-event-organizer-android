package com.eventyay.organizer.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.eventyay.organizer.R;
import com.eventyay.organizer.utils.Utils;

import timber.log.Timber;

public final class ViewUtils {

    private ViewUtils() {
        // Never Called
    }

    public static int getVisibility(String string, int hidden) {
        return Utils.isEmpty(string) ? hidden : View.VISIBLE;
    }

    public static void showView(View view, int mode, boolean show) {
        view.setVisibility(show ? View.VISIBLE : mode);
    }

    public static void showView(View view, boolean show) {
        showView(view, View.GONE, show);
    }

    public static void setTint(View view, int color) {
        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color));
    }

    public static void setTint(View view, String color) {
        setTint(view, Color.parseColor(color));
    }

    public static void setRecyclerViewScrollAwareFabBehaviour(RecyclerView recyclerView, FloatingActionButton fab) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
    }

    public static void showKeyboard(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            view.clearFocus();
            if (manager != null) {
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void setTitle(AppCompatActivity activity, String title) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar == null)
            Timber.e("No ActionBar found in Activity %s", activity);
        else
            actionBar.setTitle(title);
    }

    public static void setTitle(Fragment fragment, String title) {
        Activity activity = fragment.getActivity();

        if (activity instanceof AppCompatActivity) {
            setTitle((AppCompatActivity) activity, title);
        } else {
            Timber.e("Fragment %s is not attached to any Activity", fragment);
        }
    }

    public static void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackbar(View view, int messageResourceId) {
        Snackbar.make(view, messageResourceId, Snackbar.LENGTH_SHORT).show();
    }

    public static void showDialog(Fragment fragment, String title, String message, String buttonTitle, Runnable runnable) {
        Activity activity = fragment.getActivity();

        if (activity instanceof AppCompatActivity) {
            AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialog))
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(buttonTitle, (dialog, which) -> runnable.run())
                .create();

            alertDialog.show();
        } else {
            Timber.e("Fragment %s is not attached to any Activity", fragment);
        }
    }
}
