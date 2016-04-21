package com.evanda.ipod.aws.utilities;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3ObjectEntity;

/**
 * 
 * 
 * 
 * @author anayak
 *
 */
public class RetrieveS3Object {

	/**
	 * Returns the list of S3 Object Entity based on the triggered event
	 * 
	 * 
	 * @param _s3EventNotification
	 * @return List<S3ObjectEntity>
	 */
	public static List<S3ObjectEntity> retrieveS3Object(S3EventNotification _s3EventNotification){
		List<S3ObjectEntity> _s3ObjectEntityList = _s3EventNotification
													.getRecords()
													.parallelStream()
													.map(e -> {
														return e.getS3().getObject();
													})
													.collect(Collectors.toList());
													
		return _s3ObjectEntityList;						
	}
	
	/**
	 * S3 Client init method
	 * 
	 * @return
	 */
	public static AmazonS3Client initializeS3Client(){
		Supplier<AmazonS3Client> _supplier = () -> {
			return new AmazonS3Client(new EnvironmentVariableCredentialsProvider());
		};
		
		return _supplier.get();
	}
	
}
