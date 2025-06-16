package com.github.k1mb1.cinema_java_spring.models.movie;

import com.github.k1mb1.cinema_java_spring.models.country.CountryEntity;
import com.github.k1mb1.cinema_java_spring.models.genre.GenreEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Set;

public record MovieFilter(
        LocalDate releaseDateGte,
        LocalDate releaseDateLte,
        String titleLike,
        Set<Integer> genresIdIn,
        Set<Integer> countriesIdIn
) {
    public Specification<MovieEntity> toSpecification() {
        return Specification
                .where(releaseDateGteSpec())
                .and(releaseDateLteSpec())
                .and(titleLikeSpec())
                .and(genresIdInSpec())
                .and(countriesIdInSpec());
    }

    private Specification<MovieEntity> releaseDateGteSpec() {
        return ((root, query, cb) -> releaseDateGte != null
                ? cb.greaterThanOrEqualTo(root.get("releaseDate"), releaseDateGte)
                : null);
    }

    private Specification<MovieEntity> releaseDateLteSpec() {
        return ((root, query, cb) -> releaseDateLte != null
                ? cb.lessThanOrEqualTo(root.get("releaseDate"), releaseDateLte)
                : null);
    }

    private Specification<MovieEntity> titleLikeSpec() {
        return ((root, query, cb) -> StringUtils.hasText(titleLike)
                ? cb.like(cb.lower(root.get("title")), "%" + titleLike.toLowerCase() + "%")
                : null);
    }

    private Specification<MovieEntity> genresIdInSpec() {
        return (root, query, cb) -> {
            if (genresIdIn == null || genresIdIn.isEmpty()) {
                return null;
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<MovieEntity> subRoot = subquery.from(MovieEntity.class);
            Join<MovieEntity, GenreEntity> joinGenres = subRoot.join("genres");
            subquery.select(subRoot.get("id"))
                    .where(
                            cb.equal(subRoot.get("id"), root.get("id")),
                            joinGenres.get("id").in(genresIdIn)
                    )
                    .groupBy(subRoot.get("id"))
                    .having(cb.equal(cb.countDistinct(joinGenres.get("id")), genresIdIn.size()));

            return cb.exists(subquery);
        };
    }

    public Specification<MovieEntity> countriesIdInSpec() {
        return (root, query, cb) -> {
            if (countriesIdIn == null || countriesIdIn.isEmpty()) {
                return null;
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<MovieEntity> subRoot = subquery.from(MovieEntity.class);
            Join<MovieEntity, CountryEntity> joinCountries = subRoot.join("countries");
            subquery.select(subRoot.get("id"))
                    .where(
                            cb.equal(subRoot.get("id"), root.get("id")),
                            joinCountries.get("id").in(countriesIdIn)
                    )
                    .groupBy(subRoot.get("id"))
                    .having(cb.equal(cb.countDistinct(joinCountries.get("id")), countriesIdIn.size()));

            return cb.exists(subquery);
        };
    }
}