package org.fossasia.openevent.app.core.faq.list;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.core.faq.list.viewholder.FaqViewHolder;
import org.fossasia.openevent.app.data.faq.Faq;

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

        faqViewHolder.setLongClickAction(faqListPresenter::toolbarDeleteMode);
        faqViewHolder.setClickAction(faqListPresenter::resetToDefaultState);

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
