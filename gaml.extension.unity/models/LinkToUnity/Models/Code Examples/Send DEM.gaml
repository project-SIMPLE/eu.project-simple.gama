/**
* Name: SendStaticdata
* Show how to send dynamic geometries/agents to Unity. It works with the Scene "Assets/Scenes/Code Example/Receive Dynamic Data" from the Unity Template
* Author: Patrick Taillandier
* Tags: Unity, dynamic geometries/agents
*/


model SendDEM

global {
	
	grid_file mnt_grid_file <- grid_file("../../includes/dem.asc");

	geometry shape <- envelope(mnt_grid_file);
 	
 	unity_property up_sphere ;
 	
 	
 	float added_height <- 2.0 #m;
	
	float max_value <- 100.0;
	init {
		create sphere_ag with:(location:{20,20,70});
	}
	
	action change_height(bool increase) {
		cell c <- cell(#user_location) ;
		if (c != nil) {
			ask c {
				
				grid_value <-grid_value + (increase ? added_height : -added_height);
				grid_value <- max(0, min(max_value,grid_value));
				write sample(grid_value);
			}
			
			ask unity_linker {
				do set_terrain_values(
					player:last(unity_player), 
					id:"dem", 
					matrix: {1,1} matrix_with c.grid_value,
					index_x : c.grid_x,
					index_y : c.grid_y
				);
			}
		}
	}
	
	

}

species sphere_ag {
	aspect default {
		draw sphere(1) color: #magenta;
	}
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
	 
	 
	 	
	init {
		//define the unity properties
		do define_properties;
		
			//add the static_geometry agents as static agents/geometries to send to unity with the up_geom unity properties.
		do add_background_geometries(sphere_ag,up_sphere);
	
	}
	 
	
	//action that defines the different unity properties
	action define_properties {
		//define a unity_aspect called tree_aspect that will display in Unity the agents with the SM_arbres_001 prefab, with a scale of 2.0, no y-offset, 
		//a rotation coefficient of 1.0 (no change of rotation from the prefab), no rotation offset, and we use the default precision. 
		unity_aspect sphere_aspect <- prefab_aspect("Prefabs/Visual Prefabs/Basic shape/SphereRigidBody",1.0,0.0,0.0,0.0, precision);
		
		//define the up_car unity property, with the name "car", no specific layer, the car_aspect unity aspect, no interaction, and the agents location are not sent back 
		//to GAMA. 
		up_sphere<- geometry_properties("sphere_ag", nil, sphere_aspect, #grabable, true);
		
		// add the up_tree unity_property to the list of unity_properties
		unity_properties << up_sphere;
		
		
	}
	reflex change_mnt {
		ask cell {
			//grid_value <- grid_value * 0.95;
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
			species sphere_ag;
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
		field f <- field(matrix(cell));
		ask unity_linker {
			do create_player(id);
			
			do update_terrain (
					player:last(unity_player), 
					id:"dem", 
					field:f,
					resolution:65,
					max_value:max_value
				);
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
			event "r" {
				ask world {
					do change_height(true);
				}	
			}
			event "t" {
				ask world {
					do change_height(false);
				}	
			}
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