package com.elpdeliveries.util;

import java.io.File;
import java.util.UUID;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3Uploader {
    private static String BUCKET;
    private static final Region REGION = Region.AP_SOUTHEAST_2;
    private static String ACCESS_KEY;
    private static String SECRET_KEY;

    public static String upload(File file) {
        try {
            BUCKET = ConfigManager.getBucket();
            ACCESS_KEY = ConfigManager.getAwsAccessKey();
            SECRET_KEY = ConfigManager.getAwsSecretKey();
        } catch (Exception e) {
            AppLogger.error(e.getMessage(), e);
        }
        AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
        String fileName;
        AppLogger.info("Uploading image to S3");
        try (S3Client s3 = S3Client.builder().region(REGION)
                .credentialsProvider(StaticCredentialsProvider.create(credentials)).build()) {
            String extension = file.getName().substring(file.getName().lastIndexOf("."));
            fileName = UUID.randomUUID() + extension;
            PutObjectRequest request = PutObjectRequest.builder().bucket(BUCKET).key(fileName).build();
            s3.putObject(request, RequestBody.fromFile(file));
        }
        return String.format("https://%s.s3.%s.amazonaws.com/%s", BUCKET, REGION.id(), fileName);
    }
}
