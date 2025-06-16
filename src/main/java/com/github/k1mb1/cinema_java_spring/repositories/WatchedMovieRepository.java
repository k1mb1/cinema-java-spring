package com.github.k1mb1.cinema_java_spring.repositories;

import com.github.k1mb1.cinema_java_spring.models.watchedmovie.WatchedMovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchedMovieRepository extends JpaRepository<WatchedMovieEntity, Integer>, JpaSpecificationExecutor<WatchedMovieEntity> {

    boolean existsByUserIdAndMovieId(Integer userId, Integer movieId);

    void deleteByUserIdAndMovieId(Integer userId, Integer movieId);
}
