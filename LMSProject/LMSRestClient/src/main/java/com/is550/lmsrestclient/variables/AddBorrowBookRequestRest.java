package com.is550.lmsrestclient.variables;

public class AddBorrowBookRequestRest {

    protected long loginId;
    protected BorrowBookRest borrowBook;

    public long getLoginId() {
        return loginId;
    }
    public void setLoginId(long value) {
        this.loginId = value;
    }
    public BorrowBookRest getBorrowBook() {
        return borrowBook;
    }
    public void setBorrowBook(BorrowBookRest value) {
        this.borrowBook = value;
    }

}
