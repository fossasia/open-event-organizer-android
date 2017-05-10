
package org.fossasia.fossasiaorgaandroidapp.model;


import com.google.gson.annotations.SerializedName;



public class AttendeeDetails {

    @SerializedName("checked_in")
    private Boolean mCheckedIn;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("firstname")
    private String mFirstname;
    @SerializedName("id")
    private Long mId;
    @SerializedName("lastname")
    private String mLastname;
    @SerializedName("order")
    private Order mOrder;
    @SerializedName("ticket")
    private Ticket mTicket;

    public Boolean getCheckedIn() {
        return mCheckedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        mCheckedIn = checkedIn;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getFirstname() {
        return mFirstname;
    }

    public void setFirstname(String firstname) {
        mFirstname = firstname;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getLastname() {
        return mLastname;
    }

    public void setLastname(String lastname) {
        mLastname = lastname;
    }

    public Order getOrder() {
        return mOrder;
    }

    public void setOrder(Order order) {
        mOrder = order;
    }

    public Ticket getTicket() {
        return mTicket;
    }

    public void setTicket(Ticket ticket) {
        mTicket = ticket;
    }

}
