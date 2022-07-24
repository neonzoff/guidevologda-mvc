package ru.neonzoff.guidevologdamvc.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Класс для работы с объектным хранилищем S3
 *
 * @author Tseplyaev Dmitry
 */
public interface StorageService {
    String uploadFile(MultipartFile file);

    String uploadFile(File file);

    byte[] downloadFile(String fileName);

    String deleteFile(String fileName);
}
