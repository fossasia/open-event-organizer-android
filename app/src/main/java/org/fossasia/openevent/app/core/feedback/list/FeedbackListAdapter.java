package org.fossasia.openevent.app.core.feedback.list;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.core.feedback.list.viewholder.FeedbackViewHolder;
import org.fossasia.openevent.app.data.feedback.Feedback;

import java.util.List;

public class FeedbackListAdapter extends RecyclerView.Adapter<FeedbackViewHolder> {

    private final List<Feedback> feedbacks;

    public FeedbackListAdapter(FeedbackListPresenter feedbackListPresenter) {
        this.feedbacks = feedbackListPresenter.getFeedbacks();
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new FeedbackViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.feedbacklist_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder feedbackViewHolder, int position) {
        feedbackViewHolder.bind(feedbacks.get(position));
    }

    @Override
    public int getItemCount() {
        return feedbacks.size();
    }
}
