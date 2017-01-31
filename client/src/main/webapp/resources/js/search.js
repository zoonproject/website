"use strict";
var search_input_el;
var cloneable_close_icon = jQuery('<div />').css({ 'background-color' : 'white',
                                                    'font-size' : '16px',
                                                    'padding-left' : '2px ',
                                                    'padding-right' : '2px',
                                                    'text-align' : 'right',
                                                    'min-width' : '250px',
                                                    'height' : '18px' })
                                             .attr({ 'title' : 'Click to close' })
                                             .html('&otimes;');

var last_search;
jQuery(document).ready(
  function() {
    /* Form located in banner.jsp */
    search_input_el = jQuery('#search');

    search_input_el.on('change keyup paste', function() {
      var search = jQuery(this);
      var new_search = search.val();
      /* Clicking on a link caused re-submission, so ignore if no change in search value */
      if (new_search != last_search) {
        last_search = new_search;
        do_search(new_search);
      }
    });
  }
);

/**
 * Query the application for search possibilities.
 * 
 * @param search_string Text to search for.
 */
function ajax_for_search_possibilities(search_string, callback) {
  var js_error = jQuery('#js_error');
  jQuery.ajax({
    url: URL_PREFIX_AJAX_SEARCH,
    async: true,
    type: 'GET',
    data: { 'searchTerm' : search_string },
    dataType: 'json',
    timeout: 6000,
    contentType: 'application/json; charset=utf-8',
    success: function(response) {
               /* retrieve_json defined in html_head/javascript.jsp */
               var json = retrieve_json(response, MODEL_ATTRIBUTE_SEARCH_RESULTS, 'searchresults',
                                        js_error);
               if (json != undefined) {
                 /* Reset the js error element and count (if it's visible) */
                 if (js_error.is(':visible')) {
                   /* Defined in html_head/javascript.jsp */
                   hide_js_error(js_error);
                 }
                 callback(jQuery.parseJSON(json));
               }
             },
    error: function(xhr, status, error) {
             /* handle_error defined in html_head/javascript.jsp */
             handle_error(error, 'searchresults', js_error);
           }
  });
}

/* var search_groups = [ 'modules', 'workflow calls', 'profiles' ]; */
var search_groups = {
  'modules' : { 'url_prefix' : URL_PREFIX_VIEW_MODULE,
                'url_postfix' : '/' + LATEST + '/false' },
  'profiles' : { 'url_prefix' : URL_PREFIX_VIEW_PROFILE_BY_IDENTITY,
                 'url_postfix' : '' },
  'workflow calls' : { 'url_prefix' : URL_PREFIX_VIEW_WORKFLOW_CALL,
                       'url_postfix' : '/' + LATEST + '/false' }
};

/**
 * Display the search results.
 * 
 * @param json
 */
function display_search_results(json) {
  var search_results_el = jQuery('#found').css( { 'max-height' : '450px',
                                                  'min-height' : '300px',
                                                  'overflow-y' : 'auto' } );
  search_results_el.empty();

  jQuery.each(search_groups, function(search_group, url_data) {
    var group_div_el = jQuery('<div />');
    var group_title_el = jQuery('<div />').text(search_group).css( { 'text-transform' : 'capitalize',
                                                                     'font-weight' : 'bold',
                                                                     'text-align' : 'center', 
                                                                     'color' : '#337ab7' });
    group_title_el.appendTo(group_div_el);
    var group_results = json[search_group];
    if (group_results !== undefined) {
      jQuery.each(group_results, function(key, obj) {
        var a = jQuery('<a />').attr( { 'href' : url_data.url_prefix + encodeURIComponent(key) + url_data.url_postfix })
                               .append(key);
        var span = jQuery('<span />');
        if (search_group == 'modules') {
          var src = '/ZOON/resources/img/' + obj + '.png';
          var img = jQuery('<img />').attr( { 'src' : src,
                                              'title' : obj.toUpperCase() })
                                     .addClass('module_img_small');
          span.append(img).append('&nbsp;');
        }
        group_div_el.append(span.append(a)).append(jQuery('<br />'));
      });
    } else {
      group_div_el.append(jQuery('<p />').text('Sorry! Nothing found'));
    }
    search_results_el.append(group_div_el);
  });
}

/**
 * Search for specified text.
 * 
 * @param search_string Text to search for.
 */
function do_search(search_string) {
  var results_div_exists = jQuery('#search_results').length;
  var results_div;
  if (results_div_exists) {
    results_div = jQuery('#search_results');
  } else {
    results_div = jQuery('<div />').attr({ 'id' : 'search_results' })
                                   .addClass('rounded_5' )
                                   .css({ 'position' : 'absolute',
                                          'background-color' : 'white',
                                          'padding' : '2px',
                                          'border' : 'double steelblue 3px' });
    var inner_div = jQuery('<div />').css( { 'padding' : '5px' });
    inner_div.append(jQuery('<div />').attr('id', 'found'));
    var close_icon = cloneable_close_icon.clone();
    close_icon.appendTo(results_div);
    inner_div.appendTo(results_div);

    close_icon.click(function(e) {
      results_div.fadeOut(200, function() {
        results_div.remove();
      });
    });
  }

  if (search_string !== undefined && search_string.trim().length > 0) {
    var position = search_input_el.position();
    jQuery('body').append(results_div);
    results_div.css( { 'top' : position.top + 50 + 'px',
                       'left' : position.left + 'px' } );
    ajax_for_search_possibilities(search_string, display_search_results);
  } else {
    if (results_div_exists) {
      results_div.remove();
    }
  }
}