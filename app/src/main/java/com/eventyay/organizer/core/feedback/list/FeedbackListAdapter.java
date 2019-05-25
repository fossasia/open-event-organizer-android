package com.eventyay.organizer.core.feedback.list;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.feedback.list.viewholder.FeedbackViewHolder;
import com.eventyay.organizer.data.feedback.Feedback;

import java.util.List;

public class FeedbackListAdapter extends RecyclerView.Adapter<FeedbackViewHolder> {

    public List<Feedback> feedbacks;
    private final FeedbackListViewModel feedbackListViewModel;

    public FeedbackListAdapter(FeedbackListViewModel feedbackListViewModel) {
        this.feedbackListViewModel = feedbackListViewModel;

        feedbacks = feedbackListViewModel.getFeedbacks();
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
