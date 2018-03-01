package org.fossasia.openevent.app.module.faq.list;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.data.models.Faq;
import org.fossasia.openevent.app.module.faq.list.contract.IFaqListPresenter;
import org.fossasia.openevent.app.module.faq.list.viewholder.FaqViewHolder;

import java.util.List;

public class FaqListAdapter extends RecyclerView.Adapter<FaqViewHolder> {

    private final List<Faq> faqs;

    public FaqListAdapter(IFaqListPresenter faqListPresenter) {
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
