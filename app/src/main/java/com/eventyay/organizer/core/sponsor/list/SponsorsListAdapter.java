package com.eventyay.organizer.core.sponsor.list;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.sponsor.list.viewholder.SponsorsViewHolder;
import com.eventyay.organizer.data.sponsor.Sponsor;

import java.util.List;

public class SponsorsListAdapter extends RecyclerView.Adapter<SponsorsViewHolder> {

    private final List<Sponsor> sponsors;
    private final SponsorsPresenter sponsorsPresenter;

    public SponsorsListAdapter(SponsorsPresenter sponsorsPresenter) {
        this.sponsorsPresenter = sponsorsPresenter;
        this.sponsors = sponsorsPresenter.getSponsors();
    }

    @NonNull
    @Override
    public SponsorsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        SponsorsViewHolder sponsorsViewHolder = new SponsorsViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.sponsor_item, viewGroup, false), sponsorsPresenter);

        sponsorsViewHolder.setLongClickAction(sponsorsPresenter::longClick);
        sponsorsViewHolder.setClickAction(sponsorsPresenter::click);

        return sponsorsViewHolder;
    }

    @Override
    public void onBindViewHolder(SponsorsViewHolder sponsorViewHolder, int position) {
        sponsorViewHolder.bindSponsor(sponsors.get(position));
    }

    @Override
    public int getItemCount() {
        return sponsors.size();
    }

}
