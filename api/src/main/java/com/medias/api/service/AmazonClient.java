package com.medias.api.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medias.api.model.Media;

@Service
public class AmazonClient {

	private AmazonS3 s3client;

	@Value("${aws.endpointUrl}")
	private String endpointUrl;
	@Value("${aws.bucketName}")
	private String bucketName;
	@Value("${aws.accessKey}")
	private String accessKey;
	@Value("${aws.secretKey}")
	private String secretKey;

	@PostConstruct
	private void initializeAmazon() {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.s3client = AmazonS3ClientBuilder.standard().withRegion("sa-east-1").withCredentials(new AWSStaticCredentialsProvider(awsCreds))
				.build();
	}

	public String uploadMedia(Media media) {
		String fileUrl = "";
		File file = convertMediaToFile(media);
		String fileName = media.getName()+".txt";
		fileUrl = endpointUrl + "/" + fileName;

		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
		file.delete();

		return fileUrl;
	}

	private File convertMediaToFile(Media media) {
		File file = new File(media.getName()+".txt");
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			ObjectMapper mapper = new ObjectMapper();
			writer.write(mapper.writeValueAsString(media));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return file;
	}

	public String updateMediaFile(Media media, String registeredFileName) {
		deleteFile(registeredFileName);
		return uploadMedia(media);
	}

	public void deleteFile(String mediaName) {
		s3client.deleteObject(new DeleteObjectRequest(bucketName,mediaName));
		
	}
}
