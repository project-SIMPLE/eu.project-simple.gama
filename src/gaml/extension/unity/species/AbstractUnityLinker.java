package gaml.extension.unity.species;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.extensions.messaging.GamaMessage;
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
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Containers;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Transformations;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement.WithArgs;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.network.skills.INetworkSkill;

@species(name = "abstract_unity_linker", skills={"network"})
@vars({ 
	@variable(name = AbstractUnityLinker.CONNECT_TO_UNITY, type = IType.BOOL, init = "true",
			doc = { @doc ("Activate the unity connection; if activated, the model will wait for an connection from Unity to start")}), 
	@variable(name = AbstractUnityLinker.UNITY_CLIENT, type = IType.NONE,
	doc = { @doc ("Client for Unity")}), 
	@variable(name =AbstractUnityLinker.PORT, type = IType.INT, init="8000",
			doc = { @doc ("Connection port")}), 
	@variable(name = AbstractUnityLinker.END_MESSAGE_SYMBOL, type = IType.STRING, init = "&&&", 
			doc = { @doc ("Symbol used to end a message sent to Unity")}), 
	@variable(name = AbstractUnityLinker.PRECISION, type = IType.INT, init = "10000", 
			doc = { @doc ("Number of decimal for the data (location, rotation)")}), 
	@variable(name = AbstractUnityLinker.DELAY_AFTER_MES, type = IType.FLOAT, init = "0.0", 
			doc = { @doc ("Delay after moving the player (in ms)")}), 
	@variable(name = AbstractUnityLinker.WAITING_MESSAGE, type = IType.STRING, 
	doc = { @doc ("Which message GAMA should wait before receiving infomation ")}), 

	@variable(name = AbstractUnityLinker.AGENTS_TO_SEND, type = IType.LIST, of =  IType.AGENT, 
			doc = { @doc ("List of agents to sent to Unity. It could be updated each simulation step")}),  
	
	@variable(name = AbstractUnityLinker.BACKGROUND_GEOMS, type = IType.LIST, of =  IType.GEOMETRY, 
			doc = { @doc ("List of static geometries sent to Unity. Only sent once at the initialization of the connection")}), 

	@variable(name = AbstractUnityLinker.BACKGROUND_GEOMS_HEIGHTS, type = IType.LIST, of =  IType.INT, 
			doc = { @doc ("For each geometry sent to Unity, the height of this one.")}),  

	@variable(name = AbstractUnityLinker.BACKGROUND_GEOMS_COLLIDERS, type = IType.LIST, of =  IType.BOOL, 
			doc = { @doc ("For each geometry sent to Unity, does this one has a collider (i.e. a physical existence) ? ")}),  

	@variable(name = AbstractUnityLinker.BACKGROUND_GEOMS_NAMES, type = IType.LIST, of =  IType.STRING, 
			doc = { @doc ("For each geometry sent to Unity, its name in unity ")}), 
	
	@variable(name = AbstractUnityLinker.DO_SEND_WORLD, type = IType.BOOL, init="true", 
			doc = { @doc ("Has the agents has to be sent to unity?")}),  
	
	@variable(name = AbstractUnityLinker.INITIALIZED, type = IType.BOOL, init="false", 
	doc = { @doc ("Has the world being initialized yet?")}),  

	@variable(name = AbstractUnityLinker.PLAYER_SPECIES, type = IType.STRING, 
	doc = { @doc ("Species of the player agent")}),  
	
	@variable(name = AbstractUnityLinker.RECEIVE_INFORMATION, type = IType.BOOL, init="true", 
	doc = { @doc ("should GAMA receive information from Unity?")}),  

	@variable(name = AbstractUnityLinker.MOVE_PLAYER_EVENT, type = IType.BOOL, init="false", 
			doc = { @doc ("Does the player agent moved from GAMA?")}),
	@variable(name = AbstractUnityLinker.CREATE_PLAYER, type = IType.BOOL, init="true", 
	doc = { @doc ("Has a player agent to be created?")}),

	@variable(name = AbstractUnityLinker.MOVE_PLAYER_FROM_UNITY, type = IType.BOOL, init="true", 
			doc = { @doc ("Has the player to move in GAMA as it moves in Unity?")}), 
	
	@variable(name = AbstractUnityLinker.USE_PHYSICS_FOR_PLAYERS, type = IType.BOOL, init="true", 
			doc = { @doc ("Does the player should has a physical exitence in Unity (i.e. cannot pass through specific geometries)?")}),  
	
	@variable(name = AbstractUnityLinker.NEW_PLAYER_POSITION, type = IType.LIST, of = IType.INT, 
			doc = { @doc ("The new poistion of the player to be sent to Unity - list of int [x,y]")}), 
	@variable(name = AbstractUnityLinker.LOCATION_INIT, type = IType.POINT, init="{50.0, 50.0}", 
		doc = { @doc ("Init location of the player in the environment - this information will be sent to Unity to move the player accordingly")}), 

	@variable(name = AbstractUnityLinker.THE_PLAYER, type = IType.AGENT, 
	doc = { @doc ("Player agent")}), 
})
public class AbstractUnityLinker extends GamlAgent {
	public static final String PLAYER_SPECIES = "player_species";
	
	public static final String CONNECT_TO_UNITY = "connect_to_unity";
	public static final String PORT = "port";
	public static final String END_MESSAGE_SYMBOL = "end_message_symbol";
	public static final String PRECISION = "precision";
	public static final String DELAY_AFTER_MES = "delay_after_mes";
	public static final String AGENTS_TO_SEND = "agents_to_send";
	public static final String BACKGROUND_GEOMS = "background_geoms";
	public static final String BACKGROUND_GEOMS_HEIGHTS = "background_geoms_heights";
	public static final String BACKGROUND_GEOMS_COLLIDERS = "background_geoms_colliders";
	public static final String BACKGROUND_GEOMS_NAMES = "background_geoms_names";
	public static final String DO_SEND_WORLD = "do_send_world";
	public static final String CREATE_PLAYER = "create_player";

	public static final String MOVE_PLAYER_EVENT = "move_player_event";

	public static final String MOVE_PLAYER_FROM_UNITY = "move_player_from_unity";
	public static final String USE_PHYSICS_FOR_PLAYERS = "use_physics_for_player";
	public static final String LOCATION_INIT = "location_init";
	public static final String THE_PLAYER = "player_agent";

	public static final String NEW_PLAYER_POSITION = "new_player_position";
	public static final String UNITY_CLIENT = "unity_client";

	public static final String INITIALIZED = "initialized";

	public static final String RECEIVE_INFORMATION = "receive_information";

	public static final String WAITING_MESSAGE = "waiting_message";
	
		
	@getter (AbstractUnityLinker.UNITY_CLIENT)
	public static Object getUnityClient(final IAgent agent) {
		return (Object) agent.getAttribute(UNITY_CLIENT);
	}
	@setter(AbstractUnityLinker.UNITY_CLIENT)
	public static void setUnityClient(final IAgent agent, final Object ctu) {
		agent.setAttribute(UNITY_CLIENT, ctu);
	}
		
	@getter (AbstractUnityLinker.CONNECT_TO_UNITY)
	public static Boolean getConnectToUnity(final IAgent agent) {
		return (Boolean) agent.getAttribute(CONNECT_TO_UNITY);
	}
	@setter(AbstractUnityLinker.CONNECT_TO_UNITY)
	public static void setConnectToUnity(final IAgent agent, final Boolean ctu) {
		agent.setAttribute(CONNECT_TO_UNITY, ctu);
	}
		
	@getter (AbstractUnityLinker.PORT)
	public static Integer getPort(final IAgent agent) {
		return (Integer) agent.getAttribute(PORT);
	}
	@setter(AbstractUnityLinker.PORT)
	public static void setPort(final IAgent agent, final Integer val) {
		agent.setAttribute(PORT, val);
	}
	
	@getter (AbstractUnityLinker.END_MESSAGE_SYMBOL)
	public static String getEndMessageSymbol(final IAgent agent) {
		return (String) agent.getAttribute(END_MESSAGE_SYMBOL);
	}
	@setter(AbstractUnityLinker.END_MESSAGE_SYMBOL)
	public static void setEndMessageSymbol(final IAgent agent, final String val) {
		agent.setAttribute(END_MESSAGE_SYMBOL, val);
	}
	
	@getter (AbstractUnityLinker.PRECISION)
	public static Integer getPrecision(final IAgent agent) {
		return (Integer) agent.getAttribute(PRECISION);
	}
	@setter(AbstractUnityLinker.PRECISION)
	public static void setPrecision(final IAgent agent, final Integer val) {
		agent.setAttribute(PRECISION, val);
	}
	
	@getter (AbstractUnityLinker.DELAY_AFTER_MES)
	public static Double getDelayAfterMes(final IAgent agent) {
		return (Double) agent.getAttribute(DELAY_AFTER_MES);
	}
	@setter(AbstractUnityLinker.DELAY_AFTER_MES)
	public static void setDelayAfterMes(final IAgent agent, final Double val) {
		agent.setAttribute(DELAY_AFTER_MES, val);
	}
	
	@getter (AbstractUnityLinker.WAITING_MESSAGE)
	public static String getWaitingMessage(final IAgent agent) {
		return (String) agent.getAttribute(WAITING_MESSAGE);
	}
	@setter(AbstractUnityLinker.WAITING_MESSAGE)
	public static void setWaitingMessage(final IAgent agent, final String val) {
		agent.setAttribute(WAITING_MESSAGE, val);
	}
	
	@getter (AbstractUnityLinker.AGENTS_TO_SEND)
	public static  IList<IAgent> getAgentsToSend(final IAgent agent) {
		return ( IList<IAgent>) agent.getAttribute(AGENTS_TO_SEND);
	}
	@setter(AbstractUnityLinker.AGENTS_TO_SEND)
	public static void setAgentsToSend(final IAgent agent, final IList<IAgent> val) {
		agent.setAttribute(AGENTS_TO_SEND, val);
	}
		
	@getter (AbstractUnityLinker.BACKGROUND_GEOMS)
	public static  IList<IShape> getBackgroundGeoms(final IAgent agent) {
		return ( IList<IShape>) agent.getAttribute(BACKGROUND_GEOMS);
	}
	@setter(AbstractUnityLinker.BACKGROUND_GEOMS)
	public static void setBackgroundGeoms(final IAgent agent, final IList<IShape> val) {
		agent.setAttribute(BACKGROUND_GEOMS, val);
	}
	
	@getter (AbstractUnityLinker.BACKGROUND_GEOMS_HEIGHTS)
	public static  IList<Double> getBackgroundGeomsHeights(final IAgent agent) {
		return ( IList<Double>) agent.getAttribute(BACKGROUND_GEOMS_HEIGHTS);
	}
	@setter(AbstractUnityLinker.BACKGROUND_GEOMS_HEIGHTS)
	public static void setBackgroundGeomsHeights(final IAgent agent, final IList<Double> val) {
		agent.setAttribute(BACKGROUND_GEOMS_HEIGHTS, val);
	}
		
	@getter (AbstractUnityLinker.BACKGROUND_GEOMS_COLLIDERS)
	public static  IList<Boolean> getBackgroundGeomsColliders(final IAgent agent) {
		return ( IList<Boolean>) agent.getAttribute(BACKGROUND_GEOMS_COLLIDERS);
	}
	@setter(AbstractUnityLinker.BACKGROUND_GEOMS_COLLIDERS)
	public static void setBackgroundGeomsColliders(final IAgent agent, final IList<Boolean> val) {
		agent.setAttribute(BACKGROUND_GEOMS_COLLIDERS, val);
	}
		
	@getter (AbstractUnityLinker.BACKGROUND_GEOMS_NAMES)
	public static  IList<String> getBackgroundGeomsNames(final IAgent agent) {
		return ( IList<String>) agent.getAttribute(BACKGROUND_GEOMS_NAMES);
	}
	@setter(AbstractUnityLinker.BACKGROUND_GEOMS_NAMES)
	public static void setBackgroundGeomsNames(final IAgent agent, final IList<String> val) {
		agent.setAttribute(BACKGROUND_GEOMS_NAMES, val);
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
	
	@getter (AbstractUnityLinker.CREATE_PLAYER)
	public static Boolean getCreatePlayer(final IAgent agent) {
		return (Boolean) agent.getAttribute(CREATE_PLAYER);
	}
	@setter(AbstractUnityLinker.CREATE_PLAYER)
	public static void setCreatePlayer(final IAgent agent, final Boolean val) {
		agent.setAttribute(CREATE_PLAYER, val);
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
	
	@getter (AbstractUnityLinker.USE_PHYSICS_FOR_PLAYERS)
	public static Boolean getUsePhysicsForPlayer(final IAgent agent) {
		return (Boolean) agent.getAttribute(USE_PHYSICS_FOR_PLAYERS);
	}
	@setter(AbstractUnityLinker.USE_PHYSICS_FOR_PLAYERS)
	public static void setUsePhysicsForPlayer(final IAgent agent, final Boolean val) {
		agent.setAttribute(USE_PHYSICS_FOR_PLAYERS, val);
	}
	
	
	@getter (AbstractUnityLinker.LOCATION_INIT)
	public static GamaPoint getPlayerLocationInit(final IAgent agent) {
		return (GamaPoint) agent.getAttribute(LOCATION_INIT);
	}
	@setter(AbstractUnityLinker.LOCATION_INIT)
	public static void setPlayerLocationInit(final IAgent agent, final GamaPoint val) {
		agent.setAttribute(LOCATION_INIT, val);
	}
	
	@getter (AbstractUnityLinker.THE_PLAYER)
	public static IAgent getPlayer(final IAgent agent) {
		return (IAgent) agent.getAttribute(THE_PLAYER);
	}
	@setter(AbstractUnityLinker.THE_PLAYER)
	public static void setPlayer(final IAgent agent, final IAgent val) {
		agent.setAttribute(THE_PLAYER, val);
	}
		
	
	@getter (AbstractUnityLinker.NEW_PLAYER_POSITION)
	public static  IList<Integer> getNewPlayerPosition(final IAgent agent) {
		return ( IList<Integer>) agent.getAttribute(NEW_PLAYER_POSITION);
	}
	@setter(AbstractUnityLinker.NEW_PLAYER_POSITION)
	public static void setNewPlayerPosition(final IAgent agent, final IList<Integer> val) {
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
	

	@Override
	public boolean doStep(final IScope scope) {
		if (super.doStep(scope)) {
			IAgent ag = getAgent();
			if (getConnectToUnity(ag)) {
				if (getMovePlayerEvent(ag) && getPlayer(ag) != null) {
					doActionNoArg(scope, "move_player");
					setMovePlayerEvent(ag, false);
				}
				if(!getInitialized(ag)) {
					if (getCreatePlayer(ag)) {
						doActionNoArg(scope, "init_player");
					}
					doActionNoArg(scope, "send_init_data");
					setInitialized(ag, true);
				}
				if (getDoSendWorld(ag)) {
					doActionNoArg(scope, "send_world");
				}
				doActionNoArg(scope, "manage_message_from_unity");
			}
			return true;
		}
		return false;
	}

	
	
	private void addBackgroundGeometries(IList geoms, IList names, Double height, Boolean collider) {
		IList<IShape> backgroundGeometries = getBackgroundGeoms(getAgent());
		IList<Double> backgroundGeometriesHeight = getBackgroundGeomsHeights(getAgent());
		IList<String> backgroundGeometriesName = getBackgroundGeomsNames(getAgent());
		IList<Boolean> backgroundGeometriesCollider = getBackgroundGeomsColliders(getAgent());
	
		
		if(backgroundGeometries == null) {
			backgroundGeometries = GamaListFactory.create(Types.GEOMETRY);
			backgroundGeometriesHeight = GamaListFactory.create(Types.FLOAT);
			backgroundGeometriesName = GamaListFactory.create(Types.STRING);
			backgroundGeometriesCollider = GamaListFactory.create(Types.BOOL);
		}
		backgroundGeometries.addAll(geoms);
		
		for (int i = 0; i < geoms.size(); i++) {
			backgroundGeometriesHeight.add(height);
			backgroundGeometriesHeight.add(height);
			backgroundGeometriesCollider.add(collider);
		}
		if (names != null) 
			backgroundGeometriesName.addAll(names);
		setBackgroundGeoms(getAgent(), backgroundGeometries);
		setBackgroundGeomsHeights(getAgent(), backgroundGeometriesHeight);
		setBackgroundGeomsColliders(getAgent(), backgroundGeometriesCollider);
		setBackgroundGeomsNames(getAgent(), backgroundGeometriesName);
	
	}
	
	@action (
			name = "add_JSON",
			args = { @arg (
							name = "name",
							type = IType.STRING, 
							doc = @doc ("Name of the variable")),
					@arg (
							name = "value",
							type = IType.STRING, 
							doc = @doc ("Value of the variable"))},
					
			doc = { @doc (
					value = "Wait for the connection of a unity client and send the paramters to the client")})
	public void primAddJSON(final IScope scope) throws GamaRuntimeException {
		WithArgs actConnect = scope.getAgent().getSpecies().getAction(INetworkSkill.CONNECT_TOPIC);
		Arguments args = new Arguments();
		args.put("protocol", ConstantExpressionDescription.create("tcp_server"));
		args.put("port", ConstantExpressionDescription.create(getAgent()));
	}
	
	@action (
			name = "send_world",
			doc = { @doc (
					value = "send the current state of the world to the Unity Client")})
	public void primSentWorld(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		IMap<String, Object> toSend = GamaMapFactory.create();
		
		IList<IAgent> ags = GamaListFactory.create();
		ags.addAll(getAgentsToSend(ag).stream().filter(a -> !a.dead()).toList());
		IAgent player = (IAgent) getPlayer(ag);
		if (player != null) {
			if (((Double) player.getAttribute(AbstractUnityPlayer.PLAYER_AGENTS_PERCEPTION_RADIUS)) > 0) {
				ags = (IList<IAgent>) doAction1Arg(scope, "filter_distance", "ags", ags);
			}
			if (((Double) player.getAttribute(AbstractUnityPlayer.PLAYER_AGENTS_MIN_DIST)) > 0) {
				ags = (IList<IAgent>) doAction1Arg(scope, "filter_overlapping", "ags", ags);
			} 
		}

		IList<IMap> messageAgs = (IList<IMap>) doAction1Arg(scope, "message_agents", "ags", ags);
		toSend.put("agents", messageAgs);
		toSend.put("position", getNewPlayerPosition(ag));
		setNewPlayerPosition(ag, GamaListFactory.create());
		sendMessage(scope, toSend);
		doActionNoArg(scope, "after_sending_world");	
	}
		
	@action (
			name = "send_geometries",
					args = { @arg (
							name = "geoms",
							type = IType.LIST,
							doc = @doc ("List of geometries to send")),
							 @arg (
							name = "heights",
							type = IType.LIST,
							doc = @doc ("List of heights (float) associated to each geometry")),
							 @arg (
										name = "geometry_colliders",
										type = IType.LIST,
										doc = @doc ("For each geometry, does a collider has to be instanciated (list of bools) ")),
							 @arg (
										name = "names",
										type = IType.LIST,
										doc = @doc ("List of name (string) associated to each geometry"))},
			doc = { @doc (
					value = "send the background geometries to the Unity client")})
	public void primSentGeometries(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		IMap<String, Object> toSend = GamaMapFactory.create();
		IList<Object> points = GamaListFactory.create();
		int precision = getPrecision(ag);
		
		IList<IShape> geoms = scope.getListArg("geoms");
		IList<Double> heights = scope.getListArg("heights");
		IList<Boolean> geometry_colliders = scope.getListArg("geometry_colliders");
		IList<String> names = scope.getListArg("names");
		
		for (IShape g : geoms ) {
			for (GamaPoint pt : g.getPoints()) {
				IMap<String, Object> ptM = GamaMapFactory.create();
				IList<Integer> ptL = GamaListFactory.create(Types.INT);
				ptL.add((int)(pt.x*precision));
				ptL.add((int)(pt.y*precision));
				ptM.put("c", ptL);
				points.add(ptM);
			}
			IMap<String, Object> ptM = GamaMapFactory.create();
			ptM.put("c", GamaListFactory.create());
			points.add(ptM);
		}
		toSend.put("points", points);
		toSend.put("heights", heights);
		toSend.put("hasColliders", geometry_colliders);
		toSend.put("names", names);
		
		if (getUnityClient(ag) == null) {
			throw GamaRuntimeException.error ("no client to send to", scope);
		} else {
			sendMessage(scope, toSend);
		}
		doActionNoArg(scope, "after_sending_geometries");	
		
	}
	
	private void sendMessage(IScope scope,IMap toSend) {
		IAgent ag = getAgent();
		Arguments argsS = new Arguments();
		argsS.put("to", ConstantExpressionDescription.create(getUnityClient(ag)));
		String mes = Containers.asJsonString(scope, toSend) + getEndMessageSymbol(ag);
		argsS.put("contents", ConstantExpressionDescription.create(mes));
		
		WithArgs actS = getAgent().getSpecies().getAction("send");
		actS.setRuntimeArgs(scope, argsS);
		actS.executeOn(scope);
	}
	
	@action (
			name = "send_init_data",
			doc = { @doc (
					value = "Wait for the connection of a unity client and send the paramters to the client")})
	public void primSentInitData(final IScope scope) throws GamaRuntimeException {
		WithArgs actConnect = getAgent().getSpecies().getAction(INetworkSkill.CONNECT_TOPIC);
		
		Arguments args = new Arguments();
		args.put("protocol", ConstantExpressionDescription.create("tcp_server"));
		args.put("port", ConstantExpressionDescription.create(getPort(getAgent())));
		args.put("raw", ConstantExpressionDescription.create(true));
		args.put("with_name", ConstantExpressionDescription.create("server" ));
		args.put("force_network_use", ConstantExpressionDescription.create(true));
		actConnect.setRuntimeArgs(scope, args);
		
		if (!(Boolean) actConnect.executeOn(scope)) return ;
		
		WithArgs actHMM = scope.getAgent().getSpecies().getAction( INetworkSkill.HAS_MORE_MESSAGE_IN_BOX);
		
		WithArgs actFM = scope.getAgent().getSpecies().getAction( INetworkSkill.FETCH_MESSAGE);
		
		WithArgs actFMFN = scope.getAgent().getSpecies().getAction( INetworkSkill.FETCH_MESSAGE_FROM_NETWORK);
		
		
		while(! (Boolean) actHMM.executeOn(scope)) {
			actFMFN.executeOn(scope);	
		}
		
		while( (Boolean) actHMM.executeOn(scope)) {
			GamaMessage mes = (GamaMessage) actFM.executeOn(scope);
			setUnityClient(getAgent(), mes.getSender());
		}
		

		doActionNoArg(scope, "send_parameters" );
		
		if (getDelayAfterMes(getAgent()) > 0.0) {
			doAction1Arg(scope, "wait_for_message", "message", "ready" );
		}
		
		while( (Boolean) actHMM.executeOn(scope)) {
			actFMFN.executeOn(scope);
		}
		
		while( (Boolean) actHMM.executeOn(scope)) {
			actFM.executeOn(scope);
		}
		
		if (!getBackgroundGeoms(getAgent()).isEmpty()) {
			WithArgs actSG = scope.getAgent().getSpecies().getAction( "send_geometries");
			Arguments argsSG = new Arguments();
			argsSG.put("geoms", ConstantExpressionDescription.create(getBackgroundGeoms(getAgent())));
			argsSG.put("heights", ConstantExpressionDescription.create(getBackgroundGeomsHeights(getAgent())));
			argsSG.put("geometry_colliders", ConstantExpressionDescription.create(getBackgroundGeomsColliders(getAgent())));

			argsSG.put("names", ConstantExpressionDescription.create(getBackgroundGeomsNames(getAgent())));
			actSG.setRuntimeArgs(scope, argsSG);
			actSG.executeOn(scope);
			
			
		}
		if (getDoSendWorld(getAgent())) {
			 doActionNoArg(scope, "send_world" );
		}
	}
	
	@action (
			name = "after_sending_world",
			
			doc = { @doc (
					value = "Action trigger just after sending the world to Unity ")})
	public void primAfterSendingWorld(final IScope scope) throws GamaRuntimeException {
	
	}
	
	@action (
			name = "after_sending_geometries",
			
			doc = { @doc (
					value = "Action trigger just after sending the background geometries to Unity ")})
	public void primAfterSendingGeometries(final IScope scope) throws GamaRuntimeException {
	
	}
	
	@action (
			name = "init_player",
			
			doc = { @doc (
					value = "Action trigger just after sending the background geometries to Unity ")})
	public void primInitPlayer(final IScope scope) throws GamaRuntimeException {
		ISpecies sp = Cast.asSpecies(scope, getPlayerSpecies(getAgent()));
		if (sp == null) return;
		Map<String, Object>  init = GamaMapFactory.create();
		init.put(IKeyword.LOCATION, getPlayerLocationInit(getAgent()));
		IAgent player = sp.getPopulation(scope).createAgentAt(scope, 0, init, false, true);
		setPlayer(getAgent(), player);
	}
	
	@action (
			name = "filter_distance",
			args = { @arg (
					name = "ags",
					type = IType.LIST,
					doc = @doc ("list of agents to filter"))},
					
			doc = { @doc (
					value = "Action called by the send_world action that returns the sub-list of agents to send to Unity from a given list of agents according to a max distance to the player")})
	public IList<IAgent> primFilterDistance(final IScope scope) throws GamaRuntimeException {
		IList<IAgent> ags = GamaListFactory.create(Types.AGENT);
		ags.addAll((IList<IAgent>) scope.getArg("ags"));
		
		IAgent thePlayer = getPlayer(getAgent());
		Double dist = (Double) thePlayer.getAttribute(AbstractUnityPlayer.PLAYER_AGENTS_PERCEPTION_RADIUS);
		return (IList<IAgent>) Spatial.Queries.overlapping(scope, ags, Transformations.enlarged_by(scope, thePlayer, dist));
	}
	 
	@action (
			name = "message_agents",
			args = { @arg (
					name = "ags",
					type = IType.LIST,
					doc = @doc ("list of agents to filter"))},
					
			doc = { @doc (
					value = "Action called by the send_world action that returns the sub-list of agents to send to Unity from a given list of agents according to a max distance to the player")})
	public IList<IMap> primMessageAgents(final IScope scope) throws GamaRuntimeException {
		IList<IAgent> ags = GamaListFactory.create(Types.AGENT);
		ags.addAll((IList<IAgent>) scope.getArg("ags"));
		IList<IMap> output = GamaListFactory.create(Types.MAP);
		for (IAgent ag : ags) {
			WithArgs actTM = ag.getSpecies().getAction( "to_map");
			Arguments argsTM = new Arguments();
			argsTM.put("precision", ConstantExpressionDescription.create(getPrecision(getAgent())));
			output.add((IMap) scope.execute(actTM, ag, argsTM).getValue());
		}
		return output;
	}
	
	@action (
			name = "filter_overlapping",
			args = { @arg (
					name = "ags",
					type = IType.LIST,
					doc = @doc ("list of agents to filter"))},
					
			doc = { @doc (
					value = "Action called by the send_world action that returns the sub-list of agents to send to Unity from a given list of agents according to a min proximity to the other agents to send")})
	public IList<IAgent> primFilterOverlapping(final IScope scope) throws GamaRuntimeException {
		IList<IAgent> ags = GamaListFactory.create(Types.AGENT);
		ags.addAll((IList<IAgent>) scope.getArg("ags"));
		
		IAgent thePlayer = getPlayer(getAgent());
		IList<IAgent> toRemove = GamaListFactory.create() ;
		for (IShape ag : ags) {
			if (!toRemove.contains(ag)) { 
				Double dist = (Double) thePlayer.getAttribute(AbstractUnityPlayer.PLAYER_AGENTS_MIN_DIST);
				toRemove.addAll((Collection<? extends IAgent>) Spatial.Queries.overlapping(scope, ags, Transformations.enlarged_by(scope, thePlayer, dist)));
			}  
		}
		ags.removeAll(toRemove);
		return ags;
	}
	
	
	@action (
			name = "new_player_location",
			args = { @arg (
					name = "loc",
					type = IType.POINT,
					doc = @doc ("Location of the player agent"))},
					
			doc = { @doc (
					value = "Action called by the move_player action that returns the location to send to Unity from a given player location")})
	public GamaPoint primNewPlayerLoc(final IScope scope) throws GamaRuntimeException {
		return (GamaPoint) scope.getArg("loc");
	}
	
	@action (
			name = "move_player",
			
			doc = { @doc (
					value = "move the player agent")})
	public void primMovePlayer(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		if (getMovePlayerEvent(ag)){
			IAgent thePlayer = getPlayer(ag);
			thePlayer.setLocation((GamaPoint)(doAction1Arg(scope, "new_player_location", "loc", scope.getGui().getMouseLocationInModel())));
			doActionNoArg(scope, "send_player_position");
			setMovePlayerEvent(ag, false);
		}
	}
	

	@action (
			name = "manage_message_from_unity",
			
			doc = { @doc (
					value = "Manage messages coming from Unity")})
	public void primManageMessageFromUnity(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		while((boolean) doActionNoArg(scope, "has_more_message")) {
			GamaMessage mes = (GamaMessage) doActionNoArg(scope, "fetch_message");
			if (getWaitingMessage(ag) != null && getWaitingMessage(ag).equals(mes.getContents(scope))) {
				setReceiveInformation(ag, true);
			}	else if  ((getPlayer(ag) != null) && getMovePlayerFromUnity(ag) && getReceiveInformation(ag) && (((String) mes.getContents(scope)).contains("position"))) {
				IMap answer = Cast.asMap(scope, mes.getContents(scope), false);
				IList<BigDecimal> position = (IList<BigDecimal>) answer.get("position");
				if (position != null && position.size() == 2) {
					int precision = getPrecision(ag);
					IAgent thePlayer = getPlayer(ag);
					Double rot = ((Double) thePlayer.getAttribute("player_rotation"));
					
					 thePlayer.setAttribute("rotation", ((BigDecimal)(answer.get("rotation"))).floatValue()/precision + rot ); 
					 
					 thePlayer.setLocation(new GamaPoint(position.get(0).floatValue()/precision, position.get(1).floatValue()/precision));
					 thePlayer.setAttribute("to_display", true);
				}
				
			}	
		}
	}
	
	
	@action (
			name = "wait_for_message",
			args = { @arg (
				name = "message",
				type = IType.STRING,
				doc = @doc ("List of geometries to send"))},
					
			doc = { @doc (
					value = "set the message to wait")})
	public void primWaitForMessage(final IScope scope) throws GamaRuntimeException {
		String message = scope.getStringArg("message");
		setReceiveInformation(getAgent(), false);
		setWaitingMessage(getAgent(), message);
	}
	
	@action (
			name = "send_player_position",
			
			doc = { @doc (
					value = "send the new position of the player to Unity (used to teleport the player from GAMA) ")})
	public void primSendPlayerPosition(final IScope scope) throws GamaRuntimeException {
		IAgent ag = getAgent();
		if (!getConnectToUnity(ag)) {
			return;
		}
		IAgent player = getPlayer(ag);
		int precision = getPrecision(ag);
		IList<Integer> pos = GamaListFactory.create();
		pos.add((int)(player.getLocation().x * precision));
		pos.add((int) (player.getLocation().y * precision));
		setNewPlayerPosition(ag, pos);
		if (getDelayAfterMes(ag) > 0.0) {
			doAction1Arg(scope, "wait_for_message", "message", "ready" );
		}
	
	}
	@action (
			name = "send_parameters",
			
			doc = { @doc (
					value = "Send the parameter to Unity to intialized the connection")})
	public void primSendParameters(final IScope scope) throws GamaRuntimeException {
		GamaMap<String, Object> toSend = (GamaMap<String, Object>) GamaMapFactory.create();
		IAgent ag = getAgent();
		int precision = getPrecision(ag);
		toSend.put(PRECISION, precision);
		IList<Integer> worldT = GamaListFactory.create(Types.INT);
		worldT.add((int)(scope.getSimulation().getGeometricEnvelope().getWidth() * precision));
		worldT.add((int)(scope.getSimulation().getGeometricEnvelope().getHeight() * precision));

		toSend.put("world", worldT);
		
		toSend.put("delay", getDelayAfterMes(ag));
		toSend.put("physics", getUsePhysicsForPlayer(ag));
		
		IList<Integer> posT = GamaListFactory.create(Types.INT);
		posT.add((int)(getPlayer(ag).getLocation().x * precision));
		posT.add((int)(getPlayer(ag).getLocation().y * precision));
		toSend.put("position", posT);
		if (getUnityClient(ag) == null) {
			throw GamaRuntimeException.error ("no client to send to", scope);
		} else {
			sendMessage(scope, toSend);
		}
	}
	
	
	@action (
			name = "add_background_data",
			args = { @arg (
					name = "geoms",
					type = IType.LIST, 
					doc = @doc ("The list of geometry to send to Unity")),
			 @arg (
				name = "height",
				type = IType.FLOAT, 
				doc = @doc ("height of the geometries in Unity")),
			 @arg (
				name = "collider",
				type = IType.BOOL, 
				doc = @doc ("Add a collider to the geometries in Unity?")) },
			doc = { @doc (
					value = "Add background geometries from a list of geometries, their heights, their collider usage, their outline rendered usage")})
	public void primAddBackgroundData(final IScope scope) throws GamaRuntimeException {
		final IList geoms = (IList) scope.getArg("geoms", IType.LIST);
		final Double height = (Double) scope.getFloatArg("height");
		final Boolean collider = (Boolean) scope.getBoolArg("collider");
		
		addBackgroundGeometries(geoms, null, height, collider);
	}

	
	@action (
			name = "add_background_data_with_names",
			args = { @arg (
					name = "geoms",
					type = IType.LIST, 
					doc = @doc ("The list of geometry to send to Unity")),
					@arg (
					 name = "names",
					 type = IType.LIST, 
					 doc = @doc ("The list of names linked to the geometries to send to Unity")),
			 @arg (
				name = "height",
				type = IType.FLOAT, 
				doc = @doc ("height of the geometries in Unity")),
			 @arg (
				name = "collider",
				type = IType.BOOL, 
				doc = @doc ("Add a collider to the geometries in Unity?")) },
			doc = { @doc (
					value = "Add background geometries from a list of geometries, a list of name (one per geometry), their heights, their collider usage, their outline rendered usage")})
	public void primAddBackgroundDataWithName(final IScope scope) throws GamaRuntimeException {
		final IList geoms = (IList) scope.getArg("geoms", IType.LIST);
		final Double height = (Double) scope.getFloatArg("height");
		final Boolean collider = (Boolean) scope.getBoolArg("collider");
		final IList names = (IList) scope.getArg("names", IType.LIST);
		
		addBackgroundGeometries(geoms, names, height, collider);
	}

	
	
	

}
