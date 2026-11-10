package com.mdd.repository;

import com.mdd.models.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA pour l'entité {@link Theme}.
 * <p>
 * N'ajoute aucune méthode : les opérations CRUD héritées de {@link JpaRepository} suffisent.
 */
public interface ThemeRepository extends JpaRepository<Theme, Long> {
}