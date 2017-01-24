<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<tiles:insertAttribute name="http" />
<html>
  <head>
    <tiles:insertAttribute name="html_head.bootstrap" />
    <tiles:insertAttribute name="page.title" />
    <tiles:insertAttribute name="html_head.general" />
    <tiles:insertAttribute name="html_head.javascript" />
    <tiles:insertAttribute name="page.javascript" />
    <tiles:insertAttribute name="html_head.style" />
    <tiles:insertAttribute name="page.style" />
  </head>
  <body>
    <tiles:insertAttribute name="banner" />
    <%--
    <div id="js_debug" style="margin-top: 100px;"></div>
    <div id="js_error" style="margin-top: 100px; color: red;"></div>
    --%>
    <div class="container-fluid" style="padding-top: 35px;">
      <tiles:insertAttribute name="notification" />
      <tiles:insertAttribute name="middle" />
      <tiles:insertAttribute name="bottom" />
    </div>
    <script src="<c:url value="/resources/js/bootstrap/3.3.5/bootstrap.min.js" />"></script>
  </body>
</html>
