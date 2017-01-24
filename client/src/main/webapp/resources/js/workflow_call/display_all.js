var workflow_calls_call_failure_count = 0;

/* In middle.jsp */
var js_error_workflow_calls;

/**
 * AJAX call to retrieve workflow calls, optionally only retrieving ones which reference the 
 * specified module..
 * 
 * @param module Module information to retrieve only the workflow calls which reference it.
 */
function ajax_for_workflow_calls(module) {
  if (Object.size(module) > 0) {
    // Expecting module name, version and type data.
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    // TODO: http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/csrf.html#csrf-include-csrf-token-ajax
    $(document).ajaxSend(function(e, xhr, options) {
      xhr.setRequestHeader(header, token);
    });

  }

  jQuery.ajax({
    url: URL_AJAX_WORKFLOW_CALLS,
    async: false,
    type: 'GET',
    data: JSON.stringify(module),
    dataType: 'json',
    timeout: 30000,
    contentType: 'application/json; charset=utf-8',
    success: function(response) {
               /* retrieve_json defined in html_head/javascript.jsp */
               var json = retrieve_json(response, MODEL_ATTRIBUTE_WORKFLOW_CALLS, 'workflowcalls',
                                        js_error_workflow_calls);
               if (json != undefined) {
                 /* Reset the js error element and count (if it's visible) */
                 if (js_error_workflow_calls.is(':visible')) {
                   /* Defined in html_head/javascript.jsp */
                   hide_js_error(js_error_workflow_calls);
                   workflow_calls_call_failure_count = 0;
                }
                show_workflow_calls(jQuery.parseJSON(json));
              }
            },
    error: function(xhr, status, error) {
             /* handle_error defined in html_head/javascript.jsp */
             handle_error(error, 'workflowcalls', js_error_workflow_calls);
             /* Try a few times to reconnect but give up eventually! */
             if (workflow_calls_call_failure_count++ < 5) {
               setTimeout(function() { ajax_for_workflow_calls(module); }, 6000);
             }
           }
  });
}

/**
 * Display public and private workflow calls.
 * 
 * @param workflow_calls
 */
function show_workflow_calls(workflow_calls) {
  var DAY = 1000*60*60*24;
  var workflow_calls_href = window.location.pathname;
  var last_fwd_slash = workflow_calls_href.lastIndexOf('/', 0);
  profile_href = workflow_calls_href.substring(0, last_fwd_slash).concat(VIEW_PROFILE_BY_IDENTITY).concat('/');

  var workflow_calls_div = jQuery('#workflow_calls');
  /* Key is workflow call name, value is collection of corresponding workflow call versions */
  var workflow_call_names = Object.keys(workflow_calls);
  /* Sort map keys by name */
  workflow_call_names.sort();
  for (var ver_idx = 0; ver_idx < workflow_call_names.length; ver_idx++) {
    var workflow_call_name = workflow_call_names[ver_idx];
    var workflow_call_versions = workflow_calls[workflow_call_name];
    /* Sorted named workflow calls by version */
    artifact_sorter(workflow_call_versions);
    jQuery.each(workflow_call_versions, function(var_not_used, workflow_call) {
      var content = JSON.parse(workflow_call.content);
      var private_artifact = workflow_call.privateArtifact;
      var version = workflow_call.version;

      var outer_div = jQuery('<div />').addClass('col-lg-3 col-md-6');
      if (private_artifact) {
        outer_div.css( { 'background-image' : 'url(' + PRIVATE_IMG_SRC + ')',
                         'background-position' : 'center',
                         'background-repeat' : 'no-repeat' } );
      }
      var inner_div = jQuery('<div />').addClass('package-list-item');

      var h3 = jQuery('<h3 />');
      var h3_span = jQuery('<span />').addClass('glyphicon glyphicon-gift package-icon');
      h3_span.attr('aria-hidden', 'true');
      var href = VIEW_WORKFLOW_CALL + '/' + encodeURIComponent(workflow_call_name) +
                                      '/' + encodeURIComponent(version) +
                                      '/' + private_artifact;
      var h3_a = jQuery('<a />').attr( { 'href' : href } );

      var strong = jQuery('<strong />').html(workflow_call_name);
      h3_a.html(strong);
      h3.append(h3_span);
      h3.append('&nbsp;');
      h3.append(h3_a);

      var small = jQuery('<small />').html(' — v.' + version);
      h3.append(small);
      inner_div.append(h3);

      /* p1 */
      var p1 = jQuery('<p />').addClass('package-list-dateline');
      /* Usually of format YYYY-MM-DD hh:mm:ss */
      var days_ago = '';
      var submitted = workflow_call.submitted;
      if (submitted !== undefined && submitted != null) {
        var submitted = submitted.substr(0, 9);
        if (submitted != null) {
          var components = submitted.split("-");
          if (components.length == 3) {
            var month = components[1] - 1; /* Months are 0-11 */
            var submitted_date = new Date(components[0], month, components[2]);
            days_ago = Math.ceil((new Date().getTime() - submitted_date.getTime()) / DAY);
          }
        }
      };
      var p1_span = jQuery('<span />').addClass('package-list-date');
      if (days_ago != '') {
        p1_span.html(days_ago + ' days ago');
      } else {
        p1_span.html('Not yet submitted');
      }
      p1.append(p1_span);
      p1.append(' by ');
      if (workflow_call.authors !== undefined) {
        var authors = workflow_call.authors;
        if (authors.length == 0) {
          p1.append(jQuery('<span />').text('authors'));
        } else {
          var author_count = workflow_call.authors.length;
          jQuery.each(workflow_call.authors, function(index, each_author) {
            var p1_a = jQuery('<a />');
            p1_a.attr('href', profile_href + each_author.nameURL);
            p1_a.append(each_author.authorName);
            p1.append(p1_a);
            if (index != (author_count - 1)) {
              p1.append(', ');
            }
          });
        }
      } else {
        p1.append(jQuery('<span />').text('authors'));
      }
      inner_div.append(p1);

      var p2 = jQuery('<p />').append(content.description);
      inner_div.append(p2);

      outer_div.append(inner_div);
      workflow_calls_div.append(outer_div);

      if ((ver_idx+1)%4==0) {
        workflow_calls_div.append(jQuery('<div />').addClass('clearfix visible-lg-block'));
      }
      if ((ver_idx+1)%2==0) {
        workflow_calls_div.append(jQuery('<div />').addClass('clearfix visible-md-block'));
      }
    });
  }
}

jQuery(document).ready(
  function() {
    js_error_workflow_calls = jQuery('#js_error_workflow_calls');
  }
);