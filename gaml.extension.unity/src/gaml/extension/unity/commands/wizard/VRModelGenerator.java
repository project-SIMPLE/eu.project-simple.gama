/*******************************************************************************************************
 *
 * VRModelGenerator.java, in gaml.extension.unity, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.extension.unity.commands.wizard;

import java.util.ArrayList;
import java.util.List;

import gama.core.metamodel.shape.GamaPoint;

/**
 * The Class VRModelGenerator.
 */
public class VRModelGenerator {

	/** The species to send. */
	private List<String> speciesToSend = new ArrayList<>();

	/** The geometries. */
	private List<DataGeometries> geometries = new ArrayList<>();

	/** The location init. */
	private GamaPoint locationInit = null;

	/** The player agents perception radius. */
	private Double playerAgentsPerceptionRadius = 0.0;

	/** The player agents min dist. */
	private Double playerAgentsMinDist = 0.0;

	/** The player size. */
	private Double playerSize = 1.0;

	/** The player color. */
	private String playerColor = "#red";

	/** The minimum cycle duration. */
	private Double minimumCycleDuration = 0.1;

	/** The displays to hide. */
	private List<String> displaysToHide = new ArrayList<>();

	/** The main display. */
	private String mainDisplay;

	/** The model name. */
	private String modelName;

	/** The model path. */
	private String modelPath;

	/** The experiment name. */
	private String experimentName;

	/** The min number player. */
	private int minNumberPlayer;

	/** The max num player. */
	private int maxNumPlayer;

	/** The has max number player. */
	private boolean hasMaxNumberPlayer;

	/**
	 * Builds the VR model.
	 *
	 * @return the string
	 */
	public String BuildVRModel() {
		StringBuilder modelVR = new StringBuilder("model ").append(modelName).append("\n\n");
		modelVR.append("import \"").append(modelPath).append("\"\n\n");
		modelVR.append(UnityLinkerSpecies()).append("\n\n");

		modelVR.append(playerStr()).append("\n\n");
		modelVR.append(experimentStr());

		return modelVR.toString();
	}

	/**
	 * Experiment str.
	 *
	 * @return the string
	 */
	public String experimentStr() {
		StringBuilder modelExp =
				new StringBuilder("experiment vr_xp ").append(experimentName != null ? "parent:" + experimentName : "")
						.append(" autorun: false type: unity {\n");
		modelExp.append("\tfloat minimum_cycle_duration <- ").append(minimumCycleDuration).append(";\n");
		modelExp.append("\tstring unity_linker_species <- string(unity_linker);\n");
		String disToHide = "";
		boolean first = true;
		if (displaysToHide != null) {
			for (String d : displaysToHide) {
				disToHide += (first ? "" : ",") + "\"" + d + "\"";
				first = false;
			}
		}

		modelExp.append("\tlist<string> displays_to_hide <- [").append(disToHide).append("];\n");
		modelExp.append("\tfloat t_ref;\n\n");

		modelExp.append("\taction create_player(string id) {\n");
		modelExp.append("\t\task unity_linker {\n");
		modelExp.append("\t\t\tdo create_player(id);\n");
		modelExp.append("\t\t}\n");
		modelExp.append("\t}\n\n");

		modelExp.append("\taction remove_player(string id_input) {\n");
		modelExp.append("\t\tif (not empty(unity_player)) {\n");

		modelExp.append("\t\t\task first(unity_player where (each.name = id_input)) {\n");
		modelExp.append("\t\t\t\tdo die;\n");
		modelExp.append("\t\t\t}\n");
		modelExp.append("\t\t}\n");
		modelExp.append("\t}\n\n");

		modelExp.append("\toutput {\n");
		if (mainDisplay != null) {

			modelExp.append("\t\t display ").append(mainDisplay).append("_VR parent:").append(mainDisplay)
					.append("{\n");
			modelExp.append("\t\t\t species unity_player;\n");
			modelExp.append("\t\t\t event #mouse_down{\n");

			modelExp.append("\t\t\t\t float t <- machine_time;\n");
			modelExp.append("\t\t\t\t if (t - t_ref) > 500 {\n");

			modelExp.append("\t\t\t\t\t ask unity_linker {\n");
			modelExp.append("\t\t\t\t\t\t move_player_event <- true;\n");
			modelExp.append("\t\t\t\t\t }\n");
			modelExp.append("\t\t\t\t\t t_ref <- t;\n");
			modelExp.append("\t\t\t\t }\n");

			modelExp.append("\t\t\t }\n");
			modelExp.append("\t\t }\n");

			modelExp.append("\t}\n");

			modelExp.append("}\n");
		}

		return modelExp.toString();

	}

	/**
	 * Player str.
	 *
	 * @return the string
	 */
	public String playerStr() {
		StringBuilder modelPlayer = new StringBuilder("species unity_player parent: abstract_unity_player{\n");
		if (playerAgentsPerceptionRadius != null && playerAgentsPerceptionRadius > 0.0) {
			modelPlayer.append("\tfloat player_agents_perception_radius <- ").append(playerAgentsPerceptionRadius)
					.append(";\n");
		}
		if (playerAgentsMinDist != null && playerAgentsMinDist > 0.0) {
			modelPlayer.append("\tfloat player_agents_min_dist <- ").append(playerAgentsMinDist).append(";\n");
		}
		modelPlayer.append("\tfloat player_size <- ").append(playerSize).append(";\n");
		modelPlayer.append("\trgb color <- ").append(playerColor).append(";\n");
		modelPlayer.append("\tfloat cone_distance <- 10.0 * player_size;\n");
		modelPlayer.append("\tfloat cone_amplitude <- 90.0;\n");
		modelPlayer.append("\tfloat player_rotation <- 90.0;\n");
		modelPlayer.append("\tbool to_display <- true;\n");

		modelPlayer.append("\taspect default {\n");
		modelPlayer.append("\t\tif to_display {\n");
		modelPlayer.append("\t\t\tif selected {\n");
		modelPlayer.append("\t\t\t\t draw circle(player_size) at: location + {0, 0, 4.9} color: rgb(#blue, 0.5);\n");

		modelPlayer.append("\t\t\t}\n");

		modelPlayer.append("\t\t\tdraw circle(player_size/2.0) at: location + {0, 0, 5} color: color ;\n");
		modelPlayer.append("\t\t\tdraw player_perception_cone() color: rgb(color, 0.5);");
		modelPlayer.append("\n\t\t}");
		modelPlayer.append("\n\t}");
		modelPlayer.append("\n}");

		return modelPlayer.toString();
	}

	/**
	 * Unity linker species.
	 *
	 * @return the string
	 */
	public String UnityLinkerSpecies() {
		StringBuilder modelUnityLinker = new StringBuilder("species unity_linker parent: abstract_unity_linker {\n");
		modelUnityLinker.append("\tstring player_species <- string(unity_player);\n");

		if (locationInit != null) {
			String locationInitStr = "";
			if (maxNumPlayer <= 0) {
				locationInitStr += "" + locationInit;
			} else {
				boolean first = true;
				for (int i = 0; i < maxNumPlayer; i++) {
					locationInitStr += (first ? "" : ",") + locationInit;
					first = false;
				}
			}
			modelUnityLinker.append("\tpoint location_init <- ").append(locationInitStr).append(";\n");

		}

		if (hasMaxNumberPlayer) {
			modelUnityLinker.append("\tint max_num_players  <- ").append(maxNumPlayer).append(";\n");
		} else {
			modelUnityLinker.append("\tint max_num_players  <- -1;\n");
		}
		if (minNumberPlayer > 0) {
			modelUnityLinker.append("\tint min_num_players  <- ").append(minNumberPlayer).append(";\n");
		}
		if (speciesToSend != null && !speciesToSend.isEmpty()) {
			String lisStr = "";
			boolean first = true;
			for (String sp : speciesToSend) {
				lisStr += (first ? "" : ", ") + "string(" + sp + ")";
				first = false;
			}
			modelUnityLinker.append("\tinit {\n\t\tdo init_species_to_send([").append(lisStr).append("]);");
			for (DataGeometries geoms : geometries) {
				String gStr = "geoms: " + geoms.getSpeciesName() + " collect (each.shape"
						+ (geoms.getBuffer() != null && geoms.getBuffer() != 0 ? " buffer " + geoms.getBuffer() + " ) "
								: ") ");
				String nStr = "names: " + geoms.getSpeciesName() + " collect (each.name) ";
				String hStr = "height: " + geoms.getHeight() + " ";
				String cStr = "collider: " + geoms.getHasCollider() + " ";
				String ccStr = "color: " + geoms.getColor() + " ";
				String tDStr = "is_3D: " + geoms.getIs3D() + " ";
				String inStr = "is_interactable: " + geoms.getIsSelectable() + " ";
				String inGrab = "is_grabable: " + geoms.getIsGrabable() + " ";

				String tStr =
						geoms.getTag() == null || "".equals(geoms.getTag()) ? "" : "tag: \"" + geoms.getTag() + "\" ";

				modelUnityLinker.append("\n\t\tdo add_background_data ").append(gStr).append(nStr).append(hStr)
						.append(cStr).append(tStr).append(tDStr).append(inStr).append(ccStr).append(inGrab).append(";");

			}
			modelUnityLinker.append("\n\t}");

		}
		modelUnityLinker.append("\n}");
		return modelUnityLinker.toString();
	}

	/**
	 * Gets the player color.
	 *
	 * @return the player color
	 */
	public String getPlayerColor() { return playerColor; }

	/**
	 * Sets the player color.
	 *
	 * @param playerColor
	 *            the new player color
	 */
	public void setPlayerColor(final String playerColor) { this.playerColor = playerColor; }

	/**
	 * Gets the model path.
	 *
	 * @return the model path
	 */
	public String getModelPath() { return modelPath; }

	/**
	 * Sets the model path.
	 *
	 * @param modelPath
	 *            the new model path
	 */
	public void setModelPath(final String modelPath) { this.modelPath = modelPath; }

	/**
	 * Gets the species to send.
	 *
	 * @return the species to send
	 */
	public List<String> getSpeciesToSend() { return speciesToSend; }

	/**
	 * Sets the species to send.
	 *
	 * @param speciesToSend
	 *            the new species to send
	 */
	public void setSpeciesToSend(final List<String> speciesToSend) { this.speciesToSend = speciesToSend; }

	/**
	 * Gets the geometries.
	 *
	 * @return the geometries
	 */
	public List<DataGeometries> getGeometries() { return geometries; }

	/**
	 * Sets the geometries.
	 *
	 * @param geometries
	 *            the new geometries
	 */
	public void setGeometries(final List<DataGeometries> geometries) { this.geometries = geometries; }

	/**
	 * Gets the location init.
	 *
	 * @return the location init
	 */
	public GamaPoint getLocationInit() { return locationInit; }

	/**
	 * Sets the location init.
	 *
	 * @param locationInit
	 *            the new location init
	 */
	public void setLocationInit(final GamaPoint locationInit) { this.locationInit = locationInit; }

	/**
	 * Gets the player agents perception radius.
	 *
	 * @return the player agents perception radius
	 */
	public Double getPlayerAgentsPerceptionRadius() { return playerAgentsPerceptionRadius; }

	/**
	 * Sets the player agents perception radius.
	 *
	 * @param playerAgentsPerceptionRadius
	 *            the new player agents perception radius
	 */
	public void setPlayerAgentsPerceptionRadius(final Double playerAgentsPerceptionRadius) {
		this.playerAgentsPerceptionRadius = playerAgentsPerceptionRadius;
	}

	/**
	 * Gets the player agents min dist.
	 *
	 * @return the player agents min dist
	 */
	public Double getPlayerAgentsMinDist() { return playerAgentsMinDist; }

	/**
	 * Sets the player agents min dist.
	 *
	 * @param playerAgentsMinDist
	 *            the new player agents min dist
	 */
	public void setPlayerAgentsMinDist(final Double playerAgentsMinDist) {
		this.playerAgentsMinDist = playerAgentsMinDist;
	}

	/**
	 * Gets the player size.
	 *
	 * @return the player size
	 */
	public Double getPlayerSize() { return playerSize; }

	/**
	 * Sets the player size.
	 *
	 * @param playerSize
	 *            the new player size
	 */
	public void setPlayerSize(final Double playerSize) { this.playerSize = playerSize; }

	/**
	 * Gets the minimum cycle duration.
	 *
	 * @return the minimum cycle duration
	 */
	public Double getMinimumCycleDuration() { return minimumCycleDuration; }

	/**
	 * Sets the minimum cycle duration.
	 *
	 * @param minimumCycleDuration
	 *            the new minimum cycle duration
	 */
	public void setMinimumCycleDuration(final Double minimumCycleDuration) {
		this.minimumCycleDuration = minimumCycleDuration;
	}

	/**
	 * Gets the displays to hide.
	 *
	 * @return the displays to hide
	 */
	public List<String> getDisplaysToHide() { return displaysToHide; }

	/**
	 * Sets the displays to hide.
	 *
	 * @param displaysToHide
	 *            the new displays to hide
	 */
	public void setDisplaysToHide(final List<String> displaysToHide) { this.displaysToHide = displaysToHide; }

	/**
	 * Gets the main display.
	 *
	 * @return the main display
	 */
	public String getMainDisplay() { return mainDisplay; }

	/**
	 * Sets the main display.
	 *
	 * @param mainDisplay
	 *            the new main display
	 */
	public void setMainDisplay(final String mainDisplay) { this.mainDisplay = mainDisplay; }

	/**
	 * Gets the model name.
	 *
	 * @return the model name
	 */
	public String getModelName() { return modelName; }

	/**
	 * Sets the model name.
	 *
	 * @param modelName
	 *            the new model name
	 */
	public void setModelName(final String modelName) { this.modelName = modelName; }

	/**
	 * Gets the experiment name.
	 *
	 * @return the experiment name
	 */
	public String getExperimentName() { return experimentName; }

	/**
	 * Sets the experiment name.
	 *
	 * @param experimentName
	 *            the new experiment name
	 */
	public void setExperimentName(final String experimentName) { this.experimentName = experimentName; }

	/**
	 * Gets the min num player.
	 *
	 * @return the min num player
	 */
	public int getMin_num_player() { return minNumberPlayer; }

	/**
	 * Sets the min num player.
	 *
	 * @param min_num_player
	 *            the new min num player
	 */
	public void setMin_num_player(final int min_num_player) { this.minNumberPlayer = min_num_player; }

	/**
	 * Gets the max num player.
	 *
	 * @return the max num player
	 */
	public int getMax_num_player() { return maxNumPlayer; }

	/**
	 * Sets the max num player.
	 *
	 * @param max_num_player
	 *            the new max num player
	 */
	public void setMax_num_player(final int max_num_player) { this.maxNumPlayer = max_num_player; }

	/**
	 * Checks for max num player.
	 *
	 * @return true, if successful
	 */
	public boolean has_max_num_player() {
		return hasMaxNumberPlayer;
	}

	/**
	 * Sets the checks for max num player.
	 *
	 * @param has_max_num_player
	 *            the new checks for max num player
	 */
	public void setHas_max_num_player(final boolean has_max_num_player) {
		this.hasMaxNumberPlayer = has_max_num_player;
	}

}
