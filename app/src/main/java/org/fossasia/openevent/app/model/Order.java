package org.fossasia.openevent.app.model;

import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("amount")
    private Long amount;
    @SerializedName("completed_at")
    private String completedAt;
    @SerializedName("id")
    private Long id;
    @SerializedName("identifier")
    private String identifier;
    @SerializedName("invoice_number")
    private String invoiceNumber;
    @SerializedName("paid_via")
    private String paidVia;
    @SerializedName("payment_mode")
    private String  paymentMode;
    @SerializedName("status")
    private String status;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPaidVia() {
        return paidVia;
    }

    public void setPaidVia(String paidVia) {
        this.paidVia = paidVia;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
