package org.rm3l.pgembeddedjunitintegrationtests.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(indexes = @Index(columnList = "isbn"))
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false)
    @Column(unique = true)
    private String isbn;

    @Basic(optional = false)
    private String title;

    @ManyToOne
    private Person owner;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Person author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(getIsbn(), book.getIsbn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIsbn());
    }

    public static class Builder {

        private final Book entity = new Book();

        public Builder withIsbn(String isbn) {
            this.entity.setIsbn(isbn);
            return this;
        }

        public Builder withTitle(String title) {
            this.entity.setTitle(title);
            return this;
        }

        public Builder withOwner(Person owner) {
            this.entity.setOwner(owner);
            return this;
        }

        public Builder withAuthor(Person author) {
            this.entity.setAuthor(author);
            return this;
        }

        public Book build() {
            return this.entity;
        }

    }
}
