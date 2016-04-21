/**
 * 
 */
package com.evanda.ipod.aws.utilities;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * @author anayak
 *
 */
@FunctionalInterface
public interface DynamoDBConnector {

	public AmazonDynamoDBClient createConnectionToDynamoDB();
	
}
