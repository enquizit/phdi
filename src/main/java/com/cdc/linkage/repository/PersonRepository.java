package com.cdc.linkage.repository;


import com.cdc.linkage.entities.Person;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface PersonRepository extends ReactiveCrudRepository<Person, UUID> {

}
