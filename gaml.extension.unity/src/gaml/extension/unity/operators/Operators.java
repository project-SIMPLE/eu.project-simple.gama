package gaml.extension.unity.operators;

import gaml.extension.unity.types.UnityProperties;
import gaml.extension.unity.types.UnityAspect;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;

public class Operators {
	
	
	@operator (
			value = "prefab_aspect",
			can_be_const = true,
			category = { "Unity" },
			concept = {"Unity"})
	@doc (
			value = "creates a new unity prefab aspect for Unity with the given properties: path of the prefab, size, y-offset, rotation coeff, rotation offset",
			masterDoc = true,
			examples = @example (
					value = "prefab_aspect(\"Prefabs/Car\",1.0,0.5,1.0,90.0)",
					isExecutable = false))
	@no_test
	public static UnityAspect newUnityPrefabAspect(final String prefabPath, final double size, final double yOffset, final double rotationCoeff, final double rotationOffset, final int precision) throws GamaRuntimeException {
		return new UnityAspect(prefabPath, size, rotationCoeff,  rotationOffset, yOffset, precision);
	}


	@operator (
			value = "geometry_aspect",
			can_be_const = true,
			category = { "Unity" },
			concept = {"Unity"})
	@doc (
			value = "creates a new unity geometry aspect for Unity with the given properties: geometry to display, height, color",
			masterDoc = true,
			examples = @example (
					value = "geometry_aspect(10.0, #red)",
					isExecutable = false))
	@no_test
	public static UnityAspect newUnityGeometryAspect(final double height, final GamaColor color, final int precision) throws GamaRuntimeException {
		return new UnityAspect(height,color, precision); 
	}
	

	@operator (
			value = "geometry_properties",
			can_be_const = true,
			category = { "Unity" },
			concept = {"Unity"})
	@doc (
			value = "creates a new geometry to send to unity with the given properties: name, aspect, tag, layer name, has a collider, is interactable, is grabable, is static, frequency of sending to unity",
			masterDoc = true,
			examples = @example (
					value = "geometry_properties(\"car\",car_prefab, \"car\", true,true, false )",
					isExecutable = false))
	@no_test
	public static UnityProperties newUnityGeometrytoSend(String name, UnityAspect aspect, String tag, boolean collider,
			boolean interactable, boolean grabable) throws GamaRuntimeException {
		return new UnityProperties(name, aspect, tag, collider,
				interactable, grabable);
	}


}
