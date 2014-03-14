<%@ page import="stock.*"%>
<%@ page import="java.io.File"%>
<%@ page import="java.util.LinkedList"%>
<%@ page import="com.amazonaws.auth.AWSCredentialsProviderChain"%>
<%@ page
	import="com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider"%>
<%@ page import="com.amazonaws.auth.InstanceProfileCredentialsProvider"%>
<%@ page import="com.amazonaws.services.s3.AmazonS3Client"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>
<%@ page import="javax.servlet.http.*"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="org.apache.commons.fileupload.disk.*"%>
<%@ page import="org.apache.commons.fileupload.servlet.*"%>
<%@ page import="org.apache.commons.io.output.*"%>
<%
Twideo tw = new Twideo();

int start = Integer.parseInt(request.getParameter("s"));
int end = start+10;
LinkedList<String> results = tw.getRange(start, end);
int count = 0;
String[] colors = new String[4];
colors[0] = "cyan";
colors[1] = "magenta";
colors[2] = "yellow";
colors[3] = "black";				
for(String r : results){
	Map<String,String> map = tw.getVideoAttributes(r);
	%><li class="<%=colors[count%4]%>">
		<video width="400" height="300" controls>
		  	<source src="<%=tw.getVideo(r)%>" type="video/mp4">
			Your browser does not support the video tag.
		</video>
		<div class="title">
			<a href="video.jsp?v=<%=r%>" class="link">
				<%=map.get("name")%>
			</a>
			<a href="video.jsp?v=<%=r%>" class="link right">
				[Reply]
			</a>
		</div>
	</li><%
	count++;
}	
%>
