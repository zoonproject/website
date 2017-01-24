<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers,
                uk.ac.ox.cs.science2020.zoon.client.controller.ajax.authenticated.ManageModulesAJAX" %>
<sec:csrfMetaTags />

<c:set var="MAX_FILE_UPLOAD_SIZE"><%= ManageModulesAJAX.MAX_FILE_UPLOAD_SIZE %></c:set>

<c:url var="urlAJAXVerifyArtifact" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES_AJAX + ClientIdentifiers.ACTION_ARTIFACT_VERIFY %>" />
<c:url var="urlAJAXRetrievePrivateModules" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES_AJAX + ClientIdentifiers.RENDER_MODULES_RETRIEVE_PRIVATE %>" />
<c:url var="urlAJAXRetrieveVerificationOutput" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES_AJAX + ClientIdentifiers.RENDER_VERIFICATION_OUTPUT_RETRIEVE %>" />
<c:url var="urlViewModuleManage" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES + ClientIdentifiers.VIEW_MODULE_MANAGE %>" />

    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery.form-3.46.0.js" />"></script>
    <script type="text/javascript">
      /* <![CDATA[ */

      var uploaded_files = [];
      var verify_uploaded_select;
      var verifying_el;
      var verify_result_el;
      var verify_output_el;
      var timer = jQuery('<img>').attr({ 'src': '<c:url value="/resources/img/spinner.gif" />' });

      /**
       *
       */
      function ajax_for_verification_output(verification_identifier) {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        // TODO: http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/csrf.html#csrf-include-csrf-token-ajax
        $(document).ajaxSend(function(e, xhr, options) {
          xhr.setRequestHeader(header, token);
        });

        jQuery.ajax({
          url: '${urlAJAXRetrieveVerificationOutput}/' + verification_identifier,
          async: true,
          type: 'GET',
          data: {},
          dataType: 'json',
          timeout: 30000,
          contentType: 'application/json; charset=utf-8',
          success: function(response) {
                     var json = retrieve_json(response, '<%= ClientIdentifiers.MODEL_ATTRIBUTE_VERIFICATION_OUTPUT %>',
                                              'verification_output', jQuery('#verify_result'));
                     if (json != undefined) {
                       json = jQuery.parseJSON(json);

                       var outcome = json.outcome;
                       var output = json.output;
                       var result = json.result;

                       if (output != null) {
                         verify_output_el.text(output);
                         verify_output_el.scrollTop(verify_output_el[0].scrollHeight);
                       }
                       if (result != null) {
                         verify_result_el.text(result);
                         verify_result_el.scrollTop(verify_result_el[0].scrollHeight);
                       }

                       if (outcome) {
                         verifying_el.empty();
                         if (verify_output_el.text() == '') {
                           verify_output_el.text('No stdout/stderr output generated');
                         }
                         /* Refresh the drop-down list */
                         ajax_for_private_modules();
                       } else {
                         setTimeout(function() { ajax_for_verification_output(verification_identifier); }, 1500);
                       }
                     }
                   },
          error: function(xhr, status, error) {
                   alert('Error! ' + error);
                 }
        });
      }

      /**
      *
      */
      function ajax_for_module_verification(module_details) {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        // TODO: http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/csrf.html#csrf-include-csrf-token-ajax
        $(document).ajaxSend(function(e, xhr, options) {
          xhr.setRequestHeader(header, token);
        });

        verify_result_el.empty();
        verify_output_el.empty();

        jQuery.ajax({
          url: '${urlAJAXVerifyArtifact}/' + module_details,
          async: true,
          type: 'POST',
          data: {},
          dataType: 'json',
          timeout: 30000,
          contentType: 'application/json; charset=utf-8',
          success: function(response) {
                     var json = retrieve_json(response, '<%= ClientIdentifiers.MODEL_ATTRIBUTE_VERIFY_MODULE %>',
                                              'verify_module', jQuery('#verify_result'));
                     if (json != undefined) {
                       json = jQuery.parseJSON(json);
                       var verification_identifier = json;
                       if (Math.floor(verification_identifier) == verification_identifier &&
                           jQuery.isNumeric(verification_identifier)) {
                         ajax_for_verification_output(verification_identifier);

                         verifying_el.append(' Verifying .... ').append(timer);
                       } else {
                         jQuery('#verify_result').text(json);
                       }
                     }
                   },
          error: function(xhr, status, error) {
                   alert('Error! ' + error);
                 }
        });
      }

      /**
      *
      */
      function ajax_for_private_modules() {
        uploaded_files = [];
        jQuery.ajax({
          url: '${urlAJAXRetrievePrivateModules}',
          async: true,
          type: 'GET',
          data: {},
          dataType: 'json',
          timeout: 30000,
          contentType: 'application/json; charset=utf-8',
          success: function(response) {
                     var json = retrieve_json(response, '<%= ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES %>',
                                              'retrieve_private_modules', jQuery('#verify_result'));
                     if (json != undefined) {
                       json = jQuery.parseJSON(json);
                       if (Object.keys(json).length > 0) {
                         var artifacts = [];
                         jQuery.each(json, function(artifact_name, all_artifact_versioned_data) {
                           jQuery.each(all_artifact_versioned_data, function(index, each_artifact_versioned_data) {
                             artifacts.push(each_artifact_versioned_data);
                           });
                         });

                         artifacts.sort(function(a, b) {
                           var name_a = a.name;
                           var name_b = b.name;
                           return (name_a < name_b) ? -1 : (name_a > name_b) ? 1 : 0;
                         });
                         uploaded_files = artifacts;
                         update_verify_list(verify_uploaded_select, '-- Select module to verify --');
                       } else {
                         verify_uploaded_select.remove();
                         jQuery('#explanation').remove();
                         var text = 'Only stored private modules will appear here!';
                         var a = jQuery('<a />').attr( { 'href' : '${urlViewModuleManage}' } )
                                                .text('here');
                         var p = jQuery('<p />').append(text)
                         verifying_el.append(p).append('Click ').append(a).append(' to store a module.');
                       }
                     }
                   },
          error: function(xhr, status, error) {
                   alert('Error! ' + error);
                 }
        });
      }

      /**
       * Show a select element containing all the uploaded files.
       *
       * @param select Element to update.
       * @param title First option text.
       */
      function update_verify_list(select, title) {
        select.html('');
        select.off();
        select.append(jQuery('<option />').attr({ 'value' : 0, 'selected' : 'selected' })
                                          .html(title));
        jQuery.each(uploaded_files, function(idx, artifact) {
          var artifact_name = artifact.name;
          var artifact_vers = artifact.version;
          var artifact_module_type = artifact.moduleType;
          var artifact_verified = artifact.verified;

          var id = 'app^' + artifact_name + '^' + artifact_vers;
          var text = jQuery('<span />').text(artifact_name);

          var verified_class = artifact_verified ? 'verified' : 'unverified';
          var option_class = 'monospace ' + verified_class;
          text = text.prop('outerHTML') + ' (vers.' + artifact_vers + ') [' + artifact_module_type + ']';
          var option = jQuery('<option />').attr({ 'value' : id })
                                           .addClass(option_class)
                                           .html(text);
          select.append(option);
        });

        select.change(function() {
          var select = jQuery(this);
          var selected = select.find('option:selected');
          var value = selected.attr('value');
          if (value != '0') {
            ajax_for_module_verification(value);
          }
        });
      }

      /**
       *
       */
      jQuery(document).ready(
        function() {
          verify_uploaded_select = jQuery('#verify_uploaded_select');
          verify_result_el = jQuery('#verify_result');
          verify_output_el = jQuery('#verify_output');
          verifying_el = jQuery('#verifying');

          /* On page refresh/load update global var with collection of uploaded files. */
          if (uploaded_files.length == 0) {
            ajax_for_private_modules();
          }
        }
      );
      /* ]]> */
    </script>