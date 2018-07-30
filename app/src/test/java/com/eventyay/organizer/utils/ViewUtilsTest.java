package com.eventyay.organizer.utils;

import android.view.View;

import com.eventyay.organizer.ui.ViewUtils;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class ViewUtilsTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private View view;

    @Test
    public void shouldShowView() {
        ViewUtils.showView(view, true);

        Mockito.verify(view).setVisibility(View.VISIBLE);
    }

    @Test
    public void shouldHideView() {
        ViewUtils.showView(view, false);

        Mockito.verify(view).setVisibility(View.GONE);
    }
}
