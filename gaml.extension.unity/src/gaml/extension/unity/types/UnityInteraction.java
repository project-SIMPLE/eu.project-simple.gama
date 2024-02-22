/*******************************************************************************************************
 *
 * BDIPlan.java, in msi.gaml.architecture.simplebdi, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.extension.unity.types;


import java.awt.Color;
import java.util.Map;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.JsonValue;
import msi.gaml.types.IType;

/**
 * The Class BDIPlan.
 */
/**
 * 
 */
@vars ({ @variable (
		name = "has_collider",
		type = IType.BOOL,
		doc = @doc ("has the geometry a collider")),
@variable (
		name = "is_trigger",
		type = IType.BOOL,
		doc = @doc ("if true, the collider of this geometry is only used to trigger events and not by the physic engine")),
@variable (
		name = "is_interactable",
		type = IType.BOOL,
		doc = @doc ("is the geometry interactable")),
@variable (
	name = "is_grabable",
	type = IType.BOOL,
	doc = @doc ("is the geometry grabable (interaction with Ray interactor otherwise"))

})
public class UnityInteraction implements IValue {

	private boolean collider;
	private boolean interactable;
	private boolean grabable;
	private boolean trigger;
	
	public UnityInteraction( boolean collider,
			boolean interactable, boolean grabable, boolean trigger) {
		super();
		this.collider = collider;
		this.interactable = interactable;
		this.grabable = grabable;
		this.trigger = trigger;
	}
	

	
	public boolean isCollider() {
		return collider;
	}

	public boolean isInteractable() {
		return interactable;
	}

	public boolean isGrabable() {
		return grabable;
	}
	public boolean isTrigger() {
		return trigger;
	}
	

	public Map<String, Object> toMap() {
		Map<String, Object> map = GamaMapFactory.create();
		map.put("interaction", collider);
		map.put("isInteractable",interactable);
		map.put("isGrabable", grabable);
		map.put("hasCollider", collider);
		map.put("isTrigger", trigger);
		return map;
	}

	@Override
	public String toString() {
		return (collider ? "- has_collider" : "") +  (interactable ? "- is_interactable" : "") + (grabable ? "- is_grabable" : "") + (trigger ? "- is_trigger" : ""); 
	}

	
	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return toString();
	}


	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		return null;
	}

	@Override
	public JsonValue serializeToJson(Json json) {
		// TODO Auto-generated method stub
		return null;
	}

}
