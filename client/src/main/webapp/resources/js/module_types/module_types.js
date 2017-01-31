var appearance_sequence = [ 'occurrence', 'covariate', 'process', 'model', 'output' ];
var js_error_moduletypes;
var moduletypes_call_failure_count = 0;
var module_types = {};
var selectors_div;

jQuery(document).ready(
  function() {
    js_error_moduletypes = jQuery('#js_error_moduletypes');
    selectors_div = jQuery('#selectors');
  }
);

/**
 * Retrieve module types.
 */
function ajax_for_moduletypes() {
  jQuery.ajax({
    url: URL_PREFIX_AJAX_MODULETYPES,
    async: false,
    type: 'GET',
    data: {},
    dataType: 'json',
    timeout: 30000,
    contentType: 'application/json; charset=utf-8',
    success: function(response) {
               /* retrieve_json defined in html_head/javascript.jsp */
               var json = retrieve_json(response, MODEL_ATTRIBUTE_MODULETYPES, 'moduletypes',
                                        js_error_moduletypes);
               if (json != undefined) {
                 /* Reset the js error element and count (if it's visible) */
                 if (js_error_moduletypes.is(':visible')) {
                   /* Defined in html_head/javascript.jsp */
                   hide_js_error(js_error_moduletypes);
                   moduletypes_call_failure_count = 0;
                 }
                 var json_obj = jQuery.parseJSON(json);
                 module_types = json_obj;
               }
             },
    error: function(xhr, status, error) {
             /* handle_error defined in html_head/javascript.jsp */
             handle_error(error, 'moduletypes', js_error_moduletypes);
             /* Try a few times to reconnect but give up eventually! */
             if (moduletypes_call_failure_count++ < 5) {
               setTimeout(function() { ajax_for_moduletypes(); }, 6000);
             }
           }
  });
}

/**
 * Display module type images for selecting visible modules based on their type.
 */
function display_module_types() {
  var found_types = [];
  jQuery('img.module_img').each(function(idx, img_tag) {
    var module_type = jQuery(img_tag).attr('title');
    if (found_types.indexOf(module_type) == -1) {
      found_types.push(module_type);
    }
  });

  var display_order = [];
  var unrecognised = [];
  jQuery(found_types).each(function(idx, found_type) {
    var index_of = appearance_sequence.indexOf(found_type);
    if (index_of == -1) {
      unrecognised.push(found_type);
    } else {
      display_order[index_of] = found_type;
    }
  });

  var div = jQuery('<div />').css( { 'display' : 'inline-block',
                                     'font-size' : '12px' } );
  var instruction_div = jQuery('<div />').css( { 'display' : 'inline-block',
                                                 'margin-left' : '15px',
                                                 'text-align' : 'center' } );
  instruction_div.html('Click module type images<br />to show/hide modules.');
  div.append(instruction_div);
  jQuery(display_order).each(function(idx, type) {
    if (type !== undefined) {
      var module_description = module_types[type];
      var span = jQuery('<span />').text(module_description);
      var br = jQuery('<br />');
      var img = jQuery('<img />').attr( { 'id' : 'select_' + type,
                                          'src' : '/ZOON/resources/img/' + type + '.png' } )
                                 .addClass('module_img_select module_img_selected');
      var module_type_div = jQuery('<div />').css( { 'display' : 'inline-block', 'margin-left' : '15px', 'text-align' : 'center' } );
      module_type_div.append(span).append(br).append(img);

      img.click(function(e) {
        var clicked = jQuery(this);
        var id = clicked.attr('id');
        var type = id.replace('select_', '');
        if (clicked.hasClass('module_img_selected')) {
          clicked.removeClass('module_img_selected');
          clicked.css( { 'opacity' : '0.15' });
        } else {
          clicked.addClass('module_img_selected');
          clicked.css( { 'opacity' : '1.0' });
        }
        var view_types = [];
        jQuery('img.module_img_selected').each(function() {
          var selected = jQuery(this);
          var id = selected.attr('id');
          var type = id.replace('select_', '');
          view_types.push(type);
        });
        display_modules(view_types);
      });

      div.append(module_type_div);
    }
  });

  selectors_div.html(div);
}