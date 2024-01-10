package gaml.extensions.unity.commands.wizard;

import java.util.ArrayList;
import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;

public class VRModelGenerator {

	private List<String> speciesToSend = new ArrayList<>();
	private List<DataGeometries> geometries = new ArrayList<>();
	private int port = 8000;
	private GamaPoint locationInit = new GamaPoint(50,50,0) ;
	private Double playerAgentsPerceptionRadius = 0.0;
	private Double playerAgentsMinDist = 0.0;
	private Double playerSize = 1.0;
	private String playerColor = "#red";
	private Double minimumCycleDuration = 0.05;
	private List<String> displaysToHide = new ArrayList<>();
	private String mainDisplay;
	
	private String modelName;
	private String modelPath;
	
	private String experimentName;
	private int minNumberPlayer;
	private int maxNumPlayer;
	private boolean hasMaxNumberPlayer;
	
	public String BuildVRModel() {
		String modelVR = "model " + modelName +"\n\n";
		modelVR += "import \"" + modelPath + "\"\n\n";
		modelVR += UnityLinkerSpecies() + "\n\n";
		
		modelVR += playerStr() + "\n\n";
		modelVR += experimentStr() ;
		
		
		return modelVR;
	}
	
	public String experimentStr() {
		String modelExp = "experiment vr_xp parent:" + experimentName + " autorun: true type: unity {\n";
		modelExp += "\tfloat minimum_cycle_duration <- " + minimumCycleDuration + ";\n";
		modelExp += "\tstring unity_linker_species <- string(unity_linker);\n";
		String disToHide = ""; boolean first = true;
		if (displaysToHide != null) {
			for(String d : displaysToHide) {
				disToHide += (first ? "": ",") + "\"" + d + "\"";
				first = false;
			}
		}
		
		modelExp += "\tlist<string> displays_to_hide <- ["+  disToHide + "];\n";
		modelExp += "\toutput {\n";
		modelExp += "\t\t display "+ mainDisplay + "_VR parent:" + mainDisplay + "{\n";
		modelExp += "\t\t\t species unity_player;\n";
		modelExp += "\t\t\t event #mouse_down{\n";
		modelExp += "\t\t\t\t ask unity_linker {\n";
		modelExp += "\t\t\t\t move_player_event <- true;\n";
		modelExp += "\t\t\t\t }\n";
		
		modelExp += "\t\t\t }\n";
		modelExp += "\t\t }\n";

		modelExp += "\t}\n";

		modelExp += "}\n";
		
		return modelExp;
		
	}
	
	public String playerStr() {
		String modelPlayer = "species unity_player parent: abstract_unity_player{\n";
		if (playerAgentsPerceptionRadius != null && playerAgentsPerceptionRadius > 0.0) 
			modelPlayer+= "\tfloat player_agents_perception_radius <- " + playerAgentsPerceptionRadius +";\n";
		if (playerAgentsMinDist != null && playerAgentsMinDist > 0.0) 
			modelPlayer+= "\tfloat player_agents_min_dist <- " + playerAgentsMinDist +";\n";
		modelPlayer+= "\tfloat player_size <- " + playerSize +";\n";
		modelPlayer+= "\trgb color <- " + playerColor +";\n";
		modelPlayer+= "\tfloat cone_distance <- 10.0 * player_size;\n";
		modelPlayer+= "\tfloat cone_amplitude <- 90.0;\n";
		modelPlayer+= "\tfloat player_rotation <- 90.0;\n";
		modelPlayer+= "\tbool to_display <- true;\n";
		
		modelPlayer+= "\taspect default {\n";
		modelPlayer+= "\t\tif to_display {\n";
		modelPlayer+= "\t\t\tif selected {\n";
		modelPlayer+= "\t\t\t\t draw circle(player_size) at: location + {0, 0, 4.9} color: rgb(#blue, 0.5);\n";
		
		modelPlayer+= "\t\t\t}\n";
		
		
		modelPlayer+= "\t\t\tdraw circle(player_size/2.0) at: location + {0, 0, 5} color: color ;\n";
		modelPlayer+= "\t\t\tdraw player_perception_cone() color: rgb(color, 0.5);";
		modelPlayer+= "\n\t\t}";
		modelPlayer+= "\n\t}";
		modelPlayer+= "\n}";
						
		return modelPlayer;
	}
	
	
	public String UnityLinkerSpecies() {
		String modelUnityLinker = "species unity_linker parent: abstract_unity_linker {\n";
		modelUnityLinker += "\tint port <- " + port + ";\n";
		
		modelUnityLinker += "\tstring player_species <- string(unity_player);\n";
		if (locationInit != null) 
			modelUnityLinker += "\tpoint location_init <- " + locationInit + ";\n";
		if (hasMaxNumberPlayer) 
			modelUnityLinker += "\tint max_num_players  <- " + maxNumPlayer + ";\n";
		else
			modelUnityLinker += "\tint max_num_players  <- -1;\n";
		if (minNumberPlayer > 0) {
			modelUnityLinker += "\tint min_num_player  <- " + minNumberPlayer + ";\n";
		}
		if (speciesToSend != null && !speciesToSend.isEmpty()) {
			String lisStr = ""; boolean first = true;
			for(String sp : speciesToSend) {
				lisStr += (first ? "": ", ") + "string(" +sp + ")";
				first = false;
			} 
			modelUnityLinker += "\tinit {\n\t\tdo init_species_to_send([" + lisStr + "]);";
			for (DataGeometries geoms : geometries) {
				String gStr = "geoms: " + geoms.getSpeciesName() + " collect (each.shape" + (((geoms.getBuffer() != null) && geoms.getBuffer() != 0) ? (" buffer " + geoms.getBuffer() +" ) ") : ") ");
				String nStr = "names: " + geoms.getSpeciesName() + " collect (each.name) ";
				String hStr = "height: " + geoms.getHeight() + " ";
				String cStr = "collider: " + geoms.getHasCollider() + " ";
				String tStr = (geoms.getTag()  == null || geoms.getTag().equals(""))  ? "" :"tag: \"" + geoms.getTag() + "\" ";
				
				modelUnityLinker += "\t\tdo add_background_data " + gStr +nStr +  hStr + cStr + tStr + ";";
				
			}
			modelUnityLinker +=  "\n\t}";
			
		}
		modelUnityLinker += "\n}";
		return modelUnityLinker;
	}
	
	
	
	
	public String getPlayerColor() {
		return playerColor;
	}

	public void setPlayerColor(String playerColor) {
		this.playerColor = playerColor;
	}

	public String getModelPath() {
		return modelPath;
	}

	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<String> getSpeciesToSend() {
		return speciesToSend;
	}




	public void setSpeciesToSend(List<String> speciesToSend) {
		this.speciesToSend = speciesToSend;
	}




	public List<DataGeometries> getGeometries() {
		return geometries;
	}




	public void setGeometries(List<DataGeometries> geometries) {
		this.geometries = geometries;
	}




	public Integer getPort() {
		return port;
	}




	public void setPort(Integer port) {
		this.port = port;
	}




 
	public GamaPoint getLocationInit() {
		return locationInit;
	}




	public void setLocationInit(GamaPoint locationInit) {
		this.locationInit = locationInit;
	}




	public Double getPlayerAgentsPerceptionRadius() {
		return playerAgentsPerceptionRadius;
	}




	public void setPlayerAgentsPerceptionRadius(Double playerAgentsPerceptionRadius) {
		this.playerAgentsPerceptionRadius = playerAgentsPerceptionRadius;
	}




	public Double getPlayerAgentsMinDist() {
		return playerAgentsMinDist;
	}




	public void setPlayerAgentsMinDist(Double playerAgentsMinDist) {
		this.playerAgentsMinDist = playerAgentsMinDist;
	}




	public Double getPlayerSize() {
		return playerSize;
	}




	public void setPlayerSize(Double playerSize) {
		this.playerSize = playerSize;
	}




	public Double getMinimumCycleDuration() {
		return minimumCycleDuration;
	}




	public void setMinimumCycleDuration(Double minimumCycleDuration) {
		this.minimumCycleDuration = minimumCycleDuration;
	}




	public List<String> getDisplaysToHide() {
		return displaysToHide;
	}




	public void setDisplaysToHide(List<String> displaysToHide) {
		this.displaysToHide = displaysToHide;
	}




	public String getMainDisplay() {
		return mainDisplay;
	}




	public void setMainDisplay(String mainDisplay) {
		this.mainDisplay = mainDisplay;
	}




	public String getModelName() {
		return modelName;
	}




	public void setModelName(String modelName) {
		this.modelName = modelName;
	}




	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public int getMin_num_player() {
		return minNumberPlayer;
	}

	public void setMin_num_player(int min_num_player) {
		this.minNumberPlayer = min_num_player;
	}

	public int getMax_num_player() {
		return maxNumPlayer;
	}

	public void setMax_num_player(int max_num_player) {
		this.maxNumPlayer = max_num_player;
	}

	public boolean has_max_num_player() {
		return hasMaxNumberPlayer;
	}

	public void setHas_max_num_player(boolean has_max_num_player) {
		this.hasMaxNumberPlayer = has_max_num_player;
	}





}
