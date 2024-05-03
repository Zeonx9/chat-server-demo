package com.ade.chat.controllers;


import com.ade.chat.services.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Отвечает за загрузку и скачивание файлов. Не требует авторизации
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("chat_api/v1")
public class FileController {

    private final MinioService minioService;

    /**
     * Загружает файл в S3 хранилище
     * @param file файл для загрузи
     * @return имя сохраненного объекта (UUID)
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadFile(@RequestPart MultipartFile file) {
        String objectName = minioService.uploadFile(file);
        if (objectName == null) {
            return ResponseEntity.internalServerError().build();
        }
        log.info(
                "uploaded file '{}' type: {} as object: {}",
                file.getOriginalFilename(), file.getContentType(), objectName
        );
        return ResponseEntity.ok(objectName);
    }

    /**
     * Скачивает файл из S3 хранилища
     * @param objectName имя (UUID) объекта, полученный при его загрузке
     * @return запрошенный ресурс с указанным типом.
     */
    @GetMapping("/download/{objectName}")
    ResponseEntity<Resource> downloadFile(@PathVariable String objectName){
        log.info("file {} requested", objectName);

        String contentType = minioService.getContentType(objectName);
        InputStream objectStream = minioService.getObjectAsStream(objectName);

        if (contentType == null || objectStream == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte [] imageBytes = objectStream.readAllBytes();
            ByteArrayResource resource = new ByteArrayResource(imageBytes);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        }
        catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}

