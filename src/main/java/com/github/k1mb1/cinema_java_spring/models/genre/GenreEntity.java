package com.github.k1mb1.cinema_java_spring.models.genre;

import com.github.k1mb1.cinema_java_spring.models.BaseEntity;
import com.github.k1mb1.cinema_java_spring.models.movie.MovieEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "genres")
public class GenreEntity extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    String name;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    @Builder.Default
    Set<MovieEntity> movies = new HashSet<>();
}
