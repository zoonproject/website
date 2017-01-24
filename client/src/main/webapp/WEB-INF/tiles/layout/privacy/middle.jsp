<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="container wide-container">
  <h2 class="page-header">Privacy and Security</h2>
  <h3 class="page-header"><spring:message code="privacy.title" /></h3>
  <p>
    <spring:message code="privacy.line_1" htmlEscape="false" />
  </p>
  <p>
    Information Commissioner's Office : <a href="http://ico.org.uk/for_organisations/privacy_and_electronic_communications/the_guide/cookies"
                                           title="The reason this page exists">Cookies Regulations and the New EU Cookie Law</a>.
  </p>
  <h3 class="page-header"><spring:message code="privacy.important_security" /></h3>
  <p>
    <spring:message code="privacy.line_2" htmlEscape="false" />
  </p>
  <p style="margin-left: 40px;">
    <button type="button"
            onclick="history.go(-1)"><spring:message code="general.button_back" /></button>
  </p>
</div>
