/*******************************************************************************************************
 *
 * BDIPlanType.java, in msi.gaml.architecture.simplebdi, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.extension.unity.types;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

/**
 * The Class BDIPlanType.
 */
@type (
		name = "unity_aspect",
		id = UnityAspectType.UNITYASPECTTYPE,
		wraps = { UnityAspectType.class },
		concept = { IConcept.TYPE, "Unity" })
@doc ("a type representing a geometry to send to Unity")
public class UnityAspectType extends GamaType<UnityAspect> {

	/** The Constant id. */
	public final static int UNITYASPECTTYPE = IType.AVAILABLE_TYPES + 352583;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	@doc ("cast an object into a GeometryToSend if it is an instance of a GeometryToSend")
	public UnityAspect cast(final IScope scope, final Object obj, final Object val, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof UnityAspect) return (UnityAspect) obj;
		return null;
	}

	@Override
	public UnityAspect getDefault() { return null; }

}
