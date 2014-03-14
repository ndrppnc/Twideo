package stock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

public class DynamoDBManager {

    private AmazonDynamoDBClient dynamoDB;

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
    public void init() throws Exception {
    	/*
		 * This credentials provider implementation loads your AWS credentials
		 * from a properties file at the root of your classpath.
		 */
        dynamoDB = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider());
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        dynamoDB.setRegion(usWest2);
    }

    public void setup() throws Exception {
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
	        	    .withKeySchema(new KeySchemaElement().withAttributeName("index").withKeyType(KeyType.RANGE))
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
        
        if(!tableExists("tableinfo")){
	    	/* create users table */
	        try {
	        	System.out.println("Start creating 'tableinfo' table...");
	        	
	        	String tableName = "tableinfo";
	
	            // Create a table with a primary hash key named 'type', which holds a string
	            CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
	                .withKeySchema(new KeySchemaElement().withAttributeName("type").withKeyType(KeyType.HASH))
	                .withAttributeDefinitions(new AttributeDefinition().withAttributeName("type").withAttributeType(ScalarAttributeType.S))
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
        
        if(!tableExists("cfdistributions")){
	        /* create cfdistributions table */
	        try {
	        	System.out.println("Start creating 'cfdistributions' table...");
	        	
	            String tableName = "cfdistributions";
	
	            // Create a table with a primary hash key named 'name', which holds a string
	            CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
	                .withKeySchema(new KeySchemaElement().withAttributeName("current").withKeyType(KeyType.HASH))
	                .withAttributeDefinitions(new AttributeDefinition().withAttributeName("current").withAttributeType(ScalarAttributeType.S))
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
    private boolean tableExists(String tableName) {
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
    
    /* get videos in the range [start,end] */
    public LinkedList<String> getVideosRange(String tableName,int start, int end) {
    	ScanRequest sr = new ScanRequest();
    	sr.setTableName(tableName);
    	sr.setLimit(10);
    	
    	Map<String,Condition> scanFilter = new HashMap<String,Condition>();
    	Condition condition = new Condition()
        .withComparisonOperator(ComparisonOperator.BETWEEN.toString())
        .withAttributeValueList(new AttributeValue().withN(start+""),new AttributeValue().withN(end+""));
    	
    	scanFilter.put("index", condition);
    	sr.setScanFilter(scanFilter);
    	
    	ScanResult result = dynamoDB.scan(sr);
   	
    	LinkedList<String> retVal = new LinkedList<String>();
    	
    	for(Map<String,AttributeValue> item : result.getItems()){
    		retVal.add(item.get("filename").getS());
    	}
		return retVal;
	}
    
    /* get comments in the range [start,end] */
    public LinkedList<String> getCommentsRange(String tableName,String key,int start, int end) { 	
    	ScanRequest sr = new ScanRequest();
    	sr.setTableName(tableName);
    	sr.setLimit(10);
    	
    	Map<String,Condition> scanFilter = new HashMap<String,Condition>();
    	Condition condition = new Condition()
        .withComparisonOperator(ComparisonOperator.EQ.toString())
        .withAttributeValueList(new AttributeValue().withS(key));
    	
    	scanFilter.put("video_id", condition);
    	sr.setScanFilter(scanFilter);
    	
    	ScanResult result = dynamoDB.scan(sr);
    	
    	LinkedList<String> retVal = new LinkedList<String>();
    	
    	for(Map<String,AttributeValue> item : result.getItems()){
    		retVal.add(item.get("filename").getS());
    	}
		return retVal;
	}
    
    public Map<String, AttributeValue> getItemAttributes(String tableName,String key){
//    	ScanRequest sr = new ScanRequest();
//    	sr.setTableName(tableName);
//    	sr.setLimit(1);
//    	
//    	Map<String,Condition> scanFilter = new HashMap<String,Condition>();
//    	Condition condition = new Condition()
//        .withComparisonOperator(ComparisonOperator.EQ.toString())
//        .withAttributeValueList(new AttributeValue().withS(key));
//    	
//    	scanFilter.put("filename", condition);
//    	sr.setScanFilter(scanFilter);
//    	
//    	try {
//    	
//    	ScanResult result = dynamoDB.scan(sr);
//    	System.out.println("HELLOOOOOOOOOOO "+key+" "+result.toString());
//    	return result.getItems().get(0);
//    	
//    	} catch(Exception e){
//    		e.printStackTrace();
//    	}
//    	//LinkedList<String> retVal = new LinkedList<String>();
//    	
//    	return null;
    	
    	Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();
        map.put("filename", new AttributeValue().withS(key));
    	return dynamoDB.getItem(tableName, map).getItem();
    }

	/* create a new user */
    public Map<String, AttributeValue> newUser(String username, String email, String password) {
        Map<String, AttributeValue> user = new HashMap<String, AttributeValue>();
        user.put("username", new AttributeValue(username));
        user.put("email", new AttributeValue(email));
        user.put("password", new AttributeValue(password));

        return user;
    }
    
    /* create a new video */
    public void newVideo(String filename, String name, String description, String views, String date_posted) {
    	int index = getCurrentIndex();
    	
        Map<String, AttributeValue> video = new HashMap<String, AttributeValue>();
        video.put("filename", new AttributeValue(filename));
        video.put("name", new AttributeValue(name));
        video.put("description", new AttributeValue(description));
        video.put("views", new AttributeValue(views));
        video.put("date_posted", new AttributeValue(date_posted));
        video.put("index", new AttributeValue().withN(index+""));
        
        PutItemRequest putItemRequest = new PutItemRequest("videos", video);
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
        System.out.println("Result: " + putItemResult);
        
        increaseCurrentIndex();
    }
    
    private void increaseCurrentIndex() {
    	int current = getCurrentIndex();
    	Map<String, AttributeValue> index = new HashMap<String, AttributeValue>();
        index.put("type", new AttributeValue("current"));
        index.put("value", new AttributeValue().withN((current+1)+""));
        
        PutItemRequest putItemRequest = new PutItemRequest("tableinfo", index);
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
        System.out.println("Result: " + putItemResult);
	}

	private int getCurrentIndex() {
		Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();
        map.put("type", new AttributeValue().withS("current"));
    	Map<String, AttributeValue> result = dynamoDB.getItem("tableinfo", map).getItem();
    	return Integer.parseInt(result.get("value").getN());
    	//return dynamoDB.getItem("tableinfo", map).getItem().get("value");
	}

	/* create a new comment */
    public void newComment(String filename, String views, String date_posted, String video) {
        Map<String, AttributeValue> comment = new HashMap<String, AttributeValue>();
        comment.put("video_id", new AttributeValue(video));
        comment.put("filename", new AttributeValue(filename));
        comment.put("views", new AttributeValue(views));
        comment.put("date_posted", new AttributeValue(date_posted));

        PutItemRequest putItemRequest = new PutItemRequest("comments", comment);
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
        System.out.println("Result: " + putItemResult);
    }
    
    public Map<String, AttributeValue> getCloudFrontDistribution() {
		Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();
        map.put("current", new AttributeValue().withS("current"));
    	return dynamoDB.getItem("cfdistributions", map).getItem();
	}
    
    public void putCloudFrontDistribution(String value) {
    	Map<String, AttributeValue> cfd = new HashMap<String, AttributeValue>();
    	cfd.put("current", new AttributeValue("current"));
    	cfd.put("domain", new AttributeValue(value));

        PutItemRequest putItemRequest = new PutItemRequest("cfdistributions", cfd);
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
        System.out.println("Result: " + putItemResult);
	}

    public void waitForTableToBecomeAvailable(String tableName) {
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
