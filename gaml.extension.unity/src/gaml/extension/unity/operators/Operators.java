package gaml.extension.unity.operators;

import gaml.extension.unity.types.UnityProperties;
import gaml.extension.unity.types.UnityAspect;
import gaml.extension.unity.types.UnityInteraction;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;

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
			value = "geometry_grabable",
			can_be_const = true,
			category = { "Unity" },
			concept = {"Unity"})
	@doc (
			value = "creates a new unity interaction for Unity for a grabable geometry",
			masterDoc = true,
			examples = @example (
					value = "geometry_grabable()",
					isExecutable = false))
	@no_test
	public static UnityInteraction newUnityGeometryGrabable(IList<Boolean> constraints) throws GamaRuntimeException {
		return new UnityInteraction(true,true,true,constraints); 
	}
	
	@operator (
			value = "geometry_ray",
			can_be_const = true,
			category = { "Unity" },
			concept = {"Unity"})
	@doc (
			value = "creates a new unity interaction for Unity for a geometry interactable with a ray interactor with the given property: constraints ",
			masterDoc = true,
			examples = @example (
					value = "geometry_ray(true)",
					isExecutable = false))
	@no_test
	public static UnityInteraction newUnityGeometryRay(boolean d) throws GamaRuntimeException {
		return new UnityInteraction(true,true,false,GamaListFactory.create()); 
	}
	
	
	@operator (
			value = "new_geometry_interaction",
			can_be_const = true,
			category = { "Unity" },
			concept = {"Unity"})
	@doc (
			value = "creates a new unity interaction for Unity for a geometry with the given properties: has_collider,  is_interactable, is_grabable, constraints",
			masterDoc = true,
			examples = @example (
					value = "new_geometry_interaction(true, false,false,false)",
					isExecutable = false))
	@no_test
	public static UnityInteraction newUnityGeometryInteraction( boolean collider,
			boolean interactable, boolean grabable, IList<Boolean> constraints) throws GamaRuntimeException {
		return new UnityInteraction(collider,interactable,grabable,constraints); 
	}
 
	@operator (
			value = "geometry_properties",
			can_be_const = true,
			category = { "Unity" },
			concept = {"Unity"})
	@doc (
			value = "creates a new geometry to send to unity with the given properties: name, tag, aspect, interaction",
			masterDoc = true,
			examples = @example (
					value = "geometry_properties(\"car\",\"car\",car_prefab,interaction )",
					isExecutable = false))
	@no_test
	public static UnityProperties newUnityGeometrytoSend(String name,String tag, UnityAspect aspect, UnityInteraction interaction) throws GamaRuntimeException {
		return new UnityProperties(name, tag, aspect,interaction);
	}
	
	@operator (
			value = "geometry_properties_no_interaction",
			can_be_const = true,
			category = { "Unity" },
			concept = {"Unity"})
	@doc (
			value = "creates a new geometry to send to unity with no interaction with the given properties: name, tag, aspect",
			masterDoc = true,
			examples = @example (
					value = "geometry_properties(\"car\",\"car\",car_prefab )",
					isExecutable = false))
	@no_test
	public static UnityProperties newUnityGeometrytoSendNoInt(String name,String tag, UnityAspect aspect) throws GamaRuntimeException {
		return new UnityProperties(name, tag, aspect,new UnityInteraction(false, false, false, (IList)GamaListFactory.create()));
	}



}
