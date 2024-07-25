/**
* Name: UserInteraction
* Show how to define a simple multi-player game in Unity. It works with the Scene "Assets/Scenes/Code Example/Multi-player" from the Unity Template.
* In this model each player can observe the other players (use of an avatar for each player). They can interact with a central pylon to change its color to their color.
* Author: Patrick Taillandier
* Tags: Unity, Multi Player
*/


model MultiPlayerGame

global {
	//unity properties that will be used for the players
	unity_property up_ghost;
	unity_property up_lg;
	unity_property up_slime;
	unity_property up_turtle;
	
	//unity properties used for the pylon
	unity_property up_pylon;
	
	//initial space where the players can appear at the beginning of the game
	geometry init_space;
	
	//color of the different players
	list<rgb> color_players <- [#red, #blue,#green, #violet];
	
	init {
		//creation of the pylon and definition of the init space
		create pylon with:(shape: circle(5), color: #gray);
		init_space <- copy(shape);
		ask pylon {
			init_space <- init_space - circle(7);
		}
	}

}
//Species that will make the link between GAMA and Unity. It has to inherit from the built-in species asbtract_unity_linker
species unity_linker parent: abstract_unity_linker {
	//name of the species used to represent a Unity player
	string player_species <- string(unity_player);

	//in this model, information about other player will be automatically sent to the Player at every step, so we set do_info_world to true
	bool do_send_world <- true;
	
	//max number of players
	int max_num_players  <- 4;

	//min number of players: as the value is 0, the model will start as soon as the experiment is launch
	int min_num_players  <- 0;
	
	//initial location of the player
	list<point> init_locations <- random_loc();
	
	init {
		//define the unity properties
		do define_properties;
		
		//we add the pylon with its properties as a background geometry (it will only be sent at the beginning of the game) 
		do add_background_geometries(pylon,up_pylon);
		player_unity_properties <- [ up_lg,up_turtle, up_slime, up_ghost ];
		
	}
	
	list<point> random_loc {
		list<point> points;
		loop times: max_num_players {
			points << any_location_in(init_space);
		}
		return points;
		
	}	
	
	
	
	//action that defines the different unity properties
	action define_properties {
		
		unity_aspect ghost_aspect <- prefab_aspect("Prefabs/Visual Prefabs/Character/Ghost",2.0,0.0,-1.0,90.0,precision);
		up_ghost <- geometry_properties("ghost","",ghost_aspect,new_geometry_interaction(true, false,false,[]),false);
		unity_properties << up_ghost; 
		
		unity_aspect slime_aspect <- prefab_aspect("Prefabs/Visual Prefabs/Character/Slime",2.0,0.0,-1.0,90.0,precision);
		up_slime <- geometry_properties("slime","",slime_aspect,new_geometry_interaction(true, false,false,[]),false);
		unity_properties << up_slime; 
		
		unity_aspect lg_aspect <- prefab_aspect("Prefabs/Visual Prefabs/Character/LittleGhost",2.0,0.0,-1.0,90.0,precision);
		up_lg <- geometry_properties("little_ghost","",lg_aspect,new_geometry_interaction(true, false,false,[]),false);
		unity_properties << up_lg; 
		
		unity_aspect turtle_aspect <- prefab_aspect("Prefabs/Visual Prefabs/Character/TurtleShell",2.0,0.0,-1.0,90.0,precision);
		up_turtle <- geometry_properties("turtle","",turtle_aspect,new_geometry_interaction(true, false,false,[]),false);
		unity_properties << up_turtle; 
		
		unity_aspect pylon_aspect <- geometry_aspect(10.0,#gray,precision);
		up_pylon <- geometry_properties("pylon","selectable",pylon_aspect,#ray_interactable,false);
		unity_properties << up_pylon;
		
	}
	
	action change_color(string id, string player) {
		pylon ag <- pylon first_with (each.name = id) ;
		unity_player pl <-  unity_player first_with (each.name = player) ;
		if (ag != nil) {
			ag.color <- pl.color; 
			do send_message players: unity_player as list mes: ["id"::id, "color"::[ag.color.red,ag.color.green,ag.color.blue, ag.color.alpha]];
		}
	}
	
}


//species used to represent an unity player, with the default attributes. It has to inherit from the built-in species asbtract_unity_player
species unity_player parent: abstract_unity_player {
	//size of the player in GAMA
	float player_size <- 1.0;

	//color of the player in GAMA
	rgb color <- color_players[int(self)] ;
	
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

species pylon {
	rgb color;
	aspect default {
		draw shape color: color border: #black;
	}
}


experiment main type: gui {
	output {
		display map {
			graphics "world" {
				draw world color: #lightgray;
			}
			species pylon;
		}
	} 
}

//default Unity (VR) experiment that inherit from the SimpleMessage experiment
//The unity type allows to create at the initialization one unity_linker agent
experiment vr_xp parent:main autorun: false type: unity {
	//minimal time between two simulation step
	float minimum_cycle_duration <- 0.05;

	//name of the species used for the unity_linker
	string unity_linker_species <- string(unity_linker);
	
	//allow to hide the "map" display and to only display the displayVR display 
	list<string> displays_to_hide <- ["map"];
	


	//action called by the middleware when a player connects to the simulation
	action create_player(string id) {
		ask unity_linker {
			do create_player(id);
			
			//build invisible walls surrounding the free_area geometry
			do build_invisible_walls(
				player: last(unity_player), //player to send the information to
				id: "wall_for_world", //id of the walls
				height: 40.0, //height of the walls
				wall_width: 0.5, //width ot the walls
				geoms: [world.shape] //geometries used to defined the walls - the walls will be generated from the countour of these geometries
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