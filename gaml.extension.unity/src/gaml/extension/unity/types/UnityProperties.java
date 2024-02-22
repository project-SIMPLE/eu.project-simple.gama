/*******************************************************************************************************
 *
 * UnityProperties.java, in gaml.extension.unity, is part of the source code of the GAMA modeling and simulation
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

@vars ({ @variable (
		name = "id",
		type = IType.STRING,
		doc = @doc ("The id of the Unity properties")),
		@variable (
				name = "aspect",
				type = UnityAspectType.UNITYASPECTTYPE_ID,
				doc = @doc ("The aspect associated to the Unity properties")),
		@variable (
				name = "interaction",
				type = UnityInteractionType.UNITYINTERACTIONTYPE_ID,
				doc = @doc ("The interaction associated to the Unity properties")),
		@variable (
				name = "tag",
				type = IType.STRING,
				doc = @doc ("the tag associated to the Unity properties"))

})
public class UnityProperties implements IValue {

	/** The id. */
	private final String id;

	/** The aspect. */
	private final UnityAspect aspect;

	/** The interaction. */
	private final UnityInteraction interaction;

	/** The tag. */
	private final String tag;

	/**
	 * Instantiates a new unity properties.
	 *
	 * @param id
	 *            the id
	 * @param tag
	 *            the tag
	 * @param aspect
	 *            the aspect
	 * @param interaction
	 *            the interaction
	 */
	public UnityProperties(final String id, final String tag, final UnityAspect aspect,
			final UnityInteraction interaction) {
		this.id = id;
		this.aspect = aspect;
		this.tag = tag;
		this.interaction = interaction;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() { return id; }

	/**
	 * Gets the aspect.
	 *
	 * @return the aspect
	 */
	public UnityAspect getAspect() { return aspect; }

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	public String getTag() { return tag; }

	/**
	 * Gets the interaction.
	 *
	 * @return the interaction
	 */
	public UnityInteraction getInteraction() { return interaction; }

	@Override
	public String toString() {
		return id + " - " + aspect + " - " + interaction + " - " + tag;

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
	 * To map.
	 *
	 * @return the map
	 */
	public Map<String, Object> toMap() {
		Map<String, Object> map = GamaMapFactory.create();
		map.put("id", id);
		map.put("tag", tag);
		map.putAll(aspect.toMap());
		map.putAll(interaction.toMap());

		return map;
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
