package org.rm3l.pgembeddedjunitintegrationtests.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Table(indexes = @Index(columnList = "email"))
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false)
    @Column(unique = true)
    private String email;

    @Basic
    @Column(name = "first_name")
    private String firstName;

    @Basic
    @Column(name = "last_name")
    private String lastName;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = false,
            fetch = FetchType.LAZY,
            mappedBy = "title")
    private List<Book> bookLibrary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Book> getBookLibrary() {
        return bookLibrary;
    }

    public void setBookLibrary(List<Book> bookLibrary) {
        this.bookLibrary = bookLibrary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return getEmail().equals(person.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

    public static class Builder {

        private final Person entity = new Person();

        public Builder withEmail(String email) {
            this.entity.setEmail(email);
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.entity.setFirstName(firstName);
            return this;
        }

        public Builder withLastName(String lastName) {
            this.entity.setLastName(lastName);
            return this;
        }

        public Builder withBookLibrary(List<Book> bookLibrary) {
            this.entity.setBookLibrary(bookLibrary);
            return this;
        }

        public Builder withBookLibrary(Book... books) {
            this.withBookLibrary(books != null ? Arrays.asList(books) : null);
            return this;
        }

        public Builder withBook(Book book) {
            if (book != null) {
                List<Book> bookLibrary = this.entity.getBookLibrary();
                if (bookLibrary == null) {
                    bookLibrary = new ArrayList<>();
                    this.entity.setBookLibrary(bookLibrary);
                }
                bookLibrary.add(book);
            }
            return this;
        }

        public Person build() {
            return this.entity;
        }

    }
}
