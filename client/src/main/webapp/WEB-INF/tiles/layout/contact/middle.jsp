<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="container wide-container">
  <h3 class="page-header"><spring:message code="contact.title" /></h3>
  <p>
    <spring:message code="contact.line1" />
  </p>

  ZOON project :
  <ul class="options">
    <li>General @ <a href="mailto:zoonproject@gmail.com?subject=ZOON PoC website">zoonproject@gmail.com</a></li>
    <li>Greg @ <a href="mailto:gmcinerny@hotmail.com?subject=ZOON PoC website">gmcinerny@hotmail.com</a></li>
    <li>Nick @ <a href="mailto:nick.golding.research@gmail.com?subject=ZOON PoC website">nick.golding.research@gmail.com</a></li>
    <li>Tom @ <a href="mailto:tomaug@ceh.ac.uk?subject=ZOON PoC website">tomaug@ceh.ac.uk</a></li>
  </ul>

  Website :
  <ul class="options">
    <li>gef @ <a href="mailto:geoff.williams@cs.ox.ac.uk?subject=ZOON PoC website">geoff.williams@cs.ox.ac.uk</a></li>
  </ul>

  <p style="margin-left: 40px;">
    <button type="button"
            onclick="history.go(-1)"><spring:message code="general.button_back" /></button>
  </p>
</div>
