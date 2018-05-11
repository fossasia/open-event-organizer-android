package org.fossasia.openevent.app.core.sponsor.list.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.openevent.app.data.sponsor.Sponsor;
import org.fossasia.openevent.app.databinding.SponsorItemBinding;

public class SponsorsViewHolder extends RecyclerView.ViewHolder {

    private final SponsorItemBinding binding;

    public SponsorsViewHolder(SponsorItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindSponsor(Sponsor sponsor) {
        binding.setSponsor(sponsor);
        binding.executePendingBindings();
    }

    public View getRoot() {
        return binding.getRoot();
    }

}
