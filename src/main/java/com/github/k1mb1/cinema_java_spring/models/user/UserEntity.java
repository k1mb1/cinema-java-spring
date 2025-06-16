package com.github.k1mb1.cinema_java_spring.models.user;

import com.github.k1mb1.cinema_java_spring.models.BaseEntity;
import com.github.k1mb1.cinema_java_spring.models.watchedmovie.WatchedMovieEntity;
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
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true)
    String username;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    Set<WatchedMovieEntity> watchedMovies = new HashSet<>();
}
