/**
* Name: DemoModelVR
* Based on the internal empty template. 
* Author: patricktaillandier
* Tags: 
*/


model DemoModelVR

import "DemoModel.gaml"


 

species unity_linker parent: abstract_unity_linker {
	list<point> init_locations <- [{50.0, 50.0}, {60.0, 60.0}];
	string player_species <- string(unity_player);
	int min_num_players <- 1;
	int max_num_players <- 4;
		
	

	bool do_send_world <- true;
	init {
		do init_species_to_send([string(simple_agentA),string(simple_agentB),string(static_object)]);
		list<string> names ;
		loop i from: 0 to: length(block) {
			names << ""+i;
		}
		do add_background_data geoms: block collect each.shape names: names height: 5.0 collider: true tag: "selectable" is_interactable: true;
	}
	
	action update_hotspot(string id) {
		block b <- block[int(id)];
		
		if (b != nil) {
			ask b {
				if (not b.is_hotspot) {
					do become_hotspot;
				} else {
					do remove_hotspot;
				}
			}
			
		}
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
			if (selected) {
				draw circle(player_size) at: location + {0, 0, 4.9} color: rgb(#blue, 0.5);
			}
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
experiment vr_xp parent: simple_simulation autorun: false type: unity  {
	float minimum_cycle_duration <- 0.03;
	string unity_linker_species <- string(unity_linker);
	list<string> displays_to_hide <- ["map"];
	
	float t_ref;

	action create_player(string id) {
		ask unity_linker {
			write "create player: " + id;
			do create_player(id);
		}
	}
	action remove_player(string id_input) {
		if (not empty(unity_player)) {
			ask first(unity_player where (each.name = id_input)) {
				do die;
			}
		}
	}
	 
	output { 
		display displayVR parent: map  {
			species unity_player;
			event #mouse_down  {
				float t <- machine_time;
				if (t - t_ref) > 500 {
					ask unity_linker {
						move_player_event <- true;
					}
					t_ref <- t;
				}
				
			}
		}
		
	} 
}