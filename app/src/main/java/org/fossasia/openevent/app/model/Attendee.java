package org.fossasia.openevent.app.model;

import com.google.gson.annotations.SerializedName;

public class Attendee {

    @SerializedName("checked_in")
    private Boolean checkedIn;
    @SerializedName("email")
    private String email;
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("id")
    private Long id;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("order")
    private Order order;
    @SerializedName("ticket")
    private Ticket ticket;

    public Boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

}
