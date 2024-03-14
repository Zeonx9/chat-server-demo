package com.ade.chat.containers;

import org.testcontainers.containers.MinIOContainer;

public class MinIOTestContainer {
    private static final String IMAGE_VERSION = "minio/minio:latest";

    private static MinIOContainer container;

    public static MinIOContainer getInstance() {
        if (container == null) {
            container = new MinIOContainer(IMAGE_VERSION);
            container.start();

            System.setProperty("MINIO_ENDPOINT", container.getS3URL());
            System.setProperty("MINIO_ACCESS_KEY", container.getUserName());
            System.setProperty("MINIO_SECRET_KEY", container.getPassword());
        }
        return container;
    }
}
