var modules_call_failure_count = 0;
var KEY_MODULE_NAME = 'module_name';
var KEY_MODULE_VERSIONS = 'module_versions';
var modules = [];

/* In middle.jsp */
var js_error_modules;

var workflow_call_save_response;
var workflow_call_code;
var workflow_call_code_json;

var cloneable_delete_icon = jQuery('<span />').css({ 'background-color' : 'white',
                                                     'font-size' : '15px',
                                                     'padding-left' : '2px ',
                                                     'padding-right' : '2px' })
                                              .attr({ 'title' : 'Click to delete' })
                                              .html('&otimes;');

/**
 * Query the webapp for the ZOON modules for deployment in workflow calls.
 */
function ajax_for_modules() {
  jQuery.ajax({
    url: URL_AJAX_ALL_MODULES,
    async: false,
    type: 'GET',
    data: {},
    dataType: 'json',
    timeout: 30000,
    contentType: 'application/json; charset=utf-8',
    success: function(response) {
               /* retrieve_json defined in html_head/javascript.jsp */
               var json = retrieve_json(response, MODEL_ATTRIBUTE_MODULES, 'modules',
                                        js_error_modules);
               if (json != undefined) {
                 /* Reset the js error element and count (if it's visible) */
                 if (js_error_modules.is(':visible')) {
                   /* Defined in html_head/javascript.jsp */
                   hide_js_error(js_error_modules);
                   modules_call_failure_count = 0;
                 }
                 var json_obj = jQuery.parseJSON(json);
                 /* Transfer from Map structure to array to enable sorting by module name */
                 modules = [];
                 for (var module_name in json_obj) {
                   modules.push({
                     'module_name' : module_name,
                     'module_versions' : json_obj[module_name]
                   });
                 }
                 modules.sort(function(a, b) {
                   var name_a = a[KEY_MODULE_NAME];
                   var name_b = b[KEY_MODULE_NAME];
                   return (name_a < name_b) ? -1 : (name_a > name_b) ? 1 : 0;
                 });
               }
             },
    error: function(xhr, status, error) {
             /* handle_error defined in html_head/javascript.jsp */
             handle_error(error, 'modules', js_error_modules);
             /* Try a few times to reconnect but give up eventually! */
               if (modules_call_failure_count++ < 5) {
                 setTimeout(function() { ajax_for_modules(); }, 6000);
               }
             }
  });
}

/**
 * Save workflow call code.
 * 
 * @param json Workflow call code to save.
 */
function ajax_for_workflow_call_save(json) {
  var token = $("meta[name='_csrf']").attr("content");
  var header = $("meta[name='_csrf_header']").attr("content");

  // TODO: http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/csrf.html#csrf-include-csrf-token-ajax
  $(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
  });

  jQuery.ajax({
    url: AJAX_WORKFLOW_CALL_UPLOAD_TO_PRIVATE_URL,
    async: true,
    type: 'POST',
    data: JSON.stringify(json),
    dataType: 'json',
    timeout: 30000,
    contentType: 'application/json; charset=utf-8',
    success: function(response) {
               /* retrieve_json defined in html_head/javascript.jsp */
               var json = retrieve_json(response, MODEL_ATTRIBUTE_WORKFLOW_CALL_SAVE_RESPONSE, 'workflowcallsave',
                                        workflow_call_save_response);
               if (json != undefined) {
                 var jsonObj = jQuery.parseJSON(json);
                 workflow_call_save_response.html(jsonObj.job);
                 setTimeout(function() { workflow_call_save_response.empty(); }, 3000);
               }
             },
    error: function(xhr, status, error) {
             workflow_call_save_response.html('&nbsp; <span class="error">error! ' + error + "</span>");
           }
  });
}

var action_chained = 'Chain';
var action_listed = 'list';
var action_selected = 'select';
var action_sequence = [ action_selected, action_listed, action_chained ];

var buttons = jQuery('<div />');
var cloneable_button = jQuery('<button />').attr({ 'type' : 'button' });
var button_select = cloneable_button.clone();
button_select.addClass('button_' + action_selected)
             .appendTo(buttons)
             .text(' select ');
var button_list = cloneable_button.clone();
button_list.addClass('button_' + action_listed)
           .appendTo(buttons)
           .text(' list ');
var button_chain = cloneable_button.clone();
button_chain.addClass('button_' + action_chained)
            .appendTo(buttons)
            .text(' Chain ');

if (!String.prototype.encodeHTML) {
  String.prototype.encodeHTML = function () {
    return this.replace(/&/g, '&#x26;')
               .replace(/</g, '&#x3c;')
               .replace(/>/g, '&#x3e;')
               .replace(/"/g, '&#x22;')
               .replace(/'/g, '&#x27;');
  };
}

/**
 * Convert a multi-valued JSON object to a line-break- delimited and escaped string.
 * 
 * @param multi_valued JSON object.
 * @returns String representation.
 */
function mv_to_string(multi_valued) {
  var string_value = '';
  if (multi_valued != undefined) {
    var value_count = multi_valued.length;
    jQuery.each(multi_valued, function(idx, new_value) {
      new_value = new_value.replace('\@\@', '\@');
      new_value = new_value.encodeHTML();
      string_value += new_value;
      if (idx != value_count - 1) {
        string_value += '<br />';
      } 
    });
  }
  return string_value;
}

/**
 * Convert a single-valued object to an escaped string.
 * 
 * @param multi_valued JSON object.
 * @returns String representation.
 */
function sv_to_string(value) {
  var string_value = '';
  if (value != undefined) {
    var new_value = value.replace('\@\@', '\@');
    new_value = new_value.encodeHTML();
    string_value = new_value;
  }
  return string_value;
}

var function_call = 'workflow(';
var pad_str = Array(function_call.length + 1).join(' ');

/**
 * Display an existing workflow call based on a JSON representation
 * 
 * @param workflow_call_json Workflow Call JSON object.
 */
function display_existing(workflow_call_json) {
  var workflow_call_json_obj = workflow_call_json.workflow;
  var forcing_reproducible = workflow_call_json_obj.forceReproducible + '';
  if (forcing_reproducible.toUpperCase() == 'TRUE') {
    jQuery('#force_reproducible').attr('checked', 'checked');
  } else {
    jQuery('#force_reproducible').removeAttr('checked');
  }

  /* Var call_argument would be a module type, e.g. occurrence, forceReproducible, model, etc */
  for (var call_argument in workflow_call_json_obj) {
    if ('forceReproducible' != call_argument) {
      /* Handle module type data. */
      var module_type = call_argument;
      var selected_modules = workflow_call_json_obj[module_type];
      jQuery.each(selected_modules, function(action, modules) {
        /* Select each module in the module type */
        jQuery.each(modules, function(module_name, params_and_version) {
          var module_version = params_and_version[MODULE_VERSION_JSON_KEY];
          if (module_version === undefined) {
            /* Historic workflow call where module has no version associated with it! */
            var module_count = jQuery('div#' + module_type).find('li[data-module-name="' + module_name + '"]').length;
            switch (module_count) {
              case 0 :
                alert('Workflow call (missing module version) for unknown module ' + module_name + ' of type ' + module_type + '!');
                break;
              case 1 :
                /* Just match on the module name */
                jQuery('div#' + module_type).find('li[data-module-name="' + module_name + '"]').addClass('ui-selected');
                break;
              default :
                alert('Workflow call (missing module version) for module ' + module_name + ' of type ' + module_type + ' has more than one version available!');
                break;
            };
          } else {
            jQuery('div#' + module_type).find('li[data-module-name="' + module_name + '"][data-module-version="' + module_version + '"]').addClass('ui-selected');
          }
        });
        /* Emulate someone clicking the action button */
        jQuery('div#w' + module_type).find('button.button_' + action).click();
        /* Emulate someone mouse-moving in and out of the parameters */
        jQuery.each(modules, function(module_name, params_and_version) {
          var module_version = params_and_version[MODULE_VERSION_JSON_KEY];
          if (module_version === undefined) {
            /* Historic workflow call where module has no version associated with it! */
            /* There's no parameter data associated with historical -- ignore! */
          } else {
            jQuery('div#' + module_type).find('li[data-module-name="' + module_name + '"][data-module-version="' + module_version + '"] img').each(function() {
              /* Create the corresponding elements which mouseenter generate, e.g. info div */
              jQuery(this).trigger('mouseenter');
              jQuery(this).trigger('mouseleave');

              jQuery.each(params_and_version, function(param_name, param_value) {
                if (MODULE_VERSION_JSON_KEY != param_name) {
                  jQuery('input.param_input[data-module-name="' + module_name + '"][data-module-version="' + module_version + '"][data-param-name="' + param_name + '"]').val(param_value);
                }
              });
            });
          }
        });
        /* action_chained defined in create.js */
        if (action_chained == action) {
          /* For situations where the action is Chain we potentially need to reorder */
          var parent_ul = jQuery('#sortable_' + module_type);
          var module_idx = 0;
          jQuery.each(modules, function(module_name, params_and_version) {
            var module_version = params_and_version[MODULE_VERSION_JSON_KEY];
            var named_li;
            if (module_version === undefined) {
              /* Historic workflow call where module has no version associated with it!
                 NOTE : There are two spans with identical data-module-name / data-module-version
                        one for the module name, the other for the 'delete' icon. */
              var module_count = parent_ul.find('li span[data-module-name="' + module_name + '"]:first').length;
              if (module_count == 1) {
                /* Find the module's li (by the span's full name and subsequent parent li!) */
                named_li = parent_ul.find('li span[data-module-name="' + module_name + '"]').closest('li');
              } else {
                /* Somehow the Chain button emulated click hasn't chosen the module of that name! */
                alert('Workflow call has no version data for module ' + module_name + '!');
              }
            } else {
              /* Find the module's li (by the span's full module name and version and subsequent parent li!)
                 NOTE : There are two spans! */
              named_li = parent_ul.find('li span[data-module-name="' + module_name + '"][data-module-version="' + module_version + '"]').closest('li');
            }
            /* Determine the current position of module's li */
            var named_pos = parent_ul.children('li').index(named_li);
            /* Check if there's a difference between the module's current position and it's indexed position */
            if (module_idx != named_pos) {
              if (module_idx == 0) {
                /* If it should be at the start of the list */
                parent_ul.prepend(named_li);
              } else {
                /* Otherwise swap it with the currently indexed position */
                named_li.exchangePositionWith(parent_ul.find('li:eq(' + module_idx + ')'));
              }
            }
            module_idx++;
          });
        }
      });
    }
  }
}

/* http://stackoverflow.com/questions/4612955/jquery-how-to-move-a-li-to-another-position-in-the-ul-exchange-2-lis */
jQuery.fn.exchangePositionWith = function(selector) {
  var other = $(selector);
  /* Clone events as well! */
  this.after(other.clone(true));
  other.after(this).remove();
};

/**
 * Retrieve a javascript object representation of the module parameter values.
 * 
 * @param full_name E.g. CarolinaWrenPO.
 * @returns Javascript representation e.g. { "occurrenceType" : "asdf",
 *                                           "fish" : "chips" } , or empty no param values.
 */
function grab_params_as_obj(module_name, module_version) {
  var param_obj = {};
  jQuery('input[data-module-name="' + module_name + '"][data-module-version="' + module_version + '"]').each(function() {
    var input = jQuery(this); 
    var input_value = input.val();
    var param_name = input.attr('data-param-name');
    if (input_value != '') {
      param_obj[param_name] = input_value;
    }
  });
  param_obj[MODULE_VERSION_JSON_KEY] = module_version;
  return param_obj;
}

/**
 * Retrieve a textual representation of the module parameter values.
 * 
 * @param module_name E.g. CarolinaWrenPO.
 * @param module_version Module version.
 * @returns Textual representation e.g. (occurrenceType=asdf, fish=chips), or empty no param values.
 */
function grab_params_as_text(module_name, module_version) {
  var param_texts = [];
  jQuery('input[data-module-name="' + module_name + '"][data-module-version="' + module_version + '"]').each(function() {
    var input = jQuery(this); 
    var input_value = input.val();
    var param_name = input.attr('data-param-name');
    if (input_value != '') {
      param_texts.push(param_name + ' = ' + input_value);
    }
  });
  var param_text = '';
  if (param_texts.length > 0) {
    param_text = '(' + param_texts.join(', ') + ')';
  }
  return param_text;
}

/**
 * Generate workflow call.
 * 
 * @param return_json Instruction to return from function the workflow call in JSON format or not.
 * @returns Workflow call in JSON format if 'return_json' is {@code true}, otherwise nothing.
 */
function generate_workflow_call(return_json) {
  workflow_call_code.empty();
  workflow_call_code_json.empty();

  /* Generate the workflow call text */
  var workflow_call_lines = [];
  var workflow_call_json = {};

  for (var module_idx = 0; module_idx < appearance_sequence.length; module_idx++) {
    var module_type = appearance_sequence[module_idx];
    var module_div = jQuery('#w' + module_type);

    workflow_call_json[module_type] = {};

    for (var action_idx = 0; action_idx < action_sequence.length; action_idx++) {
      var action = action_sequence[action_idx];
      /* Find by class */
      var found = module_div.find('.' + action);
      if (found.length > 0) {
        workflow_call_json[module_type][action] = {};

        var line_text;
        var multi_choice = (action == action_chained || action == action_listed);
        if (multi_choice) {
          line_text = module_type.toLowerCase() + ' = ' + action + '(';
          var lines = [];
          found.each(function() {
            /* Two spans in the display, first contains the name, second the 'close' option */
            var span = jQuery(this).find('span:first');
            var data_module_name = span.attr('data-module-name');
            var data_module_version = span.attr('data-module-version');

            var text = data_module_name + grab_params_as_text(data_module_name, data_module_version);

            if (text.length > 0) {
              lines.push(text);
              workflow_call_json[module_type][action][data_module_name] = grab_params_as_obj(data_module_name,
                                                                                             data_module_version);
            }
          });
          line_text += lines.join(', ') + ')';
        } else {
          /* Two spans in the display, first contains the name, second the 'close' option */
          var span = found.find('span:first');
          var data_module_name = span.attr('data-module-name');
          var data_module_version = span.attr('data-module-version');

          var param_text = grab_params_as_text(data_module_name, data_module_version);

          line_text = module_type.toLowerCase() + ' = ' + data_module_name + param_text;
          workflow_call_json[module_type][action][data_module_name] = grab_params_as_obj(data_module_name,
                                                                                         data_module_version);
        }
        if (module_idx > 0) {
          /* First line shows 'workflow' */
          line_text = pad_str + line_text;
        }
        line_text += ',';

        workflow_call_lines.push(line_text);
        break;
      }
    }
  }

  var forcing_reproducible = jQuery('#force_reproducible').is(':checked') + '';
  var uc_forcing_reproducible = forcing_reproducible.toUpperCase();
  workflow_call_lines.push(pad_str + 'forceReproducible = ' + uc_forcing_reproducible);
  workflow_call_json['forceReproducible'] = uc_forcing_reproducible;

  /* + 1 to allow for the forceReproducible line */
  if (workflow_call_lines.length == appearance_sequence.length + 1) { 
    /* If the user has specified values for each of the occurrence, covariate, process, etc. */
    workflow_call_code.addClass('workflow_call_background_colored');
  } else {
    workflow_call_code.removeClass('workflow_call_background_colored');
  }

  workflow_call_code.text(function_call + workflow_call_lines.join('\n') + ')');
  workflow_call_code_json.text(JSON.stringify(workflow_call_json));

  if (return_json) {
    return workflow_call_json;
  }
}

/**
 * Validate the workflow call.
 * 
 * @param workflow_call Workflow call to validate.
 */
function validate_workflow_call(workflow_call) {
  return true;
}

jQuery(document).ready(
  function() {
    js_error_moduletypes = jQuery('#js_error_moduletypes');
    js_error_modules = jQuery('#js_error_modules');

    var module_types_div = jQuery('#module_types');
    var selection_div = jQuery('#selection');
    // var generate_button = jQuery('#button_generate');
    workflow_call_code = jQuery('#workflow_call_code');
    workflow_call_code_area = jQuery('#workflow_call_code_area');
    workflow_call_code_json = jQuery('#workflow_call_code_json');
    var save_workflow_call_button = jQuery('#save_workflow_call');
    workflow_call_save_response = jQuery('#workflow_call_save_response');

    ajax_for_moduletypes();
    var type_div_sequence = [];
    var selection_div_sequence = [];
    var ordered_list_ids = [];
    jQuery.each(module_types, function(module_type, module_description) {
      var type_div = jQuery('<div />').attr({ 'id' : module_type })
                                      .addClass('float_left monospace rounded_5 module_data');
      jQuery('<p />') /* .attr({ 'title' : module_description }) */
                     .css({ 'font-weight' : 'bold',
                            'text-align' : 'center',
                            'padding' : '5px', 
                            'background-color' : 'lightgray' })
                     .addClass('rounded_5')
                     .text(module_type)
                     .appendTo(type_div);
      var ordered_list_id = 'selectable_' + module_type;
      ordered_list_ids.push(ordered_list_id);
      jQuery('<ol>').attr({ 'id' : ordered_list_id })
                    .appendTo(type_div);

      var module_new_div_id = 'w' + module_type;
      var module_new_div = jQuery('<div />').attr({ 'id' : module_new_div_id })
                                            .css({ 'border' : 'solid steelblue 1px',
                                                   'padding' : '5px',
                                                   'margin-left' : '5px' })
                                            .addClass('float_left monospace rounded_5 module_data');
      jQuery('<p />').css({ 'font-weight' : 'bold',
                            'text-align' : 'center', 
                            'padding' : '5px', 
                            'background-color' : 'lightgray' })
                     .addClass('rounded_5')
                     .html(buttons.html())
                     .appendTo(module_new_div);

      var position = appearance_sequence.indexOf(module_type);
      type_div_sequence[position] = type_div;
      selection_div_sequence[position] = module_new_div;
    });
    jQuery.each(type_div_sequence, function(index, type_div) {
      type_div.appendTo(module_types_div);
    });
    jQuery.each(selection_div_sequence, function(index, workflow_div) {
      workflow_div.appendTo(selection_div);
    });

    jQuery('<div />').addClass('clearing')
                     .insertAfter(module_types_div.children(':last'));
    jQuery('<div />').addClass('clearing')
                     .insertAfter(selection_div.children(':last'));

    ajax_for_modules();

    jQuery.each(modules, function(module_idx, module) {
      var module_name = module[KEY_MODULE_NAME];
      var module_versions = module[KEY_MODULE_VERSIONS];
      artifact_sorter(module_versions);
      jQuery.each(module_versions, function(index, version_data) {
        /* Sample ModuleVO JSON structure ....
        {
          "privateArtifact": false,
          "name": "AirNCEP",
          "version": "1.0",
          "type": "MODULE",
          "moduleType": "covariate",
          "parameters": {
            "quiet": "Logical. If TRUE (default) the progress of downloads is not shown"
          },
          "returnValues": [ ],
          "location": "ZOON copy on fs",
          "source": null,
          "submitted": "2015-11-13",
          "authors": [
            {
              "authorName": "ZOON Developers",
              "email": "zoonproject@gmail.com",
              "nameURL": "ZOON%20Developers"
            }
          ],
          "descriptions": [
            "Covariate module to grab a coarse resolution mean air temperature raster from\n January-February 2001-2002 for the UK."
          ],
          "references": "",
          "content": null,
          "verified": true,
          "latest": true,
          "nameURL": "AirNCEP"
        }
       */
        var authors_array = [];
        jQuery.each(version_data.authors, function(author_idx, author_details) {
          authors_array.push(author_details.authorName + "; " + author_details.email);
        });
        var mv_author = mv_to_string(authors_array);
        // UKAnophelesPlumbeus has latex in module description.
        var mv_description = latex_replace(mv_to_string(version_data.descriptions));
        var sv_location = version_data.location;
        var p_param = version_data.parameters;
        var mv_return = mv_to_string(version_data.returnValues);
        var sv_type = version_data.moduleType;
        var sv_version = version_data.version;
        var sv_references = version_data.references;
        var private_artifact = version_data.privateArtifact;

        if (sv_type != undefined) {
          var sv_type_div = '#' + sv_type;
          var target_div = jQuery(sv_type_div);
          if (target_div != undefined) {
            var parent_ol = jQuery('#selectable_' + sv_type);

            var info_img = jQuery('<img />').attr( { 'src' : INFO_IMG_SRC });

            var unique_id = module_name + sv_version;
            /* info_div's are the bits that pop up on mouseover of the info image */
            var info_div = jQuery('<div />').attr({ 'id' : unique_id })
                                            .addClass('module_info fs_9')
                                            .css('position', 'absolute');

            info_div.append(jQuery('<p />').html(jQuery('<span />').text('Name : ').addClass('preformatted bold'))
                                           .append(module_name));
            info_div.append(jQuery('<p />').html(jQuery('<span />').text('Description : ').addClass('preformatted bold'))
                                           .html(mv_description));
            if (p_param != undefined && p_param != 'undefined' && Object.keys(p_param).length > 0) {
              var params_table = jQuery('<table>');
              jQuery.each(p_param, function(name, description) {
                var param_tr = jQuery('<tr />');
                var param_name = sv_to_string(name);
                param_tr.append(jQuery('<td />').addClass('preformatted italic')
                                                .css('border', 'solid 1px black')
                                                .append(param_name + ' '));
                description = latex_replace(description);
                var param_input = jQuery('<input />').addClass('param_input')
                                                     .attr( { 'type' : 'text',
                                                              'data-module-name' : module_name,
                                                              'data-module-version' : sv_version,
                                                              'data-param-name' : param_name } );
                var param_p = jQuery('<p />').css( { 'display' : 'none' } )
                                             .append(param_input);
                var description_td = jQuery('<td />').css('border', 'solid 1px black')
                                                     .append(description)
                                                     .append(param_p);
                param_tr.append(description_td);
                params_table.append(param_tr);
              });
              info_div.append(jQuery('<p />').html(jQuery('<span />').text('Params : ').addClass('preformatted bold'))
                                             .append(params_table));
            }
            if (mv_return != '') {
              info_div.append(jQuery('<p />').html(jQuery('<span />').text('Return : ').addClass('preformatted bold'))
                                             .append(mv_return));
            }
            info_div.append(jQuery('<p />').html(jQuery('<span />').text('Version : ').addClass('preformatted bold'))
                                           .append(sv_version));
            info_div.append(jQuery('<p />').html(jQuery('<span />').text('Author : ').addClass('preformatted bold'))
                                           .append(mv_author));
            info_div.append(jQuery('<p />').html(jQuery('<span />').text('Location : ').addClass('preformatted bold'))
                                           .append(sv_location));
            if (sv_references != '') {
              info_div.append(jQuery('<p />').html(jQuery('<span />').text('References : ').addClass('preformatted bold'))
                                             .append(sv_references));
            }

            /* Allow the user to move the mouse over the div from the info image and leave it
               visible so long as the mouse is over the div.
               Under normal circumstances moving the mouse away from the info img would cause the
               div to disappear immediately. Instead of instant disappearance, a timeout is set 
               which makes it disappear, but if a mouseenter over the div happens, remove the
               timeout so that it doesn't disappear until mouseleave! */
            info_div.mouseenter(function() {
              clearTimeout($(this).data('timeoutId'));
            });
            info_div.mouseleave(function() {
              $(this).hide();
            });

            var display_name = module_name.length > 21 ? module_name.substring(0, 20) + '..' : module_name;
            var new_module = jQuery('<li />').addClass('ui-widgit-content')
                                             .attr({
                                               'data-module-name' : module_name,
                                               'data-module-version' : sv_version
                                              })
                                             .css({ 'font-size' : '10px' }) 
                                             .html(display_name + ' <span style="font-size: xx-small">[' + sv_version + ']</span>')
                                             .append('&nbsp;')
                                             .append(info_img);

            info_img.mouseenter(function(e) {
              if (!info_div.is(':visible')) {
                jQuery('body').append(info_div);
                info_div.css( { 'top' : (e.clientY) + 'px',
                                'left' : (e.clientX) + 'px' } );
                /* Display <input> elements according to whether module is selected or not */
                var input_ps = info_div.find('input').parent('p');
                new_module.hasClass('ui-selected') ? input_ps.show() : input_ps.hide();
                info_div.show();
              }
            });
            info_img.mouseleave(function() {
              if (info_div.is(':visible')) {
                var timeout_id = setTimeout(function() { info_div.hide(); }, 300);
                info_div.data('timeoutId', timeout_id);
              }
            });

            new_module.appendTo(parent_ol);
            if (private_artifact) {
              var private_indicator = jQuery('<span />').css({ 'background-color' : '#faa',
                                                               'padding' : '1px',
                                                               'font-weight' : 'bold' })
                                                        .html('*');
              new_module.prepend('&nbsp;').prepend(private_indicator);
            }
          }
        }
      });
    });

    jQuery.each(ordered_list_ids, function(index, ordered_list_id) {
      jQuery('#' + ordered_list_id).selectable();
    });

    save_workflow_call_button.click(function(e) {
      workflow_call_save_response.html(timer);
      var workflow_call = generate_workflow_call(true);
      if (validate_workflow_call(workflow_call)) {
        ajax_for_workflow_call_save( {
          'name' : jQuery('#workflow_call_name').val(),
          'description' : jQuery('#workflow_call_description').val(),
          'version' : jQuery('#workflow_call_version').val(),
          'workflow' : workflow_call });
      } else {
        workflow_call_save_response.html('Sorry! Workflow Call failed the validation test!');
      }
      e.preventDefault();
    });

    /* Update the R code when the checkbox changes. */
    jQuery('#force_reproducible').on('change', function(event, ui) {
      generate_workflow_call();
    });

    jQuery('button[class^=button_]').click(function() {
      /* As soon as a user presses a button do some resetting. */
      if (workflow_call_code.hasClass('workflow_call_background_colored')) {
        workflow_call_code.removeClass('workflow_call_background_colored');
      }
      workflow_call_code.empty();

      var message_div = jQuery('#message');
      message_div.empty();

      var clicked = jQuery(this);
      /* e.g. wOCCURRENCE */
      var parent_div = clicked.closest('div');
      /* Reset any previously highlighted buttons for this module type. */
      parent_div.find('button[class^=button_]').removeClass('workflow_call_module_type_button_colored');
      var parent_div_id = parent_div.attr('id');
      /* e.g. OCCURRENCE */
      var module_type = parent_div_id.substring(1);
      /* e.g. button_select */
      var clicked_class = clicked.attr('class');
      /* e.g. select */
      var action = clicked_class.split('_')[1];

      /* Clear current content */
      parent_div.find('p:first').nextAll().remove();

      var selectable_ol_id = 'selectable_' + module_type;
      /* Find the (first) selected */
      var selected = jQuery('#' + selectable_ol_id).find('li[class*=ui-selected]');
      var selected_count = selected.length;
      switch (selected_count) {
        case 0:
          message_div.text('Nothing selected!');
          return;
        case 1:
          if (action == action_listed || action == action_chained) {
            message_div.text('The \'' + action + '\' command requires more than one selection as input');
            return;
          }
          break;
        default:
          if (action == action_selected) {
            message_div.text('The \'' + action + '\' command is for a single module! Changing to \'' + action_listed + '\'');
            action = action_listed;
          }
      }

      /* Highlight the selected (or adjusted) button */
      parent_div.find('button.button_' + action).addClass('workflow_call_module_type_button_colored');

      if (!workflow_call_code_area.is(':visible')) {
        workflow_call_code_area.show();
      }

      if (action == action_chained) {
        /* Make into sortable */
        var sortable_ul_id = 'sortable_' + module_type;
        var sortable_list = jQuery('<ul />').attr({ 'id' : sortable_ul_id });
        selected.each(function() {
          var each_selected = jQuery(this); 
          var data_module_name = each_selected.attr('data-module-name');
          var data_module_version = each_selected.attr('data-module-version');
          var module_text = each_selected.text();

          var li = jQuery('<li />').addClass('ui-state-default rounded_3 ' + action_chained)
                                   .appendTo(sortable_list);

          /* Append the module name */
          jQuery('<span />').css( { 'font-size' : '10px',
                                    'width' : '175px',
                                    'display' : 'inline-block',
                                    'text-align' : 'left' } )
                            .attr( { 'data-module-name' : data_module_name,
                                     'data-module-version' : data_module_version } )
                            .text(module_text)
                            .appendTo(li);

          /* Now append the delete icon */
          var delete_icon = cloneable_delete_icon.clone();
          delete_icon.attr( { 'data-module-name' : data_module_name,
                              'data-module-version' : data_module_version } ) 
                     .appendTo(li);
          delete_icon.click(function(e) {
            var clicked = jQuery(this);
            var clicked_module_name = clicked.attr('data-module-name');
            var clicked_module_version = clicked.attr('data-module-version');
            var attr_set = 'li[data-module-name="' + clicked_module_name + '"][data-module-version="' + clicked_module_version + '"]';
            var original = jQuery('#selectable_' + module_type).find(attr_set);
            /* Unhighlight the module the user originally clicked on but now wants removing */
            original.removeClass('ui-selected');
            /* Remove the parent li of the span which the user clicked on */
            clicked.closest('li').remove();
            /* Determine how many choices are left */
            var new_selected_count = jQuery('#' + selectable_ol_id).find('li[class*=ui-selected]').length;
            if (new_selected_count == 1) {
              /* Emulate user clicking the 'select' button */
              jQuery('div#w' + module_type).find('button.button_' + action_selected).click();
            } else if (new_selected_count == 0) {
              parent_div.find('button').removeClass('workflow_call_module_type_button_colored');
            }
            /* Repaint the workflow call */
            generate_workflow_call();
          });
        });
        parent_div.append(sortable_list);
        sortable_list.sortable();
        sortable_list.disableSelection();
        sortable_list.on('sortupdate', function(event, ui) {
          generate_workflow_call();
        });
      } else {
        selected.each(function() {
          var each_selected = jQuery(this);
          var data_module_name = each_selected.attr('data-module-name');
          var data_module_version = each_selected.attr('data-module-version');
          var module_text = each_selected.text();

          var action_nature = (selected_count == 1 ? action_selected : action_listed);

          var p = jQuery('<p />').css( { 'background-color' : 'lightblue',
                                        'padding' : '6px' } )
                                 .addClass('rounded_3 ' + action_nature)
                                 .appendTo(parent_div);

          jQuery('<span />').css({ 'font-size' : '10px',
                                   'width' : '175px',
                                   'display' : 'inline-block' })
                            .attr( { 'data-module-name' : data_module_name,
                                     'data-module-version' : data_module_version } )
                            .text(module_text)
                            .appendTo(p);

          var delete_icon = cloneable_delete_icon.clone();
          delete_icon.attr( { 'data-module-name' : data_module_name,
                              'data-module-version' : data_module_version } )
                     .appendTo(p);
          delete_icon.click(function(e) {
            var clicked = jQuery(this);
            /* Unhighlight the module the user originally clicked on but now wants removing */
            var clicked_module_name = clicked.attr('data-module-name');
            var clicked_module_version = clicked.attr('data-module-version');
            var attr_set = 'li[data-module-name="' + clicked_module_name + '"][data-module-version="' + clicked_module_version + '"]';
            var original = jQuery('#selectable_' + module_type).find(attr_set);
            /* Unhighlight the module the user originally clicked on but now wants removing */
            original.removeClass('ui-selected');
            /* Remove the parent p of the span which the user clicked on */
            clicked.closest('p').remove();
            /* Determine how many choices are left */
            var new_selected_count = jQuery('#' + selectable_ol_id).find('li[class*=ui-selected]').length;
            if (new_selected_count == 1) {
              /* Emulate user clicking the 'select' button */
              jQuery('div#w' + module_type).find('button.button_' + action_selected).click();
            } else if (new_selected_count == 0) {
              parent_div.find('button').removeClass('workflow_call_module_type_button_colored');
            }
            /* Repaint the workflow call */
            generate_workflow_call();
          });
        });
      }

      generate_workflow_call();
    });

    instantiate_workflow_call();

    var modify_workflow_call_form = jQuery('#modify_workflow_call');
    if (modify_workflow_call_form !== undefined) {
      var completed_href = modify_workflow_call_form.attr('action') + '/' + jQuery('#workflow_call_name').val();
      modify_workflow_call_form.attr('action', completed_href);
    }
  }
);