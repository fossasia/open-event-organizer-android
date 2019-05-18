package com.eventyay.organizer.core.feedback.list.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.eventyay.organizer.data.feedback.Feedback;
import com.eventyay.organizer.databinding.FeedbacklistLayoutBinding;

public class FeedbackViewHolder extends RecyclerView.ViewHolder {

    private final FeedbacklistLayoutBinding binding;

    public FeedbackViewHolder(FeedbacklistLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        binding.commentTextview.setOnClickListener(v -> {
            if (binding.commentTextview.getEllipsize() == null) {
                binding.commentTextview.setMaxLines(2);
                binding.commentTextview.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                binding.commentTextview.setEllipsize(null);
                binding.commentTextview.setMaxLines(Integer.MAX_VALUE);
            }
        });
    }

    public void bind(Feedback feedback) {
        binding.setFeedback(feedback);
        binding.executePendingBindings();
    }

}
