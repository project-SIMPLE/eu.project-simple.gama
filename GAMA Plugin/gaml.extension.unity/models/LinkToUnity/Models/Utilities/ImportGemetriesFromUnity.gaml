/**
* Name: importGeometriesFromUnity
* Author: Patrick Taillandier
* Description: A simple model allow to import geometries from Unity. To be used with the "Export geometries to GAMA"
* Tags: gis, shapefile, unity, geometry, 
*/
model importGeometriesFromUnity

global {
	
	string output_file <- "generated/blocks.shp";
	
	geometry shape <- square(100);
	
	int crs_code <- 2154;	
	
	bool geometries_received <- false;
	
	init {
		write "Waiting to recieve geometries";
	}
}


species unity_linker parent: abstract_unity_linker {
	// connection port
	list<point> init_locations <- [{50.0, 50.0}];
	int port <- 8000;
	string player_species <- string(unity_player);
	int min_num_players <- 0;
	int max_num_players <- 1;
	bool do_send_world <- false;
	
	action receive_geometries(string geoms) {
		write geoms;
		
		if ("points" in geoms) {
			map answer <- from_json(geoms);
			list<list<list>> objects <- answer["points"];
			list<int> heights <- answer["heights"];
			list<string> names <- answer["names"];
			list<point> pts;
			int cpt <- 0;
			loop coords over: objects {
				loop pt over: coords {
					if empty(pt) {
						float tol <- 0.0001;
						list<geometry> gs <- [];
						list<point> ps <- [];
						if not empty(pts)and length(pts) > 2 {
								
							list<point> ts;
							list<geometry> triangles;
							
							loop i from: 0 to: length(pts) -1 {
								ts << pts[i];
								if (length(ts) = 3) {
									triangles << polygon(ts);
									ts <- [];
									
								}
										
							}
							geometry g <- union(triangles collect (each+ tol));
							loop gg over: g.geometries {
								create object with:(shape: gg, name:names[cpt]);
							}
							
						}
					 
						cpt <- cpt +1;
						pts <- [];
					
					} else {
						pts << {float(pt[0])/self.precision ,float(pt[1]) /self.precision};
					}
				}
			}
			geometries_received <- true;
			save object to: output_file format:"shp" crs: crs_code;
			 
			write "Geometries recieved";			
			ask world {
				do pause;
			}
		}
		
			
	}
	
}

//Species to represent the object imported
species object {

	aspect default {
		draw shape color: #white ;
	}

}


//Defaut species for the player
species unity_player parent: abstract_unity_player;


experiment importGeometriesFromUnity type: unity autorun: true  {
	float minimum_cycle_duration <- 0.1;
	string unity_linker_species <- string(unity_linker);
	output{
		display carte type: 3d axes: true background: #black {
			species object ;
		}

	}

}
