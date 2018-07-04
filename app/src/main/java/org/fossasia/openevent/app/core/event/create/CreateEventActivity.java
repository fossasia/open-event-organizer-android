package org.fossasia.openevent.app.core.event.create;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.badoualy.stepperindicator.StepperIndicator;

import org.fossasia.openevent.app.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class CreateEventActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingInjector;

    @BindView(R.id.pager)
    ViewPager pager;

    @BindView(R.id.stepper_indicator)
    StepperIndicator indicator;

    @BindView(R.id.btn_next)
    Button btnNext;

    @BindView(R.id.btn_prev)
    Button btnPrev;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    public static final String EVENT_ID = "event_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_activity);
        ButterKnife.bind(this);

        assert pager != null;
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        indicator.setViewPager(pager, pager.getAdapter().getCount());

        long id = getIntent().getLongExtra(EVENT_ID, -1);

        if (savedInstanceState == null && id != -1) {
            indicator.addOnStepClickListener(step -> pager.setCurrentItem(step, true));
            indicator.setCurrentStep(0);
            Fragment fragment = UpdateEventFragment.newInstance(id);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        }

        pager.setOffscreenPageLimit(3);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    btnPrev.setVisibility(View.GONE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.GONE);
                } else if (position == 1) {
                    btnPrev.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.GONE);
                } else if (position == 2) {
                    btnPrev.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                //unimplemented
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //unimplemented
            }
        });

        btnNext.setOnClickListener(v -> {
            int currentPosition = pager.getCurrentItem();
            if (currentPosition < 2 && currentPosition >= 0)
                pager.setCurrentItem(currentPosition + 1);
        });

        btnPrev.setOnClickListener(v -> {
            int currentPosition = pager.getCurrentItem();
            if (currentPosition > 0 && currentPosition <= 2)
                pager.setCurrentItem(currentPosition - 1);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingInjector;
    }

    public int getItem() {
        return pager.getCurrentItem();
    }

}
