<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <style type="text/css">
      #selectable_occurrence .ui-selecting { background: #FECA40; }
      #selectable_occurrence .ui-selected { color: lightgray; }
      #selectable_occurrence { list-style-type: none; margin: 0; padding: 0; }
      #selectable_occurrence li { margin: 3px; padding: 0.4em; font-size: 1.4em; height: 18px; }

      #selectable_covariate .ui-selecting { background: #FECA40; }
      #selectable_covariate .ui-selected { color: lightgray; }
      #selectable_covariate { list-style-type: none; margin: 0; padding: 0; }
      #selectable_covariate li { margin: 3px; padding: 0.4em; font-size: 1.4em; height: 18px; }

      #selectable_process .ui-selecting { background: #FECA40; }
      #selectable_process .ui-selected { color: lightgray; }
      #selectable_process { list-style-type: none; margin: 0; padding: 0; }
      #selectable_process li { margin: 3px; padding: 0.4em; font-size: 1.4em; height: 18px; }

      #selectable_model .ui-selecting { background: #FECA40; }
      #selectable_model .ui-selected { color: lightgray; }
      #selectable_model { list-style-type: none; margin: 0; padding: 0; }
      #selectable_model li { margin: 3px; padding: 0.4em; font-size: 1.4em; height: 18px; }

      #selectable_output .ui-selecting { background: #FECA40; }
      #selectable_output .ui-selected { color: lightgray; }
      #selectable_output { list-style-type: none; margin: 0; padding: 0; }
      #selectable_output li { margin: 3px; padding: 0.4em; font-size: 1.4em; height: 18px; }

      #sortable_occurrence { list-style-type: none; padding: 0; }
      #sortable_occurrence li { font-weight: bold; background-color: #add8e6; text-align: center; margin-bottom: 10px; padding: 5px; }

      #sortable_covariate { list-style-type: none; margin: 0; padding: 0; }
      #sortable_covariate li { font-weight: bold; background-color: #add8e6; text-align: center; margin-bottom: 10px; padding: 5px; }

      #sortable_process { list-style-type: none; margin: 0; padding: 0; }
      #sortable_process li { font-weight: bold; background-color: #add8e6; text-align: center; margin-bottom: 10px; padding: 5px; }

      #sortable_model { list-style-type: none; margin: 0; padding: 0; }
      #sortable_model li { font-weight: bold; background-color: #add8e6; text-align: center; margin-bottom: 10px; padding: 5px; }

      #sortable_output { list-style-type: none; margin: 0; padding: 0; }
      #sortable_output li { font-weight: bold; background-color: #add8e6; text-align: center; margin-bottom: 10px; padding: 5px; }

    </style>