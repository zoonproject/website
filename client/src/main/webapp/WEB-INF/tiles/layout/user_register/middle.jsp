<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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

<c:url var="urlRegisterUser" value="<%=ClientIdentifiers.ACTION_USER_REGISTER%>" />

<div class="container wide-container">
  <h3 class="page-header"><spring:message code="user.registration" /></h3>
  <div style="padding: 10px;">
    <p>
      <spring:message code="user.intro1" /><br />
      <spring:message code="user.intro2" />
    </p>
  </div>
  <form:form action="${urlRegisterUser}"
             commandName="<%=ClientIdentifiers.MODEL_ATTRIBUTE_USER%>"
             id="registration_form" method="post">
    <input type="hidden"
           name="${_csrf.parameterName}"
           value="${_csrf.token}" />
    <table>
      <tr>
        <td colspan="4" class="capitalize">
          <spring:message code="user.identity" />
        </td>
      </tr>
      <tr>
        <td>(<b style="color: red;">*</b>)</td>
        <td class="capitalize">
          <spring:message code="user.identity_name" />
        </td>
        <td>
          <form:input path="identityName" maxlength="${max_len_identity}" size="30"/>
          (<spring:message code="user.value_conditions" arguments="${min_len_identity},${max_len_identity}" />)
        </td>
        <td rowspan="2" style="vertical-align: top;">
          (<b style="color: red;">*</b>) <spring:message code="user.identity_explain" htmlEscape="false"/>
          <br />
          <img alt="Sample user names" src="<c:url value="/resources/img/sample_username.png" />"
                                       style="box-shadow: 2px 2px 1px #888;" />
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td class="capitalize" style="vertical-align: top;">
          <spring:message code="user.email" />
        </td>
        <td>
          <form:input path="email" maxlength="${max_len_email}" size="30" />
          (<spring:message code="user.value_conditions" arguments="${min_len_email},${max_len_email}" />)
          <p style="font-size: x-small;" class="error_text">
            <b><spring:message code="user.email_info" /></b>
          </p>
        </td>
      </tr>
      <tr>
        <td colspan="4">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="4" class="capitalize">
          <spring:message code="user.password" />
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>
          <form:input path="password" type="password" maxlength="${max_len_password}" size="30" />
          (<spring:message code="user.value_conditions" arguments="${min_len_password},${max_len_password}" />)
        </td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td style="text-align: right;">
          <input type="submit" value=" Submit " />
        </td>
        <td>&nbsp;</td>
      </tr>
    </table>
  </form:form>
</div>