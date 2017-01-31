/**
 * Create an identity.
 * 
 * @param createIdentityURL URL to create identity.
 * @param identity Identity to create.
 * @param email Email to associate with identity.
 */
function ajax_for_identity_creation(createIdentityURL, identity, email) {
  var token = $("meta[name='_csrf']").attr("content");
  var header = $("meta[name='_csrf_header']").attr("content");

  // TODO: http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/csrf.html#csrf-include-csrf-token-ajax
  $(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
  });

  var data_obj = {};
  data_obj[PARAM_NAME_USER_IDENTITY] = identity;
  data_obj[PARAM_NAME_USER_EMAIL] = email;

  jQuery.ajax({
    url: createIdentityURL,
    async: false,
    type: 'POST',
    data: JSON.stringify(data_obj),
    dataType: 'json',
    timeout: 3000,
    contentType: 'application/json; charset=utf-8',
    success: function(response) {
               /* retrieve_json defined in html_head/javascript.jsp */
               var json_str = retrieve_json(response, MODEL_ATTRIBUTE_CREATE_IDENTITY,
                                            'create identity', js_error_profile_by_username);
               if (js_error_profile_by_username.is(':visible')) {
                 setTimeout(function() { hide_js_error(js_error_profile_by_username); }, 4000);
               }

               if (json_str !== undefined) {
                 var json_obj = jQuery.parseJSON(json_str);
                 var div = jQuery('<div />');
                 div.append(jQuery('<p />').addClass('message_text')
                                           .text(json_obj.outcome));
                 var button = jQuery('<button />').text(' Click to refresh page ')
                                                  .click(function() {
                                                    location.reload();
                                                  });
                 div.append(jQuery('<br />'));
                 div.append(button);
                 div.appendTo(jQuery('#create_identity').parent());
               }
             },
     error: function(xhr, status, error) {
              /* handle_error defined in html_head/javascript.jsp */
              handle_error(error, 'create identity', js_error_profile_by_username);
              setTimeout(function() { js_error_profile_by_username.hide(); }, 5000);
            }
  });
}
/**
 * Display artifacts in tabular form.
 * 
 * @param element Table element to display content.
 * @param urlPrefix URL prefix for link elements.
 * @param artifacts Artifacts to display.
 */
function display_artifacts(element, urlPrefix, artifacts) {
  var columns = 3;
  var table = element.css({ 'width' : '500px' });
  var tr = jQuery('<tr />');
  artifact_sorter(artifacts);
  jQuery.each(artifacts, function(idx, artifact) {
    if (idx % columns == 0) {
      table.append(tr);
      tr = jQuery('<tr />');
    }
    var href = urlPrefix + encodeURIComponent(artifact.name) +
                           '/' + encodeURIComponent(artifact.version) +
                           '/' + artifact.private_artifact;
    var a = jQuery('<a />').attr( { 'href' : href } )
                           .text(artifact.name);
    tr.append(jQuery('<td />').html(jQuery('<small />').append(a).append(' (v.' + artifact.version + ')')));
  });
  if (tr.has('td')) {
    table.append(tr);
  }
}