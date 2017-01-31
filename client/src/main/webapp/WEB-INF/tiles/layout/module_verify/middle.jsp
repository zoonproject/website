<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>
<sec:authorize access="isAuthenticated()">
<div class="container wide-container">
  <h3 class="page-header"><spring:message code="module.verify" /></h3>
  <form>
    <select id="verify_uploaded_select" style="vertical-align: top;"></select>
    <div id="explanation" style="display: inline-block; padding-left: 10px;">
      <p>Verified modules appear in green, unverified modules in red.</p>
      <p>To be considered 'verified' the last line of the verification result must be 'Success'.</p>
    </div>

    <p id="verifying"></p>

    <p><spring:message code="module.verify_stderr" /></p>
    <div style="margin-left: 10px;">
      <textarea id="verify_output" cols="100" rows="5" readonly="readonly" class="monospace"></textarea>
    </div>

    <p><spring:message code="module.verify_result" /></p>
    <div style="margin-left: 10px;">
      <textarea id="verify_result" cols="100" rows="15" readonly="readonly" class="monospace"></textarea>
    </div>
  </form>
</div>
</sec:authorize>