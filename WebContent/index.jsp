<%@ page import="java.io.File" %>
<%@ page import="stock.*" %>
<%@ page import="com.amazonaws.auth.AWSCredentialsProviderChain" %>
<%@ page import="com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider" %>
<%@ page import="com.amazonaws.auth.InstanceProfileCredentialsProvider" %>
<%@ page import="com.amazonaws.services.s3.AmazonS3Client" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hello world!</title>
</head>
<body>
Hello world!
<%
AmazonS3Client s3 = new AmazonS3Client(
		new AWSCredentialsProviderChain(
        new InstanceProfileCredentialsProvider(),
        new ClasspathPropertiesFileCredentialsProvider()));

// set up storage bucket
stock.S3BucketManager S3BM = new stock.S3BucketManager(s3, "twideos");

// create bucket, if it doesn't exist
S3BM.createBucket();

String objName = "test";

// put a test object
S3BM.putObject(objName, new File("screenshot.png"));

// retrieve object from S3		
S3BM.getObject(objName);
%>
</body>
</html>