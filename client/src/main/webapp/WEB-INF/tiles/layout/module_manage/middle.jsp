<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>

<sec:authorize access="isAuthenticated()">

<c:url var="urlAJAXUploadModuleToPrivate" value="<%=ClientIdentifiers.URL_PREFIX_MANAGE_MODULES_AJAX + ClientIdentifiers.ACTION_MODULE_UPLOAD_TO_PRIVATE %>" />

<div class="container wide-container">
  <h3 class="page-header"><spring:message code="module.upload_to_private" /></h3>
  <div>
    <form id="upload_form"
          method="post"
          action="${urlAJAXUploadModuleToPrivate}"
          enctype="multipart/form-data">
      <div style="display: inline-block;">
        <p><spring:message code="module.upload_click_browse" /></p> 
        <input name="artifact" id="artifact" type="file" />
      </div>
    </form>
    <div style="margin: 20px 0px 20px 100px;">
      <button id="artifact_submit"
              disabled="disabled"
              value="Submit"
              onclick="upload_module()" ><spring:message code="module.upload_to_private" /></button>
    </div>
    <div style="margin: 20px 0px 20px 100px;"
         id="upload_result">
    </div>
  </div>
  <h3 class="page-header"><spring:message code="module.delete" /></h3>
  <div>
    <form>
      <select id="deletion_select" style="vertical-align: top;"></select>
      <p id="deletion_result" style="color: red;"></p>
    </form>
  </div>
</div>
</sec:authorize>