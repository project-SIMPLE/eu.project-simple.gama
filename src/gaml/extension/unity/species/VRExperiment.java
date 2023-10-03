package gaml.extension.unity.species;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementWithChildrenDescription;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

@experiment ("unity")
@vars({
	@variable(name = VRExperiment.UNITY_LINKER_SPECIES, type = IType.STRING, doc = { @doc ("Species of the unity linker agent")}),
	@variable(name = VRExperiment.DISPLAYS_TO_HIDE , type = IType.LIST, doc = { @doc ("Displays that will not be display in the experiment")})})
@doc ("Experiments design for models with a connection with Unity")
public class VRExperiment extends ExperimentAgent {

	public static final String DISPLAYS_TO_HIDE = "displays_to_hide";
	public static final String UNITY_LINKER_SPECIES = "unity_linker_species";
	
	private IAgent unityLinker = null;	public VRExperiment(IPopulation<? extends IAgent> s, int index) throws GamaRuntimeException {
		super(s, index);
	}
	
	@getter (VRExperiment.UNITY_LINKER_SPECIES)
	public static String getUnityLinkerSpecies(final IAgent agent) {
		return (String) agent.getAttribute(UNITY_LINKER_SPECIES);
	}
	@setter(VRExperiment.UNITY_LINKER_SPECIES)
	public static void setUnityLinkerSpecies(final IAgent agent, final String val) {
		agent.setAttribute(UNITY_LINKER_SPECIES, val);
	}
	
	@getter (DISPLAYS_TO_HIDE)
	public static IList<String> getDisplaysToHide(final IAgent agent) {
		return (IList<String>) agent.getAttribute(DISPLAYS_TO_HIDE);
	}
	@setter(DISPLAYS_TO_HIDE)
	public static void setDisplaysToHide(final IAgent agent, final IList<String>  val) {
		agent.setAttribute(DISPLAYS_TO_HIDE, val);
	}
	
	@Override
	public Object _init_(final IScope scope) {
		IList<String> dispToHide = getDisplaysToHide(getAgent());
		if (dispToHide != null ) {
			List<IDescription> toRemove = new ArrayList<>();
			final IDescription des = ((ISymbol) this.getSpecies().getOriginalSimulationOutputs()).getDescription();
			for (IDescription dd : des.getOwnChildren()){
				if (dispToHide.contains(dd.getName())) {
					toRemove.add(dd);
				}
			}
			((StatementWithChildrenDescription) des).getChildren().removeAll(toRemove);
		}
		Object out = super._init_(scope);
		
		
		ISpecies sp = Cast.asSpecies(scope, getUnityLinkerSpecies(getAgent()));
		sp.getPopulation(scope).createAgentAt(scope.getSimulation().getScope(), 0,  GamaMapFactory.create(), false, true);
		

		return out;
	}

	

	public IAgent getUnityLinker() {
		return unityLinker;
	}


	public void setUnityLinker(IAgent unityLinker) {
		this.unityLinker = unityLinker;
	}


	
	
	

}
