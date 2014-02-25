package stock;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;


public class Twideo {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		AmazonS3Client s3 = new AmazonS3Client(
					new AWSCredentialsProviderChain(
		            new InstanceProfileCredentialsProvider(),
		            new ClasspathPropertiesFileCredentialsProvider()));
		
		// set up storage bucket
		S3BucketManager S3BM = new S3BucketManager(s3, "twideos");
		
		// create bucket, if it doesn't exist
		S3BM.createBucket();
		
		String objName = "test";
		
		// put a test object
		S3BM.putObject(objName, new File("screenshot.png"));
		
		// retrieve object from S3		
		S3BM.getObject(objName);
	}

}
