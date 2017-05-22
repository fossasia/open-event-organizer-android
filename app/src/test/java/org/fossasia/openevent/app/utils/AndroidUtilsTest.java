package org.fossasia.openevent.app.utils;

import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class AndroidUtilsTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    View view;

    @Test
    public void shouldShowView() {
        AndroidUtils.showView(view, true);

        Mockito.verify(view).setVisibility(View.VISIBLE);
    }

    @Test
    public void shouldHideView() {
        AndroidUtils.showView(view, false);

        Mockito.verify(view).setVisibility(View.GONE);
    }
}
