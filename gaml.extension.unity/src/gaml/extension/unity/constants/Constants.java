package gaml.extension.unity.constants;

import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IConstantCategory;
import gaml.extension.unity.types.UnityInteraction;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;

public interface Constants {

	@constant (
			value = "no_interaction",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT, "Unity" },
			doc = @doc ("unity_interaction with no interaction")) UnityInteraction noInt = new UnityInteraction(false, false, false, false);

	@constant (
			value = "grabable",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT, "Unity" },
			doc = @doc ("unity_interaction with a grabable interaction")) UnityInteraction grabable = new UnityInteraction(true, true, true, true);
	
	@constant (
			value = "ray_interactable",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT, "Unity" },
			doc = @doc ("unity_interaction with a ray interaction")) UnityInteraction rayInter = new UnityInteraction(true, true, false, false);

}
