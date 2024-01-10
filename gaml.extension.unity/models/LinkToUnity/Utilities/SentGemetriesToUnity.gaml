/**
* Name: sendGeometriesToUnity
* Author: Patrick Taillandier
* Description: A simple model allow to send geometries to Unity. To be used with the "Load geometries from GAMA"
* Tags: gis, shapefile, unity, geometry, 
*/
model sendGeometriesToUnity

global {
	
	
	//Shapefile of the bound
	shape_file bounds_shape_file <- shape_file("../Includes/bounds.shp");

	//Shapefile of the buildings
	file building_shapefile <- file("../includes/building.shp");
	//Shapefile of the roads
	file road_shapefile <- file("../includes/road.shp") ;
	//Shape of the environment

	geometry shape <- envelope(bounds_shape_file) ;
				
	init {
		//Initialization of the building using the shapefile of buildings
		create building from: building_shapefile {
			if (shape.area < 0.1) {
				do die;
			}
		}
	
		//Initialization of the road using the shapefile of roads
		create road from: road_shapefile {
			float dist <- (building closest_to self) distance_to self;
			width <- min(5.0, max(2.0,  dist - 0.5));
		}
	
	}
	
	action after_sending_background {
		do pause;
	}
}

	//Species to represent the buildings
species building {

	aspect default {
		draw shape color: darker(#darkgray).darker depth: rnd(10) + 2;
	}

}
//Species to represent the roads
species road {

	float width;
	aspect default {
		draw (shape + width) color: #white;
	}

}




species unity_linker parent: abstract_unity_linker {
	list<point> init_locations <- [{50.0, 50.0}];
	int port <- 8000;
	string player_species <- string(unity_player);
	int min_num_players <- 0;
	int max_num_players <- 1;
	bool do_send_world <- false;
	
	
	init {
		do add_background_data(building collect each.shape,  building collect each.name, "building", 10.0, true);
		do add_background_data(road collect (each.shape buffer each.width), road collect each.name, "road", 0.1, false);
		
	}
}

//Defaut species for the player
species unity_player parent: abstract_unity_player;


//Default xp with the possibility to move the player
experiment sendGeometriesToUnity  autorun: true type: unity  {
	float minimum_cycle_duration <- 0.1;
	string unity_linker_species <- string(unity_linker);
	
	output { 
		display carte type: 3d axes: false background: #black {
			species road refresh: false;
			species building refresh: false;
		}
		
	} 
}
