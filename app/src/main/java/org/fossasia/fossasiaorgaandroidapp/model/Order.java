
package org.fossasia.fossasiaorgaandroidapp.model;


import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("amount")
    private Long mAmount;
    @SerializedName("completed_at")
    private String mCompletedAt;
    @SerializedName("id")
    private Long mId;
    @SerializedName("identifier")
    private String mIdentifier;
    @SerializedName("invoice_number")
    private String mInvoiceNumber;
    @SerializedName("paid_via")
    private String mPaidVia;
    @SerializedName("payment_mode")
    private Object mPaymentMode;
    @SerializedName("status")
    private String mStatus;

    public Long getAmount() {
        return mAmount;
    }

    public void setAmount(Long amount) {
        mAmount = amount;
    }

    public String getCompletedAt() {
        return mCompletedAt;
    }

    public void setCompletedAt(String completedAt) {
        mCompletedAt = completedAt;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public void setIdentifier(String identifier) {
        mIdentifier = identifier;
    }

    public String getInvoiceNumber() {
        return mInvoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        mInvoiceNumber = invoiceNumber;
    }

    public String getPaidVia() {
        return mPaidVia;
    }

    public void setPaidVia(String paidVia) {
        mPaidVia = paidVia;
    }

    public Object getPaymentMode() {
        return mPaymentMode;
    }

    public void setPaymentMode(Object paymentMode) {
        mPaymentMode = paymentMode;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}
