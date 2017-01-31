<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
  /* <![CDATA[ */
  /* show a message temporarily */
  function show_message(message_text) {
    var div = jQuery('#message_text');
    div.empty();
    div.html(message_text);
    div.show();
    setTimeout(function() { hide_message() }, 3000);
  }
  /* hide the message */
  function hide_message() {
    var div = jQuery('#message_text');
    div.hide();
  }
  /* show an error message */
  function show_message_error(error_text) {
    var div = jQuery('#error_text');
    div.empty();
    div.html(error_text);
    div.show();
  }
  /* hide an error message */
  function hide_message_error() {
    var div = jQuery('#error_text');
    div.hide();
  }
  /* ]]> */
</script>
<c:choose>
  <c:when test="${not empty ma_message_error}">
<div id="error_text" class="error_text rounded_5"><c:out value="${ma_message_error}" /> </div>
  </c:when>
  <c:otherwise>
<div id="error_text" class="error_text rounded_5" style="display: none;"></div>
  </c:otherwise>
</c:choose>
<div id="js_error_results" class="error_text rounded_5" style="display: none;"></div>
<c:choose>
  <c:when test="${not empty ma_message_info}">
<div id="message_text" class="message_text rounded_5"><c:out value="${ma_message_info}" /> </div>
  </c:when>
  <c:otherwise>
<div id="message_text" class="message_text rounded_5" style="display:none;"></div>
  </c:otherwise>
</c:choose>