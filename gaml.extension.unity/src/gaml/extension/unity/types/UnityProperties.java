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

	private String id;
	private UnityAspect aspect;
	private UnityInteraction interaction;
	private String tag;
	private boolean toFollow;
	



	public UnityProperties(String id,  String tag, UnityAspect aspect, UnityInteraction interaction, boolean toFollow) {
		super();
		this.id = id;
		this.aspect = aspect;
		this.tag = tag;
		this.interaction = interaction;
		this.toFollow = toFollow;
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
	public UnityInteraction getInteraction() {
		return interaction;
	}

	

	public boolean isToFollow() {
		return toFollow;
	}

	@Override
	public String toString() {
		return id + " - " + aspect + " - " + interaction + " - " + tag ;
		
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return toString() ;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = GamaMapFactory.create();
		map.put("id", id);
		map.put("tag", tag);
		map.putAll(aspect.toMap());
		map.putAll(interaction.toMap());
		map.put("toFollow", toFollow);
		
		
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
