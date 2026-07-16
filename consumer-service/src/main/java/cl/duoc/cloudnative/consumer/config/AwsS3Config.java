package cl.duoc.cloudnative.consumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.credentials.access-key}")
    private String accessKey;

    @Value("${aws.credentials.secret-key}")
    private String secretKey;

    @Value("${aws.credentials.session-token}")
    private String sessionToken;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {

        return StaticCredentialsProvider.create(

                AwsSessionCredentials.create(
                        accessKey,
                        secretKey,
                        sessionToken
                )

        );

    }

    @Bean
    public S3Client s3Client(AwsCredentialsProvider credentialsProvider) {

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();

    }

}