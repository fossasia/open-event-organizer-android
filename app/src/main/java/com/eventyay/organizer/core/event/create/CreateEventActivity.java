package com.eventyay.organizer.core.event.create;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.badoualy.stepperindicator.StepperIndicator;
import com.eventyay.organizer.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class CreateEventActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingInjector;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

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
    private static final int PAGE_COUNT = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_activity);
        ButterKnife.bind(this);

        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        indicator.setViewPager(pager, pager.getAdapter().getCount());

        long id = getIntent().getLongExtra(EVENT_ID, -1);

        if (savedInstanceState == null && id != -1) {
            indicator.addOnStepClickListener(step -> pager.setCurrentItem(step, true));
            indicator.setCurrentStep(0);
            Fragment fragment = UpdateEventFragment.newInstance(id);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        }

        pager.setOffscreenPageLimit(PAGE_COUNT);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                boolean isFirstPage = position == 0;
                boolean isLastPage = position == PAGE_COUNT - 1;

                btnPrev.setVisibility(isFirstPage ? View.GONE : View.VISIBLE);
                btnNext.setVisibility(isLastPage ? View.GONE : View.VISIBLE);
                btnSubmit.setVisibility(isLastPage ? View.VISIBLE : View.GONE);
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

        CreateEventViewModel createEventViewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateEventViewModel.class);

        btnNext.setOnClickListener(v -> {
            int currentPosition = pager.getCurrentItem();
            if (currentPosition == 0 && !createEventViewModel.verify()) {
                return;
            }

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
