<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers,
                uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.workflowcall.WorkflowCallVO" %>
<sec:csrfMetaTags />

<c:url var="urlAJAXRetrieveModules" value="<%= ClientIdentifiers.URL_PREFIX_AJAX + ClientIdentifiers.RENDER_MODULES_RETRIEVE %>" />
<c:url var="urlAJAXRetrieveModuleTypes" value="<%= ClientIdentifiers.URL_PREFIX_AJAX + ClientIdentifiers.RENDER_MODULE_TYPES_RETRIEVE %>" />
<c:url var="urlAJAXUploadWorkflowCallToPrivate" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_WORKFLOW_CALLS_AJAX + ClientIdentifiers.ACTION_UPLOAD_WORKFLOW_CALL_TO_PRIVATE %>" />

    <script type="text/javascript">
      /* <![CDATA[ */
      var INFO_IMG_SRC = '<c:url value="/resources/img/info.png" />';

      var MODEL_ATTRIBUTE_MODULETYPES = '<%= ClientIdentifiers.MODEL_ATTRIBUTE_MODULETYPES %>';
      var MODEL_ATTRIBUTE_MODULES = '<%= ClientIdentifiers.MODEL_ATTRIBUTE_MODULES %>';
      var MODEL_ATTRIBUTE_WORKFLOW_CALL_SAVE_RESPONSE = '<%= ClientIdentifiers.MODEL_ATTRIBUTE_WORKFLOW_CALL_SAVE_RESPONSE %>';

      var timer = jQuery('<img>').attr({ 'src': '<c:url value="/resources/img/spinner.gif" />' });

      var URL_PREFIX_AJAX_MODULETYPES = '${urlAJAXRetrieveModuleTypes}';
      var URL_AJAX_ALL_MODULES = '${urlAJAXRetrieveModules}';
      var AJAX_WORKFLOW_CALL_UPLOAD_TO_PRIVATE_URL = '${urlAJAXUploadWorkflowCallToPrivate}';

      var MODULE_VERSION_JSON_KEY = '<%=WorkflowCallVO.MODULE_VERSION_JSON_KEY%>';
      /* ]]> */
    </script>
    <script type="text/javascript">
      /* <![CDATA[ */

      /**
       *
       */
      function instantiate_workflow_call() {
<c:if test="${not empty ma_workflow_call}">
        var workflow_call;
        try {
          <%-- TODO : Find a better way of escaping single quotes. --%>
          workflow_call = jQuery.parseJSON('${ma_workflow_call.contentEscaped}');
        } catch (exception) {}

        if (workflow_call) {
          jQuery('#workflow_call_version').val(workflow_call.version);
          jQuery('#workflow_call_description').val(workflow_call.description);
          jQuery('#workflow_call_name').val(workflow_call.name);

          display_existing(workflow_call);
        } else {
          jQuery('#workflow_call_version').val('Sorry! JSON parsing failure');
          jQuery('#workflow_call_description').val('Sorry! JSON parsing failure');
          jQuery('#workflow_call_name').val('Sorry! JSON parsing failure');
        }
</c:if>
        generate_workflow_call();
      }
      /* ]]> */
    </script>
    <script type="text/javascript" src="<c:url value="/resources/js/latex_replace.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/module_types/module_types.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/workflow_call/create.js" />"></script>