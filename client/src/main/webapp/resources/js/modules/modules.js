var DAY = 1000*60*60*24;
var modules_div;
var profile_href;
/* Array of artifact value objects */
var all_modules;

jQuery(document).ready(
  function() {
    modules_div = jQuery('#modules');

    var modules_href = window.location.pathname;
    var last_fwd_slash = modules_href.lastIndexOf('/', 0);
    profile_href = modules_href.substring(0, last_fwd_slash).concat(VIEW_PROFILE_BY_IDENTITY).concat('/');

    var loading = jQuery('<p />').text('Loading ... '); 
    var timer = jQuery('<img />').attr( { 'src' : TIMER_SRC } );
    timer.appendTo(loading);
    modules_div.html(loading);

    ajax_for_modules();

    loading.remove();
  }
);

/**
 * Display modules based on the all-module json.
 * 
 * @param view_types Module types to view.
 */
function display_modules(view_types) {
  if (view_types !== undefined) {
    modules_div.empty();
  }
  artifact_sorter(all_modules);

  var visible_count = 0;
  jQuery.each(all_modules, function(idx, module) {
    var module_type = module.moduleType;
    if (view_types !== undefined && view_types.indexOf(module_type) == -1) {
      // If module types to view is defined, and the current type is not in the collection, continue.
      return true;
    }

    var module_name = module.name;
    var module_version = module.version;
    var private_artifact = module.privateArtifact;

    var outer_div = jQuery('<div />').addClass('col-lg-3 col-md-6');
    if (private_artifact) {
      outer_div.css( { 'background-image' : 'url(' + PRIVATE_IMG_SRC + ')',
                       'background-position' : 'center',
                       'background-repeat' : 'no-repeat' } );
    }
    var inner_div = jQuery('<div />').addClass('package-list-item');

    /* h3 */
    var h3 = jQuery('<h3 />');
    var h3_a;
    if (private_artifact) {
      h3_a = jQuery('<a />').attr({ 'href' : URL_PREFIX_VIEW_MODULE +
                                             '/' + encodeURIComponent(module_name) +
                                             '/' + encodeURIComponent(module_version) +
                                             '/' + private_artifact });
    } else {
      var r_name = encodeURIComponent(module_name + '.R');
      h3_a = jQuery('<a />').attr({ 'href' : 'https://github.com/zoonproject/modules/blob/master/R/' + r_name,
                                    'target' : '_blank' });
    }
    var strong = jQuery('<strong />').html(module_name);
    var name_length = module_name.length;
    /* Simplistic resizing of text according to length */
    if (name_length > 20) {
      strong.css({ 'font-size' : 'small' });
    } else if (name_length > 15) {
      strong.css({ 'font-size' : 'smaller' });
    } 
    h3_a.html(strong);
    var h3_icon = jQuery('<img />').attr( { 'src' : CONTEXT_PATH + 'resources/img/' + module_type.toLowerCase() + '.png', 
                                            'title' : module_type } )
                                   .addClass('module_img');
    h3.append(h3_icon);
    h3.append('&nbsp;');
    h3.append(h3_a);

    var small = jQuery('<small />').html(' - v.' + module_version);
    h3.append(small);
    inner_div.append(h3);

    /* p1 */
    var p1 = jQuery('<p />').addClass('package-list-dateline');
    /* Usually of format YYYY-MM-DD */
    var days_ago = '';
    if (module.submitted !== undefined) {
      var submitted = module.submitted;
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
    if (module.authors !== undefined) {
      var authors = module.authors;
      if (authors.length == 0) {
        p1.append(jQuery('<span />').text('authors'));
      } else {
        var author_count = module.authors.length;
        jQuery.each(module.authors, function(index, each_author) {
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

    /* p2 */
    var p2 = jQuery('<p />').append(latex_replace(module.descriptions + ''));
    inner_div.append(p2);
    if (module.workflowed) {
      var workflow_call_img = jQuery('<img />').attr( { 'src' : CONTEXT_PATH + 'resources/img/workflow_call.png',
                                                        'title' : 'Click to view Workflow Calls referencing this Module' } )
                                               .css( { 'border' : 'solid 1px grey',
                                                       'height' : '15px',
                                                       'box-shadow' : '2px 2px 1px #888',
                                                       'float' : 'right' } )
                                               .addClass('rounded_5');
      var href = URL_PREFIX_VIEW_WORKFLOW_CALLS +
                 '/' + encodeURIComponent(module_name) +
                 '/' + encodeURIComponent(module_version) +
                 '/' + encodeURIComponent(module_type);

      var a = jQuery('<a />').attr( { 'href' : href } )
                             .html(workflow_call_img);
      inner_div.append(a);
    }

    outer_div.append(inner_div);
    modules_div.append(outer_div);

    if ((visible_count+1)%4==0) {
      modules_div.append(jQuery('<div />').addClass('clearfix visible-lg-block'));
    }
    if ((visible_count+1)%2==0) {
      modules_div.append(jQuery('<div />').addClass('clearfix visible-md-block'));
    }
    visible_count++;
  });

  /* It's down here because we're going to wait until we get the modules returned first! */
  if (module_types !== undefined && view_types === undefined) {
    ajax_for_moduletypes();
    display_module_types();
  }
}

/**
 * AJAX query for the collection of available modules.
 */
var check_count = 0;
function ajax_for_modules() {
  jQuery.ajax({
    /* CONTEXT_PATH defined in html_head/javascript.jsp */
    url: URL_AJAX_LATEST_MODULES,
    /* Wait for a response (which may be a while if loading and parsing modules after startup)*/
    async: false,
    type: 'GET',
    data: {},
    dataType: 'json',
    /* Note: No timeout as we may be loading private modules into cache from temporary store! */
    contentType: 'application/json; charset=utf-8',
    success: function(response) {
      /* retrieve_json defined in html_head/javascript.jsp */
      var json = retrieve_json(response, MODEL_ATTRIBUTE_MODULES, 'modules', js_error_results);
      if (json != undefined) {
        if (json == '[]') {
          /* Situation arises immediately post-restart when elastic is emptied awaiting 
             run-once module parsing. */
          if (check_count++ < 10) {
            setTimeout(function() { ajax_for_modules(); }, 5000);
          }
        } else {
          check_count = 0;
          all_modules = jQuery.parseJSON(json);
          display_modules();
        }
      } else {
        alert('JSON returned from querying for modules was undefined!');
      }
    },
    error: function(xhr, status, error) {
      alert(error);
    }
  });
}