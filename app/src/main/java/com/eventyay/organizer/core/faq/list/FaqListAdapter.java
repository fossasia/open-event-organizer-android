package com.eventyay.organizer.core.faq.list;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.faq.list.viewholder.FaqViewHolder;
import com.eventyay.organizer.data.faq.Faq;

import java.util.List;

public class FaqListAdapter extends RecyclerView.Adapter<FaqViewHolder> {

    private final List<Faq> faqs;
    private final FaqListPresenter faqListPresenter;

    public FaqListAdapter(FaqListPresenter faqListPresenter) {
        this.faqListPresenter = faqListPresenter;
        this.faqs = faqListPresenter.getFaqs();
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        FaqViewHolder faqViewHolder = new FaqViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.faq_layout, viewGroup, false), faqListPresenter);

        faqViewHolder.setLongClickAction(faqListPresenter::onLongSelect);
        faqViewHolder.setClickAction(faqListPresenter::onSingleSelect);

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
