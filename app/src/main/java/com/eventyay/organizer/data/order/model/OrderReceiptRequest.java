package com.eventyay.organizer.data.order.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class OrderReceiptRequest {

    public String orderIdentifier;
}
