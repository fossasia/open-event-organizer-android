package com.eventyay.organizer.core.faq.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.faq.list.viewholder.FaqViewHolder;
import com.eventyay.organizer.data.faq.Faq;

import java.util.List;

public class FaqListAdapter extends RecyclerView.Adapter<FaqViewHolder> {

    private final List<Faq> faqs;
    private final FaqListViewModel faqListViewModel;

    public FaqListAdapter(FaqListViewModel faqListViewModel) {
        this.faqListViewModel = faqListViewModel;
        this.faqs = faqListViewModel.getFaqs();
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        FaqViewHolder faqViewHolder = new FaqViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.faq_layout, viewGroup, false), faqListViewModel);

        faqViewHolder.setLongClickAction(faqListViewModel::onLongSelect);
        faqViewHolder.setClickAction(faqListViewModel::onSingleSelect);

        return faqViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder faqViewHolder, int position) {
        faqViewHolder.bind(faqs.get(position));
    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }
}
