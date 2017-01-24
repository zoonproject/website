<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>

<sec:csrfMetaTags />

<c:url var="urlAJAXRetrievePrivateVerifiedModules" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES_AJAX + ClientIdentifiers.RENDER_MODULES_RETRIEVE_PRIVATE_VERIFIED %>" />
<c:url var="urlAJAXUploadModuleToZOON" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES_AJAX + ClientIdentifiers.ACTION_MODULE_UPLOAD_TO_ZOON %>" />
<c:url var="urlViewModuleVerify" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES + ClientIdentifiers.VIEW_MODULE_VERIFY %>" />

    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery.form-3.46.0.js" />"></script>
    <script type="text/javascript">
      /* <![CDATA[ */

      var uploaded_verified_files = [];
      var verified_uploaded_select;
      var uploading_el;
      var timer = jQuery('<img>').attr({ 'src': '<c:url value="/resources/img/spinner.gif" />' });
      var DELIMITER = '^';
      var INFO_IMG_SRC = '<c:url value="/resources/img/info.png" />';

      /**
       * Upload a temporary file-stored file to the ZOON store (probably GitHub).
       *
       * @param module_data
       * @param callback
       */
      function ajax_for_module_upload_to_ZOON_store(module_data, callback) {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        // TODO: http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/csrf.html#csrf-include-csrf-token-ajax
        $(document).ajaxSend(function(e, xhr, options) {
          xhr.setRequestHeader(header, token);
        });

        jQuery.ajax({
          url: '${urlAJAXUploadModuleToZOON}',
          async: true,
          type: 'POST',
          data: JSON.stringify(module_data),
          dataType: 'json',
          timeout: 30000,
          contentType: 'application/json; charset=utf-8',
          success: function(response) {
                     var json = retrieve_json(response, '<%= ClientIdentifiers.MODEL_ATTRIBUTE_UPLOAD_MODULE_TO_ZOON %>',
                                              'module_upload_to_ZOON', uploading_el);
                     var upload_response = {};
                     if (json != undefined) {
                       upload_response = jQuery.parseJSON(json);
                     }
                     callback(upload_response);
                   },
          error: function(xhr, status, error) {
                   alert('Error! ' + error);
                 }
        });
      }

      function ajax_for_verified_private_modules() {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        // TODO: http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/csrf.html#csrf-include-csrf-token-ajax
        $(document).ajaxSend(function(e, xhr, options) {
          xhr.setRequestHeader(header, token);
        });

        uploaded_verified_files = [];
        jQuery.ajax({
          url: '${urlAJAXRetrievePrivateVerifiedModules}',
          async: true,
          type: 'GET',
          data: {},
          dataType: 'json',
          timeout: 30000,
          contentType: 'application/json; charset=utf-8',
          success: function(response) {
                     var json = retrieve_json(response, '<%= ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES %>',
                                              'retrieve_private_verified_modules', uploading_el);
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
                         uploaded_verified_files = artifacts;
                         update_verified_list(verified_uploaded_select, '-- Select module to upload to ZOON GitHub --');
                       } else {
                         verified_uploaded_select.remove();
                         jQuery('#remove').remove();
                         jQuery('#license').remove();
                         var text = 'Only verified private modules will appear here!';
                         var a = jQuery('<a />').attr( { 'href' : '${urlViewModuleVerify}' } )
                                                .text('here');
                         var p = jQuery('<p />').append(text)
                         uploading_el.append(p).append('Click ').append(a).append(' to verify.');
                       }
                     }
                   },
          error: function(xhr, status, error) {
                   alert('Error! ' + error);
                 }
        });
      }

      /**
       * Compose an option 'value' attribute.
       *
       * @param name Module name.
       * @param version Module version.
       * @param type Module type.
       */
      function value_constructor(name, version, type) {
        return name + DELIMITER + version + DELIMITER + type;
      }

      /**
       * Extract detail from an option 'value' attribute.
       *
       * @returns JSON object of module details.
       */
      function value_destructor(value) {
        var components = value.split(DELIMITER);
        return { 'name' : components[0],
                 'version' : components[1],
                 'type' : components[2] };
      }

      /**
       * Function to call when the module has been uploaded.
       */
      function reset_callback(json) {
        ajax_for_verified_private_modules();
        if (json !== undefined && json.response !== undefined) {
          var response = jQuery('<span />').addClass('error_text')
                                           .html(json.response);
          uploading_el.html(response);
        } else {
          uploading_el.empty();
        }
      }

      /**
       * Show a select element containing all the uploaded and verified files.
       *
       * @param div Element to update.
       */
      function update_verified_list(select, title) {
        select.html('');
        select.off();
        select.append(jQuery('<option />').attr({ 'value' : 0, 'selected' : 'selected' })
                                          .html(title));

        if (uploaded_verified_files.length > 0) {
          jQuery.each(uploaded_verified_files, function(idx, artifact) {
            var artifact_name = artifact.name;
            var artifact_vers = artifact.version;
            var artifact_module_type = artifact.moduleType;
            var id = value_constructor(artifact_name, artifact_vers, artifact_module_type);
            var text = jQuery('<span />').addClass('bold')
                                         .text(artifact_name);
            text = text.prop('outerHTML') + ' (vers.' + artifact_vers + ') [' + artifact_module_type + ']';
            var option = jQuery('<option />').attr({ 'value' : id })
                                             .addClass('monospace')
                                             .html(text);
            select.append(option);
          });

          select.change(function() {
            var select = jQuery(this);
            var selected = select.find('option:selected');
            var value = selected.attr('value');
            if (value != '0') {
              var text = 'Uploading to ZOON GitHub....  ';
              uploading_el.append(text).append(timer);
              var value_obj = value_destructor(value);
              value_obj.removeAfterUpload = jQuery('#remove_after_upload').is(':checked');
              ajax_for_module_upload_to_ZOON_store(value_obj, reset_callback);
            }
          });
        }
      }

      jQuery(document).ready(
        function() {
          verified_uploaded_select = jQuery('#verified_uploaded_select');
          uploading_el = jQuery('#uploading');

          /* On page refresh/load update global var with collection of uploaded files. */
          if (uploaded_verified_files.length == 0) {
            ajax_for_verified_private_modules();
          }
        }
      );
      /* ]]> */
    </script>