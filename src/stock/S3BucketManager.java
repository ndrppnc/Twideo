package stock;
import java.io.File;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;


public class S3BucketManager {
	AmazonS3Client s3;
	String 	bucket_name = null;
	
	public S3BucketManager(AmazonS3Client s3Instance, String bucketName)
	{
		this.s3 = s3Instance;
		this.bucket_name = bucketName;
	}

	public void createBucket()
	{
		System.out.println("Creating new bucket");
		s3.createBucket(bucket_name);
		System.out.println("Done");
	}
	
	public void putObject(String key, File file)
	{		
		try {
			//put object - bucket, key, value(file)
			System.out.println("Putting object on S3");
			s3.putObject(new PutObjectRequest(bucket_name, key, file).withCannedAcl(CannedAccessControlList.PublicRead));
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getObject(String key){
		System.out.println("Getting object '"+key+"' from S3");
		s3.getObject(
			new GetObjectRequest(bucket_name, key),
	        new File(key+".png")
		);
		System.out.println("Done");
	}

	public void deleteObject(String key){
		System.out.println("Deleting object '"+key+"'");
		s3.deleteObject(
				bucket_name,
				key
		);
		System.out.println("Done");
	}
	
	public void deleteBucket(){
		System.out.println("Deleting bucket "+bucket_name);
		s3.deleteObjects(new DeleteObjectsRequest(bucket_name));
		System.out.println("Done");
	}
}