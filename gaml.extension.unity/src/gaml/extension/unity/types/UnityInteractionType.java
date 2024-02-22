package gaml.extension.unity.types;

import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

@type (
		name = "unity_interaction",
		id = UnityInteractionType.UNITYINTERACTIONTYPE_ID,
		wraps = { UnityInteraction.class },
		concept = { IConcept.TYPE, "Unity" })
@doc ("a type representing a set of properties concerning the interaction for the geometry/agent to send to Unity")
public class UnityInteractionType extends GamaType<UnityInteraction> {

	/** The Constant id. */
	public final static int UNITYINTERACTIONTYPE_ID = IType.AVAILABLE_TYPES + 383736;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	@doc ("cast an object into a unity_interaction if it is an instance of a unity_interaction")
	public UnityInteraction cast(final IScope scope, final Object obj, final Object val, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof UnityInteraction) return (UnityInteraction) obj;
		return null;
	}

	@Override
	public UnityInteraction getDefault() { return null; }

}
