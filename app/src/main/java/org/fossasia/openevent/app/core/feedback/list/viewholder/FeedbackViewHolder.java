package org.fossasia.openevent.app.core.feedback.list.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import org.fossasia.openevent.app.data.feedback.Feedback;
import org.fossasia.openevent.app.databinding.FeedbacklistLayoutBinding;

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
