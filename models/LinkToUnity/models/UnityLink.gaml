/**
* Name: UnityLink
* Includes actions, attributes and species facilating the link with Unity. To be used with the GAMA-Unity-VR Package for Unity
* Author: Patrick Taillandier
* Tags: Unity, VR
*/

@no_experiment
model UnityLink

species unity_linker parent: abstract_unity_linker {
	// connection port
	int port <- 8000;
	//possibility to add a delay after moving the player (in ms)
	string player_species <- string(species(unity_player));
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
experiment vr_xp autorun: true virtual: true  {
  	action move_player_xp {
		ask unity_linker {
			move_player_event <- true;
		}
	}
}

