<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>

<c:url var="urlViewProfileByIdentity" value="<%=ClientIdentifiers.VIEW_PROFILE_BY_IDENTITY%>" />

<div class="container wide-container">
  <h3 class="page-header">
<c:choose>
  <c:when test="${not empty ma_module}">
    ZOON <spring:message code="module.module" /> : <c:out value="${ma_module.name}" />
  </c:when>
  <c:otherwise>
    ZOON <spring:message code="module.module" /> Not found!
  </c:otherwise>
</c:choose>
  </h3>
  <div>
    <table>
      <tr>
        <td>Name</td>
        <td>&nbsp;</td>
        <td><b><c:out value="${ma_module.name}" /></b></td>
      </tr>
      <tr>
        <td>Version</td>
        <td>&nbsp;</td>
        <td><c:out value="${ma_module.version}" /></td>
      </tr>
      <tr>
        <td>Location</td>
        <td>&nbsp;</td>
        <td><c:out value="${ma_module.location}" /></td>
      </tr>
<c:if test="${not empty ma_module.submitted}">
      <tr>
        <td>Submitted</td>
        <td>&nbsp;</td>
        <td><c:out value="${ma_module.submitted}" /></td>
      </tr>
</c:if>
<c:forEach items="${ma_module.authors}"
           var="author"
           varStatus="aStatus">
      <tr>
        <td>Author <c:out value="${aStatus.index + 1}" /></td>
        <td>&nbsp;</td>
        <td>
          <a href="${urlViewProfileByIdentity}/<c:out value="${author.nameURL}" />">
            <c:out value="${author.authorName}" />
          </a>
          <br />
          Email: <a href="mailto:<c:out value="${author.email}" />"><c:out value="${author.email}" /></a> 
        </td>
      </tr>
</c:forEach>
      <tr>
        <td>Description</td>
        <td>&nbsp;</td>
        <td>
<c:forEach items="${ma_module.descriptions}"
           var="description"
           varStatus="dStatus">
  <c:out value="${description}" />
  <c:if test="${!dStatus.last}"><br /></c:if>
</c:forEach>
        </td>
      </tr>
<c:if test="${not empty ma_module.content}">
      <tr>
        <td colspan="3">
<br />
<pre>
<c:out value="${ma_module.content}" />
</pre>
        </td>
      </tr>
</c:if>
    </table>
  </div>
</div>