package com.eventyay.organizer.core.orders.onsite.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.orders.create.CreateOrderViewModel;
import com.eventyay.organizer.core.orders.create.viewholder.CreateOrderTicketsViewHolder;
import com.eventyay.organizer.core.orders.onsite.CreateAttendeesViewModel;
import com.eventyay.organizer.core.orders.onsite.viewholder.CreateAttendeesViewHolder;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.databinding.EventLayoutBinding;
import com.eventyay.organizer.databinding.ItemCreateAttendeeBinding;

import java.util.ArrayList;
import java.util.List;

public class CreateAttendeesAdapter extends RecyclerView.Adapter<CreateAttendeesViewHolder> {

    private List<Attendee> attendees = new ArrayList<>(5); // Size hardcoded for testing
    private CreateAttendeesViewModel createAttendeesViewModel;

    public CreateAttendeesAdapter(CreateAttendeesViewModel createAttendeesViewModel) {
        this.createAttendeesViewModel = createAttendeesViewModel;
    }

    @NonNull
    @Override
    public CreateAttendeesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        CreateAttendeesViewHolder createAttendeesViewHolder = new CreateAttendeesViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
            R.layout.item_create_attendee, viewGroup, false), createAttendeesViewModel);

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        ItemCreateAttendeeBinding binding = ItemCreateAttendeeBinding.inflate(layoutInflater, viewGroup, false);

        binding.firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                attendees.get(position).setFirstname(s.toString());
            }
        });


        return createAttendeesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CreateAttendeesViewHolder createAttendeesViewHolder, int position) {
        createAttendeesViewHolder.bind(attendees.get(position));
    }

    @Override
    public int getItemCount() {
        return attendees == null ? 0 : attendees.size();
    }
}
