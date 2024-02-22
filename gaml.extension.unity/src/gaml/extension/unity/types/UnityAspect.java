/*******************************************************************************************************
 *
 * UnityAspect.java, in gaml.extension.unity, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.extension.unity.types;

import java.awt.Color;
import java.util.Map;

import gama.annotations.precompiler.GamlAnnotations.doc;
import gama.annotations.precompiler.GamlAnnotations.getter;
import gama.annotations.precompiler.GamlAnnotations.variable;
import gama.annotations.precompiler.GamlAnnotations.vars;
import gama.core.common.interfaces.IValue;
import gama.core.runtime.IScope;
import gama.core.runtime.exceptions.GamaRuntimeException;
import gama.core.util.GamaColor;
import gama.core.util.GamaMapFactory;
import gama.core.util.file.json.Json;
import gama.core.util.file.json.JsonValue;
import gama.gaml.types.IType;

/**
 * The Class UnityAspect.
 */
@vars ({ @variable (
		name = "prefab",
		type = IType.STRING,
		doc = @doc ("The prefab used to display the agent/geometry")),
		@variable (
				name = "size",
				type = IType.FLOAT,
				doc = @doc ("The size (scale) of the prefab used to display the geometry")),
		@variable (
				name = "rotation_coeff",
				type = IType.FLOAT,
				doc = @doc ("Prefab: the rotation coefficient applied to the rotation along the y axe of the prefab used to display the agent/geometry")),
		@variable (
				name = "rotation_offset",
				type = IType.FLOAT,
				doc = @doc ("Prefab: The rotation offset applied to the rotation along the y axe of the prefab used to display the agent/geometry")),
		@variable (
				name = "y_offset",
				type = IType.FLOAT,
				doc = @doc ("Prefab: The offset translation along the y axe applied to the prefab used to display the agent/geometry")),
		@variable (
				name = "height",
				type = IType.FLOAT,
				doc = @doc ("Geometry: height of the geometry displayed in Unity (extrusion along the y-axe); if the height is 0, the geometry is considered as 2D")),
		@variable (
				name = "color",
				type = IType.COLOR,
				doc = @doc ("Geometry: color of the geometry displayed in Unity"))

})
public class UnityAspect implements IValue {

	/** The prefab. */
	private String prefab;

	/** The size. */
	private double size;

	/** The rotation coeff. */
	private double rotation_coeff;

	/** The rotation offset. */
	private double rotation_offset;

	/** The y offset. */
	private double y_offset;

	/** The height. */
	private double height;

	/** The color. */
	private final GamaColor color;

	/** The prefab aspect. */
	private final boolean prefabAspect;

	/** The precision. */
	private final int precision;

	/**
	 * Instantiates a new unity aspect.
	 *
	 * @param height
	 *            the height
	 * @param color
	 *            the color
	 * @param precision
	 *            the precision
	 */
	public UnityAspect(final double height, final GamaColor color, final int precision) {
		this.precision = precision;
		this.height = height;
		this.color = color;
		this.prefabAspect = false;
	}

	/**
	 * Instantiates a new unity aspect.
	 *
	 * @param prefab
	 *            the prefab
	 * @param size
	 *            the size
	 * @param rotation_coeff
	 *            the rotation coeff
	 * @param rotation_offset
	 *            the rotation offset
	 * @param y_offset
	 *            the y offset
	 * @param precision
	 *            the precision
	 */
	public UnityAspect(final String prefab, final double size, final double rotation_coeff,
			final double rotation_offset, final double y_offset, final int precision) {
		this.precision = precision;
		this.prefab = prefab;
		this.size = size;
		this.rotation_coeff = rotation_coeff;
		this.rotation_offset = rotation_offset;
		this.y_offset = y_offset;
		this.color = GamaColor.get(Color.gray);
		this.prefabAspect = true;
	}

	/**
	 * Checks if is prefab aspect.
	 *
	 * @return true, if is prefab aspect
	 */
	public boolean isPrefabAspect() { return prefabAspect; }

	/**
	 * Gets the prefab.
	 *
	 * @return the prefab
	 */
	@getter ("prefab")
	public String getPrefab() { return prefab; }

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	@getter ("size")
	public double getSize() { return size; }

	/**
	 * Gets the rotation coeff.
	 *
	 * @return the rotation coeff
	 */
	@getter ("rotation_coeff")
	public double getRotation_coeff() { return rotation_coeff; }

	/**
	 * Gets the rotation offset.
	 *
	 * @return the rotation offset
	 */
	@getter ("rotation_offset")
	public double getRotation_offset() { return rotation_offset; }

	/**
	 * Gets the y offset.
	 *
	 * @return the y offset
	 */
	@getter ("y_offset")
	public double getY_offset() { return y_offset; }

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	@getter ("height")
	public double getHeight() { return height; }

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	@getter ("color")
	public GamaColor getColor() { return color; }

	/**
	 * To map.
	 *
	 * @return the map
	 */
	public Map<String, Object> toMap() {
		Map<String, Object> map = GamaMapFactory.create();
		map.put("hasPrefab", prefabAspect);

		if (getPrefab() != null && !getPrefab().isBlank()) {
			map.put("prefab", prefab);
			map.put("size", (int) (size * precision));
			map.put("rotationCoeff", (int) (rotation_coeff * precision));
			map.put("rotationOffset", (int) (rotation_offset * precision));
			map.put("yOffset", (int) (y_offset * precision));
		} else {
			map.put("height", (int) (height * precision));
			map.put("is3D", height != 0.0);
			map.put("red", color.red());
			map.put("green", color.green());
			map.put("blue", color.blue());
			map.put("alpha", color.alpha());
		}
		return map;
	}

	@Override
	public String toString() {
		if (prefab != null && !prefab.isBlank())
			return prefab + " - " + size + " - " + rotation_coeff + " - " + rotation_offset + " - " + y_offset;

		return "geometry - " + height + " - " + color;
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
