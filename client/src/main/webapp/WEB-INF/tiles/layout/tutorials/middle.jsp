<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>

<c:url var="urlViewProfileByIdentity" value="<%=ClientIdentifiers.VIEW_PROFILE_BY_IDENTITY%>" />
<fmt:parseDate pattern="yyyy-MM-dd" value="2016-03-09" var="parsedDate" />

<div class="container wide-container">
  <h3 class="page-header">ZOON Tutorials</h3>
  <div name="tutorials" class="explanatory_text rounded_10">
    <span style="color: grey;">Click area to <span id="tutorials_click">show</span> additional information.</span>
    <div id="tutorials_explain" style="display: none;">
      Explanation ...
    </div>
  </div>
  <div id="tutorials">
    <div>
      <div class="tutorial-thumbnail">
        Thumbnail placeholder.
      </div>
      <div class="tutorial-info">
        <h3>
          <a href="http://rawgit.com/zoonproject/zoon/master/inst/doc/basic-zoon-usage.html"
             target="_blank">
            <strong>Basic ZOON usage</strong>
          </a>
          <small> — v.1</small>
        </h3>
        <p class="package-list-dateline">
          <span class="package-list-date"><fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd" /></span>
          by <a href="${urlViewProfileByIdentity}/Tim%20Lucas">Tim Lucas</a> &
             <a href="${urlViewProfileByIdentity}/Tom%20August">Tom August</a>
        </p>
        <p>
         Basic ZOON usage
        </p>
      </div>
    </div>

    <div>
      <div class="tutorial-thumbnail">
        Thumbnail placeholder.
      </div>
      <div class="tutorial-info">
        <h3>
          <a href="http://rawgit.com/zoonproject/zoon/master/inst/doc/interactive_zoon_usage.html"
             target="_blank">
            <strong>Interactive ZOON usage</strong>
          </a>
          <small> — v.1</small>
        </h3>
        <p class="package-list-dateline">
          <span class="package-list-date"><fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd" /></span>
          by <a href="${urlViewProfileByIdentity}/Tim%20Lucas">Tim Lucas</a>
        </p>
        <p>
         Interactive ZOON usage
        </p>
      </div>
    </div>

    <div>
      <div class="tutorial-thumbnail">
        Thumbnail placeholder.
      </div>
      <div class="tutorial-info">
        <h3>
          <a href="http://rawgit.com/zoonproject/zoon/master/inst/doc/Building_a_module.html"
             target="_blank">
            <strong>Building a module</strong>
          </a>
          <small> — v.1</small>
        </h3>
        <p class="package-list-dateline">
          <span class="package-list-date"><fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd" /></span>
          by <a href="${urlViewProfileByIdentity}/Tim%20Lucas">Tim Lucas</a> &
             <a href="${urlViewProfileByIdentity}/Tom%20August">Tom August</a>
        </p>
        <p>
         Building a module
        </p>
      </div>
    </div>
  </div>
</div>
