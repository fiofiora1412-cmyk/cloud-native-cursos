package cl.duoc.cloudnative.consumer.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;



@Service
public class CursoStorageService {

    private final S3Client s3Client;

    public CursoStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Value("${aws.s3.bucket}")
    private String bucket;

    public String subirArchivo(Path archivo, String nombreArchivo)
            throws IOException {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(nombreArchivo)
                .contentType(Files.probeContentType(archivo))
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromFile(archivo)
        );

        return String.format(
                "https://%s.s3.amazonaws.com/%s",
                bucket,
                nombreArchivo
        );

    }

}