<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>

<c:url var="urlAJAXRetrieveWorkflowCalls" value="<%=ClientIdentifiers.URL_PREFIX_AJAX + ClientIdentifiers.RENDER_RETRIEVE_WORKFLOW_CALLS %>" />
<c:url var="urlViewProfileByIdentity" value="<%=ClientIdentifiers.VIEW_PROFILE_BY_IDENTITY%>" />
<c:url var="urlViewWorkflowCall" value="<%=ClientIdentifiers.VIEW_WORKFLOW_CALL%>" />

<sec:csrfMetaTags />
    <script type="text/javascript">
      /* <![CDATA[ */
      var MODEL_ATTRIBUTE_WORKFLOW_CALLS = '<%=ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALLS %>';
      var URL_AJAX_WORKFLOW_CALLS = '${urlAJAXRetrieveWorkflowCalls}';
      var VIEW_PROFILE_BY_IDENTITY = '${urlViewProfileByIdentity}';
      var VIEW_WORKFLOW_CALL = '${urlViewWorkflowCall}';

      var PRIVATE_IMG_SRC = '<c:url value="/resources/img/private.png" />';
      /* ]]> */
    </script>
    <script type="text/javascript" src="<c:url value="/resources/js/workflow_call/display_all.js" />"></script>
    <script type="text/javascript">
      /* <![CDATA[ */
      var module = {};
<c:if test="${not empty ma_module}">
<%-- Populated in WorkflowCallCtlr.viewWorkflowCalls --%>
      module.name = '<c:out value="${ma_module.name}" />';
      module.version = '<c:out value="${ma_module.version}" />';
      module.type = '<c:out value="${ma_module.type}" />';
</c:if>
      jQuery(document).ready(
        function() {
          ajax_for_workflow_calls(module);
        }
      );
      /* ]]> */
    </script>