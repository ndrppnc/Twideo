package stock;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.Aliases;
import com.amazonaws.services.cloudfront.model.CacheBehaviors;
import com.amazonaws.services.cloudfront.model.CookiePreference;
import com.amazonaws.services.cloudfront.model.CreateDistributionRequest;
import com.amazonaws.services.cloudfront.model.CreateDistributionResult;
import com.amazonaws.services.cloudfront.model.DefaultCacheBehavior;
import com.amazonaws.services.cloudfront.model.DistributionConfig;
import com.amazonaws.services.cloudfront.model.ForwardedValues;
import com.amazonaws.services.cloudfront.model.GetDistributionRequest;
import com.amazonaws.services.cloudfront.model.GetDistributionResult;
import com.amazonaws.services.cloudfront.model.LoggingConfig;
import com.amazonaws.services.cloudfront.model.Origin;
import com.amazonaws.services.cloudfront.model.Origins;
import com.amazonaws.services.cloudfront.model.PriceClass;
import com.amazonaws.services.cloudfront.model.S3OriginConfig;
import com.amazonaws.services.cloudfront.model.TrustedSigners;
import com.amazonaws.services.cloudfront.model.ViewerProtocolPolicy;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.s3.AmazonS3Client;

public class Twideo {
	
	AmazonS3Client s3 = new AmazonS3Client(
			new AWSCredentialsProviderChain(
            new InstanceProfileCredentialsProvider(),
            new ClasspathPropertiesFileCredentialsProvider()));
	
	AmazonCloudFrontClient cf = new AmazonCloudFrontClient(
			new AWSCredentialsProviderChain(
			new InstanceProfileCredentialsProvider(),
			new ClasspathPropertiesFileCredentialsProvider()));
	
	AmazonElasticTranscoderClient et = new AmazonElasticTranscoderClient(
			new AWSCredentialsProviderChain(
			new InstanceProfileCredentialsProvider(),
			new ClasspathPropertiesFileCredentialsProvider()));
	
	DynamoDBManager dy = new DynamoDBManager();
	
	String cfd = "";
	
	public Twideo(){
		try {
			// init DynamoDB connection
			dy.init();
			//dy.setup();
			
			// get CloudFrontDistribution link
			Map<String,AttributeValue> map = dy.getCloudFrontDistribution();
			
			if(map == null){
				cfd = createCloudFrontDistribution();
				dy.putCloudFrontDistribution(cfd);
			} else {
				cfd = map.get("domain").getS();
			}
			
			// create buckets
			//createBuckets();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* create S3 buckets */
	public void createBuckets(){
		// create buckets
		S3BucketManager S3BM = new S3BucketManager(s3, "twideos");
		
		// create bucket, if it doesn't exist
		S3BM.createBucket();
		
		// create transcoded buckets
		S3BM = new S3BucketManager(s3, "twideostrans");
		
		// create bucket, if it doesn't exist
		S3BM.createBucket();
		
		// create thumbnail bucket
		S3BM = new S3BucketManager(s3, "twideosthumbs");
		
		// create bucket, if it doesn't exist
		S3BM.createBucket();
	}
	
	/* get object from CloudFront given key */
	public String getVideo(String key){
		/* CloudFront client */
		return "http://"+cfd+"/"+key;
	}
	
	/* get object from CloudFront given key */
	public String getComment(String key){
		/* CloudFront client */
		return "http://"+cfd+"/"+key;
	}
	
	/* get video attributes of given key from the videos table */
	public Map<String,String> getVideoAttributes(String key){
		Map<String,String> retVal = new HashMap<String,String>();
		Map<String,AttributeValue> result = dy.getItemAttributes("videos", key);
		for(String r : result.keySet()){
			retVal.put(r, result.get(r).getS());			
		}
		return retVal;
	}
	
	/* get comment attributes of given key from the videos table */
	public Map<String,String> getCommentAttributes(String key){
		Map<String,String> retVal = new HashMap<String,String>();
		Map<String,AttributeValue> result = dy.getItemAttributes("comments", key);
		for(String r : result.keySet()){
			retVal.put(r, result.get(r).getS());			
		}
		return retVal;
	}
	
	/* put a file on S3 */
	public void putVideo(String objName,String title,String description,File file){
		// put video in database
		dy.newVideo(objName, title, description, "0", System.currentTimeMillis()+"");
		
		// set up storage bucket
		S3BucketManager S3BM = new S3BucketManager(s3, "twideos");
		
		S3BM.putObject(objName, file);
	}
	
	/* put a file on S3 */
	public void putComment(String objName,File file,String videoID){
		// put video in database
		dy.newComment(objName, "0", System.currentTimeMillis()+"",videoID);
		
		// set up storage bucket
		S3BucketManager S3BM = new S3BucketManager(s3, "twideos");
		
		S3BM.putObject(objName, file);
	}
	
	/* get a list of the following range of videos */
	public LinkedList<String> getRange(int start,int end){
		return dy.getVideosRange("videos",start,end);
	}
	
	/* get a list of comments */
	public LinkedList<String> getComments(String key,int start,int end){
		return dy.getCommentsRange("comments",key,start,end);
	}
	
	public String createCloudFrontDistribution(){
		/* CloudFront client */
		AmazonCloudFrontClient cf = new AmazonCloudFrontClient(
					new AWSCredentialsProviderChain(
					new InstanceProfileCredentialsProvider(),
					new ClasspathPropertiesFileCredentialsProvider()));
		
		/* create a distribution request */
		CreateDistributionRequest cdr = new CreateDistributionRequest();
		
		/* create distribution config */
		DistributionConfig dc = new DistributionConfig();

		/* define S3 origin */
		Origin origin = new Origin();
		origin.setDomainName("twideos.s3.amazonaws.com");
		
		dc.withCallerReference(System.currentTimeMillis() + "");
	    dc.withAliases(new Aliases().withQuantity(0));
	    dc.withDefaultRootObject("");
	    dc.withOrigins(new Origins().withItems(
	    	      new Origin().withId("1").withDomainName(origin.getDomainName()).withS3OriginConfig(new S3OriginConfig().withOriginAccessIdentity("")))
	    	      .withQuantity(1));
	    dc.withDefaultCacheBehavior(new DefaultCacheBehavior()
	      .withTargetOriginId("1")
	      .withForwardedValues(new ForwardedValues().withQueryString(false).withCookies(new CookiePreference().withForward("none")))
	      .withTrustedSigners(new TrustedSigners().withQuantity(0).withEnabled(false))
	      .withViewerProtocolPolicy(ViewerProtocolPolicy.AllowAll)
	      .withMinTTL((long) 86400));
	    dc.withCacheBehaviors(new CacheBehaviors().withQuantity(0));
	    dc.withComment("Distribution for Twideo bucket");
	    dc.withLogging(new LoggingConfig().withEnabled(false).withBucket("").withPrefix("").withIncludeCookies(false));
	    dc.withEnabled(true);
	    dc.withPriceClass(PriceClass.PriceClass_All);
		
		/* set distribution configuration */
		cdr.setDistributionConfig(dc);
		
		/* create CloudFront distribution */
		CreateDistributionResult dr = cf.createDistribution(cdr);
		
		return dr.getDistribution().getDomainName();
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		AmazonS3Client s3 = new AmazonS3Client(
					new AWSCredentialsProviderChain(
		            new InstanceProfileCredentialsProvider(),
		            new ClasspathPropertiesFileCredentialsProvider()));
		
		// set up storage bucket
		S3BucketManager S3BM = new S3BucketManager(s3, "twideos");
		
		// create bucket, if it doesn't exist
		S3BM.createBucket();
	}

}