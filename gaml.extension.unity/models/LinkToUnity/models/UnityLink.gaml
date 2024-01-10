/**
* Name: UnityLink
* Includes actions, attributes and species facilating the link with Unity. To be used with the GAMA-Unity-VR Package for Unity
* Author: Patrick Taillandier
* Tags: Unity, VR
*/

model UnityLink

species unity_linker parent: abstract_unity_linker {
	point location_init <- {50.0, 50.0};
	int port <- 8000;
	string player_species <- string(species(unity_player));
	init {
		//do init_species_to_send([string(species(speciesA))]);
	}
}

//Defaut species for the player
species unity_player parent: abstract_unity_player{
	//allow to reduce the quantity of information sent to Unity - only the agents at a certain distance are sent
	float player_agents_perception_radius <- 0.0;
	
	//allow to not send to Unity agents that are to close (i.e. overlapping) 
	float player_agents_min_dist <- 0.0;
	
	float player_size <- 3.0;
	rgb color <- #red;
	float cone_distance <- 10.0 * player_size;
	float cone_amplitude <- 90.0;
	float player_rotation <- 90.0;
	bool to_display <- true;
	 
	aspect default { 
		if to_display {
			if file_exists("../images/headset.png")  {
				draw image("../images/headset.png")  size: {player_size, player_size} at: location + {0, 0, 5} rotate: rotation - 90;
			
			} else {
				draw circle(player_size/2.0) at: location + {0, 0, 5} color: color ;
			
			}
			draw player_perception_cone() color: rgb(#red, 0.5);
		
		}			
	}
}


//Default xp with the possibility to move the player
experiment vr_xp /*parent: user_xp*/ autorun: true type: unity  {
	float minimum_cycle_duration <- 0.1;
	string unity_linker_species <- string(species(unity_linker));
	list<string> displays_to_hide <- ["user_display"];
	output {
		
		display displayVR /*parent: user_display */ {
			species unity_player;
			event #mouse_down  {
				ask unity_linker {
					move_player_event <- true;
				}
			}
		}
		
	} 
}

