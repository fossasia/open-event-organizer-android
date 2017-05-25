package org.fossasia.openevent.app.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Ticket implements Parcelable {

    @SerializedName("description")
    private String description;
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("price")
    private float price;
    @SerializedName("quantity")
    private long quantity;
    @SerializedName("type")
    private String type;

    public Ticket() {}

    public Ticket(long id, long quantity) {
        setId(id);
        setQuantity(quantity);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
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
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeFloat(this.price);
        dest.writeLong(this.quantity);
        dest.writeString(this.type);
    }

    protected Ticket(Parcel in) {
        this.description = in.readString();
        this.id = in.readLong();
        this.name = in.readString();
        this.price = in.readFloat();
        this.quantity = in.readLong();
        this.type = in.readString();
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
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
