<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>
<div id="js_error_profile_by_username" style="color: red; display: none; margin-top: 30px;">
</div>

<div class="container wide-container">
  <h3 class="page-header"><spring:message code="profile.title" /></h3>
  <div class="packagelist">
<c:choose>
  <c:when test="${not empty ma_user}">
    <table>
      <tr>
        <td><spring:message code="user.identities" /> : </td>
    <c:forEach items="${ma_user.identities}"
               var="identity"
               varStatus="status">
      <c:if test="${!status.first}">
      <tr>
        <td>&nbsp;</td>
      </c:if>
        <td><b><code><c:out value="${identity.identity}" /></code></b></td>
        <td>
      <c:forEach items="${identity.emails}"
                 var="email">
          <code><c:out value="${email}" /></code><br />
      </c:forEach>
        </td>
    </c:forEach>
      </tr>
    </table>
    <p>&nbsp;</p>
    <div>
      <div class="rounded_10"
           style="border: solid grey 1px; width: 500px;
                  text-align: center; vertical-align: middle; float: left; margin-left: 30px;">
        <b><spring:message code="module.modules" /></b>
    <c:choose>
      <c:when test="${not empty ma_modules}">
        <table id="modules"></table>
      </c:when>
      <c:otherwise>
        <p><spring:message code="profile.no_modules" />.</p>
      </c:otherwise>
    </c:choose>
      </div>
      <div class="rounded_10"
           style="border: solid grey 1px; width: 500px;
                  text-align: center; vertical-align: middle; float: left; margin-left: 30px;">
        <b><spring:message code="workflow_call.workflow_calls" /></b>
    <c:choose>
      <c:when test="${not empty ma_workflow_calls}">
        <table id="workflow_calls"></table>
      </c:when>
      <c:otherwise>
        <p><spring:message code="profile.no_workflow_calls" />.</p>
      </c:otherwise>
    </c:choose>
      </div>
    </div>
  </c:when>
  <c:otherwise>
    <p>
      <spring:message code="profile.not_available" />.
    </p>
  </c:otherwise>
</c:choose>
  </div>
<c:if test="${ma_user_currently_logged_in}">
  <div style="clear: both;"></div>
  <div style="margin-top: 30px;">
    <form id="create_identity">
      Add a ZOON identity : <br />
      <table>
        <tr>
          <td>Identity</td>
          <td><input id="<%=ClientIdentifiers.PARAM_NAME_USER_IDENTITY%>" placeholder="Initial identity"/></td> 
        </tr> 
        <tr> 
          <td>Associated email (optional)</td>
          <td><input id="<%=ClientIdentifiers.PARAM_NAME_USER_EMAIL%>" placeholder="Email for identity" /></td>
        </tr>
        <tr>
          <td colspan="2" style="text-align: right;">
            <input type="submit" value=" Submit " />
          </td>
        </tr>
      </table>
    </form>
  </div>
</c:if>
</div>
