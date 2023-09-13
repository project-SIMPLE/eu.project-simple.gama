/**
* Name: UnityLink
* Includes actions, attributes and species facilating the link with Unity. To be used with the GAMA-Unity-VR Package for Unity
* Author: Patrick Taillandier
* Tags: Unity, VR
*/

@no_experiment
model UnityLink

species unityLinker2 parent: unity_linker{
	/***************************************************
	 *
	 * PARAMETERS ABOUT THE CONNECTION AND DATA SENT
	 * 
	 ***************************************************/
	int precision <- 1000;
	
	int port <- 8000;
	
	bool do_send_world <- true;
	
	point location_init <- {50.0, 50.0};
	
	
	string end_message_symbol <- "&&&";
	
	string player_species <- string(species(default_player));
	/*************************************** 
	 *
	 * PARAMETERS ABOUT THE PLAYER
	 * 
	 ***************************************/

	//allow to create a player agent
	bool create_player <- true;
	
	//does the player should has a physical exitence in Unity (i.e. cannot pass through specific geometries)
	bool use_physics_for_player <- true;

	
}


//Defaut species for the player
species default_player parent: unity_player{
	//allow to reduce the quantity of information sent to Unity - only the agents at a certain distance are sent
	float player_agents_perception_radius <- 0.0;
	
	//allow to not send to Unity agents that are to close (i.e. overlapping) 
	float player_agents_min_dist <- 0.0;
	
	float player_size <- 3.0;
	rgb color <- #red;
	float cone_distance <- 10.0 * player_size;
	float cone_amplitude <- 90.0;
	float player_rotation <- 90.0;
	
	aspect default {
		if to_display {
			if file_exists("../images/headset.png")  {
				draw image("../images/headset.png")  size: {player_size, player_size} at: location + {0, 0, 5} rotate: rotation - 90;
			
			} else {
				draw circle(player_size/2.0) at: location + {0, 0, 5} color: color rotate: rotation - 90;
			
			}
			draw player_perception_cone() color: rgb(#red, 0.5);
		
		}			
	}
}

species agent_to_send skills: [sent_to_unity];


//Default xp with the possibility to move the player
experiment vr_xp autorun: true virtual: true  {
  	action move_player_xp {
		ask agents of_generic_species unityLinker2 {
			move_player_event <- true;
		}
	}
}

