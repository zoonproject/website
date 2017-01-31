<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>
<sec:authorize access="isAuthenticated()">
<div class="container wide-container">
  <h3 class="page-header"><spring:message code="module.upload_to_ZOON" /></h3>
  <form>
    <div>
      <div style="display: inline-block; vertical-align: top;">
        <select id="verified_uploaded_select"></select>
      </div>
      <div id="remove" style="display: inline-block; padding: 0px 5px; text-align: center; vertical-align: top;">
        Remove<br />
        after<br />
        upload? <img alt="info" src="<c:url value="/resources/img/info.png" />"
                                title="Check the box to remove modules from the temporary file store after upload." /><br />
        <input id="remove_after_upload" type="checkbox" />
      </div>
      <div id="license" style="display: inline-block; padding: 0px 20px; vertical-align: top;">
        <p>
          By submitting the selected module for upload to the ZOON repository you are agreeing to
          publish it under the ZOON
          <a href="https://opensource.org/licenses/BSD-3-ClauseBSD 3-clause licence"
             target="_blank">BSD 3-clause</a>
          repository license.
        </p>
        <p>
          Please contact the
          <a href="mailto:zoonproject@gmail.com?subject=ZOON license precondition">ZOON developers</a>
          if you wish to discuss any aspect of this precondition.
        </p>
      </div>
      <p id="uploading"></p>
    </div>
  </form>
</div>
</sec:authorize>