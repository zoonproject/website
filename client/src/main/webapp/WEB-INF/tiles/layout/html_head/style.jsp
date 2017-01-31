<%@taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="CURRENT_TIME" value="<%= System.currentTimeMillis() %>" />
    <link rel="stylesheet" href="<c:url value="/resources/css/client.css" />?random=${CURRENT_TIME}" type="text/css" />
    <link rel="stylesheet" href="<c:url value="/resources/css/site/site.css" />?random=${CURRENT_TIME}" type="text/css" />
    <link rel="stylesheet" href="<c:url value="/resources/css/smoothness/jquery-ui-1.9.2.custom.min.css" />" type="text/css" />
    <link rel="stylesheet" href="<c:url value="/resources/css/font-awesome-4.4.0/css/font-awesome.min.css" />" type="text/css" />
    <%-- Below style courtesy of http://www.r-pkg.org/ --%>
    <link rel="stylesheet" href="<c:url value="/resources/css/modules/r-pkg.org.main.css" />" type="text/css" />