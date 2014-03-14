<%@ page import="stock.*" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.amazonaws.auth.AWSCredentialsProviderChain" %>
<%@ page import="com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider" %>
<%@ page import="com.amazonaws.auth.InstanceProfileCredentialsProvider" %>
<%@ page import="com.amazonaws.services.s3.AmazonS3Client" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
Twideo tw = new Twideo();
String key = request.getParameter("v");
Map<String,String> attr = tw.getVideoAttributes(key);

Timestamp stamp = new Timestamp(Long.valueOf(attr.get("date_posted")));
Date date = new Date(stamp.getTime());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link href="css/style.css" rel="stylesheet" type="text/css" media="screen" charset="utf-8">
<link href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css" rel="stylesheet">
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<title><%=attr.get("name")%> | Twideo</title>
</head>
<body>
<script type="text/javascript" src="ibox/ibox.js"></script>
<script type="text/javascript">
iBox.inherit_frames = false;
</script>

<div id="inner_content" style="display:none;">
    <div style="background:#000000;color:#ffffff;padding:15px;margin:15px;">	
    	<form method="post" action="upload.jsp" enctype="multipart/form-data">
    		<b>Title:</b><br />
    		<input type="text" value="" name="title" id="title" class="form-control" /><br />
    		<b>Short description (max 140 chars):</b><br />
    		<input type="text" value="" name="description" maxlength="140" id="description" class="form-control" /><br />
    		<b>Video:</b><br />
    		<input type="file" name="file" id="file" class="form-control" /><br />
    		<input type="submit" value="Upload" name="upload" id="upload" class="form-control" />
    	</form>
    </div>
</div>

<div id="post_reply" style="display:none;">
    <div style="background:#000000;color:#ffffff;padding:15px;margin:15px;">	
    	<form method="post" action="upload.jsp?reply=<%=key%>" enctype="multipart/form-data">
    		<b>Video:</b><br />
    		<input type="file" name="file" id="file" class="form-control" /><br />
    		<input type="submit" value="Upload" name="upload" id="upload" class="form-control" />
    	</form>
    </div>
</div>
    
<center>
	<div id="wrapper">
		<div style="height:60px;">
			<div id="header">
				<div id="logo">
					<a href="index.jsp">
						TWIDEO
					</a>
				</div>
				<div id="menu">
					<a href="#inner_content" rel="ibox&width=420" title="Upload a video" class="item">+</a>
				</div>
			</div>
		</div>
		<ul class="grid">
			<li class="short black">
				<video width="400" height="300" controls>
				  	<source src="<%=tw.getVideo(attr.get("filename"))%>" type="video/mp4">
					Your browser does not support the video tag.
				</video>
			</li>
			<li class="short black">
				<div class="left infobox">
					<h1 class="text-left"><%=attr.get("name")%></h1>
					<p>
						<small>Added on <%=date%></small>
					</p>
					<blockquote>
						<%=attr.get("description")%>
					</blockquote>
				</div>
			</li>
		</ul>
		<h3 class="text-left">
			Replies [<a href="#post_reply" rel="ibox&width=420" title="Post a reply to '<%=attr.get("name")%>'" class="item">Post a reply</a>]
		</h3>
		<%
		String error = request.getParameter("error");
		String success = request.getParameter("success");
		if(error != null && error.equals("1")){
			%><p class="bg-danger" style="font-weight:700;text-align:left;padding:10px;">Error, the file you tried to upload is not a video.</p><%
		} else if(error != null && error.equals("2")){
			%><p class="bg-danger" style="font-weight:700;text-align:left;padding:10px;">Please fill in all fields.</p><%
		} else if(success != null){
			%><p class="bg-primary" style="font-weight:700;text-align:left;padding:10px;">Your video was successfully uploaded.</p><%
		}
		
		LinkedList<String> results = tw.getComments(key, 0, 10);
		int items = 0;
		int count = 0;
		String[] colors = new String[4];
		colors[0] = "cyan";
		colors[1] = "magenta";
		colors[2] = "yellow";
		colors[3] = "black";				
		for(String r : results){
			if(items == 0){
				%><ul class="grid"><%
			}
			%><li class="<%=colors[count%4]%> comment">
				<video width="400" height="300" controls>
				  	<source src="<%=tw.getComment(r)%>" type="video/mp4">
					Your browser does not support the video tag.
				</video>
			</li><%
			if(items == 1){
				%></ul><%
				items = 0;
			} else
				items++;
			count++;
		}
		if(items == 1){
			%></ul><%
		}			
		%>
	</div>
</div>
<script>
$(document).ready(function(){
	resize();
	function resize(){
		var WH = $(window).height();
		var WW = $(window).width();
		if(WW >= 840){
			$('#wrapper').width('840px');
			$('#header').width('840px');
		} else {
			$('#wrapper').width('420px');
			$('#header').width('420px');
		}
	}
	$(window).resize(function(){
		resize();
	});
});
</script>
</body>
</html>