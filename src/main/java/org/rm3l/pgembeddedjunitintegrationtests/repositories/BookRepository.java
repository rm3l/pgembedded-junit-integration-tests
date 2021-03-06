package org.rm3l.pgembeddedjunitintegrationtests.repositories;

import org.rm3l.pgembeddedjunitintegrationtests.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
        collectionResourceRel = "Books",
        itemResourceRel = "Book",
        path = "Books"
)
public interface BookRepository extends JpaRepository<Book, String> {
}
