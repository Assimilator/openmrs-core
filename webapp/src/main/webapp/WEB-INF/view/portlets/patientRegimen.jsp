<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:htmlInclude file="/dwr/interface/DWROrderService.js" />
<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />
<openmrs:htmlInclude file="/scripts/drugOrder.js" />

<% java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis()); %>

<style>
	.drugorder_box {
		border-collapse: collapse;		
		padding: 3px;
		border: 1px solid black;
		margin: 4px;
	}

	.drugorder_title {
	    background-color: #2aacc4;
   		padding: 3px;
    	border: 0px;
    	margin: 0px;
	}

</style>



<div id="regimenPortlet">

	<div id="regimenPortletCurrent">
		<div class="boxHeader${model.patientVariation}"><openmrs:message code="DrugOrder.regimens.current" /></div>
		<div class="box${model.patientVariation}">
			

<div id="regimenPortletCurrent">
	<table class="regimenCurrentTable" style="width:100%">
		<thead>
			<tr class="regimenCurrentHeaderRow">
				<th style="width: 50%;" class="regimenCurrentDrugOrderedHeader"></th>
				<th style="width: 40%;" class="regimenCurrentDrugOrderedHeader"></th>
				<th style="width: 10%;" class="regimenCurrentDrugOrderedHeader"></th>
			</tr>
		</thead>
		<c:forTokens var="drugSetId" items="${model.displayDrugSetIds}" delims=",">					
			<c:if test="${drugSetId == '*'}" >
				<tbody id="regimenTableCurrent_header_other">
					<tr class="regimenCurrentHeaderOtherRow">
						<td>
							<openmrs:message code="DrugOrder.header.otherRegimens"/>
						</td>
					</tr>
				</tbody>
				<tbody id="regimenTableCurrent_other">
					<tr id="emptyOtherCurrentRow" class="noDrugsOrderRow">
						<td colspan="3" class="noDrugsOrderViewModeData">
							<span class="noOrdersMessage">&nbsp;&nbsp;&nbsp;&nbsp;(<openmrs:message code="DrugOrder.list.noOrders" />)</span>
						</td>
					</tr>
				</tbody>
			</c:if>
			<c:if test="${drugSetId != '*'}" >
				<tbody id="regimenTableCurrent_header_${fn:replace(drugSetId, " ", "_")}">
					<tr class="regimenTableCurrentRow">
						<td colspan="3" class="regimenTableCurrentViewModeData">
							<table class="drugOrderTable">
								<tr class="drugOrderHeadersRow">
									<td class="drugOrderHeadersData">
										${fn:toUpperCase(drugSetId)}
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</tbody>
				<tbody id="regimenTableCurrent_${fn:replace(drugSetId, " ", "_")}">
					<tr id="empty${fn:replace(drugSetId, " ", "_")}CurrentRow" class="noDrugsOrderRow">
						<td colspan="3" class="noDrugsOrderViewModeData">
							<span class="noOrdersMessage">&nbsp;&nbsp;&nbsp;&nbsp;(<openmrs:message code="DrugOrder.list.noOrders" />)</span>
						</td>
					</tr>				
				</tbody>
			</c:if>
		</c:forTokens>
	</table>
</div>
<script>
	setPatientId("${model.patientId}");
	setDisplayDrugSetIds("${model.displayDrugSetIds}");
	setRegimenMode("${model.currentRegimenMode}");
</script>
		
		<span class="regimenPortletSpan"><input type="button" onclick="drugOrderForm.show();" value="(+) <openmrs:message code="DrugOrder.regimens.addOrChange" />"></span>
			<div id="regimenPortletAddForm" style="display:none; border: 1px dashed black; padding: 10px;">
				<table width="100%" class="patientRegimenTable">
					<tr class="patientRegimenRow">						
						<td valign="top" align="right" class="patientRegimeDataFlexible">
							<div id="regimenPortletAddFlexible">
								<form method="post" id="orderForm" onSubmit="void()">
								<table class="patientAddFlexibleTable" style="width:100%;">
									<tr class="patientAddFlexibleRow">
										<td colspan="2" class="patientAddFlexibleData"><strong><openmrs:message code="DrugOrder.regimens.addCustom"/></strong></td>
									</tr>								
									<tr class="patientAddFlexibleRow">
										<td class="patientAddFlexibleData" ><openmrs:message code="DrugOrder.drug"/></td>						
										<td class="patientAddFlexibleData">
											<openmrs:fieldGen type="org.openmrs.Drug" formFieldName="drug" val="" parameters="includeVoided=false|noBind=true|optionHeader=[blank]" />
											<div id="drug_error" class="error" style="display:none"></div>
										</td>
									</tr>
									<tr>
										<td class="patientAddFlexibleData"> Dose </td>
										<td class="patientAddFlexibleData" > 										
											<openmrs:fieldGen type="java.lang.Double" formFieldName="dose" val="" parameters="noBind=true" />
											<select name="dose_unit" id="dose_unit">
                                    		</select>
                                    		<div id="dose_error" class="error" style="display:none"></div>
										</td>
									</tr>
									<tr class="patientAddFlexibleRow">
										<td class="patientAddFlexibleData"><openmrs:message code="DrugOrder.frequency"/></td>
										<td class="patientAddFlexibleData">
											<openmrs:fieldGen type="java.lang.Integer" formFieldName="frequency" val="" parameters="noBind=true" />
											times per 
											<select name="frequency_unit" id="frequency_unit">
											</select>
											<input type="checkbox" id="drugorder_prn" name="drugorder_prn" value="no">prn/as needed<br>
											<div id="frequency_error" class="error" style="display:none"></div>
										</td>
									</tr>
									<tr class="patientAddFlexibleRow">
										<td class="patientAddFlexibleData"> Quantity </td>
										<td>
											<openmrs:fieldGen type="java.lang.Integer" formFieldName="quantity" val="" parameters="noBind=true" />
											<select name="quantity_unit" id="quantity_unit">
                                    		</select>
                                    		<div id="quantity_error" class="error" style="display:none"></div>
										</td>	
									</tr>
									<tr class="patientAddFlexibleDateRow">
										<td class="patientAddFlexibleData"><openmrs:message code="general.dateStart"/></td>
										<td class="patientAddFlexibleData">
											<openmrs:fieldGen type="java.util.Date" formFieldName="startDate" val="" parameters="noBind=true" />
											<div id="date_error" class="error" style="display:none"></div>
										</td>
									</tr>
									<tr class="patientAddFlexibleRow">
										<td class="patientAddFlexibleDate"> Duration </td>
										<td class="patientAddFlexibleData">
											<openmrs:fieldGen type="java.lang.Integer" formFieldName="duration" val="" parameters="noBind=true" />
											<select name="duration_unit" id="duration_unit">
                                    		</select>
                                    		<input type="checkbox" id="durationLong" name="durationLong" value="no">Continuos<br>
                                    		<div id="duration_error" class="error" style="display:none"></div>
										</td>
									</tr>
									<tr class="patientAddFlexibleRow">
										<td class="patientAddFlexibleData"><openmrs:message code="general.instructions"/></td>
										<td class="patientAddFlexibleData">
											<openmrs:fieldGen type="java.lang.String" formFieldName="instructions" val="" parameters="noBind=true" />
											<div id="instructions_error" class="error" style="display:none"></div>
										</td>
									</tr>
									<tr class="patientAddFlexibleButtonRow">
										<td colspan="2" align="center" class="patientAddFlexibleButtonData">
											<span id="addNew"><input type="button" value="<openmrs:message code="general.add" />" onClick="drugOrderForm.submit();"></span>
											<span id="cancelNew"><input type="button" value="<openmrs:message code="general.cancel" />" onClick="drugOrderForm.hide();"></span>
										</td>
									</tr>
								</table>
								</form>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>			
	</div>
	<br />
	<div id="regimenPortletCompleted">
		<div class="boxHeader${model.patientVariation}"><openmrs:message code="DrugOrder.regimens.completed" /></div>
		<div class="box${model.patientVariation}">


<div id="regimenPortletCompleted">
	<table class="regimenCompletedTable" style="width:100%">
		<thead>
			<tr class="regimenCompletedHeaderRow">
				<th style="width: 50%;" class="regimenCompletedDrugOrderedHeader"></th>
				<th style="width: 40%;" class="regimenCompletedDrugOrderedHeader"></th>
				<th style="width: 10%;" class="regimenCompletedDrugOrderedHeader"></th>
			</tr>
		</thead>
		<c:forTokens var="drugSetId" items="${model.displayDrugSetIds}" delims=",">					
			<c:if test="${drugSetId == '*'}" >
				<tbody id="regimenTableCompleted_header_other">
					<tr class="regimenCompletedHeaderOtherRow">
						<td>
							<openmrs:message code="DrugOrder.header.otherRegimens"/>
						</td>
					</tr>
				</tbody>
				<tbody id="regimenTableCompleted_other">
					<tr class="noDrugsOrderRow">
						<td colspan="3" class="noDrugsOrderViewModeData">
							<span class="noOrdersMessage">&nbsp;&nbsp;&nbsp;&nbsp;(<openmrs:message code="DrugOrder.list.noOrders" />)</span>
						</td>
					</tr>
				</tbody>
			</c:if>
			<c:if test="${drugSetId != '*'}" >
				<tbody id="regimenTableCompleted_header_${fn:replace(drugSetId, " ", "_")}">
					<tr class="regimenTableCompletedRow">
						<td colspan="3" class="regimenTableCompletedViewModeData">
							<table class="drugOrderTable">
								<tr class="drugOrderHeadersRow">
									<td class="drugOrderHeadersData">
										${fn:toUpperCase(drugSetId)}
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</tbody>
				<tbody id="regimenTableCompleted_${fn:replace(drugSetId, " ", "_")}">
					<tr class="noDrugsOrderRow">
						<td colspan="3" class="noDrugsOrderViewModeData">
							<span class="noOrdersMessage">&nbsp;&nbsp;&nbsp;&nbsp;(<openmrs:message code="DrugOrder.list.noOrders" />)</span>
						</td>
					</tr>				
				</tbody>
			</c:if>
		</c:forTokens>
	</table>
</div>
<script>
	setPatientId("${model.patientId}");
	setDisplayDrugSetIds("${model.displayDrugSetIds}");
	setRegimenMode("${model.currentRegimenMode}");
</script>
		
	</div>
</div>

<table id="drugorder_template" class="drugorder_box" width="100%" style="display:none">
	<tr class="drugorder_title">
		<td class="drugorder_title, drugorder_drugname"></td>
		<td class="drugorder_title, drugorder_prescriber" colspan="2"></td>
	</tr>
	<tr >
		<td width="45%"></td>
		<td width="40%"></td>
		<td width="15%"></td>	
	</tr>
	<tr class="drugorder_box, drugorder_tr">
		<td class="drugorder_details" width="45%"></td>
		<td class="drugorder_instructions" width="40%"></td>
		<td class="drugorder_buttons" width="15%">
			<input class="drugorder_stop"   type="button" value="stop"   />
			<input class="drugorder_delete" type="button" value="delete" />
			<div id="drugorder_stop_form" style="display:none" class="dashedAndHighlighted">
				<form>
					<openmrs:message code="DrugOrder.discontinuedDate" />:
					<input id="pick_date_template" class="drugorder_stop_form_date" type="text" size="10" value="" onFocus="showCalendar(this)" />
					&nbsp;&nbsp;&nbsp;&nbsp;<openmrs:message code="general.reason" />:
					<select >
					</select><br/>
					&nbsp;&nbsp;<input type="button" id="drugorder_stop_form_stop" value="<openmrs:message code="DrugOrder.discontinue" />" />
					&nbsp;&nbsp;<input type="button" id="drugorder_stop_form_cancel" value="<openmrs:message code="general.cancel" />" />
				</form>
			</div>
			<div id="drugorder_del_form" style="display:none" class="dashedAndHighlighted">
					<form>
						<openmrs:message code="general.reason" />:
						<select>
							<option value="" ></option>
							<option value="<openmrs:message code="DrugOrder.void.reason.error" />" ><openmrs:message code="DrugOrder.void.reason.error" /></option>
							<option value="<openmrs:message code="DrugOrder.void.reason.dateError" />" ><openmrs:message code="DrugOrder.void.reason.dateError" /></option>
							<option value="<openmrs:message code="DrugOrder.void.reason.other" />" ><openmrs:message code="DrugOrder.void.reason.other" /></option>													
						</select><br/>
						&nbsp;&nbsp;<input type="button" id="drugorder_del_form_del"  value="<openmrs:message code="DrugOrder.void" />"  />
						&nbsp;&nbsp;<input type="button" id="drugorder_del_form_cancel" value="<openmrs:message code="general.cancel" />" />
					</form>
			</div>
		</td>	
	</tr>
</table>



	<openmrs:htmlInclude file="/dwr/interface/DWROrderService.js" />
	<openmrs:htmlInclude file="/dwr/engine.js" />
	<openmrs:htmlInclude file="/dwr/util.js" />
	<script>
		<!-- // begin

		var drugOrderForm = new function () {
			
			datePickerLongIdGenerationNUmber = 0;
			
			var BaseField = function (fname, ferror, emsg) {
				var self = this;
				
				self.field = $j(fname);
				self.error = $j(ferror);
				self.emsg = emsg;
				
				self.validate =  function () {return true;};
				
				self.clear =  function() {
					self.field.val("");
					self.error.text("");
					self.error.hide();
				}
			};
			
			
			var UnitField = function (fname, funit, ferror, vunits, emsg, plural) {
				
				var self = this;
				self.field = $j(fname);
				self.field_unit = $j(funit);
				self.error = $j(ferror);
				self.units = vunits.split("|");
					
				self.validate = function () {
					var f = self.field.val();
					var fu = self.field_unit.val();
					var pattern =  /(^0|[^0-9])/;
					
					
					if (!f || pattern.test(f) || f <= 0 ) {
						self.error.text(emsg);
						self.error.show();
						return false;
					}
				
					if (!fu || !fu.match(vunits)) {
						self.error.text(emsg);
						self.error.show();
						return false;						
					}
					
					self.error.text("");
					self.error.hide();
					return true;
				};
				
				self.populate = function () {
					var op;
					self.field_unit.children().remove();
					
					op = document.createElement("OPTION");
					op.innerHTML = "";
					op.value = "";
					op.id=fname+"_void";
					self.field_unit.append(op);
					
					for (var i = 0; i < self.units.length; i++) {
						op = document.createElement("OPTION");
						op.innerHTML = self.units[i] + (plural ? "(s)" : "");
						op.value = self.units[i];
						op.id=fname+"_"+self.units[i];
						self.field_unit.append(op);					
					}
				};
				
				self.clear = function () {
					self.field.val("");
					self.field_unit.val("");
					self.error.text("");
					self.error.hide();
				};
				
			};
			
			var self = this;
			
			self.visible = false;
			
			self.form = $j("#regimenPortletAddForm");
		
			self.drug = new BaseField("#drug", "#drug_error");
			
			self.drug.validate =  function() {
				var id = self.drug.field.attr("numId");
				
				if (!id || id <= 0 ) {
					self.drug.error.text("Select a valid drug");
					self.drug.error.show();
					return false;
				}
				
				if (!self.drug.field.val() || self.drug.field.val() == "") {
					self.drug.field.attr("numId", 0);
					self.drug.error.text("Select a valid drug");
					self.drug.error.show();
					return false;					
				}
				
				self.drug.error.text("");
				self.drug.error.hide();
				return true;
			};
			

			self.drug.clear = function() {
				self.drug.field.attr("numId", 0); 
				self.drug.field.val("");
				self.drug.error.text("");
				self.drug.error.hide();
			}

			
			self.start = new BaseField("#startDate", "#date_error");
			
			self.start.validate =  function () {
				var d;
				
				if(self.start.field.val()=="") {
					self.start.error.text("Select a valid start date");
					self.start.error.show();
					return false;
				}
				
				d = new Date(self.start.field.val());
				
				if (!d.getTime()) {
					self.start.error.text("Select a valid start date");
					self.start.error.show();
					return false;					
				}
				
				self.start.error.text("");
				self.start.error.hide();					
				return true;
			};


			self.instructions = new BaseField("#instructions", "#instructions_error");
			
			self.freq = new UnitField("#frequency","#frequency_unit", "#frequency_error", "day|week|month", "select a valid frequency", false);
			self.freq.prn = $j("#drugorder_prn");
			
			self.dose = new UnitField("#dose","#dose_unit", "#dose_error", "mg|ml|grams|liters|tablets|capsules|bottles|other", "select a valid dosage", false);
			
			self.quantity = new UnitField("#quantity","#quantity_unit", "#quantity_error", "mg|ml|grams|liters|tablets|capsules|bottles|other", "select a valid quantity", false);
			
			self.quantity.f = self.quantity.validate;
			self.quantity.validate = function(){
				
				var v = self.quantity.field.val();
				
				if (v == "") {
					self.quantity.field_unit.val("");
					return true;
				}
					
				return self.quantity.f();
			}
			
			self.duration = new UnitField("#duration","#duration_unit", "#duration_error", "day|week|month", "select a valid duration", true);
			self.duration.field_check = $j("#durationLong");
			
			
			self.duration.f = self.duration.validate;
			
			self.duration.validate = function () {
				if (self.duration.field_check.attr( "checked" ))
					return true;
				
				return self.duration.f();
			}
			
			
			self.show =  function(){
				if (self.visible)
					return;
				
				self.form.show(); 
				self.clear();
				self.visible = true;
			};
				
			self.hide = function(){self.form.hide(); self.clear();self.visible = false;};
			
			self.validate = function() {
				var ret = true;
				
				ret = (ret && self.drug.validate());
				ret = (ret &&  self.freq.validate());
				ret = (ret &&  self.dose.validate());
				ret = (ret &&  self.quantity.validate());
				ret = (ret &&  self.start.validate());
				ret = (ret &&  self.duration.validate());
				ret = (ret &&  self.instructions.validate());
				
				return ret;
			};
			
			
			self.clear = function() {
				self.drug.clear();
				self.freq.clear();
				self.dose.clear();
				self.quantity.clear();
				self.start.clear();
				self.duration.clear();
				self.instructions.clear();
				self.duration.field_check.attr( "checked", false);
				self.freq.prn.attr("checked", false);
			};
			
			self.getFilledData = function () {

				var f = function (n) {return "" + (n.getMonth()+1) + "/" + n.getDate()+ "/"+  n.getFullYear();};
				
				var drugOrderItem = {
						  patientId: 	patientId,	
						  drugId: 		self.drug.field.attr("numId"),	
						  dose:			self.dose.field.val(),	
						  units: 		self.dose.field_unit.val(),	
						  frequency: 	"" + self.freq.field.val() + " times a " + self.freq.field_unit.val(),	
						  instructions: self.instructions.field.val(),	
						  startDate: 	new Date(self.start.field.val()),
						  prn:			self.freq.prn.attr( "checked" ),
						};
								
				
				if (self.quantity.field.val() != "") {
					drugOrderItem.quantity = self.quantity.field.val();
				}
				
				if (self.quantity.field.val() && self.quantity.field.val() != "")
					drugOrderItem.instructions += " (quantity: " +self.quantity.field.val()+" "+self.quantity.field_unit.val()+")"; 
				
						
				if (!self.duration.field_check.attr( "checked" )) {
					var durMs = (parseInt(self.duration.field.val())) * 24 * 3600 * 1000;
					var durationUnit =  self.duration.field_unit.val();
					
					if  (durationUnit === "week") {
						durMs *= 7;
					} else if ( self.duration.field_unit.val() === "month") {
						durMs *= 30;
					}
					
					var end = new Date (drugOrderItem.startDate.getTime() + durMs);

					drugOrderItem.autoExpireDate = f(end);
				}
				
				if (drugOrderItem.autoExpireDate == "")
					drugOrderItem.autoExpireDate = null;
				
				drugOrderItem.startDate = f(drugOrderItem.startDate);
				
				return drugOrderItem;
			};
			
			self.create = function () {
				self.freq.populate();
				self.dose.populate();
				self.quantity.populate();
				self.duration.populate();
				self.clear();
				self.hide();
			};
			
			self.submit = function () {
				if(!drugOrderForm.validate())
					return;				
				
				var drugOrder = self.getFilledData();
				self.hide();
				DWROrderService.createDrugOrderByObject(drugOrder, function(d) {if (d) {regimentTables.refreshTables();} } );
				
				
			};
		};
		
		$j(document).ready(function(){
			drugOrderForm.create();
			regimentTables.refreshTables();
		});
		
		var regimentTables = new function(){

			<openmrs:fieldGen type="org.openmrs.DrugOrder.discontinuedReason" formFieldName="discReasons" val="" parameters="optionHeader=[blank]|jsVar=discReasons|globalProp=concept.reasonOrderStopped" />
		
		
			var DrugTable = function (id, drugorder, isCurrent) {
				
				var self = this;
				
				self.table = $j("#drugorder_template").clone();
				self.table.attr("id",drugorder.orderId + "drugOrederForm");
				self.table.addClass("drugorder_table");
				
				self.name = self.table.find(".drugorder_drugname");
				self.prescriber = self.table.find(".drugorder_prescriber");

				self.details = self.table.find(".drugorder_details");
				self.instructions = self.table.find(".drugorder_instructions");

				self.del = self.table.find(".drugorder_delete");
				self.stop = self.table.find(".drugorder_stop");
	
				
				var sform = self.table.find("#drugorder_stop_form");
				var dform = self.table.find("#drugorder_del_form");
				
				self.sform = sform;
				self.dform = dform;
				
				sform.find(".drugorder_stop_form_date").attr("id", drugorder.orderId + "date_picker" + (datePickerLongIdGenerationNUmber++));
				
				<openmrs:hasPrivilege privilege="Manage Orders" inverse="true">
					self.name.append(drugorder.drugName +  " (" +drugorder.dose+" "+drugorder.units+ ")");
				</openmrs:hasPrivilege>
				<openmrs:hasPrivilege privilege="Manage Orders">
				if (isCurrent) {
					self.name.append("<a class=\"patientRegimenDrugName\" href=\"admin/orders/orderDrug.form?orderId=" + drugorder.orderId + "\">" + drugorder.drugName+ " (" +drugorder.dose+" "+drugorder.units+ ")" + "</a>");
				} else {
					self.name.append(drugorder.drugName +  " (" +drugorder.dose+" "+drugorder.units+ ")");
				}
				</openmrs:hasPrivilege>

				if (isCurrent) {
					
					if (drugorder.ordererName != null && drugorder.ordererName.length > 0) {	
						self.prescriber.append("Prescribed by "+drugorder.ordererName + " in " + drugorder.createdDate + ".");
					} else if (drugorder.creatorName != null && drugorder.creatorName.length > 0) {
						self.prescriber.append("Prescribed by "+drugorder.creatorName + " in " + drugorder.createdDate + ".");
					} else {
						DWRPersonService.getPerson(drugorder.creatorId, function (d) {					
							self.prescriber.append("Prescribed by "+d.personName + " in " + drugorder.createdDate + ".");
						});
					}
					

					if (drugorder.instructions != null && drugorder.instructions != "") 
						self.instructions.append(drugorder.instructions + ".");

				} else {

					self.showed = false;
					self.table.fadeTo(0, 0.5);
					self.table.find(".drugorder_tr").hide();
					
					self.table.find(".drugorder_title").click(function() {
						if(self.showed) {
							self.showed = false;
							self.table.find(".drugorder_tr").hide();
							self.table.fadeTo(0, 0.5);
						} else {
							self.showed = true;
							self.table.fadeTo(0, 1);
							self.table.find(".drugorder_tr").show();
						}
					});
		
					var inst = "";
					if (drugorder.discontinued) {

						self.prescriber.append("DISCONTINUED");
						if (drugorder.discontinuerName != null && drugorder.discontinuerName.length > 0) {
							
							if (drugorder.instructions != null && drugorder.instructions != "") 
								inst = drugorder.instructions + ". ";
							
							inst += "Discontinued by "+drugorder.discontinuerName + " in " + drugorder.discontinuedDate + ". Reason: "+getReason(drugorder.discontinueReason)+". ";
							
							if (drugorder.ordererName != null && drugorder.ordererName.length > 0) {
								inst = inst + ("Prescribed by "+drugorder.ordererName + " in " + drugorder.createdDate + ".");
							} else if (drugorder.creatorName != null && drugorder.creatorName.length > 0) {
								inst = inst + ("Prescribed by "+drugorder.creatorName + " in " + drugorder.createdDate + ".");
							}
							
							self.instructions.append(inst);						
							
						} else {
						
							DWRPersonService.getPerson(drugorder.discontinuerId, function (d) {
		
								if (drugorder.instructions != null && drugorder.instructions != "") 
									inst = drugorder.instructions + ". ";
								
								inst += "Discontinued by "+d.personName + " in " + drugorder.discontinuedDate + ". Reason: "+ getReason(drugorder.discontinueReason)+". ";
		
								if (drugorder.ordererName != null && drugorder.ordererName.length > 0) {
									inst = inst + ("Prescribed by "+drugorder.ordererName + " in " + drugorder.createdDate + ".");
								} else if (drugorder.creatorName != null && drugorder.creatorName.length > 0) {
									inst = inst + ("Prescribed by "+drugorder.creatorName + " in " + drugorder.createdDate + ".");
								}
								
								self.instructions.append(inst);
							});
						};
						
					} else {
						
						self.prescriber.append("AUTO-EXPIRED");
						
						if (drugorder.instructions != null && drugorder.instructions != "") 
							inst = drugorder.instructions + ". ";

						if (drugorder.autoExpireDate != null)
							inst = inst + ("Auto-expired in " + drugorder.autoExpireDate + ". ");

						if (drugorder.ordererName != null && drugorder.ordererName.length > 0) {
							inst = inst + ("Prescribed by "+drugorder.ordererName + " in " + drugorder.createdDate + ".");
						} else if (drugorder.creatorName != null && drugorder.creatorName.length > 0) {
							inst = inst + ("Prescribed by "+drugorder.creatorName + " in " + drugorder.createdDate + ".");
						}
						
						self.instructions.append(inst);						
					};				
				}
				

				var details = "";
				
				if (drugorder.frequency != null || drugorder.frequency != "")
					details = (details + drugorder.frequency);
				
				if (drugorder.prn != null)
					details = (details + (drugorder.prn? " (as needed)" : ""));
				
				details = (details + ".");
				
				if (drugorder.autoExpireDate == null || drugorder.autoExpireDate == "") {
					details += " Start: " + drugorder.startDate;
				} else {
					details += " From " + drugorder.startDate + " to " + drugorder.autoExpireDate + ".";
				}
				
				self.details.append(details);
			
				self.del.click(function() {
					self.del.hide(); 
					self.stop.hide();
					sform.hide();
					dform.show();
				});
				
				self.stop.click(function() {
					self.del.hide(); 
					self.stop.hide();
					dform.hide();
					sform.show();
				});
				
				
				sform.find("#drugorder_stop_form_stop").click(function(){
					
					var res = sform.find("select").val();
					var date = sform.find(".drugorder_stop_form_date").val();
					
					if (date == null || date =="") {
						alert("<openmrs:message code="DrugOrder.drugSet.discontinue.error.noDate" />");
						return;
					}
					
					if (res == null || res == "") {
						alert("Please, select a reason.");
						return;						
					}
										
					//console.log(drugorder.orderId +" " + res + "  " +  date);
					DWROrderService.discontinueOrder(drugorder.orderId, res, date, function () {regimentTables.refreshTables();});
				});
				
				sform.find("#drugorder_stop_form_cancel").click(function(){
					self.del.show(); 
					self.stop.show();
					dform.hide();
					sform.hide();					
				});
				
				dform.find("#drugorder_del_form_del").click(function(){
					var res = dform.find("select").val();
					
					if (res == null || res == "") {
						alert("Please, select a reason.");
						return;						
					}

					console.log(drugorder.orderId +" " + res);
					DWROrderService.voidOrder(drugorder.orderId, res, function () {regimentTables.refreshTables();});
					
				});
				
				dform.find("#drugorder_del_form_cancel").click(function(){
					self.del.show(); 
					self.stop.show();
					dform.hide();
					sform.hide();					
				});
				
				var sel = sform.find("select");
				
				discReasons.forEach(function(e, i){sel.append("<option value="+e.val+">"+e.display.toUpperCase()+"</option>");});
				
				self.envolve = function() {
					var tr = $j(document.createElement("tr"));
					
					tr.append("<td colspan=\"3\" width=\"100%\"></td>").find("td").append(self.table);	
					return tr;
				};
				
				
				self.show = function() {self.table.show();}; 				
			}
			
			var self = this;
			
			self.refreshing = false;
			self.drugSetNames = displayDrugSetIds.split(",");
			self.names  = [];
			self.currentTables = {};
			self.completedTables = {};
			
			self.removeTables = function () {$j(".drugorder_table").parent().parent().remove();};
			
			<c:forTokens var="drugSetId" items="${model.displayDrugSetIds}" delims=",">
				var tname = "${fn:replace(drugSetId, " ", "_")}";
				
				<c:if test="${drugSetId == '*'}" >
				tname = "other";

				</c:if>

				self.names["${drugSetId}"] = tname;
				self.currentTables[tname] = $j("#regimenTableCurrent_"+tname);
				self.completedTables[tname] = $j("#regimenTableCompleted_"+tname);
			
			</c:forTokens>

			self.tdata = {};
			self.ddata = {};
			
			
			self.addToCurrent = function (drugOrder) {

				var t = new DrugTable(drugOrder.ortherId+drugOrder.drugSetLabel, drugOrder, true);
				
				self.currentTables[drugOrder.drugSetLabel].find(".noDrugsOrderRow").hide();
				self.currentTables[drugOrder.drugSetLabel].append(t.envolve()); 				
				t.show();
			} 
			
			self.addToCompleted = function (drugOrder) {

				var t = new DrugTable(drugOrder.ortherId+drugOrder.drugSetLabel, drugOrder, false);
				
				self.completedTables[drugOrder.drugSetLabel].find(".noDrugsOrderRow").hide();
				self.completedTables[drugOrder.drugSetLabel].append(t.envolve());
				t.sform.children().remove();
				t.stop.remove();	
				t.show();
			}
			
			
			self.refreshTables =  function() {

				if (self.refreshing)
					return;
				
				self.refreshing = true;
				
				self.removeTables();

				self.names.forEach(function(id){
					self.completedTables[id].find(".noDrugsOrderRow").show();
					self.currentTables[id].find(".noDrugsOrderRow").show();
				});
				
	
				
				DWROrderService.getMappedDrugOrdersByPatientIdDrugSetId(patientId, displayDrugSetIds, 4, function(data){
					
					var comp = function (a, b) {var da=new Date(a.createdDate); var db=new Date(b.createdDate); return da>db?-1:1;}; 
					
					var now = new Date();
					
					data.drugSets.forEach(function(set, k) {
						
						if (set == null)
							return;
						
						var drugOrderList = data.drugsMap[set]; 
						
						if (drugOrderList == null || drugOrderList.length == 0)
							return;
						
						drugOrderList.sort(comp);
						drugOrderList.forEach(function (drugOrder, i) {
							
							if (drugOrder.drugSetLabel == "*") 
								drugOrder.drugSetLabel = "other";
							
							if (drugOrder.discontinued) {
								self.addToCompleted(drugOrder);
							} else {
								if (drugOrder.autoExpireDate == null || drugOrder.autoExpireDate == "") {
									self.addToCurrent(drugOrder);
								}else {
									var ddate  = new Date(drugOrder.autoExpireDate);
									
									if (ddate > now) {
										self.addToCurrent(drugOrder);
									} else {
										self.addToCompleted(drugOrder);
									};
								};
							};
														
						});
					});
				});
				
				/*
				self.drugSetNames.forEach( function(id) {
					
					if ( id == '*' ) {
						DWROrderService.getCurrentOtherDrugOrdersByPatientIdDrugSetId(patientId, displayDrugSetIds, function(data) {current(self.names[id], data)});
						DWROrderService.getCompletedOtherDrugOrdersByPatientIdDrugSetId(patientId, displayDrugSetIds, function(data) {completed(self.names[id],data)});
					} else {
						DWROrderService.getCurrentDrugOrdersByPatientIdDrugSetId(patientId, id, function(data) {current(self.names[id],data)});
						DWROrderService.getCompletedDrugOrdersByPatientIdDrugSetId(patientId, id, function(data) {completed(self.names[id],data)});
					}			
				});
				*/
				
				self.refreshing = false;

				return;								
			};
					
		};

		
		var hasOrders = ${fn:length(model.currentDrugOrders)};
		//alert("hasOrders starting as " + hasOrders);

		
		function setUnitsField(unitsText) {
			dwr.util.setValue(gUnitsFieldId + "Span", unitsText);
			dwr.util.setValue(gUnitsFieldId, unitsText);
			hideOtherStandards("New");
			showAppropriateActions("New");
		}
		
		function showAppropriateActions(codeName) {
			//alert("hasOrders is " + hasOrders + " in showAppropriateActions");
			if ( hasOrders > 0 ) {
				hideDiv('add' + codeName);
				showDiv('action' + codeName);
			} else {
				hideDiv('action' + codeName);
				dwr.util.setValue('actionSelect' + codeName, 'add');
				showDiv('add' + codeName);
			}
			showDiv('cancel' + codeName);
		}



		function handleStandardActionChangeNew() {
			handleStandardActionChange("New");
		}
		
		function addNewComponent() {
			handleAddDrugOrder(${model.patientId}, 'drug', 'dose', 'units', 'frequencyDay', 'quantity', 'startDate', 'instructions');
		}
		//var opt = $j("#close_204_reason").find("option");
		//opt.each(function(i,e){ var je = $j(e); je.html( je.html().toUpperCase() ) } );
		//$j(document).ready(function() { refreshRegimenTables(); } );

		// end -->
		
	</script>
</div>
