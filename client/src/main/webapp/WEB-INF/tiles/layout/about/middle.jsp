<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="container wide-container">
  <h3 class="page-header"><spring:message code="about.title" /></h3>
  <p>
    <spring:message code="about.introduction" />
  </p>

  <p style="margin-left: 40px;">
    <button type="button"
            onclick="history.go(-1)"><spring:message code="general.button_back" /></button>
  </p>
</div>
