<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div id="js_error_moduletypes" style="color: red; display: none;">
</div>
<div id="js_error_modules" style="color: red; display: none;">
</div>
<div class="container wide-container">
  <h3 class="page-header">Workflow Call - <c:out value="${ma_workflow_call.name}" /></h3>
<c:if test="${not empty ma_workflow_call.figshareId}">
  <div name="workflow_call" class="explanatory_text rounded_10">
    <span style="color: grey;">Click here to <span id="workflow_call_click">hide</span> Figshare link.</span>
    <div id="workflow_call_explain">
      <iframe src="https://widgets.figshare.com/articles/<c:out value="${ma_workflow_call.figshareId}" />/embed" height="275" width="100%" frameborder="0"></iframe>
    </div>
  </div>
</c:if>
  <div class="workflow_call_container">
    <div id="module_types"></div>
  </div>
  <div class="workflow_call_container">
    <div id="message"></div>
  </div>
  <div class="workflow_call_container">
    <div id="selection"></div>
  </div>
  <div class="workflow_call_container">
    <label for="force_reproducible">Force reproducible</label> :
    <input type="checkbox" id="force_reproducible" />
  </div>
  <div style="padding: 5px;">
    <div id="workflow_call_code_area" style="display: none;">
      <h3 class="page-header">R code</h3>
      <textarea id="workflow_call_code" rows="6" cols="130" class="preformatted" spellcheck="false"></textarea>
      <br />
      <button id="clipboard_button"
              class="button"
              data-clipboard-action="copy"
              data-clipboard-target="#workflow_call_code">
        Copy to clipboard
      </button>
      <script type="text/javascript" src="<c:url value="/resources/js/clipboard/clipboard.1.5.10.min.js" />"></script>
      <script type="text/javascript" >
        jQuery(document).ready(
          function() {
            var clipboard = new Clipboard('#clipboard_button');
            clipboard.on('success', function(e) {
              console.info('Action:', e.action);
              console.info('Text:', e.text);
              console.info('Trigger:', e.trigger);

              e.clearSelection();
            });

            clipboard.on('error', function(e) {
              console.error('Action:', e.action);
              console.error('Trigger:', e.trigger);
            });
          }
        );
      </script>
    </div>
<sec:authorize access="isAuthenticated()">
    <form>
      <table>
        <tr>
          <td><label for="workflow_call_name">Name</label></td>
          <td>&nbsp;</td>
          <td><input type="text" id="workflow_call_name" name="name" maxlength="50" /></td>
        </tr>
        <tr>
          <td><label for="workflow_call_version">Version</label></td>
          <td>&nbsp;</td>
          <td><input type="text" id="workflow_call_version" name="version" maxlength="20" /></td>
        </tr>
        <tr>
          <td><label for="workflow_call_description">Description</label></td>
          <td>&nbsp;</td>
          <td>
            <textarea id="workflow_call_description" name="description" rows="6" cols="100"></textarea>
          </td>
        </tr>
        <tr>
          <td colspan="3">
            <button type="button" id="save_workflow_call"> Save Workflow Call </button>
            <span style="color: red;" id="workflow_call_save_response"></span>
          </td>
        </tr>
    </form>
</sec:authorize>
    <textarea id="workflow_call_code_json" rows="5" cols="135" class="preformatted" readonly="readonly" style="display: none;">
    </textarea>
  </div>
</div>
