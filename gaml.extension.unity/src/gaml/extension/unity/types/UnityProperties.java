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

import java.util.Map;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.JsonValue;
import msi.gaml.types.IType;

/**
 * The Class BDIPlan.
 */

@vars ({ @variable (
		name = "id",
		type = IType.STRING,
		doc = @doc ("The id of the Unity properties")),
	@variable (
		name = "aspect",
		type = UnityAspectType.UNITYASPECTTYPE,
		doc = @doc ("The aspect associated to the Unity properties")),
	@variable (
				name = "tag",
				type = IType.STRING,
				doc = @doc ("the tag associated to the Unity properties")),
		@variable (
				name = "collider",
				type = IType.BOOL,
				doc = @doc ("has the geometry a collider")),
		@variable (
				name = "interactable",
				type = IType.BOOL,
				doc = @doc ("is the geometry interactable")),
		@variable (
			name = "grabable",
			type = IType.BOOL,
			doc = @doc ("is the geometry grabable (interaction with Ray interactor otherwise")),
					
		 })
public class UnityProperties implements IValue {

	private String id;
	private UnityAspect aspect;
	private String tag;
	private boolean collider;
	private boolean interactable;
	private boolean grabable;
	



	public UnityProperties(String id, UnityAspect aspect, String tag,  boolean collider,
			boolean interactable, boolean grabable) {
		super();
		this.id = id;
		this.aspect = aspect;
		this.tag = tag;
		this.collider = collider;
		this.interactable = interactable;
		this.grabable = grabable;
	}

	public String getId() {
		return id;
	}

	public UnityAspect getAspect() {
		return aspect;
	}

	public String getTag() {
		return tag;
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

	

	@Override
	public String toString() {
		return id + " - " + aspect + " - " + tag + (collider ? "- has_collider" : "") +  (interactable ? "- is_interactable" : "") + (grabable ? "- is_grabable" : ""); 
		//return serializeToGaml(true);
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return id + " - " + aspect + " - " + tag + (collider ? "- has_collider" : "") +  (interactable ? "- is_interactable" : "") + (grabable ? "- is_grabable" : ""); 
		
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = GamaMapFactory.create();
		map.put("id", id);
		map.putAll(aspect.toMap());
		map.put("tag", tag);
		map.put("collider", collider);
		map.put("interactable",interactable);
		map.put("grabable", grabable);
		map.put("collider", collider);
		
		return map;
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
