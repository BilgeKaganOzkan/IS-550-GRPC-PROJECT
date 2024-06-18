package com.is550.lmsrestclient.variables;

public class AddReturnBookRequestRest {

    protected long loginId;
    protected ReturnBookRest returnBook;

    public long getLoginId() {
        return loginId;
    }
    public void setLoginId(long value) {
        this.loginId = value;
    }
    public ReturnBookRest getReturnBook() {
        return returnBook;
    }
    public void setReturnBook(ReturnBookRest value) {
        this.returnBook = value;
    }

}