package com.elpdeliveries.model;
import java.sql.Date;

public class Delivery {
    private int id;
    private Date createdDate;
    private Date dlvDate;
    private String fname;
    private String lname;
    private String phnum;
    private String addr;
    private String storeNotes;
    private String dlvNotes;
    private int receiptNum;
    private String photo;
    private boolean completed;
    private String completedBy;
    private Date completedDate;
    private boolean handedToCustomer;
    private String addEdit;

    public Delivery(int id, Date createdDate, Date dlvDate,
            String fname, String lname, String phnum,
            String addr, String storeNotes, String dlvNotes,
            int receiptNum, String photo,
            boolean completed, String completedBy,
            Date completedDate, boolean handedToCustomer) {
        this.id = id;
        this.createdDate = createdDate;
        this.dlvDate = dlvDate;
        this.fname = fname;
        this.lname = lname;
        this.phnum = phnum;
        this.addr = addr;
        this.storeNotes = storeNotes;
        this.dlvNotes = dlvNotes;
        this.receiptNum = receiptNum;
        this.photo = photo;
        this.completed = completed;
        this.completedBy = completedBy;
        this.completedDate = completedDate;
        this.handedToCustomer = handedToCustomer;
        this.addEdit = "Edit";
    }

    public Delivery(Date createdDate, Date dlvDate,
            String fname, String lname, String phnum,
            String addr, String storeNotes, String dlvNotes,
            int receiptNum, String photo,
            boolean completed, String completedBy,
            Date completedDate, boolean handedToCustomer) {
        this.createdDate = createdDate;
        this.dlvDate = dlvDate;
        this.fname = fname;
        this.lname = lname;
        this.phnum = phnum;
        this.addr = addr;
        this.storeNotes = storeNotes;
        this.dlvNotes = dlvNotes;
        this.receiptNum = receiptNum;
        this.photo = photo;
        this.completed = completed;
        this.completedBy = completedBy;
        this.completedDate = completedDate;
        this.handedToCustomer = handedToCustomer;
        this.addEdit = "Edit";
    }

    public int getId() {
        return id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getDlvDate() {
        return dlvDate;
    }

    public void setDlvDate(Date dlvDate) {
        this.dlvDate = dlvDate;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhnum() {
        return phnum;
    }

    public void setPhnum(String phnum) {
        this.phnum = phnum;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getStoreNotes() {
        return storeNotes;
    }

    public void setStoreNotes(String storeNotes) {
        this.storeNotes = storeNotes;
    }

    public String getDlvNotes() {
        return dlvNotes;
    }

    public void setDlvNotes(String dlvNotes) {
        this.dlvNotes = dlvNotes;
    }

    public int getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(int receiptNum) {
        this.receiptNum = receiptNum;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public boolean isHandedToCustomer() {
        return handedToCustomer;
    }

    public void setHandedToCustomer(boolean handedToCustomer) {
        this.handedToCustomer = handedToCustomer;
    }

    public String getName() {
        return this.fname + " " + this.lname;
    }

    public String getAddEdit() {
        return addEdit;
    }

}
