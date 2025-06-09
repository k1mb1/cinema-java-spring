package com.github.k1mb1.cinema_java_spring.repositories;

import com.github.k1mb1.cinema_java_spring.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
}
