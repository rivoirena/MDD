package com.mdd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'API REST MDD (Monde de Dév).
 * <p>
 * {@link SpringBootApplication} active la configuration automatique de Spring Boot
 * et le scan des composants du package {@code com.mdd} et de ses sous-packages
 * (contrôleurs, services, repositories, configuration de sécurité).
 */
@SpringBootApplication
public class BackApplication {

    /**
     * Démarre l'application Spring Boot.
     *
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

}
