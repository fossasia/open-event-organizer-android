package org.fossasia.openevent.app.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Order implements Parcelable {

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

    public Order() {}

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

    @Override
    public String toString() {
        return "Order{" +
            "amount=" + amount +
            ", completedAt='" + completedAt + '\'' +
            ", id=" + id +
            ", identifier='" + identifier + '\'' +
            ", invoiceNumber='" + invoiceNumber + '\'' +
            ", paidVia='" + paidVia + '\'' +
            ", paymentMode='" + paymentMode + '\'' +
            ", status='" + status + '\'' +
            '}';
    }

    // Parcelable Implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.amount);
        dest.writeString(this.completedAt);
        dest.writeValue(this.id);
        dest.writeString(this.identifier);
        dest.writeString(this.invoiceNumber);
        dest.writeString(this.paidVia);
        dest.writeString(this.paymentMode);
        dest.writeString(this.status);
    }

    protected Order(Parcel in) {
        this.amount = (Long) in.readValue(Long.class.getClassLoader());
        this.completedAt = in.readString();
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.identifier = in.readString();
        this.invoiceNumber = in.readString();
        this.paidVia = in.readString();
        this.paymentMode = in.readString();
        this.status = in.readString();
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
