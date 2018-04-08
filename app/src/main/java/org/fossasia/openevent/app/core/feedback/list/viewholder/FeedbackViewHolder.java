package org.fossasia.openevent.app.core.feedback.list.viewholder;


import android.support.v7.widget.RecyclerView;

import org.fossasia.openevent.app.data.feedback.Feedback;
import org.fossasia.openevent.app.databinding.FeedbacklistLayoutBinding;


public class FeedbackViewHolder extends RecyclerView.ViewHolder {

    private final FeedbacklistLayoutBinding binding;

    public FeedbackViewHolder(FeedbacklistLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Feedback feedback) {
        binding.setFeedback(feedback);
        binding.executePendingBindings();
    }

}
