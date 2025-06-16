package com.github.k1mb1.cinema_java_spring.repositories;

import com.github.k1mb1.cinema_java_spring.models.movie.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Integer>, JpaSpecificationExecutor<MovieEntity> {

    @EntityGraph(attributePaths = {"genres", "countries"})
    Optional<MovieEntity> findWithRelationshipsById(Integer id);

    @EntityGraph(attributePaths = {"genres", "countries"})
    @NonNull
    Page<MovieEntity> findAll(@Nullable Specification<MovieEntity> spec, @NonNull Pageable pageable);
}
