package org.rm3l.pgembeddedjunitintegrationtests.repositories;

import org.rm3l.pgembeddedjunitintegrationtests.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PersonRepository extends JpaRepository<Person, Long> {
}
