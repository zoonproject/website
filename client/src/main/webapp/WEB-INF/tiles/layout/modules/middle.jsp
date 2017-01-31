<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>
<div id="js_error_moduletypes" style="color: red; display: none;">
</div>
<div class="container wide-container">
  <h3 class="page-header">ZOON Modules</h3>
  <div name="modules" class="explanatory_text rounded_10">
    <span style="color: grey;">Click area to <span id="modules_click">show</span> additional information.</span>
    <div id="modules_explain" style="display: none;">
      Explanation ...
    </div>
  </div>
  <div id="selectors" class="rounded_10" style="border: solid 1px steelblue; margin-top: 5px; padding: 10px;"></div>
  <div id="modules" class="packagelist"></div>
</div>
