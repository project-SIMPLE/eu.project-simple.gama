/**
* Name: SendStaticdata
* Show how to send dynamic geometries/agents to Unity. It works with the Scene "Assets/Scenes/Code Example/Receive Dynamic Data" from the Unity Template
* Author: Patrick Taillandier
* Tags: Unity, dynamic geometries/agents
*/


model SendDEM

global {
	
	grid_file mnt_grid_file <- grid_file("../../includes/mnt.asc");

	geometry shape <- envelope(mnt_grid_file);
 	matrix<int> mat;

}

grid cell file: mnt_grid_file ;

//Species that will make the link between GAMA and Unity. It has to inherit from the built-in species asbtract_unity_linker
species unity_linker parent: abstract_unity_linker {
	//name of the species used to represent a Unity player
	string player_species <- string(unity_player);

	//in this model, the agents location and heading will be sent to the Players at every step, so we set do_info_world to true
	bool do_send_world <- false;
	
	//initial location of the player
	list<point> init_locations <- [world.location];
	 
	reflex change_mnt {
		ask cell {
			grid_value <- grid_value * 0.95;
		}
	}
	
	reflex send_mnt when: every(100 #cycle){
	
		field f <- field(cell);
		loop p over: unity_player {
			ask unity_linker {
				do update_terrain (
					player:p, 
					id:"dem", 
					field:f,
					resolution:257
				);
			}
		}
	}
	
	
}

//species used to represent an unity player, with the default attributes. It has to inherit from the built-in species asbtract_unity_player
species unity_player parent: abstract_unity_player {
	//size of the player in GAMA
	float player_size <- 1.0;

	//color of the player in GAMA
	rgb color <- #red ;
	
	//vision cone distance in GAMA
	float cone_distance <- 10.0 * player_size;
	
	//vision cone amplitude in GAMA
	float cone_amplitude <- 90.0;

	//rotation to apply from the heading of Unity to GAMA
	float player_rotation <- 90.0;
	
	//display the player
	bool to_display <- true;
	
	
	//default aspect to display the player as a circle with its cone of vision
	aspect default {
		if to_display {
			if selected {
				 draw circle(player_size) at: location + {0, 0, 4.9} color: rgb(#blue, 0.5);
			}
			draw circle(player_size/2.0) at: location + {0, 0, 5} color: color ;
			draw player_perception_cone() color: rgb(color, 0.5);
		}
	}
}
experiment main type: gui {
	output {
		display map type: 3d{
			mesh cell grayscale: true triangulation: true smooth: true  ;
		}
	}
}

//default Unity (VR) experiment that inherit from the SimpleMessage experiment
//The unity type allows to create at the initialization one unity_linker agent
experiment vr_xp parent:main autorun: false type: unity {
	//minimal time between two simulation step
	float minimum_cycle_duration <- 0.1;

	//name of the species used for the unity_linker
	string unity_linker_species <- string(unity_linker);
	
	//allow to hide the "map" display and to only display the displayVR display 
	list<string> displays_to_hide <- ["map"];
	


	//action called by the middleware when a player connects to the simulation
	action create_player(string id) {
		ask unity_linker {
			do create_player(id);
		}
	}

	//action called by the middleware when a plyer is remove from the simulation
	action remove_player(string id_input) {
		if (not empty(unity_player)) {
			ask first(unity_player where (each.name = id_input)) {
				do die;
			}
		}
	}
	
	//variable used to avoid to move too fast the player agent
	float t_ref;

		 
	output { 
		//In addition to the layers in the map display, display the unity_player and let the possibility to the user to move players by clicking on it.
		display displayVR parent: map  {
			species unity_player;
			event #mouse_down  {
				float t <- gama.machine_time;
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