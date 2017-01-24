/**
 * Delete a user.
 * 
 * @param username Username to delete.
 */
function ajax_for_deleting(username) {
  var token = $("meta[name='_csrf']").attr("content");
  var header = $("meta[name='_csrf_header']").attr("content");

  // TODO: http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/csrf.html#csrf-include-csrf-token-ajax
  $(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
  });

  jQuery.ajax({
    url: URL_PREFIX_USER_DELETING + encodeURIComponent(username),
    async: true,
    type: 'POST',
    data: {},
    dataType: 'json',
    timeout: 30000,
    contentType: 'application/json; charset=utf-8',
    success: function(response) {
               var user_deleted = response[MODEL_ATTRIBUTE_USER_DELETED];
               if (user_deleted != undefined) {
                 if (user_deleted) {
                   /* Refresh the page */
                   location.reload();
                 } else {
                   show_message_error('Could not delete user');
                   setTimeout(function() { hide_message_error(); }, 3000);
                 }
               } else {
                 show_not_found(MODEL_ATTRIBUTE_USER_DELETED, 'User deletion model attribute',
                                js_error_results);
               }
             },
    error: function(xhr, status, error) {
             show_js_error(error);
           }
  });
}

/**
 * (Un)set user enabled status.
 * 
 * @param username Username to adjust.
 * @param enabled Enabled value to set. 
 */
function ajax_for_enabling(username, enabled) {
  var token = $("meta[name='_csrf']").attr("content");
  var header = $("meta[name='_csrf_header']").attr("content");

  // TODO: http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/csrf.html#csrf-include-csrf-token-ajax
  $(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
  });

  jQuery.ajax({
    url: URL_PREFIX_USER_ENABLING + encodeURIComponent(username) + '/' + enabled,
    async: true,
    type: 'POST',
    data: {},
    dataType: 'json',
    timeout: 30000,
    contentType: 'application/json; charset=utf-8',
    success: function(response) {
             },
    error: function(xhr, status, error) {
           }
  });
}