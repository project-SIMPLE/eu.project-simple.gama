/**
* Name: UserInteraction
* Show how to define interaction in Unity. It works with the Scene "Assets/Scenes/Code Example/User Interactions" from the Unity Template
* Author: Patrick Taillandier
* Tags: Unity, User interaction
*/


model UserInteraction

global {
	//unity properties that will be used for sending geometries/agents to Unity
	unity_property up_no_interaction;
	unity_property up_collider ;
	unity_property up_ray_interaction ;
	unity_property up_grab_interaction ;
	
 	init {
 		//no interaction at all with the gray square. The player can pass through it.
 		create square_ag  {
 			name <- "no interaction";
 			shape <- square(2) at_location {45, 50};
 			color <- #gray;
 		}

 		//no interaction with the yellow square, but the player cannot pass through it (it has a physical existence in the virtual universe.
		create square_ag  {
			name <- "collider";
 			shape <- square(2) at_location {48, 50};
 			color <- #yellow;
 		} 
 		
 		//ray interaction with the green square. The player will be able to select this square to change its color to red
 		create square_ag  {
 			name <- "ray interaction";
 			shape <- square(2) at_location {51, 50};
 			color <- #green;
 		} 
 		
 		//grab interaction with the pink square. The player will be able to grab this square and move it. The movement of the square in Unity will be reported in GAMA.
 		create square_ag  {
 			name <- "grab interaction";
 			shape <- square(2) at_location {54, 50};
 			color <- #pink;
 		} 		
 		
 	}

}

species square_ag {
	rgb color <- #black;
	bool selected <- false;
	
	aspect default {
		draw shape color: selected ? #red : color;
	}
}


//Species that will make the link between GAMA and Unity. It has to inherit from the built-in species asbtract_unity_linker
species unity_linker parent: abstract_unity_linker {
	//name of the species used to represent a Unity player
	string player_species <- string(unity_player);

	//in this model, no information will be automatically sent to the Player at every step, so we set do_info_world to false
	bool do_send_world <- false;
	
	//initial location of the player
	list<point> init_locations <- [{50, 57}];
	
	init {
		//define the unity properties
		do define_properties;
		
		//add the gray square agents as static geoemtry to send to unity with the up_no_interaction unity properties.
		do add_background_geometries([square_ag[0]],up_no_interaction);
		
		
		//add the yellow square agents as static geoemtry to send to unity with the up_collider unity properties.
		do add_background_geometries([square_ag[1]],up_collider);
		
		
		//add the green square agents as static geoemtry to send to unity with the up_ray_interaction unity properties.
		do add_background_geometries([square_ag[2]],up_ray_interaction);
		
		
		//add the pink square agents as static geoemtry to send to unity with the up_grab_interaction unity properties.
		do add_background_geometries([square_ag[3]],up_grab_interaction);
		
	}
	
	
	//action that defines the different unity properties
	action define_properties {
		
		//define a unity_aspect called gray_square_aspect that will display the agents using their geometries, with a height of 2 meters, the gray color, and we use the default precision. 
		unity_aspect gray_square_aspect <- geometry_aspect(2.0, #gray, precision);
		
		//define the up_no_interaction unity property, with the name "no interaction", no specific layer, the gray_square_aspect aspect, no interaction, and the agents location are not sent back 
		//to GAMA. 
		up_no_interaction <- geometry_properties("no interaction", nil, gray_square_aspect, #no_interaction, false);
		
		// add the up_geom up_no_interaction to the list of unity_properties
		unity_properties << up_no_interaction;
		
		
		//define a unity_aspect called gray_square_aspect that will display the agents using their geometries, with a height of 2 meters, the yellow color, and we use the default precision. 
		unity_aspect yellow_square_aspect <- geometry_aspect(2.0, #yellow, precision);
		
		//define the up_collider unity property, with the name "collider", no specific layer, the yellow_square_aspect aspect, a collider interaction, and the agents location are not sent back 
		//to GAMA. 
		up_collider <- geometry_properties("collider", nil, yellow_square_aspect, #collider, false);
		
		// add the up_geom up_collider to the list of unity_properties
		unity_properties << up_collider;
	
	
		//define a unity_aspect called gray_square_aspect that will display the agents using their geometries, with a height of 2 meters, the green color, and we use the default precision. 
		unity_aspect green_square_aspect <- geometry_aspect(2.0, #green, precision);
		
		//define the up_collider unity property, with the name "collider", no specific layer, the green_square_aspect aspect, the possibility to interact with the objects through the ray interactor, and the agents location are not sent back 
		//to GAMA. 
		up_ray_interaction <- geometry_properties("ray interaction", "selectable", green_square_aspect, #ray_interactable, false);
		
		// add the up_geom up_ray_interaction to the list of unity_properties
		unity_properties << up_ray_interaction;
	
	
		//define a unity_aspect called gray_square_aspect that will display the agents using their geometries, with a height of 2 meters, the pink color, and we use the default precision. 
		unity_aspect pink_square_aspect <- geometry_aspect(2.0, #pink, precision);
		
		//define the up_grab_interaction unity property, with the name "grab interaction", no specific layer, the pink_square_aspect aspect, the possibility to grab the object, and the agents location are sent back 
		//to GAMA. 
		up_grab_interaction <- geometry_properties("grab interaction", nil, pink_square_aspect, #grabable, false);
		
		// add the up_geom up_grab_interaction to the list of unity_properties
		unity_properties << up_grab_interaction;
		
	}
	
	//action called by Unity when the green square is selected
	action select_object(string id) {
		//get the selected square_ag
		square_ag sq <- square_ag first_with (each.name = id);
		
		//if this one is not nil
		if (sq!= nil) {
			//select/unselect the square
			sq.selected <- ! sq.selected ;
			write name + " " + (sq.selected ? "is selected" : "not selected");
		}
		
	}
	
}


//species used to represent an unity player, with the default attributes. It has to inherit from the built-in species asbtract_unity_player
species unity_player parent: abstract_unity_player;


experiment main type: gui {
	output {
		display map {
			species square_ag;
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