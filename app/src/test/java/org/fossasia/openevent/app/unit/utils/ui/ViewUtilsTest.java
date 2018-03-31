package org.fossasia.openevent.app.unit.utils.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.fossasia.openevent.app.ui.ViewUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ViewUtilsTest {

    @Test
    public void testGetVisibilityNonEmpty() {
        assertEquals(View.VISIBLE, ViewUtils.getVisibility("hello", View.GONE));
    }

    @Test
    public void testGetVisibilityEmptyGone() {
        assertEquals(View.GONE, ViewUtils.getVisibility(null, View.GONE));
        assertEquals(View.GONE, ViewUtils.getVisibility("", View.GONE));
    }

    @Test
    public void testGetVisibilityEmptyInvisible() {
        assertEquals(View.INVISIBLE, ViewUtils.getVisibility(null, View.INVISIBLE));
        assertEquals(View.INVISIBLE, ViewUtils.getVisibility("", View.INVISIBLE));
    }

    @Test
    public void testShowViewHide() {
        View view = mock(View.class);
        int mode = View.GONE;

        ViewUtils.showView(view, mode, false);

        verify(view).setVisibility(mode);
    }

    @Test
    public void testShowViewShow() {
        View view = mock(View.class);

        ViewUtils.showView(view, View.GONE, true);

        verify(view).setVisibility(View.VISIBLE);
    }

    @Test
    public void testDefaultShowView() {
        View view = mock(View.class);

        ViewUtils.showView(view, false);

        verify(view).setVisibility(View.GONE);
    }

    @Test
    public void testSetTitleWithSimpleActivity() {
        FragmentActivity activity = mock(FragmentActivity.class);
        Fragment fragment = mock(Fragment.class);

        when(fragment.getActivity()).thenReturn(activity);

        ViewUtils.setTitle(fragment, "Title");

        // Nothing should happen
    }

    @Test
    public void testSetTitleWithAppCompat() {
        AppCompatActivity activity = mock(AppCompatActivity.class);
        Fragment fragment = mock(Fragment.class);
        ActionBar actionBar = mock(ActionBar.class);

        when(fragment.getActivity()).thenReturn(activity);
        when(activity.getSupportActionBar()).thenReturn(actionBar);

        ViewUtils.setTitle(fragment, "Title");

        verify(actionBar).setTitle("Title");
    }

    @Test
    public void shouldHideKeyboard() {
        // Test null view
        ViewUtils.hideKeyboard(null);

        // Test null InputManager
        View view = mock(View.class);
        Context context = mock(Context.class);
        InputMethodManager inputMethodManager = mock(InputMethodManager.class);
        when(view.getContext()).thenReturn(context);
        when(context.getSystemService(Context.INPUT_METHOD_SERVICE))
            .thenReturn(null)
            .thenReturn(inputMethodManager);

        ViewUtils.hideKeyboard(view);

        // Test mock InputManager

        ViewUtils.hideKeyboard(view);

        verify(view, atLeastOnce()).clearFocus();
        verify(inputMethodManager).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
