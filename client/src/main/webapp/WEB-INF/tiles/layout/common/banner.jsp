<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>

<c:url var="urlContextPath" value="/" />
<c:url var="urlLogout" value="<%=ClientIdentifiers.LOGOUT%>" />
<c:url var="urlViewAbout" value="<%=ClientIdentifiers.VIEW_ABOUT%>" />
<c:url var="urlViewContact" value="<%=ClientIdentifiers.VIEW_CONTACT%>" />
<c:url var="urlViewLogin" value="<%= ClientIdentifiers.VIEW_LOGIN %>" />
<c:url var="urlViewManageUsers" value="<%=ClientIdentifiers.URL_PREFIX_MANAGE_USERS + ClientIdentifiers.VIEW_USERS%>" />
<c:url var="urlViewModules" value="<%=ClientIdentifiers.VIEW_MODULES%>" />
<c:url var="urlViewModuleManage" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES + ClientIdentifiers.VIEW_MODULE_MANAGE %>" />
<c:url var="urlViewModuleUploadZOON" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES + ClientIdentifiers.VIEW_MODULE_UPLOAD_ZOON %>" />
<c:url var="urlViewModuleVerify" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES + ClientIdentifiers.VIEW_MODULE_VERIFY %>" />
<c:url var="urlViewPrivacy" value="<%=ClientIdentifiers.VIEW_PRIVACY%>" />
<c:url var="urlViewProfileByUsername" value="<%=ClientIdentifiers.VIEW_PROFILE_BY_USERNAME%>" />
<c:url var="urlViewTutorials" value="<%=ClientIdentifiers.VIEW_TUTORIALS%>" />
<c:url var="urlViewUserRegister" value="<%=ClientIdentifiers.VIEW_USER_REGISTER%>" />
<c:url var="urlViewWorkflowCall" value="<%=ClientIdentifiers.VIEW_WORKFLOW_CALL%>" />
<c:url var="urlViewWorkflowCalls" value="<%=ClientIdentifiers.VIEW_WORKFLOW_CALLS%>" />

<nav class="navbar navbar-default navbar-fixed-top">
  <div class="container-fluid">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="${urlContextPath}"><%@include file="logo.jsp" %></a>
    </div>
    <div class="nav navbar-right" style="margin-top: 10px; margin-right: 10px;">
<sec:authorize access="isAuthenticated()">
      <img src="<c:url value="/resources/img/user.png" />" style="height: 16px; width: 16px; vertical-align: middle;" />
      <a href="${urlViewProfileByUsername}/<sec:authentication property="principal.username" />"
         title="View Profile"><sec:authentication property="principal.username" /></a>&nbsp;
      <form class="inline" action="${urlLogout}" method="post">
        <input type="submit"
               value="<spring:message code="general.sign_out" />" />
        <input type="hidden"
               name="${_csrf.parameterName}"
               value="${_csrf.token}" />
      </form>
</sec:authorize>
<sec:authorize access="!isAuthenticated()">
      <form class="inline" action="${urlViewLogin}" method="get">
        <input type="submit"
               value="<spring:message code="login.login" />" />
      </form>
</sec:authorize>
    </div>
    <div class="navbar-right">
      <form class="navbar-form" role="search">
        <div class="form-group">
          <input type="text" class="form-control" id="search" size="30" maxlength="50" 
                 placeholder="Enter search term" />
        </div>
      </form>
    </div>
    <div id="navbar" class="navbar-collapse collapse">
      <ul class="nav navbar-nav">

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Tutorials <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li>
              <a href="${urlViewTutorials}">View</a>
            </li>
          </ul>
        </li>

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Modules <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li>
              <a href="${urlViewModules}"><spring:message code="general.view_all" /></a>
            </li>
<sec:authorize access="isAuthenticated()">
            <li>
              <a href="${urlViewModuleManage}"><spring:message code="module.manage" /></a>
            </li>
            <li>
              <a href="${urlViewModuleVerify}"><spring:message code="module.verify" /></a>
            </li>
            <li>
              <a href="${urlViewModuleUploadZOON}"><spring:message code="module.upload_to_ZOON" /></a>
            </li>
</sec:authorize>
          </ul>
        </li>

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Workflow Calls<span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li>
              <a href="${urlViewWorkflowCalls}"><spring:message code="general.view_all" /></a>
            </li>
            <li>
              <a href="${urlViewWorkflowCall}"><spring:message code="general.create" /></a>
            </li>
          </ul>
        </li>

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Users<span class="caret"></span></a>
          <ul class="dropdown-menu">
<sec:authorize access="!isAuthenticated()">
            <li>
              <a href="${urlViewUserRegister}">ZOON Site Registration</a>
            </li>
</sec:authorize>
<sec:authorize access="hasRole('ROLE_ADMINISTRATOR')">
            <li>
              <a href="${urlViewManageUsers}">Manage Users</a>
            </li>
</sec:authorize>
          </ul>
        </li>

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">General <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li>
              <a href="${urlViewAbout}"><spring:message code="general.about" /></a>
            </li>
            <li>
              <a href="${urlViewContact}"><spring:message code="general.contact" /></a>
            </li>
            <li>
              <a href="${urlViewPrivacy}"><spring:message code="general.privacy" /></a>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</nav>