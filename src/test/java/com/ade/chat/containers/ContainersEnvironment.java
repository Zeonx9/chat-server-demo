package com.ade.chat.containers;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ContainersEnvironment {
    @Container
    public static PostgresTestContainer postgresTestContainer = PostgresTestContainer.getInstance();
}
