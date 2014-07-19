<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ attribute name="formFieldName" required="true" %>
<%@ attribute name="drugs" required="true" type="java.util.List" %>
<%@ attribute name="initialValue" required="false" %>
<%@ attribute name="optionHeader" required="false" %>
<%@ attribute name="onChange" required="false" %>
<%@ attribute name="includeVoided" required="false" %>

<c:if test="${empty drugs}">
	<openmrs:message code="Drug.list.empty" />
</c:if>
<c:if test="${not empty drugs}">
	<input style="width:50%;" name="${formFieldName}" id="${formFieldName}" numId="0" <c:if test="${not empty onChange}">onChange=${onChange}</c:if> />
	<script type="text/javascript">
		//Save global references to jquery to avoid conflicts
		var old_j = $j;
		var old$ = $;	
	</script>
	<link rel="stylesheet" href="//code.jquery.com/ui/1.11.0/themes/smoothness/jquery-ui.css" />
	<script src="//code.jquery.com/jquery-1.10.2.js"></script>
	<script src="//code.jquery.com/ui/1.11.0/jquery-ui.js"></script>
	<script type="text/javascript">
		$j(document).ready(function() {

			var drugs_name_id = [
				{label: "", value:"0", },
			    <c:forEach var="drug" items="${drugs}">
		       		<c:if test="${includeVoided == 'true' || !drug.retired}">
		        		{label: "${drug.name}", value:"${drug.drugId}" },
		        	</c:if>		
		        </c:forEach>             
		     ];
		
			var drugs_autocomp = $j("input#${formFieldName}");
			
			//var drugs_select = $j("select#${formFieldName}");
		
			/*
			drugs_select.hide();
			drugs_select.change(function(e) {
				console.log(e);
				if (drugs_select.val() === "") {
					
				}
				
			}); 
			*/
			
			drugs_autocomp.autocomplete({
				source: drugs_name_id,
				minLength: 3,
				focus: function( event, ui ) {
					event.preventDefault();
				},
				select: function (event, ui) {
					var item = ui.item;
					//drugs_select.val(item.value);
					drugs_autocomp.val(item.label);
					drugs_autocomp.attr("numId",item.value); 
					event.preventDefault();
					//drugs_select.change();
				}, 
			});
			
		});
	</script>
	<script type="text/javascript">
		//Restore the jquery global variables.
		$j = old_j;
		$ = old$;
	</script>
</c:if>