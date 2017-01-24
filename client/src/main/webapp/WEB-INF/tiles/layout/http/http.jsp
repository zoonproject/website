<%@page session="false" %>
<%
  response.setHeader("Expires", "Sat, 6 May 1995 12:00:00");
  response.setHeader("Cache-control", "private, no-cache, no-store, must-revalidate");
  response.setHeader("Cache-control", "post-check=0, precheck=0");
  response.setHeader("Pragma", "no-cache");
  response.setDateHeader("Expires", 0);

  response.setCharacterEncoding("utf-8");
  response.setContentType("text/html;charset=UTF-8");
%>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">