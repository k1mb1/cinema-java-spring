package com.github.k1mb1.cinema_java_spring.models.movie;

import com.github.k1mb1.cinema_java_spring.models.BaseEntity;
import com.github.k1mb1.cinema_java_spring.models.country.CountryEntity;
import com.github.k1mb1.cinema_java_spring.models.genre.GenreEntity;
import com.github.k1mb1.cinema_java_spring.models.watchedmovie.WatchedMovieEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "movies")
public class MovieEntity extends BaseEntity {
    @Column(name = "title", nullable = false, unique = true)
    String title;

    String description;

    Integer year;

    @Column(name = "release_date")
    LocalDate releaseDate;

    @Column(name = "world_gross")
    Long worldGross;

    Long budget;

    @Column(name = "age_rating")
    String ageRating;

    @Column(name = "duration_minutes")
    Integer durationMinutes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    Set<GenreEntity> genres = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_countries",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    @Builder.Default
    Set<CountryEntity> countries = new HashSet<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    @Builder.Default
    Set<WatchedMovieEntity> watchedMovies = new HashSet<>();
}
