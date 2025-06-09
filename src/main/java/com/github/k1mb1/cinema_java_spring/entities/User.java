package com.github.k1mb1.cinema_java_spring.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String username;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    Set<WatchedMovie> watchedMovies = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updateAt;
}
