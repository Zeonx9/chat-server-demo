package com.ade.chat.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.bucket}")
    private String bucket;

    @Bean
    MinioClient getMinioClient() {
        log.info("MinIO client built with endpoint: {}", endpoint);
        MinioClient client =  MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        BucketExistsArgs existsArgs = BucketExistsArgs.builder().bucket(bucket).build();
        try {
            if (!client.bucketExists(existsArgs)) {
                MakeBucketArgs makeArgs = MakeBucketArgs.builder().bucket(bucket).build();
                client.makeBucket(makeArgs);
                log.info("MinIO made new bucket: {}", bucket);
            } else {
                log.info("MinIO bucket {} already existed", bucket);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }


        return client;
    }

}
