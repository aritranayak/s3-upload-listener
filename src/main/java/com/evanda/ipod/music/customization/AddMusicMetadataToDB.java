package com.evanda.ipod.music.customization;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3ObjectEntity;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.evanda.ipod.aws.utilities.DynamoDBConnector;
import com.evanda.ipod.aws.utilities.InsertDynamoDBItem;
import com.evanda.ipod.aws.utilities.Mp3FileMetadataExtractor;
import com.evanda.ipod.aws.utilities.RetrieveS3Object;
import com.evanda.ipod.dynamoDB.MusicInfo;

/**
 * The handler class representing the AWS Lambda function.
 * 
 * Whenever a MP3 file is uploaded into a bucket named 'my-music-files', the handleRequest() method gets triggered.
 * The method is responsible in reading the file as Mp3, extracting its metadata and storing the info in a DynamoDB table..
 * An item will be created in the DynamoDB table, for every time the file is uploaded in S3.
 * 
 * 
 * @author anayak
 *
 */
public class AddMusicMetadataToDB implements RequestHandler<S3EventNotification, String> {
	
	private final String FAILURE = "FAILURE";
	
	private final String SUCCESS = "SUCCESS";
	
	private final String TRYING = "Trying to connect to DynamoDB ..................";
	
	private final String vBucketName = "my-music-files";
	
	LambdaLogger vLogger = null;

	public String handleRequest(S3EventNotification _s3EventNotification, Context pContext) {

		/**
		 * Stores logger retrieved from AWS Context object
		 */
		vLogger = pContext.getLogger();

		String returnValue = "No uploads in S3 detected. So Why the fuss?";

		long _noOfUploads = _s3EventNotification.getRecords().size();

		if (0 < _noOfUploads)
			returnValue = saveMusicInfoInDynamoDBTable(_s3EventNotification);

		return returnValue;
	}

	/**
	 * 
	 * 
	 * 
	 * @param _tableName
	 * @return
	 */
	private String saveMusicInfoInDynamoDBTable(S3EventNotification _s3EventNotification) {
		
		String rMessage = TRYING;
		vLogger.log(rMessage);
		
		DynamoDBConnector _dynamoDBConnector = () -> {
			AmazonDynamoDBClient _amazonDynamoDBClient = new AmazonDynamoDBClient(new EnvironmentVariableCredentialsProvider());
			Region iRegion = Regions.getCurrentRegion();
			if (iRegion == null) iRegion = Region.getRegion(Regions.US_WEST_2);
			_amazonDynamoDBClient.setRegion(iRegion);
			return _amazonDynamoDBClient;
		};
		
		// first retrieve the S3 Object that needs to be analyzed
		List<S3ObjectEntity> _s3ObjectEntityList = RetrieveS3Object.retrieveS3Object(_s3EventNotification);
		
		if((null != _s3ObjectEntityList) && (0 != _s3ObjectEntityList.size()))
		{
			List<String> _resultInfoList = _s3ObjectEntityList
					  .stream()
					  .map(_s3Object -> {
						  InsertDynamoDBItem<S3ObjectEntity, String> insertDynamoDBItem = (_s3ObjectEntity) -> {
							  try{
								  
								  // now, get the S3 client
								  AmazonS3Client _s3Client = RetrieveS3Object.initializeS3Client();
								  // now insert into the Table, the required info
								  String _keyOfNewObject = _s3ObjectEntity.getKey();
								  vLogger.log("Key =========================>>>>" + _keyOfNewObject);
								  
								  _s3Client.getObject(
										  new GetObjectRequest(vBucketName, _keyOfNewObject), 
										  new File("/tmp/" + _keyOfNewObject)
										  );
								
								  vLogger.log("\n MP3 File Creation is complete");
								  
								  // now read all info from this temp file
								  MusicInfo musicInfo = Mp3FileMetadataExtractor.createMusicInfoObjectFromFile("/tmp/" + _keyOfNewObject);
								  
								  vLogger.log("MusicInfo object =========================>>>>" + musicInfo.toString());
								  
								  //finally enter into the table
								  DynamoDBMapper _dynamoDBMapper = new DynamoDBMapper(_dynamoDBConnector.createConnectionToDynamoDB());
								  _dynamoDBMapper.save(musicInfo);
								  return SUCCESS;
							  }catch(Exception ex){
								  vLogger.log(ex.getMessage());
								  return FAILURE;
							  }
						  };
						  // execute the lambda
						  return insertDynamoDBItem.saveDynamoDBItem(_s3Object);
					  })
					  .collect(Collectors.toList());
			
				rMessage = _resultInfoList.stream().collect(Collectors.joining(";")).concat(". So now check the database table!!");
			
		}
		
		return rMessage;
	}

}
