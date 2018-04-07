package org.fossasia.openevent.app.core.attendee.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.fossasia.openevent.app.data.attendee.Attendee;
import org.fossasia.openevent.app.ui.HeaderViewHolder;
import org.fossasia.openevent.app.common.model.HeaderProvider;
import org.fossasia.openevent.app.databinding.HeaderLayoutBinding;

import java.util.List;

class StickyHeaderAdapter<T extends IItem & HeaderProvider> extends AbstractAdapter<T>
    implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private boolean sortByName;

    @Override
    public long getHeaderId(int position) {
        if (sortByName) {
            Attendee attendee = (Attendee) getFastAdapter().getItem(position);
            return attendee.getTicket().getType().hashCode();
        } else {
            return getFastAdapter().getItem(position).getHeaderId();
        }
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return new HeaderViewHolder(HeaderLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int position) {
        if (sortByName) {
            Attendee attendee = (Attendee) getFastAdapter().getItem(position);
            viewHolder.bindHeader(attendee.getTicket().getType());
        } else
            viewHolder.bindHeader(getFastAdapter().getItem(position).getHeader());
    }

    @Override
    public int getItemCount() {
        return getFastAdapter().getItemCount();
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
    public List<T> getAdapterItems() {
        return null;
    }

    @Override
    public T getAdapterItem(int i) {
        return null;
    }

    @Override
    public int getAdapterPosition(T t) {
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

    public void setSortByName(boolean sortByName) {
        this.sortByName = sortByName;
    }
}
