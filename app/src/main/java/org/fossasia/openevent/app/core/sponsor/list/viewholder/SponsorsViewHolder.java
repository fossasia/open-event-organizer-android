package org.fossasia.openevent.app.core.sponsor.list.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.core.sponsor.list.SponsorsPresenter;
import org.fossasia.openevent.app.data.sponsor.Sponsor;
import org.fossasia.openevent.app.databinding.SponsorItemBinding;

public class SponsorsViewHolder extends RecyclerView.ViewHolder {

    private final SponsorItemBinding binding;
    private Sponsor sponsor;
    private SponsorsPresenter sponsorsPresenter;

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
