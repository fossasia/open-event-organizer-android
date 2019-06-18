package com.eventyay.organizer.core.role.list.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.core.role.list.RoleListViewModel;
import com.eventyay.organizer.data.role.RoleInvite;
import com.eventyay.organizer.databinding.RoleItemBinding;

public class RoleViewHolder extends RecyclerView.ViewHolder {

    private final RoleItemBinding binding;
    private final RoleListViewModel roleListViewModel;

    public RoleViewHolder(RoleItemBinding binding, RoleListViewModel roleListViewModel) {
        super(binding.getRoot());
        this.binding = binding;
        this.roleListViewModel = roleListViewModel;
    }

    public void bind(RoleInvite role) {
        binding.setRole(role);
        binding.setRoleListViewModel(roleListViewModel);
        binding.executePendingBindings();
    }
}
