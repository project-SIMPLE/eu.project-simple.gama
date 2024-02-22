package gaml.extension.unity.species;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.java_websocket.exceptions.WebsocketNotConnectedException;

import gaml.extension.unity.types.UnityProperties;
import gaml.extension.unity.types.UnityPropertiesType;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.root.PlatformAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.operators.Spatial.Queries;
import msi.gaml.operators.Spatial.Transformations;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement.WithArgs;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.serializer.gaml.SerialisationOperators;

@species(name = "abstract_unity_linker", skills={"network"})
@vars({ 
	@variable(name = AbstractUnityLinker.CONNECT_TO_UNITY, type = IType.BOOL, init = "true",
			doc = { @doc ("Activate the unity connection; if activated, the model will wait for an connection from Unity to start")}), 
	@variable(name = AbstractUnityLinker.READY_TO_MOVE_PLAYER, type = IType.LIST, init = "[]",
	doc = { @doc ("list of players that are readdy to have their position updated from Unity")}), 

	@variable(name = AbstractUnityLinker.MIN_NUMBER_PLAYERS, type = IType.INT, init = "0",
	doc = { @doc ("Number of Unity players required to start the simulation")}), 
	@variable(name = AbstractUnityLinker.MAX_NUMBER_PLAYERS, type = IType.INT, init = "1",
	doc = { @doc ("Maximal number of Unity players")}), 
	@variable(name = AbstractUnityLinker.PRECISION, type = IType.INT, init = "10000", 
			doc = { @doc ("Number of decimal for the data (location, rotation)")}),  

	@variable(name = AbstractUnityLinker.UNITY_PROPERTIES, type = IType.LIST, of =  UnityPropertiesType.UNITYPROPERTIESTYPE_ID, 
	doc = { @doc ("List of background geometries to sent to Unity.")}),  

	@variable(name = AbstractUnityLinker.BACKGROUND_GEOMETRIES, type = IType.MAP, 
	doc = { @doc ("Map of background geometries to sent to Unity with the unity properties to use.")}),  

	@variable(name = AbstractUnityLinker.GEOMETRIES_TO_SEND, type = IType.MAP,  
			doc = { @doc ("List of geometries to sent to Unity with the unity properties to use. It could be updated each simulation step")}),  
	
	@variable(name = AbstractUnityLinker.DO_SEND_WORLD, type = IType.BOOL, init="true", 
			doc = { @doc ("Has the agents has to be sent to unity?")}),  
	
	@variable(name = AbstractUnityLinker.INITIALIZED, type = IType.BOOL, init="false", 
	doc = { @doc ("Has the world being initialized yet?")}),  

	@variable(name = AbstractUnityLinker.PLAYER_SPECIES, type = IType.STRING, 
	doc = { @doc ("Species of the player agent")}),  
	
	@variable(name = AbstractUnityLinker.END_MESSAGE_SYMBOL, type = IType.STRING, init = "|||",
	doc = { @doc ("Symbol to be added at the end of the messages (only when the middleware is not used); it should be the same defined in Unity")}),  
	
	@variable(name = AbstractUnityLinker.RECEIVE_INFORMATION, type = IType.BOOL, init="false", 
	doc = { @doc ("should GAMA receive information from Unity?")}),  

	@variable(name = AbstractUnityLinker.MOVE_PLAYER_EVENT, type = IType.BOOL, init="false", 
			doc = { @doc ("Does the player agent moved from GAMA?")}),
	
	@variable(name = AbstractUnityLinker.MOVE_PLAYER_FROM_UNITY, type = IType.BOOL, init="true", 
			doc = { @doc ("Has the player to move in GAMA as it moves in Unity?")}), 
	
	@variable(name = AbstractUnityLinker.USE_MIDDLEWARE, type = IType.BOOL, init="true", 
	doc = { @doc ("Use of the middleware to connect Unity and GAMA? Direct connection is only usable for 1 player game")}),  

	@variable(name = AbstractUnityLinker.NEW_PLAYER_POSITION, type = IType.MAP,  
			doc = { @doc ("The new poistion of the player to be sent to Unity - map with key: agent name, value: list of int [x,y]")}), 
	@variable(name = AbstractUnityLinker.DISTANCE_PLAYER_SELECTION, type = IType.FLOAT, init = "2.0", 
	doc = { @doc ("Maximal distance to select a player agent")}), 
	@variable(name = AbstractUnityLinker.INIT_LOCATIONS, type = IType.LIST, of=  IType.POINT, 
		doc = { @doc ("Init locations of the player agents in the environment - this information will be sent to Unity to move the players accordingly")}), 

	@variable(name = AbstractUnityLinker.THE_PLAYERS, type = IType.MAP, 
	doc = { @doc ("Player agents indexes by their name")}), 
})
public class AbstractUnityLinker extends GamlAgent { 
	public static final String PLAYER_SPECIES = "player_species";
	public static final String MIN_NUMBER_PLAYERS = "min_num_players";
	public static final String MAX_NUMBER_PLAYERS = "max_num_players";
	public static final String CONNECT_TO_UNITY = "connect_to_unity";
	public static final String PRECISION = "precision";
	public static final String GEOMETRIES_TO_SEND = "geometries_to_send";
	public static final String BACKGROUND_GEOMETRIES = "background_geometries";
	public static final String UNITY_PROPERTIES = "unity_properties";
	public static final String DO_SEND_WORLD = "do_send_world";

	public static final String GROUND_DEPTH = "ground_depth";
	public static final String END_MESSAGE_SYMBOL = "end_message_symbol";

	public static final String MOVE_PLAYER_EVENT = "move_player_event";
	public static final String USE_MIDDLEWARE = "use_middleware";
	public static final String MOVE_PLAYER_FROM_UNITY = "move_player_from_unity";
	public static final String INIT_LOCATIONS = "init_locations";
	public static final String THE_PLAYERS = "player_agents";

	public static final String NEW_PLAYER_POSITION = "new_player_position";
	public static final String DISTANCE_PLAYER_SELECTION = "distance_player_selection";

	public static final String INITIALIZED = "initialized";

	public static final String RECEIVE_INFORMATION = "receive_information";

	public static final String WAITING_MESSAGE = "ready";
	
	public static final String READY_TO_MOVE_PLAYER = "ready_to_move_player";
	
	
	public static final String HEADING = "heading";
	public static final String ADD_TO_MAP = "add_to_map";
	
	public static final String LOC_TO_SEND = "loc_to_send";
	public static final String TO_MAP = "to_map";
	public static final String SPECIES_INDEX = "index";
	
	public static final String CONTENTS = "contents";
	public static final String ID = "id";
	public static final String CONTENT_MESSAGE = "contents";

	public static final String OUTPUT = "output";
	public static final String TYPE = "type";
	public static final String SERVER = "server";

	private IMap currentMessage;
	
	
	
	@getter (AbstractUnityLinker.DISTANCE_PLAYER_SELECTION)
	public static Double getDistanceSelection(final IAgent agent) {
		return (Double) agent.getAttribute(DISTANCE_PLAYER_SELECTION);
	}
	@setter(AbstractUnityLinker.DISTANCE_PLAYER_SELECTION)
	public static void setDistanceSelection(final IAgent agent, final Double val) {
		agent.setAttribute(DISTANCE_PLAYER_SELECTION, val);
	}
	
		
	@getter (AbstractUnityLinker.CONNECT_TO_UNITY)
	public static Boolean getConnectToUnity(final IAgent agent) {
		return (Boolean) agent.getAttribute(CONNECT_TO_UNITY);
	}
	@setter(AbstractUnityLinker.CONNECT_TO_UNITY)
	public static void setConnectToUnity(final IAgent agent, final Boolean ctu) {
		agent.setAttribute(CONNECT_TO_UNITY, ctu);
	}
	
	@getter (AbstractUnityLinker.USE_MIDDLEWARE)
	public static Boolean getUseMiddleware(final IAgent agent) {
		return (Boolean) agent.getAttribute(USE_MIDDLEWARE);
	}
	@setter(AbstractUnityLinker.USE_MIDDLEWARE)
	public static void setUseMiddleware(final IAgent agent, final Boolean ctu) {
		agent.setAttribute(USE_MIDDLEWARE, ctu);
	}
		
	@getter (AbstractUnityLinker.MIN_NUMBER_PLAYERS)
	public static Integer getMinPlayer(final IAgent agent) {
		return (Integer) agent.getAttribute(MIN_NUMBER_PLAYERS);
	}
	@setter(AbstractUnityLinker.MIN_NUMBER_PLAYERS)
	public static void setMinPlayer(final IAgent agent, final Integer val) {
		agent.setAttribute(MIN_NUMBER_PLAYERS, val);
	}
	
		
	@getter (AbstractUnityLinker.MAX_NUMBER_PLAYERS)
	public static Integer getMaxPlayer(final IAgent agent) {
		return (Integer) agent.getAttribute(MAX_NUMBER_PLAYERS);
	}
	@setter(AbstractUnityLinker.MAX_NUMBER_PLAYERS)
	public static void setMaxPlayer(final IAgent agent, final Integer val) {
		agent.setAttribute(MAX_NUMBER_PLAYERS, val);
	}
	

	@getter (AbstractUnityLinker.PRECISION)
	public static Integer getPrecision(final IAgent agent) {
		return (Integer) agent.getAttribute(PRECISION);
	}
	@setter(AbstractUnityLinker.PRECISION)
	public static void setPrecision(final IAgent agent, final Integer val) {
		agent.setAttribute(PRECISION, val);
	}
	
	@getter (AbstractUnityLinker.UNITY_PROPERTIES)
	public static  IList<UnityProperties> getUnityProperties(final IShape agent) {
		return ( IList<UnityProperties>) agent.getAttribute(UNITY_PROPERTIES);
	}
	@setter(AbstractUnityLinker.UNITY_PROPERTIES)
	public static void setUnityProperties(final IAgent agent, final IList<UnityProperties> val) {
		agent.setAttribute(UNITY_PROPERTIES, val);
	}
	
	@getter (AbstractUnityLinker.BACKGROUND_GEOMETRIES)
	public static  IMap<IShape,UnityProperties> getBackgroundGeometries(final IShape agent) {
		return ( IMap<IShape,UnityProperties>) agent.getAttribute(BACKGROUND_GEOMETRIES);
	}
	@setter(AbstractUnityLinker.BACKGROUND_GEOMETRIES)
	public static void setBackgroundGeometries(final IAgent agent, final IMap<IShape,UnityProperties> val) {
		agent.setAttribute(BACKGROUND_GEOMETRIES, val);
	}
		
	@getter (AbstractUnityLinker.GEOMETRIES_TO_SEND)
	public static  IMap<IShape, UnityProperties> getGeometriesToSend(final IAgent agent) {
		return ( IMap<IShape,UnityProperties>) agent.getAttribute(GEOMETRIES_TO_SEND);
	}
	@setter(AbstractUnityLinker.GEOMETRIES_TO_SEND)
	public static void setGeometriesToSend(final IAgent agent, final IMap<IShape,UnityProperties> val) {
		agent.setAttribute(GEOMETRIES_TO_SEND, val);
	}
	
	@getter (AbstractUnityLinker.END_MESSAGE_SYMBOL)
	public static String getEndMessageSymbol(final IAgent agent) {
		return (String) agent.getAttribute(END_MESSAGE_SYMBOL);
	}
	@setter(AbstractUnityLinker.END_MESSAGE_SYMBOL)
	public static void setEndMessageSymbol(final IAgent agent, final String val) {
		agent.setAttribute(END_MESSAGE_SYMBOL, val);
	}
	
	@getter (AbstractUnityLinker.PLAYER_SPECIES)
	public static String getPlayerSpecies(final IAgent agent) {
		return (String) agent.getAttribute(PLAYER_SPECIES);
	}
	@setter(AbstractUnityLinker.PLAYER_SPECIES)
	public static void setPlayerSpecies(final IAgent agent, final String val) {
		agent.setAttribute(PLAYER_SPECIES, val);
	}
	
	@getter (AbstractUnityLinker.DO_SEND_WORLD)
	public static Boolean getDoSendWorld(final IAgent agent) {
		return (Boolean) agent.getAttribute(DO_SEND_WORLD);
	}
	@setter(AbstractUnityLinker.DO_SEND_WORLD)
	public static void setDoSendWorld(final IAgent agent, final Boolean val) {
		agent.setAttribute(DO_SEND_WORLD, val);
	}
	
	@getter (AbstractUnityLinker.RECEIVE_INFORMATION)
	public static Boolean getReceiveInformation(final IAgent agent) {
		return (Boolean) agent.getAttribute(RECEIVE_INFORMATION);
	}
	@setter(AbstractUnityLinker.RECEIVE_INFORMATION)
	public static void setReceiveInformation(final IAgent agent, final Boolean val) {
		agent.setAttribute(RECEIVE_INFORMATION, val);
	}
	
	@getter (AbstractUnityLinker.INITIALIZED)
	public static Boolean getInitialized(final IAgent agent) {
		return (Boolean) agent.getAttribute(INITIALIZED);
	}
	@setter(AbstractUnityLinker.INITIALIZED)
	public static void setInitialized(final IAgent agent, final Boolean val) {
		agent.setAttribute(INITIALIZED, val);
	}
		
	@getter (AbstractUnityLinker.MOVE_PLAYER_EVENT)
	public static Boolean getMovePlayerEvent(final IAgent agent) {
		return (Boolean) agent.getAttribute(MOVE_PLAYER_EVENT);
	}
	@setter(AbstractUnityLinker.MOVE_PLAYER_EVENT)
	public static void setMovePlayerEvent(final IAgent agent, final Boolean val) {
		agent.setAttribute(MOVE_PLAYER_EVENT, val);
	}
		
	@getter (AbstractUnityLinker.MOVE_PLAYER_FROM_UNITY)
	public static Boolean getMovePlayerFromUnity(final IAgent agent) {
		return (Boolean) agent.getAttribute(MOVE_PLAYER_FROM_UNITY);
	}
	@setter(AbstractUnityLinker.MOVE_PLAYER_FROM_UNITY)
	public static void setMovePlayerFromUnity(final IAgent agent, final Boolean val) {
		agent.setAttribute(MOVE_PLAYER_FROM_UNITY, val);
	}
	
	@getter (AbstractUnityLinker.INIT_LOCATIONS)
	public static IList<GamaPoint> getPlayerLocationInit(final IAgent agent) {
		return (IList<GamaPoint>) agent.getAttribute(INIT_LOCATIONS);
	}
	@setter(AbstractUnityLinker.INIT_LOCATIONS)
	public static void setPlayerLocationInit(final IAgent agent, final IList val) {
		agent.setAttribute(INIT_LOCATIONS, val);
	}
	
	@getter (AbstractUnityLinker.READY_TO_MOVE_PLAYER)
	public static IList<IAgent> getReadyToMovePlayers(final IAgent agent) {
		return (IList<IAgent>) agent.getAttribute(READY_TO_MOVE_PLAYER);
	}
	@setter(AbstractUnityLinker.READY_TO_MOVE_PLAYER)
	public static void setReadyToMovePlayers(final IAgent agent, final IList val) {
		agent.setAttribute(READY_TO_MOVE_PLAYER, val);
	}
	
	@getter (AbstractUnityLinker.THE_PLAYERS)
	public static IMap<String, IAgent> getPlayers(final IAgent agent) {
		return (IMap<String, IAgent>) agent.getAttribute(THE_PLAYERS);
	}
	@setter(AbstractUnityLinker.THE_PLAYERS)
	public static void setPlayers(final IAgent agent, final IMap<String, IAgent> val) {
		agent.setAttribute(THE_PLAYERS, val);
	}
		
	
	@getter (AbstractUnityLinker.NEW_PLAYER_POSITION)
	public static  IMap<String, IList<Integer>> getNewPlayerPosition(final IAgent agent) {
		return ( IMap<String,IList<Integer>>) agent.getAttribute(NEW_PLAYER_POSITION);
	}
	@setter(AbstractUnityLinker.NEW_PLAYER_POSITION)
	public static void setNewPlayerPosition(final IAgent agent, final IMap<String, IList<Integer>> val) {
		agent.setAttribute(NEW_PLAYER_POSITION, val);
	}
	
	public AbstractUnityLinker(IPopulation<? extends IAgent> s, int index) {
		super(s, index);
		
	}
	
	@Override
	public void dispose() {
		
		super.dispose();
	}

	
	private Object doActionNoArg(IScope scope, String actionName) {
		WithArgs act = getAgent().getSpecies().getAction(actionName);
		return act.executeOn(scope);
	}
	
	private Object doAction1Arg(IScope scope, String actionName, String argName, Object ArgVal ) {
		Arguments args = new Arguments();
		args.put(argName, ConstantExpressionDescription.create(ArgVal));
		WithArgs act = getAgent().getSpecies().getAction(actionName);
		act.setRuntimeArgs(scope, args);
		return act.executeOn(scope);
	}
	
	private Object doAction2Arg(IScope scope, String actionName, String argName1, Object ArgVal1, String argName2, Object ArgVal2 ) {
		Arguments args = new Arguments();
		args.put(argName1, ConstantExpressionDescription.create(ArgVal1));
		args.put(argName2, ConstantExpressionDescription.create(ArgVal2));
		WithArgs act = getAgent().getSpecies().getAction(actionName);
		act.setRuntimeArgs(scope, args);
		return act.executeOn(scope);
	}
	
	private Object doAction3Arg(IScope scope, String actionName, String argName1, Object ArgVal1, String argName2, Object ArgVal2 , String argName3, Object ArgVal3) {
		Arguments args = new Arguments();
		args.put(argName1, ConstantExpressionDescription.create(ArgVal1));
		args.put(argName2, ConstantExpressionDescription.create(ArgVal2));
		args.put(argName3, ConstantExpressionDescription.create(ArgVal3));
		WithArgs act = getAgent().getSpecies().getAction(actionName);
		act.setRuntimeArgs(scope, args);
		return act.executeOn(scope);
	}
	
	
	

	@Override
	public Object _init_(IScope scope) {
		Object init = super._init_(scope);
		startSimulation(scope);
		return init ;
	}
	
	private void interactionWithPlayer(final IScope scope, IAgent ag) {
		GamaPoint pt = scope.getGui().getMouseLocationInModel();
		if (pt != null) {
			IList<IAgent> ags = GamaListFactory.create();
			for (String playerName : getPlayers(ag).keySet()) {
				IAgent a = getPlayers(ag).get(playerName);
				if (a == null) {
					getPlayers(ag).remove(playerName);
					continue;
				}
				if (a.euclidianDistanceTo(pt) <=   getDistanceSelection(ag))
					ags.add(a);
			}
			if (ags.isEmpty()) {
				Optional<IAgent> selected = getPlayers(ag).getValues().stream().filter (a -> (Boolean) a.getAttribute("selected")).findFirst(); 
				if (selected.isPresent()) {
					doAction2Arg(scope, "move_player", "player", selected.get(), "loc", pt) ;
				}
			} else {
				IAgent player = (IAgent) Queries.closest_to(scope, ags, pt);
				
				player.setAttribute(AbstractUnityPlayer.SELECTED, !((Boolean)player.getAttribute(AbstractUnityPlayer.SELECTED)));
			}
				
		} 
	}
	
 	@Override
	public boolean doStep(final IScope scope) {
		if (super.doStep(scope)) {
			IAgent ag = getAgent();
			setInitialized(ag, true);
			if (getConnectToUnity(ag)) {
				if (getInitialized(ag) && getMovePlayerEvent(ag) && !getPlayers(ag).isEmpty()) {
					setMovePlayerEvent(ag, false);
					interactionWithPlayer(scope, ag);
				}
				if(getInitialized(ag)) {
					if (getDoSendWorld(ag)) {
						doActionNoArg(scope, "send_world");
					}
					
				}

				if (currentMessage != null && !currentMessage.isEmpty()) {
					sendCurrentMessage(scope);
				}
				
			}
			return true;
		}
		return false;
	}

 	
	private void sendCurrentMessage(IScope scope) {
		PlatformAgent pa = GAMA.getPlatformAgent();
		String mes = "";
		if (getUseMiddleware(getAgent())) {
			mes = SerialisationOperators.toJson(scope, currentMessage, false);
			pa.sendMessage(scope,ConstantExpressionDescription.create(mes));
		} else {
			Iterator<IMap> it = (Iterator<IMap>) ((IList<IMap>) currentMessage.get(CONTENTS)).iterable(scope).iterator();
			while(it.hasNext()) {
				IMap v = it.next();
				Object c = v.get(CONTENT_MESSAGE);
				mes += SerialisationOperators.toJson(scope, c, false) + "|||" ;	
			}
		}
		if (!mes.isBlank() && !mes.equals("{}")) {
			try {
				pa.sendMessage(scope,ConstantExpressionDescription.create(mes));
			} catch (WebsocketNotConnectedException e ) {
				if (!getUseMiddleware(getAgent())) {
					getPlayers(pa).get(0).dispose();
					getPlayers(pa).clear();
				}
			}
		}	
		currentMessage.clear();
		
	}
	
	
	private void addToCurrentMessage(IScope scope, IList<String> recipients, Object content ) {
		
		if (currentMessage == null) {
			
			currentMessage = GamaMapFactory.synchronizedMap(GamaMapFactory.create());
		}
		if (currentMessage.isEmpty()) {
			currentMessage.put(CONTENTS, GamaListFactory.create());
			currentMessage.put(TYPE, OUTPUT);
		}
		IMap newMessage = GamaMapFactory.create();
		newMessage.put(ID, recipients);
		newMessage.put(CONTENT_MESSAGE, content);
		((IList) currentMessage.get(CONTENTS)).add(newMessage);
	}
	
	@action (
			name = "send_message",
			args = {
					 @arg (name = "players",
								type = IType.LIST,
								doc = @doc ("Players to send the geometries to")),
					 @arg (name = "mes",
							type = IType.MAP, 
							doc = @doc ("Map to send"))},
			doc = { @doc (
					value = "send a message to the Unity Client")})
	public void primSentMessage(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		IList<IAgent> players = (IList) scope.getListArg("players");
		IMap mes = (IMap) scope.getArg("mes");
		
		
		IMap message = GamaMapFactory.create();
		message.put(CONTENTS, GamaListFactory.create());
		message.put(TYPE, OUTPUT);
		
		IMap newMessage = GamaMapFactory.create();
		
		IList<String> recipients = GamaListFactory.create();
		for(IAgent a : players) {
			recipients.add(a.getName());
		}
		newMessage.put(ID, recipients);
		newMessage.put(CONTENT_MESSAGE, mes);
		((IList) message.get(CONTENTS)).add(newMessage);
		
		PlatformAgent pa = GAMA.getPlatformAgent();
		String mesStr = SerialisationOperators.toJson(scope, message, false);
		
		pa.sendMessage(scope,ConstantExpressionDescription.create(mesStr));
	
	}

	@action (
			name = "send_world",
			doc = { @doc (
					value = "send the current state of the world to the Unity clients")})
	public void primSentWorld(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		IMap<String, Object> toSend = GamaMapFactory.create();
		Set<String> players = getPlayers(ag).keySet();
		
		for (String playerName : players)  {
			IAgent player = getPlayers(ag).get(playerName);
			if (player == null) {
				getPlayers(ag).remove(playerName);
				continue;
			}
			if (getDoSendWorld(getAgent())) {
				
			
				IMap<IShape, UnityProperties> geoms =  getGeometriesToSend(ag);
			
				if (geoms != null) {
					boolean filterDist = player.getAttribute(AbstractUnityPlayer.PLAYER_AGENTS_PERCEPTION_RADIUS) != null &&
							((Double) player.getAttribute(AbstractUnityPlayer.PLAYER_AGENTS_PERCEPTION_RADIUS)) > 0 ;
					
					boolean filterProx = player.getAttribute(AbstractUnityPlayer.PLAYER_AGENTS_MIN_DIST) != null &&
							((Double) player.getAttribute(AbstractUnityPlayer.PLAYER_AGENTS_MIN_DIST)) > 0;
					
					if (filterDist || filterProx) {
						IList<IShape> geomsPl = GamaListFactory.create();
						geomsPl.addAll(geoms.keySet());
						if (filterDist) geomsPl = (IList<IShape>) doAction1Arg(scope, "filter_distance", "geometries", geomsPl);
						if (filterProx) geomsPl = (IList<IShape>) doAction1Arg(scope, "filter_overlapping", "geometries", geomsPl);
						geoms = GamaMapFactory.create();
						IMap<IShape, UnityProperties> geoms2 =  getGeometriesToSend(ag);
						
						for (IShape s : geomsPl) {
							geoms.put(s, geoms2.get(s));
						}
					}
					
					doAction3Arg(scope, "send_geometries", "player", player, "geoms" ,geoms, "update_position", !getNewPlayerPosition(ag).get(player.getName()).isEmpty());
					
					
					
				}
			}
			
			
			//toSend.put("position", getNewPlayerPosition(ag).get(player.getName()));
			addToCurrentMessage(scope, buildPlayerListfor1Player(scope, player), toSend);
			
			
			doAction1Arg(scope, "after_sending_world", "map_to_send", toSend);
		}		
	}
		
	@action (
			name = "send_geometries",
					args = { @arg (
							name = "player",
							type = IType.AGENT,
							doc = @doc ("Player to which the message will be sent")),
							@arg (
									name = "update_position",
									type = IType.BOOL,
									doc = @doc ("Has the player to be sent to Unity?")),
							@arg (
								name = "geoms",
										type = IType.MAP,
										doc = @doc ("Map of geometry to send (geometry::unity_property)"))},
					doc = { @doc (
					value = "send the background geometries to the Unity client")})
	public void primSentGeometries(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		IAgent player = (IAgent) scope.getArg("player");
		Boolean updatePos = scope.getBoolArg("update_position");
		IMap<IShape,UnityProperties> geoms =  (IMap<IShape, UnityProperties>) scope.getArg("geoms");
		IMap<String, Object> toSend = GamaMapFactory.create();
		IList<Integer> posT = GamaListFactory.create(Types.INT);
		int precision = getPrecision(ag);
		
		if (updatePos) {
			List<Integer> pos = new ArrayList<>(getNewPlayerPosition(ag).get(player.getName()));
			toSend.put("position", pos);
			getNewPlayerPosition(ag).get(player.getName()).clear();
		}
		List<String> names = new ArrayList<>();
		List<String> propertyID = new ArrayList<>();
		
		
	    List pointsLoc = new ArrayList<>();
	    List pointsGeom = new ArrayList<>();
	    for(IShape g : geoms.keySet()) {
	    	UnityProperties up = geoms.get(g);
			names.add( g instanceof IAgent ? ((IAgent)g).getName()  : (String) g.getAttribute("name"));
			propertyID.add(up.getId());
	    	boolean hp = up.getAspect().isPrefabAspect();
	    	if (hp) {
				pointsLoc.add((IMap) doAction1Arg(scope, "message_geometry_loc", "geom", g));
			} else {
				pointsGeom.add((IMap) doAction1Arg(scope, "message_geometry_shape", "geom", g));
			}
		}
	  	toSend.put("names", names);
	  	toSend.put("propertyID", propertyID);
	  	toSend.put("pointsLoc", pointsLoc);
	  	toSend.put("pointsGeom", pointsGeom);
		
		addToCurrentMessage(scope, buildPlayerListfor1Player(scope, player), toSend);
		
		doAction1Arg(scope, "after_sending_geometries", "player", player);	
		
	}

	@action (
			name = "message_geometry_shape",
			args = { @arg (
					name = "geom",
					type = IType.GEOMETRY,
					doc = @doc ("Geometry to send to Unity"))},
					
			doc = { @doc (
					value = "Action called by the send_world action that returns the message to send to Unity")})
	public IMap primMessageGeomsShape(final IScope scope) throws GamaRuntimeException {
		IList<Integer> vals = GamaListFactory.create();
		IShape geom = (IShape) scope.getArg("geom");
		int precision = getPrecision(getAgent());
		
		
		for (GamaPoint pt : geom.getPoints()) {
			vals.add((int)(pt.x * precision));
			vals.add((int)(pt.y * precision));
			//vals.add((int)(pt.z * precision));

		}
		IMap<String, Object> map  = GamaMapFactory.create();
		map.put("c", vals);
		Arguments args = new Arguments();
		args.put("map", ConstantExpressionDescription.create(map));
		args.put("geom", ConstantExpressionDescription.create(geom));
		WithArgs actATM = getAgent().getSpecies().getAction(ADD_TO_MAP);
			
		actATM.setRuntimeArgs(scope, args);
		actATM.executeOn(scope);
		
		return map;
	}
	
	@action (
			name = "message_geometry_loc",
			args = { @arg (
					name = "geom",
					type = IType.GEOMETRY,
					doc = @doc ("Geometry to send to Unity"))},
					
			doc = { @doc (
					value = "Action called by the send_world action that returns the message to send to Unity")})
	public IMap primMessageGeoms(final IScope scope) throws GamaRuntimeException {
		IList<Integer> vals = GamaListFactory.create();
		IShape geom = (IShape) scope.getArg("geom");
		int precision = getPrecision(getAgent());
		vals.add((int)(geom.getLocation().x * precision));
		vals.add((int)(geom.getLocation().y * precision));
		vals.add((int)(geom.getLocation().z * precision));
		Double hd = (Double) geom.getAttribute("heading");
		if (hd == null) hd = 0.0;
		vals.add((int)(hd * precision));
		IMap<String, Object> map  = GamaMapFactory.create();
		map.put("c", vals);
			
		Arguments args = new Arguments();
		args.put("map", ConstantExpressionDescription.create(map));
		args.put("geom", ConstantExpressionDescription.create(geom));
		WithArgs actATM = getAgent().getSpecies().getAction(ADD_TO_MAP);
			
		actATM.setRuntimeArgs(scope, args);
		actATM.executeOn(scope);
		
		return map;
	}
	
	@action (
			name = "send_init_data",
					args = { 
							 @arg (name = "id",
								type = IType.STRING,
								doc = @doc ("if of the player to send the geometries to"))},
			doc = { @doc (
					value = "Wait for the connection of a unity client and send the paramters to the client")})
	public void primSentInitData(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		IAgent player = getPlayers(ag).get(scope.getStringArg("id")) ;
	
		doAction1Arg(scope, "send_parameters", "player", player );

		doAction1Arg(scope, "send_unity_propetries", "player", player );
		doAction1Arg(scope, "send_player_position", "player", player );
		
		doAction3Arg(scope, "send_geometries", "player", player, "geoms" ,getBackgroundGeometries(ag), "update_position", true);
		
		doActionNoArg(scope, "send_world" );
		
		
		startSimulation(scope);
	}
	
	@action (
			name = "after_sending_world",
			args = {@arg (
					name = "map_to_send",
					type = IType.MAP,
					doc = @doc ("data already sent to the client"))},
			doc = { @doc (
					value = "Action trigger just after sending the world to Unity ")})
	public void primAfterSendingWorld(final IScope scope) throws GamaRuntimeException {
	
	}
	
	@action (
			name = "after_sending_geometries",
					args = { @arg (
							name = "player",
							type = IType.AGENT,
							doc = @doc ("Player to which the message will be sent"))},
					
			doc = { @doc (
					value = "Action trigger just after sending the background geometries to a Unity client ")})
	public void primAfterSendingGeometries(final IScope scope) throws GamaRuntimeException {
	
	}
	
	@action (
			name = "create_init_player",
					args = { @arg (
							name = "id",
							type = IType.STRING,
							doc = @doc ("name of the player agent"))},
							
				
			doc = { @doc (
					value = "Create and init a new unity player agent")})
	public void primCreateInitPlayer(final IScope scope) throws GamaRuntimeException {
		setUseMiddleware(getAgent(), false);
		String id = scope.getStringArg("id");
		doAction1Arg(scope, "create_player", "id", id);
		doAction1Arg(scope, "send_init_data", "id", id);
		
	}
	
	@action (
			name = "create_player",
					args = { @arg (
							name = "id",
							type = IType.STRING,
							doc = @doc ("name of the player agent"))},
							
				
			doc = { @doc (
					value = "Create a new unity player agent")})
	public void primInitPlayer(final IScope scope) throws GamaRuntimeException {
		IMap<String, IAgent> players = getPlayers(getAgent());
		IAgent ag = getAgent();
		String id = scope.getStringArg("id");
		
		ISpecies sp = Cast.asSpecies(scope, getPlayerSpecies(ag));
		if (sp == null) return;
		if (getMaxPlayer(ag) >= 0 && (getPlayers(ag).length(scope) >= getMaxPlayer(ag))) return;
		if (getPlayers(ag).containsKey(id) && getPlayers(ag).get(id) != null){
			return;
		}
		
		Map<String, Object>  init = GamaMapFactory.create();
		if (getPlayerLocationInit(ag).size() <= players.length(scope)) {
			getPlayerLocationInit(ag).add(Punctal.any_location_in(scope, scope.getSimulation()));
		}
		init.put(IKeyword.LOCATION, getPlayerLocationInit(ag).get(players.length(scope)));
		init.put(IKeyword.NAME, id);
		
		IAgent player = sp.getPopulation(scope).createAgentAt(scope, 0, init, false, true);
		getPlayers(getAgent()).put(id, player);
	}
	
	@action (
			name = "filter_distance",
			args = { @arg (
					name = "geometries",
					type = IType.LIST,
					doc = @doc ("list of geometries to filter")),
					 @arg (name = "player",
								type = IType.AGENT,
								doc = @doc ("the player agent"))},
					
			doc = { @doc (
					value = "Action called by the send_world action that returns the sub-list of geometries to send to Unity from a given list of geometries according to a max distance to the player")})
	public IList<IShape> primFilterDistance(final IScope scope) throws GamaRuntimeException {
		IAgent player= (IAgent) scope.getArg("player");
		IList<IShape> geoms = GamaListFactory.create();
		geoms.addAll((IList<IShape>) scope.getArg("geometries"));
		
		Double dist = (Double) player.getAttribute(AbstractUnityPlayer.PLAYER_AGENTS_PERCEPTION_RADIUS);
		return (IList<IShape>) Spatial.Queries.overlapping(scope, geoms, Transformations.enlarged_by(scope, player, dist));
	}
	 
	
	
	@action (
			name = "filter_overlapping",
			args = { @arg (
					name = "geometries",
					type = IType.LIST,
					doc = @doc ("list of geometries to filter")),
			 @arg (name = "player",
				type = IType.AGENT,
				doc = @doc ("the player agent"))},
					
			doc = { @doc (
					value = "Action called by the send_world action that returns the sub-list of geometries to send to Unity from a given list of geometries according to a min proximity to the other geometries to send")})
	public IList<IShape> primFilterOverlapping(final IScope scope) throws GamaRuntimeException {
		IAgent player= (IAgent) scope.getArg("player");
		IList<IShape> geoms = GamaListFactory.create();
		geoms.addAll((IList<IShape>) scope.getArg("geometries"));
		
		IList<IShape> toRemove = GamaListFactory.create() ;
		for (IShape g : geoms) {
			if (!toRemove.contains(g)) { 
				Double dist = (Double) player.getAttribute(AbstractUnityPlayer.PLAYER_AGENTS_MIN_DIST);
				toRemove.addAll((Collection<? extends IShape>) Spatial.Queries.overlapping(scope, geoms, Transformations.enlarged_by(scope, player, dist)));
			}  
		}
		geoms.removeAll(toRemove);
		return geoms;
	}
	
	
	@action (
			name = "new_player_location",
			args = { @arg (
					name = "loc",
					type = IType.POINT,
					doc = @doc ("Location of the player agent")),
					
					@arg (
							name = "player",
							type = IType.AGENT,
							doc = @doc ("the player agent"))},
					
			doc = { @doc (
					value = "Action called by the move_player action that returns the location to send to Unity from a given player location")})
	public GamaPoint primNewPlayerLoc(final IScope scope) throws GamaRuntimeException {
		return (GamaPoint) scope.getArg("loc");
	}
	
	@action (
			name = "move_player",
					args = { 
						@arg (name = "player",
						type = IType.AGENT,
						doc = @doc ("the player agent to move")),
						
						@arg (
								name = "loc",
								type = IType.POINT,
								doc = @doc ("Location of the player agent"))},
			doc = { @doc (
					value = "move the player agent")})
	public void primMovePlayer(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		IAgent player = (IAgent) scope.getArg("player");
		GamaPoint loc = (GamaPoint) scope.getArg("loc");
		getReadyToMovePlayers(ag).remove(player);
		loc = (GamaPoint)doAction2Arg(scope, "new_player_location", "player", player, "loc", loc);
		player.setLocation(loc);
		doAction1Arg(scope, "send_player_position", "player", player);
		
	}
	
	
	@action (
			name = "move_player_external",
					args = { 
						@arg (name = "id",
						type = IType.STRING,
						doc = @doc ("id of the player agent to move")),
						
						@arg (
								name = "x",
								type = IType.INT,
								doc = @doc ("x Location of the player agent")),	@arg (
										name = "y",
										type = IType.INT,
										doc = @doc ("y Location of the player agent")),	@arg (
												name = "z",
												type = IType.INT,
												doc = @doc ("z Location of the player agent")),@arg (
												name = "angle",
												type = IType.INT,
												doc = @doc ("angle of the player agent"))},
			doc = { @doc (
					value = "move the player agent ")})
	public void primMovePlayerFromUnity(final IScope scope) throws GamaRuntimeException {
	
		IAgent ag = getAgent();
		IAgent thePlayer = getPlayers(ag).get(scope.getStringArg("id")) ;
		if (thePlayer == null ) return;
		Integer x = scope.getIntArg("x");
		Integer y = scope.getIntArg("y");
		Integer z = scope.getIntArg("z");
		Integer angle = scope.getIntArg("angle");
		int precision = getPrecision(ag); 
		Double rot = ((Double) thePlayer.getAttribute("player_rotation"));

		//if ( !getReadyToMovePlayers(ag).isEmpty()) System.out.println("getReadyToMovePlayers(ag): "+ getReadyToMovePlayers(ag));
		if (getReadyToMovePlayers(ag).contains(thePlayer)) {
			if (rot != null)
				thePlayer.setAttribute("rotation", angle.floatValue()/precision + rot ); 
			if (x !=null && y != null) 
				thePlayer.setLocation(new GamaPoint(x.floatValue()/precision, y.floatValue()/precision, z.floatValue()/precision));
			thePlayer.setAttribute("to_display", true);
		} 
		
	}
	
	@action (
			name = "ping_GAMA",
					args = { @arg (
							name = "id",
							type = IType.STRING,
							doc = @doc ("Player agent that try to ping GAMA"))},
			doc = { @doc (
					value = "Ping GAMA to test the connection")})
	public void primPingGAMA(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		IAgent thePlayer = getPlayers(ag).get(scope.getStringArg("id")) ;
		if (thePlayer == null ) return;
		PlatformAgent pa = GAMA.getPlatformAgent();
		pa.sendMessage(scope,ConstantExpressionDescription.create("pong"));		
	}
	

	@action (
			name = "player_position_updated",
					args = { @arg (
							name = "id",
							type = IType.STRING,
							doc = @doc ("Player agent of which the position has been updated"))},
			doc = { @doc (
					value = "reactivate the reception of player position")})
	public void primPlayerPositionUpdated(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		IAgent thePlayer = getPlayers(ag).get(scope.getStringArg("id")) ;
		if (thePlayer == null ) return;
		getNewPlayerPosition(ag).put(thePlayer.getName(), GamaListFactory.create()); 
		if (!getReadyToMovePlayers(ag).contains(thePlayer.getName()))
			getReadyToMovePlayers(ag).add(thePlayer);
		
		
	}
	
	
	
	
	@action (
			name = "send_player_position",
					args = { @arg (
							name = "player",
							type = IType.AGENT,
							doc = @doc ("Player agent of which to send the position"))},
			doc = { @doc (
					value = "send the new position of the player to Unity (used to teleport the player from GAMA) ")})
	public void primSendPlayerPosition(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		if (!getConnectToUnity(ag)) {
			return; 
		}
		IAgent player = (IAgent) scope.getArg("player");
		int precision = getPrecision(ag);
		IList<Integer> pos = GamaListFactory.create();
		pos.add((int)(player.getLocation().x * precision));
		pos.add((int) (player.getLocation().y * precision));
		pos.add((int) (player.getLocation().z * precision));
		getNewPlayerPosition(ag).put(player.getName(), pos); 
	}
	
	
	private void startSimulation(IScope scope) {
		if (getPlayers(getAgent()).size() >= getMinPlayer(getAgent())) {
			scope.getSimulation().resume(scope);
		} 
	}
	
	
	@action (
			name = "add_to_send_parameter",
					args = {@arg (
							name = "map_to_send",
							type = IType.MAP,
							doc = @doc ("data already sent to the client"))},
			doc = { @doc (
					value = "add values to the parameters sent to the Unity Client")})
	public void primAddToSentParameter(final IScope scope) throws GamaRuntimeException {
		
	}
	
	@action (
			name = "send_parameters",
					args = { @arg (
							name = "player",
							type = IType.AGENT,
							doc = @doc ("Player to which the message will be sent"))},
								
			doc = { @doc (
					value = "Send the parameter to Unity to intialized the connection")})
	public void primSendParameters(final IScope scope) throws GamaRuntimeException {
		GamaMap<String, Object> toSend = (GamaMap<String, Object>) GamaMapFactory.create();
		IAgent ag = getAgent();
		IAgent player = (IAgent) scope.getArg("player");
		int precision = getPrecision(ag);
		toSend.put(PRECISION, precision);
		IList<Integer> worldT = GamaListFactory.create(Types.INT);
		worldT.add((int)(scope.getSimulation().getGeometricEnvelope().getWidth() * precision));
		worldT.add((int)(scope.getSimulation().getGeometricEnvelope().getHeight() * precision));
		worldT.add((int)(scope.getSimulation().getGeometricEnvelope().getHeight() * precision));

		toSend.put("world", worldT);
		
		doAction1Arg(scope, "add_to_send_parameter", "map_to_send", toSend );
		addToCurrentMessage(scope, buildPlayerListfor1Player(scope, player), toSend);
	}
	
	
	@action (
			name = "send_unity_propetries",
					args = { @arg (
							name = "player",
							type = IType.AGENT,
							doc = @doc ("Player to which the message will be sent"))},
								
			doc = { @doc (
					value = "Send the Unity properties to intialize the possible properties of geometries")})
	public void primSendUnityProperties(final IScope scope) throws GamaRuntimeException {
		GamaMap<String, Object> toSend = (GamaMap<String, Object>) GamaMapFactory.create();
		IAgent ag = getAgent();
		IAgent player = (IAgent) scope.getArg("player");
		List<UnityProperties> props = getUnityProperties(ag);
		List<Map> propMap = new ArrayList<>();
		for(UnityProperties p : props) {
			propMap.add(p.toMap());
		}
 		toSend.put("properties", propMap);
		addToCurrentMessage(scope, buildPlayerListfor1Player(scope, player), toSend);
	}
	
	private IList buildPlayerListfor1Player(IScope scope, IAgent player) {
		IList players = GamaListFactory.create();
		players.add(player.getName());
		return players; 
	}
	
	
	
	
	@action (
			name = LOC_TO_SEND,
			doc = @doc (
					returns = "the location to send to Unity"))
	public GamaPoint primLocToSend(final IScope scope) throws GamaRuntimeException {
		return scope.getAgent().getLocation();

	}
	
	@action (
			name = ADD_TO_MAP,
					args = { @arg (
							name = "map",
							type = IType.MAP,
							doc = @doc ("map of data to send to Unity")),
							@arg (
									name = "geom",
									type = IType.GEOMETRY,
									doc = @doc ("Geometry to send to Unity"))},
			doc = @doc (
					returns = "other elements than the location to add to the data sent to Unity"))
	public void primAddToMap(final IScope scope) throws GamaRuntimeException {
		
	}
	public Integer getIndexSpecies(IAgent agent) {
		return (Integer) agent.getAttribute(SPECIES_INDEX);
	}
	
	public Double getHeading(IAgent agent) {
		if (agent.hasAttribute(HEADING))
			return (Double) agent.getAttribute(HEADING);
		return 0.0;
	}
	

}
