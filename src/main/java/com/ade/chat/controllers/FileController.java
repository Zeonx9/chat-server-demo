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

@RestController
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final MinioService minioService;

    @PostMapping(value = "/upload")
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


    @GetMapping("/download/{objectName}")
    ResponseEntity<Resource> downloadFile(@PathVariable String objectName) throws IOException {
        log.info("file {} requested", objectName);

        String contentType = minioService.getContentType(objectName);
        InputStream objectStream = minioService.getObjectAsStream(objectName);

        if (contentType == null || objectStream == null) {
            return ResponseEntity.notFound().build();
        }

        byte [] imageBytes = objectStream.readAllBytes();
        ByteArrayResource resource = new ByteArrayResource(imageBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

}

