package com.is550.lmsrest.variables;

import javax.xml.datatype.XMLGregorianCalendar;

public class ReturnBookRest {

    protected long studentId;
    protected long bookId;
    protected XMLGregorianCalendar returningTime;

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
    public XMLGregorianCalendar getReturningTime() {
        return returningTime;
    }
    public void setReturningTime(XMLGregorianCalendar value) {
        this.returningTime = value;
    }

}
