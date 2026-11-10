package com.mdd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class BackApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_delegatesToSpringApplication() {
        String[] args = {"--spring.profiles.active=test"};

        try (var springApplication = mockStatic(SpringApplication.class)) {
            BackApplication.main(args);

            springApplication.verify(
                    () -> SpringApplication.run(BackApplication.class, args)
            );
        }
    }

}
