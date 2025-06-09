package com.github.k1mb1.cinema_java_spring.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

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

    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    Set<Genre> genres = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "movie_countries",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    @Builder.Default
    Set<Country> countries = new HashSet<>();

    @OneToMany(mappedBy = "movie")
    @Builder.Default
    Set<WatchedMovie> watchedMovies = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updateAt;
}
