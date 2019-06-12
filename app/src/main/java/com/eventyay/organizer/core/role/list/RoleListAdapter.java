package com.eventyay.organizer.core.role.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.role.list.viewholder.RoleViewHolder;
import com.eventyay.organizer.data.role.RoleInvite;

import java.util.List;

public class RoleListAdapter extends RecyclerView.Adapter<RoleViewHolder> {

    public final List<RoleInvite> roles;
    private final RoleListViewModel roleListViewModel;

    public RoleListAdapter(RoleListViewModel roleListViewModel) {
        this.roleListViewModel = roleListViewModel;
        this.roles = roleListViewModel.getRoles();
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        RoleViewHolder roleViewHolder= new RoleViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.role_item, viewGroup, false), roleListViewModel);

        return roleViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder roleViewHolder, int position) {
        roleViewHolder.bind(roles.get(position));
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

}
