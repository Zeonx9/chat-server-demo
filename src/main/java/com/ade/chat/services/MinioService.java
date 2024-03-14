package com.ade.chat.services;

import com.ade.chat.exception.UploadFailedException;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Сервис, предоставляющий методы взаимодействия с S3
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    /**
     * Сохраняет переданный файл в хранилище
     * @param file файл полученный по сети
     * @return имя сохраненного объекта (UUID)
     */
    public String uploadFile(MultipartFile file) {
        try {
            return uploadFile(file.getContentType(), file.getSize(), file.getInputStream());
        }
        catch (IOException e) {
            throw new UploadFailedException("unable to get bytes from file");
        }
    }

    /**
     * Сохраняет файл в хранилище
     * @param contentType MediaType файла
     * @param size размер в байтах
     * @param fileBytes InputStream с байтами файла
     * @return имя сохраненного объекта (UUID)
     */
    public String uploadFile(String contentType, long size, InputStream fileBytes) {
        String objectName = UUID.randomUUID().toString();

        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .contentType(contentType)
                    .stream(fileBytes, size, -1)
                    .build();

            minioClient.putObject(args);
            return objectName;
        }
        catch (Exception e) {
            throw new UploadFailedException("Error during s3 uploading");
        }
    }

    /**
     * Получает MIME тип сохраненного объекта
     * @param objectName имя объекта (UUID)
     * @return MediaType сохраненный при загрузке
     */
    public String getContentType(String objectName) {
        try {
            StatObjectArgs args = StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build();
            StatObjectResponse statObject = minioClient.statObject(args);
            return statObject.contentType();
        }
        catch (Exception e) {
            log.error("get stats error", e);
            return null;
        }
    }

    /**
     * Получает поток байт сохраненного объекта
     * @param objectName имя объекта (UUID)
     * @return поток байтов для чтения
     */
    public InputStream getObjectAsStream(String objectName) {
        try {
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build();
            return minioClient.getObject(args);
        }
        catch (Exception e) {
            log.error("get object error", e);
            return null;
        }
    }
}
