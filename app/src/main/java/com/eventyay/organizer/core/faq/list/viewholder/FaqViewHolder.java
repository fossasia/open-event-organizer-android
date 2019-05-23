package com.eventyay.organizer.core.faq.list.viewholder;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.core.faq.list.FaqListViewModel;
import com.eventyay.organizer.data.faq.Faq;
import com.eventyay.organizer.databinding.FaqLayoutBinding;

public class FaqViewHolder extends RecyclerView.ViewHolder {

    private final FaqLayoutBinding binding;
    private final FaqListViewModel faqListViewModel;
    private Faq faq;
    private Pipe<Faq> longClickAction;
    private Pipe<Faq> clickAction;

    public FaqViewHolder(FaqLayoutBinding binding, FaqListViewModel faqListViewModel) {
        super(binding.getRoot());
        this.binding = binding;
        this.faqListViewModel = faqListViewModel;
        binding.answerLayout.setVisibility(View.GONE);

        binding.getRoot().setOnLongClickListener(view -> {
            if (longClickAction != null) {
                longClickAction.push(faq);
            }
            return true;
        });
        binding.getRoot().setOnClickListener(view -> {
            if (clickAction != null)
                clickAction.push(faq);
        });
    }

    public void setLongClickAction(Pipe<Faq> longClickAction) {
        this.longClickAction = longClickAction;
    }

    public void setClickAction(Pipe<Faq> clickAction) {
        this.clickAction = clickAction;
    }

    public void bind(Faq faq) {
        this.faq = faq;
        binding.setFaq(faq);
        binding.setFaqListViewModel(faqListViewModel);
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
