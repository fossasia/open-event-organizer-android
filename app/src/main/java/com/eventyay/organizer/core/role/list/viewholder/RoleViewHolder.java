package com.eventyay.organizer.core.role.list.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.common.Pipe;
import com.eventyay.organizer.core.role.list.RoleListViewModel;
import com.eventyay.organizer.data.role.RoleInvite;
import com.eventyay.organizer.databinding.RoleItemBinding;

public class RoleViewHolder extends RecyclerView.ViewHolder {

    private final RoleItemBinding binding;
    private final RoleListViewModel roleListViewModel;
    private RoleInvite role;

    private Pipe<RoleInvite> longClickAction;

    public RoleViewHolder(RoleItemBinding binding, RoleListViewModel roleListViewModel) {
        super(binding.getRoot());
        this.binding = binding;
        this.roleListViewModel = roleListViewModel;

        binding.getRoot().setOnLongClickListener(view -> {
            if (longClickAction != null) {
                longClickAction.push(role);
            }
            return true;
        });
    }

    public void setLongClickAction(Pipe<RoleInvite> longClickAction) {
        this.longClickAction = longClickAction;
    }

    public void bind(RoleInvite role) {
        this.role = role;
        binding.setRole(role);
        binding.setRoleListViewModel(roleListViewModel);
        binding.executePendingBindings();
    }
}
