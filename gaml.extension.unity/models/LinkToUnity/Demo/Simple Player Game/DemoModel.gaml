/**
* Name: DemoModel
* Example of model using the UnityLink Template
* Author: Patrick Taillandier
* Tags: 
*/
model DemoModel
 

global {
	int nb_agentsA <- 100 parameter: true min: 0 max: 5000 step: 1;
	int nb_agentsB <- 100 parameter: true min: 0 max: 5000 step: 1;
	float cycle_duration <- 0.03 parameter: true min: 0.0 max: 0.1 step: 0.01;
	float step <- 0.1 parameter: true min: 0.1 max: 2.0 step: 0.1;
	int nb_blocks <- 10 parameter: true min: 0 max: 10 step: 1.0;
	float block_size <- 5.0 parameter: true min: 1.0 max: 10.0 step: 1.0;
	float distance_hostspot <- 10.0 parameter: true min: 1.0 max: 20.0 step: 1.0;
	geometry free_place ;
	
	init {
		create static_object with:(location: {5, 50}) {
			taken_place <- rectangle(10.0, 100.0) at_location {5, 50};
		}
		
		if (nb_blocks > 0) {
			free_place <- copy(shape) - (block_size/2.0) - (world.location buffer block_size);
			ask static_object {
				free_place <- free_place - taken_place;
			}
			loop times: nb_blocks {
				if free_place = nil {break;}
				create block {
					shape <- square(block_size);
					location <- any_location_in(free_place);
					free_place <- free_place - shape;
				}
			} 
			
		}
		ask block {
			bounds <- (shape + distance_hostspot) inter free_place;
		}
		create simple_agentA number: nb_agentsA with: (location:any_location_in(free_place));
		create simple_agentB number: nb_agentsB with: (location:any_location_in(free_place));
		
	}
	
	
	
	 
}



species block {
	rgb color <- #black;
	rgb color_hotspot <- #red;
	rgb color_hotspot_dist <- rgb(255,0,0.0,0.5);
	bool is_hotspot <- false;
	geometry bounds;
	
	action update_hotspots {
		list<block> hotspots <- block where each.is_hotspot;
		if (empty(hotspots)) {
			ask simple_agentA + simple_agentB {
				my_hot_spot <- nil;
				bounds <- nil;
			}
		}
		else {
			ask simple_agentA + simple_agentB {
				my_hot_spot <- one_of(hotspots);
				bounds <- my_hot_spot.bounds;
				target <- any_location_in(free_place);
			}
		}
	}
	action become_hotspot {
		is_hotspot <- true;
		do update_hotspots;
	}
	action remove_hotspot {
		is_hotspot <- false;
		do update_hotspots;
	}
	aspect default {
		if (is_hotspot) {
			draw shape + distance_hostspot color: color_hotspot_dist;
		}
		draw shape color:is_hotspot ?color_hotspot : color ;
		
	}
}
 


species simple_agentA  skills: [moving ] {
	int index <- 0;
	rgb color <- #blue;
	float amplitude <- 30.0;
	block my_hot_spot;
	geometry bounds;
	point target;
	path my_path;
	
	action choose_target(geometry bds) {
		target <- any_location_in(bds);
		int cpt <- 10;
		loop while: cpt >= 0 and ! (bds covers line([location, target])  ) {
			target <- any_location_in(bds);
			cpt <- cpt - 1;
		}
		if (cpt <= 0) {
			location <- any_location_in(free_place);
			target <- location;
		}
	}
	reflex move {
		if (target = nil ){
			if (bounds != nil) {
				do choose_target(bounds);
			} else {
				do choose_target(free_place);
			}
		}	
		do goto target: target;
		if (location = target) {
			target <- nil;
		}  
		if !(location overlaps free_place) {
			target <- any_location_in(world);
		}
		
	}
	
	aspect default {
		draw triangle(1) rotate:heading +90 color: color ;
	}
}

species simple_agentB parent: simple_agentA skills: [moving] {
	int index <- 1;
	rgb color <- #green;
	float amplitude <- 60.0;
}

species static_object  {
	rgb color <- #green;
	int index <- 2;
	
	geometry taken_place;
	
	aspect default {
		draw cube(2) color: color ;
	}
}


experiment simple_simulation type: gui autorun: true{
	float minimum_cycle_duration <- cycle_duration;
	
	
	output {
		display map { 
			
			species simple_agentA;
			species simple_agentB;
			species static_object;
			species block;
		}
	}
}