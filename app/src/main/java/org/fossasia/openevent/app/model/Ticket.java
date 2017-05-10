
package org.fossasia.openevent.app.model;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Ticket {

    @SerializedName("description")
    private String mDescription;
    @SerializedName("id")
    private Long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("price")
    private Float mPrice;
    @SerializedName("quantity")
    private Long mQuantity;
    @SerializedName("type")
    private String mType;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Float getPrice() {
        return mPrice;
    }

    public void setPrice(Float price) {
        mPrice = price;
    }

    public Long getQuantity() {
        return mQuantity;
    }

    public void setQuantity(Long quantity) {
        mQuantity = quantity;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
