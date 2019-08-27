package com.eventyay.organizer.core.main;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.MenuItem;
import androidx.appcompat.app.AlertDialog;
import com.eventyay.organizer.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class DrawerNavigatorTest {

    private static final int RANDOM_ID = 1234;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private OrganizerViewModel organizerViewModel;
    @Mock private FragmentNavigator fragmentNavigator;
    @Mock private AlertDialog alertDialog;
    @Mock private MenuItem menuItem;

    private DrawerNavigator drawerNavigator;

    @Before
    public void setUp() {
        drawerNavigator = new DrawerNavigator(null, fragmentNavigator, organizerViewModel);
    }

    @Test
    public void testCallLogoutDialog() {
        drawerNavigator.setLogoutDialog(alertDialog);
        when(menuItem.getItemId()).thenReturn(R.id.nav_logout);
        drawerNavigator.selectItem(menuItem);
        verify(alertDialog).show();
    }

    @Test
    public void testCallFragmentNavigator() {
        when(menuItem.getItemId()).thenReturn(RANDOM_ID);
        drawerNavigator.selectItem(menuItem);
        verify(fragmentNavigator).loadFragment(RANDOM_ID);
    }
}
