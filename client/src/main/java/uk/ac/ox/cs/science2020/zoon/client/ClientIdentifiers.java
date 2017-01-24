/*

  Copyright (c) 2017, University of Oxford.
  All rights reserved.

  University of Oxford means the Chancellor, Masters and Scholars of the
  University of Oxford, having an administrative office at Wellington
  Square, Oxford OX1 2JD, UK.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
   * Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice,
     this list of conditions and the following disclaimer in the documentation
     and/or other materials provided with the distribution.
   * Neither the name of the University of Oxford nor the names of its
     contributors may be used to endorse or promote products derived from this
     software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package uk.ac.ox.cs.science2020.zoon.client;

/**
 * System-wide identifiers.
 *
 * @author geoff
 */
public class ClientIdentifiers {

  /* 
   * The following generally result in some modification to the system. For non-modification 
   * activities see RENDER_
   */
  public static final String ACTION_ARTIFACT_VERIFY = "/artifact_verify";
  public static final String ACTION_IDENTITY_CREATE = "/identity_create";
  public static final String ACTION_MODULE_DELETE = "/module_delete";
  public static final String ACTION_MODULE_UPLOAD_TO_PRIVATE = "/module_upload_to_private";
  public static final String ACTION_MODULE_UPLOAD_TO_ZOON = "/module_upload_to_ZOON";
  public static final String ACTION_UPLOAD_WORKFLOW_CALL_TO_PRIVATE = "/workflow_call_upload_to_private";
  public static final String ACTION_USER_DELETING = "/user_deleting/";
  public static final String ACTION_USER_ENABLING = "/user_enabling/";
  public static final String ACTION_USER_REGISTER = "/user_register";

  /**
   * Log out URL.
   * <p>
   * <b>Note</b> Prefix important. Update appCtx.root-security.xml if modified.
   */
  public static final String LOGOUT = "/logout";

  /**
   * Component name for the Business Manager WS WSS4J security interceptor
   * <p>
   * See also {@code (sample.)appCtx.ws.security-outgoing.xml}.
   */
  public static final String COMPONENT_BUS_MANAGER_SERVICES_INTERCEPTOR = "wsBusManagerServicesInterceptor";
  public static final String COMPONENT_BUSINESS_SERVICES_PROXY = "businessServicesProxy";
  public static final String COMPONENT_BUSINESS_MANAGER_MANAGER = "businessManagerManager";
  public static final String COMPONENT_CLIENT_SERVICE = "clientService";
  public static final String COMPONENT_MAIL_MANAGER = "mailManager";
  // see appCtx.mail.xml
  public static final String COMPONENT_MAIL_REGISTRATION_TEMPLATE = "templateRegistrationMail";
  // see appCtx.mail.xml
  public static final String COMPONENT_MAIL_SENDER = "mailSender";
  public static final String COMPONENT_REPOSITORY_MANAGER = "repositoryManager";
  public static final String COMPONENT_USER_MANAGER = "userManager";
  public static final String COMPONENT_USER_DAO = "userDAO";

  public static final String ERROR_BUS_MGR_OUT_OF_CONTACT = "error.business_manager_out_of_contact";
  public static final String ERROR_BUS_MGR_SOMETHING_WRONG = "error.business_manager_something_wrong";

  /**
   * For drawing in additional resources, e.g. .js files, at runtime.
   */
  public static final String I18N_LANGS = "es,zh"; // TODO : Make configurable.

  /**
   * These two key values correspond to the data structure returned by {@linkplain General}
   * controller
   */
  public static final String KEY_JSON = "json";
  public static final String KEY_EXCEPTION = "exception";

  public static final String MODEL_ATTRIBUTE_CREATE_IDENTITY = "ma_create_identity";
  public static final String MODEL_ATTRIBUTE_DELETE_MODULE = "ma_delete_module";
  public static final String MODEL_ATTRIBUTE_EMAIL_ADDRESS = "ma_email_address";
  public static final String MODEL_ATTRIBUTE_MESSAGE_ERROR = "ma_message_error";
  public static final String MODEL_ATTRIBUTE_MESSAGE_INFO = "ma_message_info";
  public static final String MODEL_ATTRIBUTE_MODULETYPES = "ma_moduletypes";
  public static final String MODEL_ATTRIBUTE_MODULE = "ma_module";
  public static final String MODEL_ATTRIBUTE_MODULES = "ma_modules";
  /** Model attribute for private modules. */
  public static final String MODEL_ATTRIBUTE_PRIVATE_MODULES = "ma_private_modules";
  public static final String MODEL_ATTRIBUTE_PUBLICISE_MODULE = "ma_publicise_module";
  public static final String MODEL_ATTRIBUTE_REGISTRATION_ERROR = "ma_registration_error";
  public static final String MODEL_ATTRIBUTE_REGISTRATION_SUCCESS = "ma_registration_success";
  public static final String MODEL_ATTRIBUTE_SEARCH_RESULTS = "ma_search_results";
  public static final String MODEL_ATTRIBUTE_UPLOAD_MODULE = "ma_upload_module";
  public static final String MODEL_ATTRIBUTE_UPLOAD_MODULE_TO_ZOON = "ma_upload_module_to_ZOON";
  public static final String MODEL_ATTRIBUTE_USER = "ma_user";
  public static final String MODEL_ATTRIBUTE_USER_CURRENTLY_LOGGED_IN = "ma_user_currently_logged_in";
  public static final String MODEL_ATTRIBUTE_USER_DELETED = "ma_user_deleted";
  public static final String MODEL_ATTRIBUTE_USERS = "ma_users";
  public static final String MODEL_ATTRIBUTE_VERIFICATION_OUTPUT = "ma_verification_output";
  public static final String MODEL_ATTRIBUTE_VERIFY_MODULE = "ma_verify_module";
  public static final String MODEL_ATTRIBUTE_WORKFLOW_CALL_SAVE_RESPONSE = "ma_workflow_call_save_response";
  public static final String MODEL_ATTRIBUTE_WORKFLOW_CALL = "ma_workflow_call";
  public static final String MODEL_ATTRIBUTE_WORKFLOW_CALLS = "ma_workflow_calls";

  /*
   * Update tiles.xml if any of these properties change.
   */
  public static final String PAGE_ABOUT = "page_about";
  public static final String PAGE_CONTACT = "page_contact";
  public static final String PAGE_INDEX = "page_index";
  public static final String PAGE_LOGIN = "page_login";
  public static final String PAGE_MODULE = "page_module";
  public static final String PAGE_MODULE_MANAGE = "page_module_manage";
  public static final String PAGE_MODULE_UPLOAD_ZOON = "page_module_upload_ZOON";
  public static final String PAGE_MODULE_VERIFY = "page_module_verify";
  public static final String PAGE_MODULES = "page_modules";
  public static final String PAGE_PRIVACY = "page_privacy";
  public static final String PAGE_TUTORIALS = "page_tutorials";
  public static final String PAGE_USER_MANAGE = "page_user_manage";
  public static final String PAGE_USER_PROFILE = "page_user_profile";
  public static final String PAGE_USER_REGISTER = "page_user_register";
  public static final String PAGE_WORKFLOW_CALL = "page_workflow_call";
  public static final String PAGE_WORKFLOW_CALLS = "page_workflow_calls";

  /*
   * Parameter names.
   */
  public static final String PARAM_NAME_ARTIFACT_DATA = "artifact_data";
  public static final String PARAM_NAME_EMAIL_ADDRESS = "email_address";
  public static final String PARAM_NAME_MODULE_NAME = "module_name";
  public static final String PARAM_NAME_PROFILE_IDENTITY = "profile_identity";
  public static final String PARAM_NAME_USER_EMAIL = "user_email";
  public static final String PARAM_NAME_USER_ENABLING = "user_enabling";
  public static final String PARAM_NAME_USER_IDENTITY = "user_identity";
  public static final String PARAM_NAME_USER_NAME = "user_name";
  public static final String PARAM_NAME_USER_PWD = "user_pwd";
  public static final String PARAM_NAME_VERIFICATION_IDENTIFIER = "verification_identifier";

  /*
   * The following generally result in no modification, usually just retrieval of existing data. For
   * modification activities see ACTION_
   */
  public static final String RENDER_LOGIN = "/login";
  public static final String RENDER_MODULE_TYPES_RETRIEVE = "/module_types_retrieve";
  public static final String RENDER_MODULES_RETRIEVE = "/modules_retrieve";
  public static final String RENDER_MODULES_RETRIEVE_LATEST = "/modules_retrieve_latest";
  public static final String RENDER_MODULES_RETRIEVE_PRIVATE = "/modules_retrieve_private";
  public static final String RENDER_MODULES_RETRIEVE_PRIVATE_VERIFIED = "/modules_retrieve_private_verified";
  public static final String RENDER_RETRIEVE_WORKFLOW_CALLS = "/workflow_calls_retrieve";
  public static final String RENDER_SEARCH = "/search";
  public static final String RENDER_VERIFICATION_OUTPUT_RETRIEVE = "/verification_output_retrieve";

  // Specified in appCtx.root-security.xml
  public static final String URL_PREFIX_AJAX = "/ajax";
  /**
   * Module management activities.
   * <p>
   * <b>Note</b> : Value specified in src/main/webapp/WEB-INF/spring/appCtx.root-security.xml.
   */
  public static final String URL_PREFIX_MANAGE_MODULES = "/manageModules";
  public static final String URL_PREFIX_MANAGE_MODULES_AJAX = URL_PREFIX_MANAGE_MODULES + URL_PREFIX_AJAX;
  /**
   * User management activities.
   * <p>
   * <b>Note</b> : Value specified in src/main/webapp/WEB-INF/spring/appCtx.root-security.xml.
   */
  public static final String URL_PREFIX_MANAGE_USERS = "/manageUsers";
  public static final String URL_PREFIX_MANAGE_USERS_AJAX = URL_PREFIX_MANAGE_USERS + URL_PREFIX_AJAX;
  /**
   * Workflow Call management activities.
   * <p>
   * <b>Note</b> : Value specified in src/main/webapp/WEB-INF/spring/appCtx.root-security.xml.
   */
  public static final String URL_PREFIX_MANAGE_WORKFLOW_CALLS = "/manageWorkflowCalls";
  public static final String URL_PREFIX_MANAGE_WORKFLOW_CALLS_AJAX = URL_PREFIX_MANAGE_WORKFLOW_CALLS + URL_PREFIX_AJAX;

  public static final String VIEW_ABOUT = "/about";
  public static final String VIEW_CONTACT = "/contact";
  public static final String VIEW_LOGIN = "/zoonlogin";
  public static final String VIEW_MODULE = "/module";
  public static final String VIEW_MODULE_MANAGE = "/module_manage";
  public static final String VIEW_MODULE_UPLOAD_ZOON = "/module_upload_ZOON";
  public static final String VIEW_MODULE_VERIFY = "/module_verify";
  public static final String VIEW_MODULES = "/modules";
  public static final String VIEW_PRIVACY = "/privacy";
  public static final String VIEW_PROFILE_BY_IDENTITY = "/profile_by_identity";
  public static final String VIEW_PROFILE_BY_USERNAME = "/profile_by_username";
  public static final String VIEW_RESULTS = "/results";
  public static final String VIEW_TUTORIALS = "/tutorials";
  public static final String VIEW_USER_REGISTER = "/user_register";
  public static final String VIEW_USERS = "/view_users";
  public static final String VIEW_WORKFLOW_CALL = "/workflow_call";
  public static final String VIEW_WORKFLOW_CALLS = "/workflow_calls";

  /* See appCtx.view.xml.
     viewJSON is a MappingJacksonJsonView where the returned MVC model is converted to JSON. */
  public static final String VIEW_NAME_JSON = "viewJSON";

  private ClientIdentifiers() {}

}