package org.fossasia.openevent.app.core.faq.list.viewholder;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.faq.Faq;
import org.fossasia.openevent.app.databinding.FaqLayoutBinding;

public class FaqViewHolder extends RecyclerView.ViewHolder {

    private final FaqLayoutBinding binding;

    public FaqViewHolder(FaqLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        binding.answerLayout.setVisibility(View.GONE);
    }

    public void bind(Faq faq) {
        binding.setFaq(faq);
        binding.executePendingBindings();
        binding.questionLayout.setOnClickListener(v -> {
            if (binding.answerLayout.getVisibility() == View.GONE) {
                binding.answerLayout.setVisibility(View.VISIBLE);
                binding.addIcon.setImageDrawable(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.ic_arrow_up));
            } else {
                binding.answerLayout.setVisibility(View.GONE);
                binding.addIcon.setImageDrawable(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.ic_arrow_drop_down));
            }
        });
    }
}
