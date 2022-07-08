/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.core.entity;

/**
 *
 * @author cripan
 */
public class Parameters {

	// =================================================================================================
	// members
	String name;
	String type;
	String style;
	String required;

	// =================================================================================================
	// assistant methods

	// -------------------------------------------------------------------------------------------------
	public Parameters(String name, String type, String style, String required) {
		this.name = name;
		this.type = type;
		this.style = style;
		this.required = required;
	}

	// -------------------------------------------------------------------------------------------------
	public String getName() { return name; }
	public String getType() { return type; }
	public String getStyle() { return style; }
	public String getRequired() { return required; }
	
	// -------------------------------------------------------------------------------------------------
	public void setName(String name) { this.name = name; }
	public void setType(String type) { this.type = type; }
	public void setStyle(String style) { this.style = style; }
	public void setRequired(String required) { this.required = required; }

	// -------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return "Param{" + "name=" + name + ", type=" + type + ", style=" + style + ", required=" + required + '}';
	}

}
