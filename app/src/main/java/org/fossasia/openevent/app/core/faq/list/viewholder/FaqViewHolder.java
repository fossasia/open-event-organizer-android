package org.fossasia.openevent.app.core.faq.list.viewholder;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.Pipe;
import org.fossasia.openevent.app.data.faq.Faq;
import org.fossasia.openevent.app.databinding.FaqLayoutBinding;

public class FaqViewHolder extends RecyclerView.ViewHolder {

    private final FaqLayoutBinding binding;
    private Faq faq;
    private Pipe<Faq> longClickAction;
    private Runnable clickAction;

    public FaqViewHolder(FaqLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        binding.answerLayout.setVisibility(View.GONE);

        binding.getRoot().setOnLongClickListener(view -> {
            if (longClickAction != null) {
                faq.getSelected().set(true);
                longClickAction.push(faq);
            }
            return true;
        });
        binding.getRoot().setOnClickListener(view -> {
            if (clickAction != null)
                clickAction.run();
        });
    }

    public void setLongClickAction(Pipe<Faq> longClickAction) {
        this.longClickAction = longClickAction;
    }

    public void setClickAction(Runnable clickAction) {
        this.clickAction = clickAction;
    }

    public void bind(Faq faq) {
        this.faq = faq;
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
