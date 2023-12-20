package gaml.extension.unity.species;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.operators.Spatial;
import msi.gaml.types.IType;

@species(name = "abstract_unity_player")
@vars({ @variable(name = IKeyword.ROTATION, type = IType.FLOAT,
	doc = { @doc ("rotation to apply for the display of the agent in GAMA")}),
	@variable(name = IKeyword.COLOR, type = IType.COLOR,
			doc = { @doc ("color of the agent for the display in GAMA")}),
	@variable(name = AbstractUnityPlayer.TO_DISPLAY, type = IType.BOOL,
			doc = { @doc ("display or not the agent in GAMA")}),
	@variable(name = AbstractUnityPlayer.SELECTED, type = IType.BOOL,
	doc = { @doc ("is the agent selected")}),
	@variable(name = AbstractUnityPlayer.CONE_DISTANCE, type = IType.FLOAT,
			doc = { @doc ("distance of the cone for the display of the agent in GAMA")}),
	@variable(name = AbstractUnityPlayer.CONE_AMPLITUDE, type = IType.FLOAT,
			doc = { @doc ("amplitude of the cone for the display of the agent in GAMA")}),
	@variable(name = AbstractUnityPlayer.PLAYER_SIZE, type = IType.FLOAT, init = "3.0", 
			doc = { @doc ("Size of the player for the display of the agent in GAMA")}), 
	//@variable(name = AbstractUnityPlayer.UNITY_CLIENT, type = IType.NONE,
//		doc = { @doc ("Client for Unity")}), 
	@variable(name = AbstractUnityPlayer.PLAYER_ROTATION, type = IType.FLOAT, init = "90.0", 
	doc = { @doc ("Rotation (angle in degrees) to add to the player for the display of the agent in GAMA")}), 
	@variable(name = AbstractUnityPlayer.PLAYER_AGENTS_PERCEPTION_RADIUS, type = IType.FLOAT, init = "0.0", 
	doc = { @doc ("Allow to reduce the quantity of information sent to Unity - only the agents at a certain distance are sent")}), 
	@variable(name = AbstractUnityPlayer.PLAYER_AGENTS_MIN_DIST, type = IType.FLOAT, init = "0.0", 
			doc = { @doc ("Allow to not send to Unity agents that are to close (i.e. overlapping) ")})})
public class AbstractUnityPlayer extends GamlAgent{ 
	
	public static final String ACTION_CONE = "player_perception_cone";
	
	public static final String TO_DISPLAY = "to_display";
	public static final String SELECTED = "selected";
	public static final String CONE_DISTANCE = "cone_distance"; 
	public static final String CONE_AMPLITUDE = "cone_amplitude";
	public static final String PLAYER_AGENTS_PERCEPTION_RADIUS = "player_agents_perception_radius";
	public static final String PLAYER_AGENTS_MIN_DIST = "player_agents_min_dist";
	public static final String PLAYER_SIZE = "player_size";
	public static final String PLAYER_ROTATION = "player_rotation";
	//public static final String UNITY_CLIENT = "unity_client";
	
	
	public AbstractUnityPlayer(IPopulation<? extends IAgent> s, int index) {
		super(s, index);
	} 
	
	@getter (SELECTED)
	public static Boolean getSelected(final IAgent agent) {
		return (Boolean) agent.getAttribute(SELECTED);
	}
	@setter(SELECTED)
	public static void setSelected(final IAgent agent, final Boolean val) {
		agent.setAttribute(SELECTED, val);
	}
	@getter (TO_DISPLAY)
	public static Boolean getToDisplay(final IAgent agent) {
		return (Boolean) agent.getAttribute(TO_DISPLAY);
	}
	@setter(TO_DISPLAY)
	public static void setToDisplay(final IAgent agent, final Boolean val) {
		agent.setAttribute(TO_DISPLAY, val);
	}
	@getter (IKeyword.ROTATION)
	public static Double getRotation(final IAgent agent) {
		return (Double) agent.getAttribute(IKeyword.ROTATION);
	}
	@setter(IKeyword.ROTATION)
	public static void setRotation(final IAgent agent, final Double val) {
		agent.setAttribute(IKeyword.ROTATION, val);
	}
	
	@getter (IKeyword.COLOR)
	public static GamaColor getColor(final IAgent agent) {
		return (GamaColor) agent.getAttribute(IKeyword.COLOR);
	}
	@setter(IKeyword.COLOR)
	public static void setColor(final IAgent agent, final GamaColor val) {
		agent.setAttribute(IKeyword.COLOR, val);
	}
	
	@getter (CONE_DISTANCE)
	public static Double getConeDistance(final IAgent agent) {
		return (Double) agent.getAttribute(CONE_DISTANCE);
	}
	@setter(CONE_DISTANCE)
	public static void setConeDistance(final IAgent agent, final Double val) {
		agent.setAttribute(CONE_DISTANCE, val);
	}
	
	@getter (PLAYER_ROTATION)
	public static Double getPlayerRotation(final IAgent agent) {
		return (Double) agent.getAttribute(PLAYER_ROTATION);
	}
	@setter(PLAYER_ROTATION)
	public static void setPlayerRotation(final IAgent agent, final Double val) {
		agent.setAttribute(PLAYER_ROTATION, val);
	}	
	
	@getter (PLAYER_SIZE)
	public static Double getPlayerSize(final IAgent agent) {
		return (Double) agent.getAttribute(PLAYER_SIZE);
	}
	@setter(PLAYER_SIZE)
	public static void setPlayerSize(final IAgent agent, final Double val) {
		agent.setAttribute(PLAYER_SIZE, val);
	}	
	
	@getter (CONE_AMPLITUDE)
	public static Double getConeAmplitude(final IAgent agent) {
		return (Double) agent.getAttribute(CONE_AMPLITUDE);
	}
	@setter(CONE_AMPLITUDE)
	public static void setConeAmplitude(final IAgent agent, final Double val) {
		agent.setAttribute(CONE_AMPLITUDE, val);
	}	
	
	@getter (PLAYER_AGENTS_PERCEPTION_RADIUS)
	public static Double getPlayerAgentPerceptionRadius(final IAgent agent) {
		return (Double) agent.getAttribute(PLAYER_AGENTS_PERCEPTION_RADIUS);
	}
	@setter(PLAYER_AGENTS_PERCEPTION_RADIUS)
	public static void setPlayerAgentPerceptionRadius(final IAgent agent, final Double val) {
		agent.setAttribute(PLAYER_AGENTS_PERCEPTION_RADIUS, val);
	}
		
	@getter (PLAYER_AGENTS_MIN_DIST)
	public static Double getPlayerAgentMinDist(final IAgent agent) {
		return (Double) agent.getAttribute(PLAYER_AGENTS_MIN_DIST);
	}
	@setter(PLAYER_AGENTS_MIN_DIST)
	public static void setPlayerAgentMinDist(final IAgent agent, final Double val) {
		agent.setAttribute(PLAYER_AGENTS_MIN_DIST, val);
	}
	
	@action (
			name = ACTION_CONE,
			doc = { @doc (
					value = "Wait for the connection of a unity client and send the paramters to the client")})
	public IShape primGetCone(final IScope scope) throws GamaRuntimeException {
		return getCone(scope, getAgent());
	}
	
	private static IShape getCone(IScope scope, IAgent agent) {
		Double rotation = getRotation(agent);
		Double cone_amplitude = getConeAmplitude(agent);
		IShape g = Spatial.Creation.cone(scope, (int)(rotation - cone_amplitude/2),(int)(rotation + cone_amplitude/2));
		g = Spatial.Operators.inter(scope, g, Spatial.Creation.circle(scope, getConeDistance(agent)));
		g = Spatial.Transformations.translated_by(scope, g, new GamaPoint(0,0,4.9));
		return g;
		
	}
	

}
