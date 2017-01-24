<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers,
                uk.ac.ox.cs.science2020.zoon.client.controller.ajax.authenticated.ManageModulesAJAX" %>
<sec:csrfMetaTags />

<c:set var="MAX_FILE_UPLOAD_SIZE"><%= ManageModulesAJAX.MAX_FILE_UPLOAD_SIZE %></c:set>

<c:url var="urlAJAXDeleteModule" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES_AJAX + ClientIdentifiers.ACTION_MODULE_DELETE %>" />
<c:url var="urlAJAXRetrievePrivateModules" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_MODULES_AJAX + ClientIdentifiers.RENDER_MODULES_RETRIEVE_PRIVATE %>" />

    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery.form-3.46.0.js" />"></script>
    <script type="text/javascript">
      /* <![CDATA[ */

      var artifact_input;
      var timer = jQuery('<img>').attr({ 'src': '<c:url value="/resources/img/spinner.gif" />' });

      var DELIMITER = '^';

      var uploaded_files = [];
      var deletion_select_el;
      var deletion_result_el;

      /**
      *
      */
      function ajax_for_module_deletion(module_data) {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        // TODO: http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/csrf.html#csrf-include-csrf-token-ajax
        $(document).ajaxSend(function(e, xhr, options) {
          xhr.setRequestHeader(header, token);
        });

        jQuery.ajax({
          url: '${urlAJAXDeleteModule}',
          async: true,
          type: 'POST',
          data: JSON.stringify(module_data),
          dataType: 'json',
          timeout: 30000,
          contentType: 'application/json; charset=utf-8',
          success: function(response) {
                     var json = retrieve_json(response, '<%= ClientIdentifiers.MODEL_ATTRIBUTE_DELETE_MODULE %>',
                                              'delete_module', deletion_result_el);
                     if (json != undefined) {
                       deletion_result_el.html(json);
                       setTimeout(function() { ajax_for_private_modules(); }, 1500);
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
                     deletion_result_el.empty();
                     var json = retrieve_json(response, '<%= ClientIdentifiers.MODEL_ATTRIBUTE_PRIVATE_MODULES %>',
                                              'retrieve_private_modules', deletion_result_el);
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
                         update_deletion_list(deletion_select_el, '-- Select module to delete --');
                       } else {
                         deletion_select_el.hide();
                       }
                     }
                   },
          error: function(xhr, status, error) {
                   deletion_result_el.empty();
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
       * Show a select element containing all the uploaded files.
       *
       * @param div Element to delete.
       */
      function update_deletion_list(select, title) {
        if (!deletion_select_el.is(':visible')) {
          deletion_select_el.show();
        }
        select.html('');
        select.off();
        select.append(jQuery('<option />').attr({ 'value' : 0, 'selected' : 'selected' })
                                          .html(title));
        jQuery.each(uploaded_files, function(idx, artifact) {
          var artifact_name = artifact.name;
          var artifact_vers = artifact.version;
          var artifact_module_type = artifact.moduleType;
          var artifact_verified = artifact.verified;

          var id = value_constructor(artifact_name, artifact_vers, artifact_module_type);
          var text = jQuery('<span />').text(artifact_name);

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
          if (value != "0") {
            var text = 'Deleting....  ';
            deletion_result_el.append(text).append(timer);
            var value_obj = value_destructor(value);
            ajax_for_module_deletion(value_obj);
          }
        });
      }

      /**
       * Upload an module using the jQuery form plugin.
       */
      function upload_module() {
        $('#upload_result').empty();
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $('#upload_result').text('Please wait! Parsing module content ').append(timer);

        $('#upload_form').ajaxForm({
          beforeSubmit: function(arr, $form, options) {
            options.beforeSend=function(xhr) {
              xhr.setRequestHeader(header, token);
            }
          },
          dataType: "text",
          success: function(response) {
                     response = JSON.parse(response);
                     var json = retrieve_json(response, '<%= ClientIdentifiers.MODEL_ATTRIBUTE_UPLOAD_MODULE %>',
                                              'upload_module', jQuery('#upload_result'));
                     if (json != undefined) {
                       var success = jQuery('<span />').css('color', 'red').text(json);
                       $('#upload_result').html(success);
                       ajax_for_private_modules();
                     }
                   },
          error: function(xhr, status, error) {
                   alert('Error! ' + error);
                 }
        }).submit();
      }

      jQuery(document).ready(
        function() {
          artifact_input = jQuery('#artifact');
          deletion_select_el = jQuery('#deletion_select');
          deletion_result_el = jQuery('#deletion_result');

          /* On page refresh/load update global var with collection of uploaded files. */
          if (uploaded_files.length == 0) {
            ajax_for_private_modules();
          }

          artifact_input.change(function() {
            var submit_button = jQuery('#artifact_submit');
            submit_button.removeAttr('disabled');
            jQuery('#upload_result').empty();
            var file = this.files[0];
            if (file.size > <c:out value='${MAX_FILE_UPLOAD_SIZE}' />) {
              jQuery('#upload_result').text('Sorry! Maximum upload size of <c:out value="${MAX_FILE_UPLOAD_SIZE}" /> bytes.');
              submit_button.attr('disabled', 'disabled');
            }
          });
        }
      );
      /* ]]> */
    </script>