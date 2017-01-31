<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>

<c:url var="urlViewProfileByIdentity" value="<%=ClientIdentifiers.VIEW_PROFILE_BY_IDENTITY%>" />
<c:url var="urlViewProfileByUsername" value="<%=ClientIdentifiers.VIEW_PROFILE_BY_USERNAME%>" />

<div class="container wide-container">
  <h3 class="page-header">User Management</h3>
    <table id="user_table" class="display">
      <thead>
        <tr>
          <th>User name</th>
          <th>Identities</th>
          <th>Enabled</th>
          <th>Delete</th>
        </tr>
      </thead>
      <tbody>
<c:forEach var="user" items="${ma_users}">
        <tr>
          <td>
      <a href="${urlViewProfileByUsername}/<c:out value="${user.username}" />"
               title="View Profile"><c:out value="${user.username}" /></a>&nbsp;
          </td>
          <td>
  <c:choose>
    <c:when test="${fn:length(user.identities) gt 0}">
      <c:forEach var="identity" items="${user.identities}">
            <a href="${urlViewProfileByIdentity}/<c:out value="${identity.identity}" />"
               title="View Profile"><c:out value="${identity.identity}" /></a>&nbsp;
        <c:forEach var="email" items="${identity.emails}">
            (<a href="mailto:<c:out value="${email}" />"><c:out value="${email}" /></a>)
        </c:forEach>
        <c:if test="${!status.last}">
            <br />
        </c:if>
      </c:forEach>
    </c:when>
    <c:otherwise>
            N/A
    </c:otherwise>
  </c:choose>
          </td>
          <td>
      <input id="ch_<c:out value="${user.username}" />"
             type="checkbox" <c:if test="${user.enabled == 1}">checked="checked"</c:if> />
          </td>
          <td>
      <input id="del_<c:out value="${user.username}" />"
             type="checkbox" />
          </td>
        </tr>
</c:forEach>
      </tbody>
    </table>
</div>