/**
* Name: testSendAgentLocation
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
	
	init {
		create simple_agentA number: nb_agentsA;
		create simple_agentB number: nb_agentsB;
		create static_object with:(location: {50, 40});
		
		if (nb_blocks > 0) {
			geometry free_place <- copy(shape) - (block_size/2.0) - (world.location buffer block_size);
			loop times: nb_blocks {
				if free_place = nil {break;}
				create block {
					shape <- square(block_size);
					location <- any_location_in(free_place);
					free_place <- free_place - shape;
				}
			} 
			
		}
	}
	
	
	/*reflex update_agent {
		
		do update_agents(simple_agentA, nb_agentsA);
		do update_agents(simple_agentB, nb_agentsB);
	}*/
	
	action update_agents( species<agent> sp, int number) {
		if length(sp) > number {
			ask (length(sp) - number) among sp {
				do die;
			}
		} else if length(sp) < number {
			create sp number: number - length(sp) ; 
		}	
	}
	
	
}



species block {
	rgb color <- #black;
	rgb color_hotspot <- #red;
	rgb color_hotspot_dist <- rgb(255,0,0.0,0.5);
	bool is_hotspot <- false;
	
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
				bounds <- my_hot_spot + distance_hostspot;
				target <- any_location_in(bounds);
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
	
	
	reflex move {
		if (target = nil ){
			if (bounds != nil) {
				target <- any_location_in(bounds);
			} else {
				target <- any_location_in(world);
			}
		}	
		do goto target: target;
		if (location = target) {
			target <- nil;
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
	rgb color <- #red;
	int index <- 2;
	
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
