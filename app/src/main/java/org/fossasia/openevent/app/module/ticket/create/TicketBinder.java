package org.fossasia.openevent.app.module.ticket.create;

import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.utils.core.DateUtils;
import org.fossasia.openevent.app.databinding.TicketCreateFormBinding;

import java.util.Date;

import br.com.ilhasoft.support.validation.Validator;

public class TicketBinder {

    private final Ticket ticket;
    private final TicketCreateFormBinding binding;
    private final Validator validator;

    public TicketBinder(Ticket ticket, TicketCreateFormBinding binding) {
        this.ticket = ticket;
        this.binding = binding;
        this.validator = new Validator(binding);

        Date current = new Date();
        binding.salesStartTime.setText(DateUtils.formatDateToIso(current));
        binding.salesEndTime.setText(DateUtils.formatDateToIso(current));
    }

    private String getString(TextInputEditText editText) {
        return editText.getText().toString();
    }

    private Long getLong(TextInputEditText editText) {
        String input = getString(editText);

        try {
            return TextUtils.isEmpty(input) ? null : Long.parseLong(input);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private Float getFloat(TextInputEditText editText) {
        String input = getString(editText);

        try {
            return TextUtils.isEmpty(input) ? null : Float.parseFloat(input);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private String getSelectedRadioText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();

        RadioButton radioButton = (RadioButton) radioGroup.findViewById(selectedId);

        return radioButton.getText().toString();
    }

    public boolean bound() {
        if (!validator.validate())
            return false;

        ticket.setName(getString(binding.name));
        ticket.setType(getSelectedRadioText(binding.type).toLowerCase());
        ticket.setQuantity(getLong(binding.quantity));
        ticket.setPrice(getFloat(binding.price));
        ticket.setSalesStartsAt(getString(binding.salesStartTime));
        ticket.setSalesEndsAt(getString(binding.salesEndTime));

        return true;
    }
}
