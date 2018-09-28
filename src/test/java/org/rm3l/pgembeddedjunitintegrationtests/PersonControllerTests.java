package org.rm3l.pgembeddedjunitintegrationtests;

import org.junit.Test;
import org.rm3l.pgembeddedjunitintegrationtests.domain.Book;
import org.rm3l.pgembeddedjunitintegrationtests.domain.Person;
import org.rm3l.pgembeddedjunitintegrationtests.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.UUID;

public class PersonControllerTests extends AbstractIT {

    @Autowired
    private PersonRepository personRepository;

    private void injectTestData() {
        final Book aFirstBook = new Book.Builder()
                .withTitle("a first book")
                .withIsbn(UUID.randomUUID().toString())
                .withAuthor(new Person.Builder()
                        .withEmail("a@b.cd").build())
                .build();
        final Book aSecondBook = new Book.Builder()
                .withTitle("a second book")
                .withIsbn(UUID.randomUUID().toString())
                .withAuthor(new Person.Builder()
                        .withEmail("b@c.de").build())
                .build();
        final Book aThirdBook = new Book.Builder()
                .withTitle("a first book")
                .withIsbn(UUID.randomUUID().toString())
                .withAuthor(new Person.Builder()
                        .withEmail("c@d.ef").build())
                .build();
        final Person homerSimpson = new Person.Builder()
                .withFirstName("Homer")
                .withLastName("Simpson")
                .withEmail("homer@simps.on")
                .withBook(aFirstBook)
                .build();
        final Person margeSimpson = new Person.Builder()
                .withFirstName("Marge")
                .withLastName("Simpson")
                .withEmail("marge@simps.on")
                .withBookLibrary(aFirstBook, aSecondBook, aThirdBook)
                .build();

        this.personRepository.saveAll(Arrays.asList(homerSimpson, margeSimpson));
    }

    @Test
    public void testGetMethod() {
        this.injectTestData();
    }
}
