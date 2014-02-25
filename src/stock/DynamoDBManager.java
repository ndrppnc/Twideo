package stock;
/*

 * Copyright 2012-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

public class DynamoDBManager {

    static AmazonDynamoDBClient dynamoDB;

    /**
     * The only information needed to create a client are security credentials
     * consisting of the AWS Access Key ID and Secret Access Key. All other
     * configuration, such as the service endpoints, are performed
     * automatically. Client parameters, such as proxies, can be specified in an
     * optional ClientConfiguration object when constructing a client.
     *
     * @see com.amazonaws.auth.BasicAWSCredentials
     * @see com.amazonaws.auth.PropertiesCredentials
     * @see com.amazonaws.ClientConfiguration
     */
    private static void init() throws Exception {
    	/*
		 * This credentials provider implementation loads your AWS credentials
		 * from a properties file at the root of your classpath.
		 */
        dynamoDB = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider());
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        dynamoDB.setRegion(usWest2);
    }

    public static void main(String[] args) throws Exception {
        init();

        if(!tableExists("users")){
	    	/* create users table */
	        try {
	        	System.out.println("Start creating 'users' table...");
	        	
	        	String tableName = "users";
	
	            // Create a table with a primary hash key named 'name', which holds a string
	            CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
	                .withKeySchema(new KeySchemaElement().withAttributeName("username").withKeyType(KeyType.HASH))
	                .withAttributeDefinitions(new AttributeDefinition().withAttributeName("username").withAttributeType(ScalarAttributeType.S))
	                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
	            TableDescription createdTableDescription = dynamoDB.createTable(createTableRequest).getTableDescription();
	            System.out.println("Created Table: " + createdTableDescription);
	
	            // Wait for it to become active
	            waitForTableToBecomeAvailable(tableName);
	
	            // Describe our new table
	            DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
	            TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
	            System.out.println("Table Description: " + tableDescription);
	        } catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, which means your request made it "
	                    + "to AWS, but was rejected with an error response for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	        } catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, which means the client encountered "
	                    + "a serious internal problem while trying to communicate with AWS, "
	                    + "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
        }
        
        if(!tableExists("videos")){
	        /* create videos table */
	        try {
	        	System.out.println("Start creating 'videos' table...");
	        	
	            String tableName = "videos";
	
	            // Create a table with a primary hash key named 'name', which holds a string
	            CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
	                .withKeySchema(new KeySchemaElement().withAttributeName("filename").withKeyType(KeyType.HASH))
	                .withAttributeDefinitions(new AttributeDefinition().withAttributeName("filename").withAttributeType(ScalarAttributeType.S))
	                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
	            TableDescription createdTableDescription = dynamoDB.createTable(createTableRequest).getTableDescription();
	            System.out.println("Created Table: " + createdTableDescription);
	
	            // Wait for it to become active
	            waitForTableToBecomeAvailable(tableName);
	
	            // Describe our new table
	            DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
	            TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
	            System.out.println("Table Description: " + tableDescription);
	        } catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, which means your request made it "
	                    + "to AWS, but was rejected with an error response for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	        } catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, which means the client encountered "
	                    + "a serious internal problem while trying to communicate with AWS, "
	                    + "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
        }
        
        if(!tableExists("comments")){
	        /* create comments table */
	        try {
	        	System.out.println("Start creating 'comments' table...");
	        	
	            String tableName = "comments";
	
	            // Create a table with a primary hash key named 'name', which holds a string
	            CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
	                .withKeySchema(new KeySchemaElement().withAttributeName("filename").withKeyType(KeyType.HASH))
	                .withAttributeDefinitions(new AttributeDefinition().withAttributeName("filename").withAttributeType(ScalarAttributeType.S))
	                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
	            TableDescription createdTableDescription = dynamoDB.createTable(createTableRequest).getTableDescription();
	            System.out.println("Created Table: " + createdTableDescription);
	
	            // Wait for it to become active
	            waitForTableToBecomeAvailable(tableName);
	
	            // Describe our new table
	            DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
	            TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
	            System.out.println("Table Description: " + tableDescription);
	        } catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, which means your request made it "
	                    + "to AWS, but was rejected with an error response for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	        } catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, which means the client encountered "
	                    + "a serious internal problem while trying to communicate with AWS, "
	                    + "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
        }
    }
    
    /* checks if a table is active */
    private static boolean tableExists(String tableName) {
    	try {
            DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
            TableDescription tableDescription = dynamoDB.describeTable(request).getTable();
            String tableStatus = tableDescription.getTableStatus();
            System.out.println(tableName+" - current state: " + tableStatus);
            if (tableStatus.equals(TableStatus.ACTIVE.toString())) return true;
        } catch (AmazonServiceException ase) {
            if (ase.getErrorCode().equalsIgnoreCase("ResourceNotFoundException") == false) throw ase;
        }
		return false;
	}

	/* create a new user */
    private static Map<String, AttributeValue> newUser(String username, String email, String password) {
        Map<String, AttributeValue> user = new HashMap<String, AttributeValue>();
        user.put("username", new AttributeValue(username));
        user.put("email", new AttributeValue(email));
        user.put("password", new AttributeValue(password));

        return user;
    }
    
    /* create a new video */
    private static Map<String, AttributeValue> newVideo(String filename, String name, String description, String views, String date_posted) {
        Map<String, AttributeValue> video = new HashMap<String, AttributeValue>();
        video.put("filename", new AttributeValue(filename));
        video.put("name", new AttributeValue(name));
        video.put("description", new AttributeValue(description));
        video.put("views", new AttributeValue(views));
        video.put("date_posted", new AttributeValue(date_posted));

        return video;
    }
    
    /* create a new comment */
    private static Map<String, AttributeValue> newComment(String filename, String views, String date_posted, String video) {
        Map<String, AttributeValue> comment = new HashMap<String, AttributeValue>();
        comment.put("video_id", new AttributeValue(video));
        comment.put("filename", new AttributeValue(filename));
        comment.put("views", new AttributeValue(views));
        comment.put("date_posted", new AttributeValue(date_posted));

        return comment;
    }

    private static void waitForTableToBecomeAvailable(String tableName) {
        System.out.println("Waiting for " + tableName + " to become ACTIVE...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (10 * 60 * 1000);
        while (System.currentTimeMillis() < endTime) {
            try {Thread.sleep(1000 * 20);} catch (Exception e) {}
            try {
                DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
                TableDescription tableDescription = dynamoDB.describeTable(request).getTable();
                String tableStatus = tableDescription.getTableStatus();
                System.out.println("  - current state: " + tableStatus);
                if (tableStatus.equals(TableStatus.ACTIVE.toString())) return;
            } catch (AmazonServiceException ase) {
                if (ase.getErrorCode().equalsIgnoreCase("ResourceNotFoundException") == false) throw ase;
            }
        }

        throw new RuntimeException("Table " + tableName + " never went active");
    }

}
