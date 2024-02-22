/*******************************************************************************************************
 *
 * UnityConstantSupplier.java, in gaml.extension.unity, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.extension.unity.constants;

import gama.gaml.constants.IConstantAcceptor;
import gama.gaml.constants.IConstantsSupplier;
import gaml.extension.unity.types.UnityInteraction;

/**
 * The Class UnityConstantSupplier.
 */
public class UnityConstantSupplier implements IConstantsSupplier {

	/**
	 * Supply constants to.
	 *
	 * @param acceptor
	 *            the acceptor
	 */
	@Override
	public void supplyConstantsTo(final IConstantAcceptor acceptor) {

		// browse(Constants.class, acceptor);
		UnityInteraction noInt = new UnityInteraction(false, false, false, false);

		acceptor.accept("no_interaction", noInt, "Unity interaction with no interaction", null, false);

	}

}
