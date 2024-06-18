package com.is550.lmsrestclient.variables;

import javax.xml.datatype.XMLGregorianCalendar;

public class UserBorrowingInfosRest {

    protected BookRest book;
    protected XMLGregorianCalendar borrowingTime;
    protected XMLGregorianCalendar dueDate;
    protected XMLGregorianCalendar returningTime;
    protected String paid;
    protected long fine;

    public BookRest getBook() {
        return book;
    }
    public void setBook(BookRest value) {
        this.book = value;
    }
    public XMLGregorianCalendar getBorrowingTime() {
        return borrowingTime;
    }
    public void setBorrowingTime(XMLGregorianCalendar value) {
        this.borrowingTime = value;
    }
    public XMLGregorianCalendar getDueDate() {
        return dueDate;
    }
    public void setDueDate(XMLGregorianCalendar value) {
        this.dueDate = value;
    }
    public XMLGregorianCalendar getReturningTime() {
        return returningTime;
    }
    public void setReturningTime(XMLGregorianCalendar value) {
        this.returningTime = value;
    }
    public String getPaid() {
        return paid;
    }
    public void setPaid(String value) {
        this.paid = value;
    }
    public long getFine() {
        return fine;
    }
    public void setFine(long value) {
        this.fine = value;
    }
}
