package ru.neonzoff.guidevologdamvc.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.DELETE_FILE;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.UPLOAD_FILE;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_ID;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_USERNAME;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertMultipartFileToFile;

/**
 * @author Tseplyaev Dmitry
 */
@Service
public class StorageServiceImpl implements StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    private final AuditService auditService;

    public StorageServiceImpl(AmazonS3 s3Client, AuditService auditService) {
        this.s3Client = s3Client;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public String uploadFile(MultipartFile file) {
        File convertedFile = convertMultipartFileToFile(file);
        s3Client.putObject(
                new PutObjectRequest(bucketName, file.getOriginalFilename(), convertedFile)
        );
        convertedFile.delete();

        auditService.saveAction(
                SYSTEM_ID,
                SYSTEM_USERNAME,
                String.format(UPLOAD_FILE.getAction(), file.getOriginalFilename()),
                new Date()
        );
        return "File " + file.getOriginalFilename() + " was uploaded";
    }

    @Override
    public String uploadFile(File file) {
        s3Client.putObject(
                new PutObjectRequest(bucketName, file.getName(), file)
        );

        auditService.saveAction(
                SYSTEM_ID,
                SYSTEM_USERNAME,
                String.format(UPLOAD_FILE.getAction(), file.getName()),
                new Date()
        );
        return "File " + file.getName() + " was uploaded";
    }

    @Override
    public byte[] downloadFile(String fileName) {
        S3Object object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = object.getObjectContent();
        byte[] bytes = null;
        try {
            bytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    @Override
    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);

        auditService.saveAction(
                SYSTEM_ID,
                SYSTEM_USERNAME,
                String.format(DELETE_FILE.getAction(), fileName),
                new Date()
        );
        return fileName + " was removed";
    }

}
