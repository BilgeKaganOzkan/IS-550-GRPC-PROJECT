package com.is550.lmsrest.variables;

public class AddBookRequestRest {

    protected long loginId;
    protected AddBookRest addBook;

    public long getLoginId() {
        return loginId;
    }
    public void setLoginId(long value) {
        this.loginId = value;
    }
    public AddBookRest getAddBook() {
        return addBook;
    }
    public void setAddBook(AddBookRest value) {
        this.addBook = value;
    }

}