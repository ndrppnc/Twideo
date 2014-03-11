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

	//Create a factory for disk-based file items
	DiskFileItemFactory factory = new DiskFileItemFactory();

	//Configure a repository (to ensure a secure temp location is used)
	ServletContext servletContext = this.getServletConfig()
			.getServletContext();
	File repository = (File) servletContext
			.getAttribute("javax.servlet.context.tempdir");
	factory.setRepository(repository);

	//Create a new file upload handler
	ServletFileUpload upload = new ServletFileUpload(factory);

	//Set overall request size constraint
	upload.setSizeMax(50000 * 1024);

	//Parse the request
	List<FileItem> items = upload.parseRequest(request);

	File uploadedFile = new File("temp");

	Map<String, String> params = new HashMap<String, String>();
	boolean isVideo = true;
	boolean allFields = true;
		
	// Process the uploaded items
	Iterator<FileItem> iter = items.iterator();
	while (iter.hasNext()) {
		FileItem item = iter.next();
	
		if (!item.isFormField()) {
			if (item.getContentType().equals("video/3gpp2")
			|| item.getContentType().equals("video/3gpp")
			|| item.getContentType().equals("video/x-ms-asf")
			|| item.getContentType().equals("video/mpeg")
			|| item.getContentType().equals("video/vnd.dlna.mpeg-tts")
			|| item.getContentType().equals("video/x-m4v")
			|| item.getContentType().equals("video/quicktime")
			|| item.getContentType().equals("video/mp4")
			|| item.getContentType().equals("video/x-ms-wm")
			|| item.getContentType().equals("video/avi")) {
				uploadedFile = new File("/tmp/"+item.getName());
				System.out.println(item.toString());
				
				item.write(uploadedFile);
				
				System.out.println("File name: "
						+ uploadedFile.getAbsolutePath());
			} else {
				isVideo = false;
			}
		} else {
			if(!item.getString().equals("")){
				System.out.println(item.getString() + " "
						+ item.getFieldName());
				params.put(item.getFieldName(), item.getString());
			} else {
				allFields = false;
			}
		}
	}

	String replyID = request.getParameter("reply");
	if(isVideo && allFields){
		if(replyID != null && replyID != ""){
			tw.putComment(uploadedFile.getName(), uploadedFile, replyID);
			%><META http-equiv="refresh" content="0;URL=video.jsp?v=<%=replyID%>&success"><%
		} else {
			tw.putVideo(uploadedFile.getName(), params.get("title"),
				   params.get("description"), uploadedFile);
			%><META http-equiv="refresh" content="0;URL=index.jsp?success"><%
		}
		System.out.println("Done.");
	} else if(!allFields) {
		if(replyID != null && replyID != ""){
			System.out.println("Please fill in all fields.");
			%><META http-equiv="refresh" content="0;URL=video.jsp?v=<%=replyID%>&error=2"><%
		} else {
			System.out.println("Please fill in all fields.");
			%><META http-equiv="refresh" content="0;URL=index.jsp?error=2"><%
		}
	} else {
		if(replyID != null && replyID != ""){
			System.out.println("Error, the file you tried to upload is not a video.");
			%><META http-equiv="refresh" content="0;URL=video.jsp?v=<%=replyID%>&error=1"><%
		} else {
			System.out.println("Error, the file you tried to upload is not a video.");
			%><META http-equiv="refresh" content="0;URL=index.jsp?error=1"><%
		}
	}
%>
