package org.fossasia.openevent.app.core.faq.list;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.core.faq.list.viewholder.FaqViewHolder;
import org.fossasia.openevent.app.data.models.Faq;

import java.util.List;

public class FaqListAdapter extends RecyclerView.Adapter<FaqViewHolder> {

    private final List<Faq> faqs;

    public FaqListAdapter(FaqListPresenter faqListPresenter) {
        this.faqs = faqListPresenter.getFaqs();
    }

    @Override
    public FaqViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        return new FaqViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.faq_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(FaqViewHolder faqViewHolder, int position) {
        faqViewHolder.bind(faqs.get(position));
    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }
}
