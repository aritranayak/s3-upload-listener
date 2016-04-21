/**
 * 
 */
package com.evanda.ipod.aws.utilities;

/**
 * @author anayak
 *
 */
@FunctionalInterface
public interface InsertDynamoDBItem<T, R> {

	public R saveDynamoDBItem(T obj);
	
}
