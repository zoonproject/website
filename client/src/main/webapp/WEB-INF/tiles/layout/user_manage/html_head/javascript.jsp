<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="uk.ac.ox.cs.science2020.zoon.client.ClientIdentifiers" %>
<sec:csrfMetaTags />

<c:url var="urlAJAXDeleteUser" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_USERS_AJAX + ClientIdentifiers.ACTION_USER_DELETING %>" />
<c:url var="urlAJAXEnableUser" value="<%= ClientIdentifiers.URL_PREFIX_MANAGE_USERS_AJAX + ClientIdentifiers.ACTION_USER_ENABLING %>" />

    <script type="text/javascript">
      /* <![CDATA[ */
      var MODEL_ATTRIBUTE_USER_DELETED = '<%=ClientIdentifiers.MODEL_ATTRIBUTE_USER_DELETED %>';
      var URL_PREFIX_USER_DELETING = '${urlAJAXDeleteUser}';
      var URL_PREFIX_USER_ENABLING = '${urlAJAXEnableUser}';
      /* ]]> */
    </script>
    <script type="text/javascript" charset="utf8" src="<c:url value="/resources/js/users/users.js" />"></script>
    <script type="text/javascript" charset="utf8" src="<c:url value="/resources/js/datatables/jquery.dataTables.js" />"></script>
    <script type="text/javascript">
      /* <![CDATA[ */
      /* https://datatables.net/examples/plug-ins/dom_sort.html */
      /* Create an array with the values of all the checkboxes in a column */
      $.fn.dataTable.ext.order['dom-checkbox'] = function  ( settings, col )
      {
        return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) {
          return $('input', td).prop('checked') ? '1' : '0';
        } );
      }

      jQuery(document).ready(
        function() {

          var prefix = 'ch_';
          jQuery('input:checkbox[id^=' + prefix + ']').click(function() {
            var checkbox = jQuery(this);
            var id = checkbox.attr('id');

            var username = id.substr(prefix.length);
            var checked = checkbox.is(':checked');

            ajax_for_enabling(username, checked);
          });

          var prefix_delete = 'del_';
          jQuery('input:checkbox[id^=' + prefix_delete + ']').click(function() {
            var checkbox = jQuery(this);
            var id = checkbox.attr('id');

            var username = id.substr(prefix_delete.length);
            var checked = checkbox.is(':checked');

            if (checked) {
              var response = confirm('Are you sure?');
              if (response == true) {
                ajax_for_deleting(username);
              }
            }
          });

          /* This must appear after onclick event handlers in case table paging renders only first
             few checkboxes on page, leaving other pages without event handlers on checkboxes! */
          jQuery('#user_table').DataTable( {
            'order': [[ 0, 'asc' ]],
            'columns' : [ null, null, { 'orderDataType' : 'dom-checkbox' }, null ]
          } );
        }
      );

      /* ]]> */
    </script>