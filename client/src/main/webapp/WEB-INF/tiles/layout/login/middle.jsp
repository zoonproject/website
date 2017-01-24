<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>

<c:url var="urlLogin" value="<%=ClientIdentifiers.RENDER_LOGIN%>" />
<c:url var="urlContextPath" value="/" />
<c:url var="urlViewPrivacy" value="<%=ClientIdentifiers.VIEW_PRIVACY%>" />

<div class="container wide-container">
  <h3 class="page-header">Log In</h3>
  <p>
    <spring:message code="login.note" />
    (<spring:message code="general.see" />&nbsp;
     <a href="${urlViewPrivacy}"
        title="<spring:message code="privacy.title" />"><spring:message code="privacy.title" /></a>
     <spring:message code="general.for_more_information" />.)
  </p>
  <%-- top row : login + welcome text --%>
  <div>
<c:choose>
  <c:when test="${not empty _csrf.parameterName}">
    <%-- On page display or refresh a new session started and the CSRF token is available --%>
    <form method="post"
          name="login"
          action="${urlLogin}">
      <input type="hidden"
             name="${_csrf.parameterName}"
             value="${_csrf.token}" />
<%
if (request.getParameter("login_error") != null) {
%>
      <div class="error_text center">
        <p>
          <b><spring:message code="login.invalid_login" /></b>
        </p>
      </div>
<%
}
%>
      <p><spring:message code="login.login_details" /> :</p>
      <table>
        <tr>
          <td align="right"><spring:message code="login.email_address" /></td>
          <td><input type="text" name="username" size="30" /></td>
        </tr>
        <tr>
          <td align="right"><spring:message code="login.password" /></td>
          <td><input type="password" name="password" size="30"/></td>
        </tr>
        <tr>
          <td colspan="2" align="right">
            <input type="submit" value="<spring:message code="login.login" />" />
            <input type="reset" value="<spring:message code="general.reset" />" />
          </td>
        </tr>
      </table>
    </form>
  </c:when>
  <c:otherwise>
    <%-- If session has been lost prior to viewing page --%>
    <p>
      <spring:message code="login.page_refresh_required" /> -
      <a href="${urlContextPath}"
         title="<spring:message code="general.click_here" />"><spring:message code="general.click_here" /></a>.
    </p>
  </c:otherwise>
</c:choose>
  </div>

  <%-- End the 'two column' view --%>
  <div style="clear: both;">&nbsp;</div>

  <script type="text/javascript">
    // <!--
    jQuery(document).ready(
      function() {
        document.forms['login'].elements['username'].focus();
      }
    );
    //-->
  </script>
</div>
