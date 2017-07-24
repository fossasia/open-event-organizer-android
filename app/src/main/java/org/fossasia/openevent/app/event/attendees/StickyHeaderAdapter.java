package org.fossasia.openevent.app.event.attendees;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.fossasia.openevent.app.common.HeaderViewHolder;
import org.fossasia.openevent.app.data.models.contract.IHeaderProvider;
import org.fossasia.openevent.app.databinding.HeaderLayoutBinding;

import java.util.List;

class StickyHeaderAdapter<Item extends IItem & IHeaderProvider> extends AbstractAdapter<Item> implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {
    @Override
    public long getHeaderId(int position) {
        return getItem(position).getHeaderId();
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return new HeaderViewHolder(HeaderLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int position) {
        viewHolder.bindHeader(getItem(position).getHeader());
    }

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public List<Item> getAdapterItems() {
        return null;
    }

    @Override
    public Item getAdapterItem(int i) {
        return null;
    }

    @Override
    public int getAdapterPosition(Item item) {
        return -1;
    }

    @Override
    public int getAdapterPosition(long l) {
        return -1;
    }

    @Override
    public int getGlobalPosition(int i) {
        return -1;
    }
}
