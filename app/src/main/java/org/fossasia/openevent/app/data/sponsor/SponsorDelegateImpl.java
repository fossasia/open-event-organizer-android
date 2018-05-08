package org.fossasia.openevent.app.data.sponsor;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.core.sponsor.list.viewholder.SponsorsViewHolder;
import org.fossasia.openevent.app.utils.CompareUtils;

import java.util.List;

public class SponsorDelegateImpl extends AbstractItem<Sponsor, SponsorsViewHolder> implements SponsorDelegate {

    private final Sponsor sponsor;

    public SponsorDelegateImpl(Sponsor sponsor) {
        this.sponsor = sponsor;
    }

    @Override
    public int compareTo(@NonNull Sponsor other) {
        return CompareUtils.compareCascading(sponsor, other, Sponsor::getName);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.sponsor_item;
    }

    @Override
    public SponsorsViewHolder getViewHolder(@NonNull View view) {
        return new SponsorsViewHolder(DataBindingUtil.bind(view));
    }

    @Override
    public void bindView(@NonNull SponsorsViewHolder holder, @NonNull List<Object> list) {
        super.bindView(holder, list);
        holder.bindSponsor(sponsor);
    }

    @Override
    public String getHeader() {
        return String.format("Level: %d", sponsor.getLevel());
    }

    @Override
    public long getHeaderId() {
        return getHeader().hashCode();
    }
}
