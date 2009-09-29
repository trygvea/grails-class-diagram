
	<g:if test="${flash.message}">
		<div class="message">${flash.message}</div>
	</g:if>
	<g:hasErrors bean="${prefs}">
		<div class="errors">
			<g:renderErrors bean="${prefs}" as="list" />
		</div>
	</g:hasErrors>

	<div class="dialog">
		<g:form name="preferencesForm" method="post">

			<fieldset id="misc_fields" class="">
	            <label for="skin">Skin:</label>
				<g:select name="skin" value="${prefs?.skin}" from="${skins}" optionKey="key" optionValue="value"></g:select>
			</fieldset>
			
			<fieldset id="show_fields" class="">
	            <label for="show_fields_inner">Show:</label>
				<fieldset id="show_fields_inner" class="">
					<ol>
						<li>
				            <label for="showProperties" title="Show or hide properties. This includes declared properties together with implicit properties declared with getters and/or setter methods.">
								<g:checkBox name="showProperties" value="${prefs?.showProperties}" ></g:checkBox>
								Properties
							</label>
						</li>
						<li>
							<fieldset id="showPropertiesSelected" class="">
					            <label for="showPropertyType" title="Show or hide property types">
									<g:checkBox name="showPropertyType" value="${prefs?.showPropertyType}" ></g:checkBox>
									Property Type
								</label>
							</fieldset>
						</li>
						<li>
				            <label for="showMethods" title="Show or hide methods. Note that property setters and getters are not included!">
								<g:checkBox name="showMethods" value="${prefs?.showMethods}" ></g:checkBox>
								Methods
							</label>
						</li>
						<li>
							<fieldset id="showMethodsSelected" class="">
								<ol>
									<li>
							            <label for="showMethodReturnType" title="Show or hide method return types">
											<g:checkBox name="showMethodReturnType" value="${prefs?.showMethodReturnType}" ></g:checkBox>
											Method Return Type
										</label>
									</li>
									<li>
							            <label for="showMethodSignature" title="Show or hide method signature (parameter list)">
											<g:checkBox name="showMethodSignature" value="${prefs?.showMethodSignature}" ></g:checkBox>
											Method Signature
										</label>
									</li>
								</ol>
							</fieldset>
						</li>
						<li>
				            <label for="showAssociationNames" title="Show or hide the names of the associations">
								<g:checkBox name="showAssociationNames" value="${prefs?.showAssociationNames}" ></g:checkBox>
								Association Names
							</label>
						</li>
						<li>
				            <label for="showEmbeddedAsProperty" title="Show embedded references (aka Value Objects) as properties in the containing class. Uncheck to see Embedded references as first class objects. (Embedded references is declared with static embedded = [], see gorm doc.) ">
								<g:checkBox name="showEmbeddedAsProperty" value="${prefs?.showEmbeddedAsProperty}" ></g:checkBox>
								Embedded Objects as Property
							</label>
						</li>
					</ol>
				</fieldset>
			</fieldset>
			
			<fieldset id="misc_fields" class="">
				<ol>
					<li title="Changes the size of the underlying model image.">
						<input id="fontsize" name="fontsize" type="hidden" value="${prefs?.fontsize}"/>
			            <label for="fontsize_slider">Size:</label>
						<div id="fontsize_slider"></div>
					</li>
					<li title="Image format, try for instance jpg or png. See http://graphviz.org/doc/info/output.html for valid formats. Not all formats works, but most image types does. Warning: An invalid format may not give a sensible error message!">
	                    <label for="outputFormat">Output Format:</label>
						<input type="text" id="outputFormat" name="outputFormat" value="${fieldValue(bean:prefs,field:'outputFormat')}"/>
					</li>
					<li title="Graph orientation, or, in graphviz terminology: rankdir">
	                    <label for="graphOrientation">Orientation:</label>
						<g:select name="graphOrientation" from="${graphOrientations}" optionKey="key" optionValue="value"></g:select>
					</li>
					<li title="Re-order the classes in the diagram. Note: Default ordering will be restored when the diagram is changed.">
						<input id="randomizeOrder" class="autoUpdate" name="randomizeOrder" type="hidden" value="${prefs?.randomizeOrder}"/>
	                    <label for="randomize-class-order">&nbsp;</label>
						<button id="randomize-class-order" type="button">Re-order layout</button>
					</li>
				</ol>
			</fieldset>

        </div>
			<div id="autoUpdate-selection" title="Update the model image instantly when you change any preferences. Uncheck if you want to delay updating until you hit 'Update'">
	            <label for="autoUpdate">
					<g:checkBox name="autoUpdate" value="${prefs?.autoUpdate}"></g:checkBox>
					Auto Update
				</label>
			</div>
    </g:form>



