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
		
	unity_linker the_linker;
	
	init {
		create unity_linker with:(location_init:{50.0, 50.0}, port: 8000)		
		{
			create_player <- false;
			the_linker <- self;
		}
		write "Waiting to recieve geometries";
	}
}

species unity_linker parent: abstract_unity_linker {
	// connection port
	int port <- 8000;
	
	action manage_new_message(string mes) {
		if ("points" in mes) and not geometries_received{
			map answer <- map(mes);
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
						pts << {float(pt[0])/the_linker.precision ,float(pt[1]) /the_linker.precision};
					}
				}
			}
			geometries_received <- true;
			save object to: output_file format: shp crs: crs_code;
			
			write "Geometries recieved";			
			if the_linker.unity_client = nil {
				write "no client to send to";
			} else {
				ask the_linker{
					do send_message mes:  'ok';	
				}
			}
			the_linker.connect_to_unity <- false;
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

experiment importGeometriesFromUnity type: gui autorun: true  {
	float minimum_cycle_duration <- 0.1;
	output{
		display carte type: 3d axes: true background: #black {
			species object ;
		}

	}

}
