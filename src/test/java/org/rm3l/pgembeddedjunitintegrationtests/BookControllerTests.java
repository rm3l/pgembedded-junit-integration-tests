package org.rm3l.pgembeddedjunitintegrationtests;

import org.junit.Test;
import org.rm3l.pgembeddedjunitintegrationtests.domain.Book;
import org.rm3l.pgembeddedjunitintegrationtests.domain.Person;
import org.rm3l.pgembeddedjunitintegrationtests.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookControllerTests extends AbstractIT {

    @Autowired
    private BookRepository bookRepository;

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

        this.bookRepository.saveAll(
                Arrays.asList(aFirstBook, aSecondBook, aThirdBook));
    }

    @Test
    public void testGetMethod() throws Exception {
        this.injectTestData();

        this.mvc.perform(get("/Books"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.Books", hasSize(3)));

    }
}
