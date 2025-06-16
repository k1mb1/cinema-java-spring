package com.github.k1mb1.cinema_java_spring.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    /**
     * Поле версии, используемое для механизма оптимистической блокировки.
     * <p>
     * Аннотация @Version используется JPA для обнаружения одновременных изменений одной и той же сущности.
     * При обновлении сущности версия автоматически увеличивается.
     * Если две транзакции пытаются обновить одну и ту же сущность одновременно, вторая транзакция
     * завершится с ошибкой OptimisticLockException, поскольку версия больше не соответствует.
     * <p>
     * Это поле не следует изменять вручную.
     */
//    @Version
//    Long version;//TODO сделать OptimisticLock
}
