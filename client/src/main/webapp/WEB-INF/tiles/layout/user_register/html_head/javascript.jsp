<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers,
                uk.ac.ox.cs.science2020.zoon.client.entity.Identity,
                uk.ac.ox.cs.science2020.zoon.client.entity.User" %>
<c:set var="max_len_email" value="<%=Identity.MAX_LENGTH_EMAIL%>" />
<c:set var="max_len_identity" value="<%=Identity.MAX_LENGTH_IDENTITY_NAME%>" />
<c:set var="max_len_password" value="<%=User.MAX_LENGTH_PASSWORD%>" />
<c:set var="min_len_email" value="<%=Identity.MIN_LENGTH_EMAIL%>" />
<c:set var="min_len_identity" value="<%=Identity.MIN_LENGTH_IDENTITY_NAME%>" />
<c:set var="min_len_password" value="<%=User.MIN_LENGTH_PASSWORD%>" />
<c:set var="name_identity_name">identity name</c:set>
<c:set var="name_email"><spring:message code="user.email" /></c:set>
<c:set var="name_user_name">user name</c:set>
<c:set var="name_password">password</c:set>
    <script type="text/javascript" src="<c:url value="/resources/js/validate/jquery.validate-1.11.1.min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/additional-methods/additional-methods.1.11.1.min.js" />"></script>
    <script type="text/javascript">
      /* <![CDATA[ */
      jQuery(document).ready(
        function() {
          jQuery('#identityName').focus();

          var registration_form = jQuery('#registration_form');
          registration_form.validate({
            rules: {
              'identityName' : { required: true, maxlength: ${max_len_identity}, minlength: ${min_len_identity} },
              'email' : { required: true, maxlength: ${max_len_email}, minlength: ${min_len_email} },
               /*'userName' : { required: true, maxlength: ${max_len_username}, minlength: ${min_len_username} },*/
              'password' : { required: true, maxlength: ${max_len_password}, minlength: ${min_len_password} }
            },
            messages: {
              'identityName' : { required: '<spring:message code="input.form_value_required" arguments="${name_identity_name}" />',
                                 maxlength: '<spring:message code="input.form_max_len_value" arguments="${name_identity_name}, ${max_len_identity}" />',
                                 minlength: '<spring:message code="input.form_min_len_value" arguments="${name_identity_name}, ${min_len_identity}" />' },
              'email' : { required: '<spring:message code="input.form_value_required" arguments="${name_email}" />',
                          maxlength: '<spring:message code="input.form_max_len_value" arguments="${name_email}, ${max_len_email}" />',
                          minlength: '<spring:message code="input.form_min_len_value" arguments="${name_email}, ${min_len_email}" />' },
              'password' : { required: '<spring:message code="input.form_value_required" arguments="${name_password}" />',
                             maxlength: '<spring:message code="input.form_max_len_value" arguments="${name_password}, ${max_len_password}" />',
                             minlength: '<spring:message code="input.form_min_len_value" arguments="${name_password}, ${min_len_password}" />'  }
            }
          });
        }
      );
      /* ]]> */
    </script>