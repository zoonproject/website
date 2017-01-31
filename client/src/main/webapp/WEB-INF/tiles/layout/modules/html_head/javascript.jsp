<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>

<c:url var="urlAJAXRetrieveLatestModules" value="<%= ClientIdentifiers.URL_PREFIX_AJAX + ClientIdentifiers.RENDER_MODULES_RETRIEVE_LATEST %>" />
<c:url var="urlAJAXRetrieveModuleTypes" value="<%= ClientIdentifiers.URL_PREFIX_AJAX + ClientIdentifiers.RENDER_MODULE_TYPES_RETRIEVE %>" />
<c:url var="urlViewModule" value="<%=ClientIdentifiers.VIEW_MODULE%>" />
<c:url var="urlViewProfileByIdentity" value="<%=ClientIdentifiers.VIEW_PROFILE_BY_IDENTITY%>" />
<c:url var="urlViewWorkflowCalls" value="<%=ClientIdentifiers.VIEW_WORKFLOW_CALLS%>" />

    <script type="text/javascript">
      /* <![CDATA[ */
      var MODEL_ATTRIBUTE_MODULES = '<%=ClientIdentifiers.MODEL_ATTRIBUTE_MODULES %>';
      var MODEL_ATTRIBUTE_MODULETYPES = '<%= ClientIdentifiers.MODEL_ATTRIBUTE_MODULETYPES %>';

      var PRIVATE_IMG_SRC = '<c:url value="/resources/img/private.png" />';
      var TIMER_SRC = '<c:url value="/resources/img/spinner.gif" />';

      var URL_AJAX_LATEST_MODULES = '${urlAJAXRetrieveLatestModules}';
      var VIEW_PROFILE_BY_IDENTITY = '${urlViewProfileByIdentity}';
      var URL_PREFIX_AJAX_MODULETYPES = '${urlAJAXRetrieveModuleTypes}';
      var URL_PREFIX_VIEW_MODULE = '${urlViewModule}';
      var URL_PREFIX_VIEW_WORKFLOW_CALLS = '${urlViewWorkflowCalls}';
      /* ]]> */
    </script>
    <script type="text/javascript" src="<c:url value="/resources/js/latex_replace.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/module_types/module_types.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/modules/modules.js" />"></script>