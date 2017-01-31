<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>
<sec:csrfMetaTags />

<c:url var="urlAJAXCreateIdentity" value="<%=ClientIdentifiers.URL_PREFIX_MANAGE_USERS_AJAX + ClientIdentifiers.ACTION_IDENTITY_CREATE%>" />
<c:url var="urlViewModule" value="<%=ClientIdentifiers.VIEW_MODULE%>" />
<c:url var="urlViewWorkflowCall" value="<%=ClientIdentifiers.VIEW_WORKFLOW_CALL%>" />

    <script type="text/javascript">
      /* <![CDATA[ */
<c:if test="${ma_user_currently_logged_in}">
      var MODEL_ATTRIBUTE_CREATE_IDENTITY = '<%=ClientIdentifiers.MODEL_ATTRIBUTE_CREATE_IDENTITY%>';
      var PARAM_NAME_USER_EMAIL = '<%=ClientIdentifiers.PARAM_NAME_USER_EMAIL%>';
      var PARAM_NAME_USER_IDENTITY = '<%=ClientIdentifiers.PARAM_NAME_USER_IDENTITY%>';
      var js_error_profile_by_username;
</c:if>
      jQuery(document).ready(
        function() {
<sec:authorize access="hasRole('ROLE_ADMINISTRATOR')">
          var submitButton = jQuery('#submit');
          var editButton = jQuery('#edit');

          editButton.click(function() {
            editButton.hide();
            submitButton.show();
            jQuery('input.form_input:text:disabled').removeAttr('disabled');
          });

          submitButton.click(function() {
            editButton.show();
            submitButton.hide();
            jQuery('input.form_input:text').attr('disabled', 'disabled');
          });
</sec:authorize>

<c:if test="${not empty ma_modules}">
          var modules = [];
  <c:forEach items="${ma_modules}" var="module">
          modules.push( { name : '<c:out value="${module.name}" />',
                          version : '<c:out value="${module.version}" />',
                          nameURL : '<c:out value="${module.nameURL}" />',
                          private_artifact : <c:out value="${module.privateArtifact}" /> } );
  </c:forEach>
          display_artifacts(jQuery('#modules'), '${urlViewModule}/', modules);
</c:if>
<c:if test="${not empty ma_workflow_calls}">
          var workflow_calls = [];
  <c:forEach items="${ma_workflow_calls}" var="workflowCall">
          workflow_calls.push( { name : '<c:out value="${workflowCall.name}" />',
                                 version : '<c:out value="${workflowCall.version}" />',
                                 nameURL : '<c:out value="${workflowCall.nameURL}" />',
                                 private_artifact : <c:out value="${workflowCall.privateArtifact}" /> } );
  </c:forEach>
          display_artifacts(jQuery('#workflow_calls'), '${urlViewWorkflowCall}/',
                            workflow_calls);
</c:if>
<c:if test="${ma_user_currently_logged_in}">
          js_error_profile_by_username = jQuery('#js_error_profile_by_username');

          jQuery('#create_identity').submit(function(event) {
            ajax_for_identity_creation('${urlAJAXCreateIdentity}',
                                       jQuery('#' + PARAM_NAME_USER_IDENTITY).val(),
                                       jQuery('#' + PARAM_NAME_USER_EMAIL).val());
            event.preventDefault();
          });
</c:if>
        }
      );
      /* ]]> */
    </script>
    <script type="text/javascript" src="<c:url value="/resources/js/user_profile/user_profile.js" />"></script>