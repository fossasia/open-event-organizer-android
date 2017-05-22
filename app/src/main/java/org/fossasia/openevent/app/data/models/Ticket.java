package org.fossasia.openevent.app.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Ticket implements Parcelable {

    @SerializedName("description")
    private String description;
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("price")
    private Float price;
    @SerializedName("quantity")
    private Long quantity;
    @SerializedName("type")
    private String type;

    public Ticket() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Ticket{" +
            "description='" + description + '\'' +
            ", id=" + id +
            ", name='" + name + '\'' +
            ", price=" + price +
            ", quantity=" + quantity +
            ", type='" + type + '\'' +
            '}';
    }

    // Parcelable Implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeValue(this.price);
        dest.writeValue(this.quantity);
        dest.writeString(this.type);
    }

    protected Ticket(Parcel in) {
        this.description = in.readString();
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.price = (Float) in.readValue(Float.class.getClassLoader());
        this.quantity = (Long) in.readValue(Long.class.getClassLoader());
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Ticket> CREATOR = new Parcelable.Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel source) {
            return new Ticket(source);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };
}
