<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers,
                uk.ac.ox.cs.science2020.zoon.shared.value.object.artifact.AbstractArtifactVO" %>
<%-- Correspond to properties defined in AJAX controller inner class --%>
<c:set var="key_exception" value="<%=ClientIdentifiers.KEY_EXCEPTION%>" />
<c:set var="key_json" value="<%=ClientIdentifiers.KEY_JSON%>" />

<c:url var="urlSearch" value="<%= ClientIdentifiers.URL_PREFIX_AJAX + ClientIdentifiers.RENDER_SEARCH %>" />
<c:url var="urlContextPath" value="/" />
<c:url var="urlViewModule" value="<%=ClientIdentifiers.VIEW_MODULE%>" />
<c:url var="urlViewProfileByIdentity" value="<%=ClientIdentifiers.VIEW_PROFILE_BY_IDENTITY%>" />
<c:url var="urlViewWorkflowCall" value="<%=ClientIdentifiers.VIEW_WORKFLOW_CALL%>" />

    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery-1.11.3.min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery-ui-1.9.2.custom.min.js" />"></script>
    <link href="<c:url value="/resources/css/bootstrap/3.3.5/bootstrap.min.css" />" rel="stylesheet" />

    <script type="text/javascript">
      /* <![CDATA[ */
      "use strict";

      /**
       * Show a javascript error message.
       *
       * @param error_text to show in error message.
       * @param element Element to display message in.
       */
      function show_js_error(error_text, element) {
        element.text(error_text).show();
      }

      /**
       * Show the "object not found" text.
       *
       * @param not_found Object which wasn't found (e.g. perhaps a model attribute or object property).
       * @param descriptive Textual description of what was being sought (e.g. workload data).
       * @param error_element Error element to display message in.
       */
      function show_not_found(not_found, descriptive, error_element) {
        show_js_error('Application problem! Could not find object ' + not_found + 
                      ' when querying for ' + descriptive + '.', error_element);
      }

     /**
       * Retrieve JSON object from the AJAX response.
       *
       * @param response AJAX response.
       * @param model_attr The model attribute being sought.
       * @param descriptive Textual representation of the AJAX request which was made.
       * @return JSON object (or nothing if not found).
       */
      function retrieve_json(response, model_attr, descriptive, error_element) {
        var response_obj = response[model_attr];
        if (response_obj != undefined) {
           var response_exception = response_obj['<c:out value="${key_exception}" />'];
          if (response_exception != undefined) {
            show_js_error(response_exception, error_element);
          } else {
            var json_object = response_obj['<c:out value="${key_json}" />'];
            if (json_object != undefined) {
              return json_object;
            } else {
              show_not_found('<c:out value="${key_json}" />', descriptive, error_element);
            }
          }
        } else {
          show_not_found(model_attr, descriptive, error_element);
        }
      }

      /**
       * Handle a situation when a AJAX call has returned an unhandled error.
       *
       * @param error AJAX-generated error (or empty string!).
       * @param descriptive Textual representation of the AJAX request which was made.
       * @param element Error element to display.
       */
      function handle_error(error, descriptive, element) {
        if (error != undefined) {
          if (error == '') {
            /* May imply client has been switched off! */
            show_js_error('Application problem! An unspecified error was returned when querying for ' + descriptive +
                          ' which is indicative of the application having been turned off!. Please reload/refresh the page!', element);
          } else if (error == 'SyntaxError: JSON.parse: unexpected character') {
            /* Could be app manager failing but NOT generating a SOAP fault, e.g. a db connectivity problem */
            show_js_error('Application problem! An error has occured when querying for ' + descriptive + '.' +
                          ' A page reload/refresh may resolve the problem!', element);
          } else {
            show_js_error('Application problem! The following error text has been returned when querying for ' + descriptive +
                          ' data: ' + error, element);
          }
        }
      }

      /**
       * Hide the javascript error element.
       *
       * @param element Element to hide.
       */
      function hide_js_error(element) {
        element.hide();
      }

      var debug_div;
      function debug(text) {
        var span = jQuery('<span>').text(text);
        debug_div.prepend(span);
      }
      function debug_object(incoming) {
       for (var key in incoming) {
          var val = incoming[key];
          debug('\'' + key + '\' = \'' + val + '\'');
          /*
          for (var prop in val) {
            if (val.hasOwnProperty(prop)) {
              debug('key ' + key + ' val ' + val + ' prop ' + prop);
            }
          }
          */
        }
      }

      jQuery(document).tooltip();

      var js_error_div;
      jQuery(document).ready(
        function() {
          debug_div = jQuery('#js_debug');
          js_error_div = jQuery('#js_error');

          jQuery('.explanatory_text').click(function(e) {
            var what = jQuery(this).attr('name');
            if (jQuery('#' + what + '_explain').is(':visible')) {
              jQuery('#' + what + '_click').text('show');
            } else {
              jQuery('#' + what + '_click').text('hide');
            }
            jQuery('#' + what + '_explain').toggle('200');
          });
        }
      );

      var CONTEXT_PATH = '${urlContextPath}/';
      var KEY_EXCEPTION = '<c:out value="${key_exception}" />';
      var KEY_JSON = '<c:out value="${key_json}" />';

      var LATEST = '<%= AbstractArtifactVO.LATEST %>';
      var MODEL_ATTRIBUTE_SEARCH_RESULTS = '<%= ClientIdentifiers.MODEL_ATTRIBUTE_SEARCH_RESULTS %>';
      var URL_PREFIX_AJAX_SEARCH = '${urlSearch}';
      var URL_PREFIX_VIEW_MODULE = '${urlViewModule}/';
      var URL_PREFIX_VIEW_PROFILE_BY_IDENTITY = '${urlViewProfileByIdentity}/';
      var URL_PREFIX_VIEW_WORKFLOW_CALL = '${urlViewWorkflowCall}/';

      /* ]]> */
    </script>
    <script type="text/javascript" src="<c:url value="/resources/js/search.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/general.js" />"></script>