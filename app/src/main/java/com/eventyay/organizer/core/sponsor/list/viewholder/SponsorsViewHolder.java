package com.eventyay.organizer.core.sponsor.list.viewholder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.core.sponsor.list.SponsorsPresenter;
import com.eventyay.organizer.data.sponsor.Sponsor;
import com.eventyay.organizer.databinding.SponsorItemBinding;

public class SponsorsViewHolder extends RecyclerView.ViewHolder {

    private final SponsorItemBinding binding;
    private final SponsorsPresenter sponsorsPresenter;
    private Sponsor sponsor;

    private Pipe<Sponsor> longClickAction;
    private Pipe<Long> clickAction;

    public SponsorsViewHolder(SponsorItemBinding binding, SponsorsPresenter sponsorsPresenter) {
        super(binding.getRoot());
        this.binding = binding;
        this.sponsorsPresenter = sponsorsPresenter;

        binding.getRoot().setOnLongClickListener(view -> {
            if (longClickAction != null) {
                longClickAction.push(sponsor);
            }
            return true;
        });
        binding.getRoot().setOnClickListener(view -> {
            if (clickAction != null)
                clickAction.push(sponsor.getId());
        });
    }

    public void setLongClickAction(Pipe<Sponsor> longClickAction) {
        this.longClickAction = longClickAction;
    }

    public void setClickAction(Pipe<Long> clickAction) {
        this.clickAction = clickAction;
    }

    public void bindSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
        binding.setSponsor(sponsor);
        binding.setSponsorsPresenter(sponsorsPresenter);
        binding.executePendingBindings();
    }

    public View getRoot() {
        return binding.getRoot();
    }
}
