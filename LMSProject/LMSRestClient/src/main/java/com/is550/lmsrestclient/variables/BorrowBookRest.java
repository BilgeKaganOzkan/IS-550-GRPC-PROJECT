package com.is550.lmsrestclient.variables;

import javax.xml.datatype.XMLGregorianCalendar;

public class BorrowBookRest {

    protected long studentId;
    protected long bookId;
    protected XMLGregorianCalendar borrowingTime;
    protected XMLGregorianCalendar dueDate;

    public long getStudentId() {
        return studentId;
    }
    public void setStudentId(long value) {
        this.studentId = value;
    }
    public long getBookId() {
        return bookId;
    }
    public void setBookId(long value) {
        this.bookId = value;
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

}

