/*******************************************************************************************************
 *
 * UnityInteraction.java, in gaml.extension.unity, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.extension.unity.types;

import java.util.Map;

import gama.annotations.precompiler.GamlAnnotations.doc;
import gama.annotations.precompiler.GamlAnnotations.variable;
import gama.annotations.precompiler.GamlAnnotations.vars;
import gama.core.common.interfaces.IValue;
import gama.core.runtime.IScope;
import gama.core.runtime.exceptions.GamaRuntimeException;
import gama.core.util.GamaMapFactory;
import gama.core.util.file.json.Json;
import gama.core.util.file.json.JsonValue;
import gama.gaml.types.IType;

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

	/** The collider. */
	private final boolean collider;

	/** The interactable. */
	private final boolean interactable;

	/** The grabable. */
	private final boolean grabable;

	/** The trigger. */
	private final boolean trigger;

	/**
	 * Instantiates a new unity interaction.
	 *
	 * @param collider
	 *            the collider
	 * @param interactable
	 *            the interactable
	 * @param grabable
	 *            the grabable
	 * @param trigger
	 *            the trigger
	 */
	public UnityInteraction(final boolean collider, final boolean interactable, final boolean grabable,
			final boolean trigger) {
		this.collider = collider;
		this.interactable = interactable;
		this.grabable = grabable;
		this.trigger = trigger;
	}

	/**
	 * Checks if is collider.
	 *
	 * @return true, if is collider
	 */
	public boolean isCollider() { return collider; }

	/**
	 * Checks if is interactable.
	 *
	 * @return true, if is interactable
	 */
	public boolean isInteractable() { return interactable; }

	/**
	 * Checks if is grabable.
	 *
	 * @return true, if is grabable
	 */
	public boolean isGrabable() { return grabable; }

	/**
	 * Checks if is trigger.
	 *
	 * @return true, if is trigger
	 */
	public boolean isTrigger() { return trigger; }

	/**
	 * To map.
	 *
	 * @return the map
	 */
	public Map<String, Object> toMap() {
		Map<String, Object> map = GamaMapFactory.create();
		map.put("interaction", collider);
		map.put("isInteractable", interactable);
		map.put("isGrabable", grabable);
		map.put("hasCollider", collider);
		map.put("isTrigger", trigger);
		return map;
	}

	@Override
	public String toString() {
		return (collider ? "- has_collider" : "") + (interactable ? "- is_interactable" : "")
				+ (grabable ? "- is_grabable" : "") + (trigger ? "- is_trigger" : "");
	}

	/**
	 * String value.
	 *
	 * @param scope
	 *            the scope
	 * @return the string
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return toString();
	}

	/**
	 * Copy.
	 *
	 * @param scope
	 *            the scope
	 * @return the i value
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return null;
	}

	/**
	 * Serialize to json.
	 *
	 * @param json
	 *            the json
	 * @return the json value
	 */
	@Override
	public JsonValue serializeToJson(final Json json) {
		// TODO Auto-generated method stub
		return null;
	}

}
