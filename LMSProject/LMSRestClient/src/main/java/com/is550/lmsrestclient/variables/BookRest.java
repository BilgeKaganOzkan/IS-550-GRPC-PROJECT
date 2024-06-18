package com.is550.lmsrestclient.variables;

import java.util.Objects;

public class BookRest {

    protected long id;
    protected String name;
    protected String author;
    protected BookTypeRest type;
    protected String location;
    protected String available;

    public long getId() {
        return id;
    }
    public void setId(long value) {
        this.id = value;
    }
    public String getName() {
        return name;
    }
    public void setName(String value) {
        this.name = value;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String value) {
        this.author = value;
    }
    public BookTypeRest getType() {
        return type;
    }
    public void setType(BookTypeRest value) {
        this.type = value;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String value) {
        this.location = value;
    }
    public String getAvailable() {
        return available;
    }
    public void setAvailable(String value) {
        this.available = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookRest book = (BookRest) o;
        return id == book.id &&
                Objects.equals(name, book.name) &&
                Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, author);
    }
}
