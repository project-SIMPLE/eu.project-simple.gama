/*******************************************************************************************************
 *
 * AbstractUnityPlayer.java, in gaml.extension.unity, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.extension.unity.species;

import gama.annotations.precompiler.GamlAnnotations.action;
import gama.annotations.precompiler.GamlAnnotations.doc;
import gama.annotations.precompiler.GamlAnnotations.getter;
import gama.annotations.precompiler.GamlAnnotations.setter;
import gama.annotations.precompiler.GamlAnnotations.species;
import gama.annotations.precompiler.GamlAnnotations.variable;
import gama.annotations.precompiler.GamlAnnotations.vars;
import gama.core.common.interfaces.IKeyword;
import gama.core.metamodel.agent.GamlAgent;
import gama.core.metamodel.agent.IAgent;
import gama.core.metamodel.population.IPopulation;
import gama.core.metamodel.shape.GamaPoint;
import gama.core.metamodel.shape.IShape;
import gama.core.runtime.IScope;
import gama.core.runtime.exceptions.GamaRuntimeException;
import gama.core.util.GamaColor;
import gama.gaml.operators.Spatial;
import gama.gaml.types.IType;

/**
 * The Class AbstractUnityPlayer.
 */
@species (
		name = "abstract_unity_player")
@vars ({ @variable (
		name = IKeyword.ROTATION,
		type = IType.FLOAT,
		doc = { @doc ("rotation to apply for the display of the agent in GAMA") }),
		@variable (
				name = IKeyword.COLOR,
				type = IType.COLOR,
				doc = { @doc ("color of the agent for the display in GAMA") }),
		@variable (
				name = AbstractUnityPlayer.TO_DISPLAY,
				type = IType.BOOL,
				doc = { @doc ("display or not the agent in GAMA") }),
		@variable (
				name = AbstractUnityPlayer.SELECTED,
				type = IType.BOOL,
				doc = { @doc ("is the agent selected") }),
		@variable (
				name = AbstractUnityPlayer.CONE_DISTANCE,
				type = IType.FLOAT,
				doc = { @doc ("distance of the cone for the display of the agent in GAMA") }),
		@variable (
				name = AbstractUnityPlayer.CONE_AMPLITUDE,
				type = IType.FLOAT,
				doc = { @doc ("amplitude of the cone for the display of the agent in GAMA") }),
		@variable (
				name = AbstractUnityPlayer.PLAYER_SIZE,
				type = IType.FLOAT,
				init = "3.0",
				doc = { @doc ("Size of the player for the display of the agent in GAMA") }),
		@variable (
				name = AbstractUnityPlayer.PLAYER_ROTATION,
				type = IType.FLOAT,
				init = "90.0",
				doc = { @doc ("Rotation (angle in degrees) to add to the player for the display of the agent in GAMA") }),
		@variable (
				name = AbstractUnityPlayer.PLAYER_AGENTS_PERCEPTION_RADIUS,
				type = IType.FLOAT,
				init = "0.0",
				doc = { @doc ("Allow to reduce the quantity of information sent to Unity - only the agents at a certain distance are sent") }),
		@variable (
				name = AbstractUnityPlayer.PLAYER_AGENTS_MIN_DIST,
				type = IType.FLOAT,
				init = "0.0",
				doc = { @doc ("Allow to not send to Unity agents that are to close (i.e. overlapping) ") }) })
public class AbstractUnityPlayer extends GamlAgent {

	/** The Constant ACTION_CONE. */
	public static final String ACTION_CONE = "player_perception_cone";

	/** The Constant TO_DISPLAY. */
	public static final String TO_DISPLAY = "to_display";

	/** The Constant SELECTED. */
	public static final String SELECTED = "selected";

	/** The Constant CONE_DISTANCE. */
	public static final String CONE_DISTANCE = "cone_distance";

	/** The Constant CONE_AMPLITUDE. */
	public static final String CONE_AMPLITUDE = "cone_amplitude";

	/** The Constant PLAYER_AGENTS_PERCEPTION_RADIUS. */
	public static final String PLAYER_AGENTS_PERCEPTION_RADIUS = "player_agents_perception_radius";

	/** The Constant PLAYER_AGENTS_MIN_DIST. */
	public static final String PLAYER_AGENTS_MIN_DIST = "player_agents_min_dist";

	/** The Constant PLAYER_SIZE. */
	public static final String PLAYER_SIZE = "player_size";

	/** The Constant PLAYER_ROTATION. */
	public static final String PLAYER_ROTATION = "player_rotation";

	/**
	 * Instantiates a new abstract unity player.
	 *
	 * @param s
	 *            the s
	 * @param index
	 *            the index
	 */
	public AbstractUnityPlayer(final IPopulation<? extends IAgent> s, final int index) {
		super(s, index);
	}

	/**
	 * Gets the selected.
	 *
	 * @param agent
	 *            the agent
	 * @return the selected
	 */
	@getter (SELECTED)
	public static Boolean getSelected(final IAgent agent) {
		return (Boolean) agent.getAttribute(SELECTED);
	}

	/**
	 * Sets the selected.
	 *
	 * @param agent
	 *            the agent
	 * @param val
	 *            the val
	 */
	@setter (SELECTED)
	public static void setSelected(final IAgent agent, final Boolean val) {
		agent.setAttribute(SELECTED, val);
	}

	/**
	 * Gets the to display.
	 *
	 * @param agent
	 *            the agent
	 * @return the to display
	 */
	@getter (TO_DISPLAY)
	public static Boolean getToDisplay(final IAgent agent) {
		return (Boolean) agent.getAttribute(TO_DISPLAY);
	}

	/**
	 * Sets the to display.
	 *
	 * @param agent
	 *            the agent
	 * @param val
	 *            the val
	 */
	@setter (TO_DISPLAY)
	public static void setToDisplay(final IAgent agent, final Boolean val) {
		agent.setAttribute(TO_DISPLAY, val);
	}

	/**
	 * Gets the rotation.
	 *
	 * @param agent
	 *            the agent
	 * @return the rotation
	 */
	@getter (IKeyword.ROTATION)
	public static Double getRotation(final IAgent agent) {
		return (Double) agent.getAttribute(IKeyword.ROTATION);
	}

	/**
	 * Sets the rotation.
	 *
	 * @param agent
	 *            the agent
	 * @param val
	 *            the val
	 */
	@setter (IKeyword.ROTATION)
	public static void setRotation(final IAgent agent, final Double val) {
		agent.setAttribute(IKeyword.ROTATION, val);
	}

	/**
	 * Gets the color.
	 *
	 * @param agent
	 *            the agent
	 * @return the color
	 */
	@getter (IKeyword.COLOR)
	public static GamaColor getColor(final IAgent agent) {
		return (GamaColor) agent.getAttribute(IKeyword.COLOR);
	}

	/**
	 * Sets the color.
	 *
	 * @param agent
	 *            the agent
	 * @param val
	 *            the val
	 */
	@setter (IKeyword.COLOR)
	public static void setColor(final IAgent agent, final GamaColor val) {
		agent.setAttribute(IKeyword.COLOR, val);
	}

	/**
	 * Gets the cone distance.
	 *
	 * @param agent
	 *            the agent
	 * @return the cone distance
	 */
	@getter (CONE_DISTANCE)
	public static Double getConeDistance(final IAgent agent) {
		return (Double) agent.getAttribute(CONE_DISTANCE);
	}

	/**
	 * Sets the cone distance.
	 *
	 * @param agent
	 *            the agent
	 * @param val
	 *            the val
	 */
	@setter (CONE_DISTANCE)
	public static void setConeDistance(final IAgent agent, final Double val) {
		agent.setAttribute(CONE_DISTANCE, val);
	}

	/**
	 * Gets the player rotation.
	 *
	 * @param agent
	 *            the agent
	 * @return the player rotation
	 */
	@getter (PLAYER_ROTATION)
	public static Double getPlayerRotation(final IAgent agent) {
		return (Double) agent.getAttribute(PLAYER_ROTATION);
	}

	/**
	 * Sets the player rotation.
	 *
	 * @param agent
	 *            the agent
	 * @param val
	 *            the val
	 */
	@setter (PLAYER_ROTATION)
	public static void setPlayerRotation(final IAgent agent, final Double val) {
		agent.setAttribute(PLAYER_ROTATION, val);
	}

	/**
	 * Gets the player size.
	 *
	 * @param agent
	 *            the agent
	 * @return the player size
	 */
	@getter (PLAYER_SIZE)
	public static Double getPlayerSize(final IAgent agent) {
		return (Double) agent.getAttribute(PLAYER_SIZE);
	}

	/**
	 * Sets the player size.
	 *
	 * @param agent
	 *            the agent
	 * @param val
	 *            the val
	 */
	@setter (PLAYER_SIZE)
	public static void setPlayerSize(final IAgent agent, final Double val) {
		agent.setAttribute(PLAYER_SIZE, val);
	}

	/**
	 * Gets the cone amplitude.
	 *
	 * @param agent
	 *            the agent
	 * @return the cone amplitude
	 */
	@getter (CONE_AMPLITUDE)
	public static Double getConeAmplitude(final IAgent agent) {
		return (Double) agent.getAttribute(CONE_AMPLITUDE);
	}

	/**
	 * Sets the cone amplitude.
	 *
	 * @param agent
	 *            the agent
	 * @param val
	 *            the val
	 */
	@setter (CONE_AMPLITUDE)
	public static void setConeAmplitude(final IAgent agent, final Double val) {
		agent.setAttribute(CONE_AMPLITUDE, val);
	}

	/**
	 * Gets the player agent perception radius.
	 *
	 * @param agent
	 *            the agent
	 * @return the player agent perception radius
	 */
	@getter (PLAYER_AGENTS_PERCEPTION_RADIUS)
	public static Double getPlayerAgentPerceptionRadius(final IAgent agent) {
		return (Double) agent.getAttribute(PLAYER_AGENTS_PERCEPTION_RADIUS);
	}

	/**
	 * Sets the player agent perception radius.
	 *
	 * @param agent
	 *            the agent
	 * @param val
	 *            the val
	 */
	@setter (PLAYER_AGENTS_PERCEPTION_RADIUS)
	public static void setPlayerAgentPerceptionRadius(final IAgent agent, final Double val) {
		agent.setAttribute(PLAYER_AGENTS_PERCEPTION_RADIUS, val);
	}

	/**
	 * Gets the player agent min dist.
	 *
	 * @param agent
	 *            the agent
	 * @return the player agent min dist
	 */
	@getter (PLAYER_AGENTS_MIN_DIST)
	public static Double getPlayerAgentMinDist(final IAgent agent) {
		return (Double) agent.getAttribute(PLAYER_AGENTS_MIN_DIST);
	}

	/**
	 * Sets the player agent min dist.
	 *
	 * @param agent
	 *            the agent
	 * @param val
	 *            the val
	 */
	@setter (PLAYER_AGENTS_MIN_DIST)
	public static void setPlayerAgentMinDist(final IAgent agent, final Double val) {
		agent.setAttribute(PLAYER_AGENTS_MIN_DIST, val);
	}

	/**
	 * Prim get cone.
	 *
	 * @param scope
	 *            the scope
	 * @return the i shape
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = ACTION_CONE,
			doc = { @doc (
					value = "Wait for the connection of a unity client and send the paramters to the client") })
	public IShape primGetCone(final IScope scope) throws GamaRuntimeException {
		return getCone(scope, getAgent());
	}

	/**
	 * Gets the cone.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @return the cone
	 */
	private static IShape getCone(final IScope scope, final IAgent agent) {
		Double rotation = getRotation(agent);
		Double cone_amplitude = getConeAmplitude(agent);
		IShape g = Spatial.Creation.cone(scope, (int) (rotation - cone_amplitude / 2),
				(int) (rotation + cone_amplitude / 2));
		g = Spatial.Operators.inter(scope, g, Spatial.Creation.circle(scope, getConeDistance(agent)));
		g = Spatial.Transformations.translated_by(scope, g, new GamaPoint(0, 0, 4.9));
		return g;

	}

}
