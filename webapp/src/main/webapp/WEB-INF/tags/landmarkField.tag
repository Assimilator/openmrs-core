<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ attribute name="formFieldName" required="true" %>
<%@ attribute name="landmarks" required="true" type="java.util.List" %>
<%@ attribute name="initialValue" required="false" %>
<%@ attribute name="optionHeader" required="false" %>
<%@ attribute name="onChange" required="false" %>
<%@ attribute name="includeVoided" required="false" %>

<c:if test="${empty landmarks}">
	<openmrs:message code="Landmark.list.empty" />
</c:if>
<c:if test="${not empty landmarks}">
	<input style="width:50%;" name="${formFieldName}" id="${formFieldName}" numId="0" <c:if test="${not empty onChange}">onChange=${onChange}</c:if> />

	<script type="text/javascript">
		$j(document).ready(function() {

			var landmarks_name_id = [
				{label: "", value:"0", },
			    <c:forEach var="landmark" items="${landmarks}">
		       		<c:if test="${includeVoided == 'true' || !landmark.retired}">
		        		{label: "${landmark.name}", value:"${landmark.landmarkId}" },
		        	</c:if>		
		        </c:forEach>             
		     ];
		
			var landmarks_autocomp = $j("input#${formFieldName}");
			
			//var landmarks_select = $j("select#${formFieldName}");
		
			/*
			landmarks_select.hide();
			landmarks_select.change(function(e) {
				console.log(e);
				if (landmarks_select.val() === "") {
					
				}
				
			}); 
			*/
			
			landmarks_autocomp.autocomplete({
				source: landmarks_name_id,
				minLength: 3,
				focus: function( event, ui ) {
					event.preventDefault();
				},
				select: function (event, ui) {
					var item = ui.item;
					//landmarks_select.val(item.value);
					landmarks_autocomp.val(item.label);
					landmarks_autocomp.attr("numId",item.value); 
					event.preventDefault();
					//landmarks_select.change();
				}, 
			});
			
		});
	</script>

</c:if>