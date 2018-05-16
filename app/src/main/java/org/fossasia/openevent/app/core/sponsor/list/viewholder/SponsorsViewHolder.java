package org.fossasia.openevent.app.core.sponsor.list.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.sponsor.Sponsor;
import org.fossasia.openevent.app.databinding.SponsorItemBinding;

public class SponsorsViewHolder extends RecyclerView.ViewHolder {

    private final SponsorItemBinding binding;
    private Sponsor sponsor;

    private Pipe<Long> editAction;

    public SponsorsViewHolder(SponsorItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        binding.actionChangeSponsor.setOnClickListener(view -> {
            if (editAction != null) editAction.push(sponsor.getId());
        });
    }

    public void setEditAction(Pipe<Long> editAction) {
        this.editAction = editAction;
    }

    public void bindSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
        binding.setSponsor(sponsor);
        binding.executePendingBindings();
    }

    public View getRoot() {
        return binding.getRoot();
    }

}
