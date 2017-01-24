<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>
<%-- name_? vars are not escaped because the values are arguments to a subsequent spring:message --%>
<c:set var="name_email_address"><spring:message code="login.email_address" htmlEscape="false" /></c:set>
<c:set var="locale" value="${fn:toLowerCase(pageContext.response.locale)}" />
<c:set var="available_langs"><%=ClientIdentifiers.I18N_LANGS%></c:set>

    <script type="text/javascript" src="<c:url value="/resources/js/validate/jquery.validate-1.11.1.min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/additional-methods/additional-methods.1.11.1.min.js" />"></script>
<c:if test="${fn:contains(available_langs,locale)}">
    <!-- https://github.com/jzaefferer/jquery-validation/tree/master/src/localization -->
    <script type="text/javascript" src="<c:url value="/resources/js/validate/messages_${locale}.js" />"></script>
</c:if>