/*******************************************************************************************************
 *
 * Operators.java, in gaml.extension.unity, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.extension.unity.operators;

import gama.annotations.precompiler.GamlAnnotations.doc;
import gama.annotations.precompiler.GamlAnnotations.example;
import gama.annotations.precompiler.GamlAnnotations.no_test;
import gama.annotations.precompiler.GamlAnnotations.operator;
import gama.core.runtime.exceptions.GamaRuntimeException;
import gama.core.util.GamaColor;
import gaml.extension.unity.types.UnityAspect;
import gaml.extension.unity.types.UnityInteraction;
import gaml.extension.unity.types.UnityProperties;

/**
 * The Class Operators.
 */
public class Operators {

	/**
	 * New unity prefab aspect.
	 *
	 * @param prefabPath
	 *            the prefab path
	 * @param size
	 *            the size
	 * @param yOffset
	 *            the y offset
	 * @param rotationCoeff
	 *            the rotation coeff
	 * @param rotationOffset
	 *            the rotation offset
	 * @param precision
	 *            the precision
	 * @return the unity aspect
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "prefab_aspect",
			can_be_const = true,
			category = { "Unity" },
			concept = { "Unity" })
	@doc (
			value = "creates a new unity prefab aspect for Unity with the given properties: path of the prefab, size, y-offset, rotation coeff, rotation offset",
			masterDoc = true,
			examples = @example (
					value = "prefab_aspect(\"Prefabs/Car\",1.0,0.5,1.0,90.0)",
					isExecutable = false))
	@no_test
	public static UnityAspect newUnityPrefabAspect(final String prefabPath, final double size, final double yOffset,
			final double rotationCoeff, final double rotationOffset, final int precision) throws GamaRuntimeException {
		return new UnityAspect(prefabPath, size, rotationCoeff, rotationOffset, yOffset, precision);
	}

	/**
	 * New unity geometry aspect.
	 *
	 * @param height
	 *            the height
	 * @param color
	 *            the color
	 * @param precision
	 *            the precision
	 * @return the unity aspect
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "geometry_aspect",
			can_be_const = true,
			category = { "Unity" },
			concept = { "Unity" })
	@doc (
			value = "creates a new unity geometry aspect for Unity with the given properties: geometry to display, height, color",
			masterDoc = true,
			examples = @example (
					value = "geometry_aspect(10.0, #red)",
					isExecutable = false))
	@no_test
	public static UnityAspect newUnityGeometryAspect(final double height, final GamaColor color, final int precision)
			throws GamaRuntimeException {
		return new UnityAspect(height, color, precision);
	}

	/**
	 * New unity geometry grabable.
	 *
	 * @param n
	 *            the n
	 * @return the unity interaction
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "geometry_grabable",
			can_be_const = true,
			category = { "Unity" },
			concept = { "Unity" })
	@doc (
			value = "creates a new unity interaction for Unity for a grabable geometry",
			masterDoc = true,
			examples = @example (
					value = "geometry_grabable()",
					isExecutable = false))
	@no_test
	public static UnityInteraction newUnityGeometryGrabable(final boolean n) throws GamaRuntimeException {
		return new UnityInteraction(true, true, true, false);
	}

	/**
	 * New unity geometry ray.
	 *
	 * @param trigger
	 *            the trigger
	 * @return the unity interaction
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "geometry_ray",
			can_be_const = true,
			category = { "Unity" },
			concept = { "Unity" })
	@doc (
			value = "creates a new unity interaction for Unity for a geometry interactable with a ray interactor with the given property: is_trigger; if true, the collider of this geometry is only used to trigger events and not by the physic engine ",
			masterDoc = true,
			examples = @example (
					value = "geometry_ray(true)",
					isExecutable = false))
	@no_test
	public static UnityInteraction newUnityGeometryRay(final boolean trigger) throws GamaRuntimeException {
		return new UnityInteraction(true, true, false, trigger);
	}

	/**
	 * New unity geometry interaction.
	 *
	 * @param collider
	 *            the collider
	 * @param interactable
	 *            the interactable
	 * @param grabable
	 *            the grabable
	 * @param trigger
	 *            the trigger
	 * @return the unity interaction
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "new_geometry_interaction",
			can_be_const = true,
			category = { "Unity" },
			concept = { "Unity" })
	@doc (
			value = "creates a new unity interaction for Unity for a geometry with the given properties: has_collider,  is_interactable, is_grabable, is_trigger",
			masterDoc = true,
			examples = @example (
					value = "new_geometry_interaction(true, false,false,false)",
					isExecutable = false))
	@no_test
	public static UnityInteraction newUnityGeometryInteraction(final boolean collider, final boolean interactable,
			final boolean grabable, final boolean trigger) throws GamaRuntimeException {
		return new UnityInteraction(collider, interactable, grabable, trigger);
	}

	/**
	 * New unity geometryto send.
	 *
	 * @param name
	 *            the name
	 * @param tag
	 *            the tag
	 * @param aspect
	 *            the aspect
	 * @param interaction
	 *            the interaction
	 * @return the unity properties
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "geometry_properties",
			can_be_const = true,
			category = { "Unity" },
			concept = { "Unity" })
	@doc (
			value = "creates a new geometry to send to unity with the given properties: name, tag, aspect, interaction",
			masterDoc = true,
			examples = @example (
					value = "geometry_properties(\"car\",\"car\",car_prefab,interaction )",
					isExecutable = false))
	@no_test
	public static UnityProperties newUnityGeometrytoSend(final String name, final String tag, final UnityAspect aspect,
			final UnityInteraction interaction) throws GamaRuntimeException {
		return new UnityProperties(name, tag, aspect, interaction);
	}

	/**
	 * New unity geometryto send no int.
	 *
	 * @param name
	 *            the name
	 * @param tag
	 *            the tag
	 * @param aspect
	 *            the aspect
	 * @return the unity properties
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "geometry_properties_no_interaction",
			can_be_const = true,
			category = { "Unity" },
			concept = { "Unity" })
	@doc (
			value = "creates a new geometry to send to unity with no interaction with the given properties: name, tag, aspect",
			masterDoc = true,
			examples = @example (
					value = "geometry_properties(\"car\",\"car\",car_prefab )",
					isExecutable = false))
	@no_test
	public static UnityProperties newUnityGeometrytoSendNoInt(final String name, final String tag,
			final UnityAspect aspect) throws GamaRuntimeException {
		return new UnityProperties(name, tag, aspect, new UnityInteraction(false, false, false, false));
	}

}
