package org.fossasia.openevent.app.module.faq.list.viewholder;

import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.common.data.models.Faq;
import org.fossasia.openevent.app.databinding.FaqLayoutBinding;

public class FaqViewHolder extends RecyclerView.ViewHolder {

    private final FaqLayoutBinding binding;

    public FaqViewHolder(FaqLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Faq faq) {
        binding.setFaq(faq);
        binding.executePendingBindings();
    }

}
