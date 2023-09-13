package gaml.extension.unity.skills;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.skills.Skill;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement.WithArgs;
import msi.gaml.types.IType;

@vars ({ @variable (
		name = SentToUnitySkill.HEADING,
		type = IType.FLOAT,
		doc = @doc ("the heading of the agent to send")),
		@variable (
				name = SentToUnitySkill.SPECIES_INDEX,
				type = IType.INT,
				doc = @doc ("the index of the species"))
})
	@doc ("The sent_to_unity skill is intended to define the minimal set of behaviours required for agents to be sent to unity")
	@skill (
			name = "sent_to_unity",
			concept = { IConcept.SKILL, "Unity" })
	public class SentToUnitySkill extends Skill {
		private static final String HEADING = "heading";
		private static final String ADD_TO_MAP = "add_to_map";
		
		private static final String LOC_TO_SEND = "loc_to_send";
		private static final String TO_MAP = "to_map";
		private static final String SPECIES_INDEX = "index";
		
		
		@getter (SPECIES_INDEX)
		public static Integer getIndexSpecies (final IAgent agent) {
			return (Integer) agent.getAttribute(SPECIES_INDEX);
		}
		@setter(SPECIES_INDEX)
		public static void setIndexSpecues(final IAgent agent, final Integer val) {
			agent.setAttribute(SPECIES_INDEX, val);
		}
		
		@getter (HEADING)
		public static Double getHeading(final IAgent agent) {
			return (Double) agent.getAttribute(HEADING);
		}
		@setter(HEADING)
		public static void setHeading(final IAgent agent, final Double val) {
			agent.setAttribute(HEADING, val);
		}

		@action (
				name = LOC_TO_SEND,
				doc = @doc (
						returns = "the location to send to Unity"))
		public GamaPoint primLocToSend(final IScope scope) throws GamaRuntimeException {
			return scope.getAgent().getLocation();

		}
		
		@action (
				name = ADD_TO_MAP,
						args = { @arg (
								name = "map",
								type = IType.MAP,
								doc = @doc ("map of data to send to Unity"))},
				doc = @doc (
						returns = "other elements than the location to add to the data sent to Unity"))
		public void primAddToMap(final IScope scope) throws GamaRuntimeException {
			
		}
		
		@action (
				name = TO_MAP,
						args = { @arg (
								name = "precision",
								type = IType.INT,
								doc = @doc ("precision of the data to send (number of decimals)"))},
					
				doc = @doc (
						returns = "a map containing all the information to sent to unity"))
		public IMap<String, IList<Integer>> primToMap(final IScope scope) throws GamaRuntimeException {
			Integer precision = scope.getIntArg("precision");
			IAgent ag = scope.getAgent();
			IMap<String, IList<Integer>> map = GamaMapFactory.create();
			
			IList<Integer> vals = GamaListFactory.create();
			vals.add(getIndexSpecies(ag));
			vals.add(ag.getIndex());
			vals.add((int)(ag.getLocation().x * precision));
			vals.add((int)(ag.getLocation().y * precision));
			vals.add((int)(getHeading(ag) * precision));
			map.put("v", vals);
			
			Arguments args = new Arguments();
			args.put("map", ConstantExpressionDescription.create(map));
			
			WithArgs actATM = ag.getSpecies().getAction(ADD_TO_MAP);
			
			actATM.setRuntimeArgs(scope, args);
			actATM.executeOn(scope);
			
			return map;

		}

	}
