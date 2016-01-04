package gui;

import exceptions.gui.MapInitException;
import javafx.geometry.Point2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import world.*;
import world.ports.CivilianAirport;
import world.ports.Harbour;
import world.ports.MilitaryAirport;
import world.ports.Port;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * Class to initialize map's elements.
 */
public class MapInitializer implements Serializable{
    private DocumentBuilder builder;
    private Document doc;
    private volatile Element el;
    private final int DEFAULT_SLEEP_TIME = 1000;
    private final int DEFAULT_CAPACITY = 10;
    private Map<String, Cross> seaCrossings;
    private Map<String, Cross> airCrossings;
    private Map<String, Harbour> seaPorts;
    private Map<String, CivilianAirport> airPorts;
    private Map<String, MilitaryAirport> mAirPorts;
    private Map<String, Set<String>> landConnections;
    private Set<PortButton<Harbour>> harboursBtns;
    private Set<PortButton<CivilianAirport>> cAirportBtns;
    private Set<PortButton<MilitaryAirport>> mAirportBtns;

    /**
     * Creates new MapInitializer with source specified in parm.
     * @param path path of source
     * @throws MapInitException Problem with source.
     */
    public MapInitializer(String path) throws MapInitException {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(new File(path));
            doc.getDocumentElement().normalize();
        }catch(Exception ex){
            ex.printStackTrace();
            throw new MapInitException(ex, MapInitException.ErrorType.FILE_PARSE);
        }
        seaPorts = new HashMap<>();
        airPorts = new HashMap<>();
        mAirPorts = new HashMap<>();
        landConnections = new HashMap<>();
    }
    public void init(){
        getHarbours();
        getCivilianAirports();
        getMilitaryAirports();
        setLandConnections();
    }
    public void setLandConnections(){
        for(CivilianAirport a : airPorts.values()){
            String name = a.getName();
            Set<String> set = landConnections.get(name);
            Set<Port> conn = new HashSet<>();
            for(String s : set){
                conn.add(seaPorts.get(s));
            }
            a.setLandConnectionPorts(conn);
        }
        for(Harbour a : seaPorts.values()){
            String name = a.getName();
            Set<String> set = landConnections.get(name);
            Set<Port> conn = new HashSet<>();
            for(String s : set){
                conn.add(airPorts.get(s));
            }
            a.setLandConnectionPorts(conn);
        }
    }
    /**
     * Gathers harbours from xml file.
     * @return set of harbour from source file.
     */
    public void getHarbours(){
        Set<PortButton<Harbour>> set = new HashSet<>();
        List<PortButton> list = new ArrayList<>();
        List<Map<String,String[]>> routes= new ArrayList<>();
        if(doc == null)
            return;
        if(seaCrossings == null)
            gatherCrossings(MoveType.SEA);
        NodeList buttons = doc.getElementsByTagName("harbour");
        for(int i = 0; i< buttons.getLength(); i++){
            Node node = buttons.item(i);
            el = (Element)node;
            PortButton<Harbour> btn = new PortButton<>();
            setLayouts(btn);
            btn.setModel(gatherHarbour(new Point2D(btn.getLayoutX(),btn.getLayoutY())));
            list.add(btn);
            landConnections.put(el.getAttribute("name"),getLandConnections());
            routes.add(getMapOfRoutesAsStrings());
            set.add(btn);
            seaPorts.put(el.getAttribute("name"),btn.getModel());
        }
        setWays(MoveType.SEA, AvailabilityType.CIVILIAN, list, routes);
        harboursBtns = set;
    }

    /**
     * Gathers crossings form source file.
     * @param s kind of crossing
     */
    private void gatherCrossings(MoveType s){
        Map<String, Cross> map = null;
        NodeList nodes = null;
        if(s == MoveType.AIR) {
            airCrossings = new HashMap<>();
            map = airCrossings;
            nodes = doc.getElementsByTagName("air-crossing");
        }
        else if(s == MoveType.SEA) {
            seaCrossings = new HashMap<>();
            map = seaCrossings;
            nodes = doc.getElementsByTagName("sea-crossing");
        }
        for(int i=0; i<nodes.getLength(); ++i){
            Node node = nodes.item(i);
            Element elem = (Element)node;
            int x = Integer.valueOf(elem.getAttribute("x"));
            int y = Integer.valueOf(elem.getAttribute("y"));
            int r = Integer.valueOf(elem.getAttribute("radius"));
            String name = elem.getAttribute("name");
            Crossing c = new Crossing(x,y,r);
            c.setName(name);
            map.put(name,c);
        }
    }

    /**
     * Sets layouts(localization) of the port button.
     * @param btn   button that have layouts set.
     */
    private void setLayouts(PortButton btn){
        String att = el.getAttribute("x");
        btn.setLayoutX(Double.valueOf(att));
        att = el.getAttribute("y");
        btn.setLayoutY(Double.valueOf(att));
    }
    private synchronized Harbour gatherHarbour(Point2D location){
        String att =  el.getAttribute("sleep-time");
        int sleepTime;
        if(att.equals(""))
            sleepTime = DEFAULT_SLEEP_TIME;
        else
            sleepTime = Integer.valueOf(att);
        att = el.getAttribute("capacity");
        int capacity;
        if(att.equals(""))
            capacity = DEFAULT_CAPACITY;
        else
            capacity = Integer.valueOf(att);
        Harbour h = new Harbour(sleepTime, capacity, location);
        h.setName(el.getAttribute("name"));
        seaPorts.put(el.getAttribute("name"),h);
        return h;
    }

    /**
     * Returns map of routes for specific port as array of strings.
     * @return map of routes for specific port as array of strings.
     */
    private Map<String,String[]>  getMapOfRoutesAsStrings(){
        NodeList nodes = el.getElementsByTagName("connection");
        Map <String, String[]> map = new HashMap<>();
        for (int i = 0; i<nodes.getLength();++i){
            Node n = nodes.item(i);
            Element e = (Element)n;
            String[] route = n.getTextContent().split(" ");
            String target = e.getAttribute("name");
            map.put(target,route);
        }
        return map;
    }
    private Set<String> getLandConnections(){
        NodeList nodes = el.getElementsByTagName("land");
        Set<String> list = new HashSet<>();
        for (int i = 0; i<nodes.getLength();++i){
            Node n = nodes.item(i);
            Element e = (Element)n;
            String target = e.getAttribute("name");
            list.add(target);
        }
        return list;
    }

    /**
     * Gathers civilian airports from source file.
     * @return civilian airports.
     */
    public synchronized void getCivilianAirports(){

        Set<PortButton<CivilianAirport>> set = new HashSet<>();
        List<PortButton> list = new ArrayList<>();
        List<Map<String,String[]>> routes= new ArrayList<>();
        if(doc == null)
            return;
        if(airCrossings == null)
            gatherCrossings(MoveType.AIR);
        NodeList buttons = doc.getElementsByTagName("c-airport");
        for(int i = 0; i< buttons.getLength(); i++){
            Node node = buttons.item(i);
            el = (Element)node;
            PortButton<CivilianAirport> btn = new PortButton<>();
            setLayouts(btn);
            btn.setModel(gatherCivilianAirport(new Point2D(btn.getLayoutX(),btn.getLayoutY())));
            list.add(btn);

            landConnections.put(el.getAttribute("name"),getLandConnections());
            routes.add(getMapOfRoutesAsStrings());
            airPorts.put(el.getAttribute("name"),btn.getModel());
            set.add(btn);
        }
        setWays(MoveType.AIR, AvailabilityType.CIVILIAN, list, routes);
        cAirportBtns = set;
    }

    /**
     * Set routes to specific port.
     * @param type  movement type
     * @param atype civility type
     * @param list  list of port buttons
     * @param routes map of routes saved as Strings.
     */
    public void setWays(MoveType type,AvailabilityType atype, List<PortButton> list,List<Map<String,String[]>> routes){
        Map<String, Cross> crosses =null;
        if(type.equals( MoveType.SEA))
            crosses = seaCrossings;
        else if(type == MoveType.AIR)
            crosses = airCrossings;
        for(int i = 0; i<list.size();++i){
            Map<Port,List<Cross>> route = new HashMap<>();
            Map<String,String[]> stringRoutes = routes.get(i);
            for(String s : stringRoutes.keySet()){
                List<Cross> rt = new LinkedList<>();
                String[] stringRoute = stringRoutes.get(s);
                for(String crs : stringRoute){
                    Cross tmp = crosses.get(crs);
                    if(tmp!= null)
                        rt.add(tmp);
                }
                if(type == MoveType.SEA && atype == AvailabilityType.CIVILIAN) {
                    rt.add(seaPorts.get(s));
                    route.put(seaPorts.get(s), rt);

                }else if(type == MoveType.AIR && atype == AvailabilityType.CIVILIAN){
                    rt.add(airPorts.get(s));
                    route.put(airPorts.get(s), rt);

                }else if(type == MoveType.AIR && atype == AvailabilityType.MILITARY){
                    rt.add(mAirPorts.get(s));
                    route.put(mAirPorts.get(s), rt);
                }
            }
            (list.get(i)).getModel().setWays(route);

        }
    }

    /**
     * Gathers single civilian airport from source.
     * @param location location of civilian airport
     * @return new civilian airport.
     */
    private CivilianAirport gatherCivilianAirport(Point2D location){
        String att =  el.getAttribute("sleep-time");
        int sleepTime;
        if(att.equals(""))
            sleepTime = DEFAULT_SLEEP_TIME;
        else
            sleepTime = Integer.valueOf(att);
        att = el.getAttribute("capacity");
        int capacity;
        if(att.equals(""))
            capacity = DEFAULT_CAPACITY;
        else
            capacity = Integer.valueOf(att);
        CivilianAirport c = new CivilianAirport(sleepTime, capacity, location);
        c.setName(el.getAttribute("name"));
        airPorts.put(el.getAttribute("name"),c);
        return c;
    }

    /**
     * Gathers military airports from source file.
     * @return set of military airports.
     */
    public synchronized void getMilitaryAirports(){

        Set<PortButton<MilitaryAirport>> set = new HashSet<>();
        List<PortButton> list = new ArrayList<>();
        List<Map<String,String[]>> routes= new ArrayList<>();
        if(doc == null)
            return;
        if(airCrossings == null)
            gatherCrossings(MoveType.AIR);
        NodeList buttons = doc.getElementsByTagName("m-airport");
        for(int i = 0; i< buttons.getLength(); i++){
            Node node = buttons.item(i);
            el = (Element)node;
            PortButton<MilitaryAirport> btn = new PortButton<>();
            setLayouts(btn);
            btn.setModel(gatherMilitaryAirport(new Point2D(btn.getLayoutX(),btn.getLayoutY())));
            list.add(btn);
            routes.add(getMapOfRoutesAsStrings());
            mAirPorts.put(el.getAttribute("name"),btn.getModel());
            set.add(btn);
        }
        setWays(MoveType.AIR, AvailabilityType.MILITARY, list, routes);
        mAirportBtns = set;
    }

    /**
     * Gathers single military airport form source.
     * @param location location of port
     * @return new military airport
     */
    private MilitaryAirport gatherMilitaryAirport(Point2D location){
        String att =  el.getAttribute("sleep-time");
        int sleepTime;
        if(att.equals(""))
            sleepTime = DEFAULT_SLEEP_TIME;
        else
        try {
            sleepTime = Integer.valueOf(att);
        }catch(NumberFormatException ex){
            ex.printStackTrace();
            sleepTime = DEFAULT_SLEEP_TIME;
        }
        att = el.getAttribute("capacity");
        int capacity;
        if(att.equals(""))
            capacity = DEFAULT_CAPACITY;
        else
        try {
            capacity = Integer.valueOf(att);
        }catch(NumberFormatException ex){
            ex.printStackTrace();
            capacity = DEFAULT_CAPACITY;
        }
        MilitaryAirport c = new MilitaryAirport(sleepTime, capacity, location);
        c.setName(el.getAttribute("name"));
        mAirPorts.put(el.getAttribute("name"),c);
        return c;
    }

    /**
     * Returns Map to initialize aircraft carriers.
     * @return Map to initialize aircraft carriers.
     * @throws MapInitException
     */
    public Map<Port, Map<Port,List<Cross>>> getConnectionForAircraftCarrier() throws MapInitException{
        Map<Port,Map<Port,List<Cross>>> result = new HashMap<>();
        if(mAirPorts.isEmpty())
            throw new MapInitException("Military airports not initialized!", MapInitException.ErrorType.PORTS_NOT_INIT);
        if(seaPorts.isEmpty())
            throw new MapInitException("Harbours not initialized!", MapInitException.ErrorType.PORTS_NOT_INIT);
        NodeList conns = doc.getElementsByTagName("carrier-connection");
        for(int i =0; i<conns.getLength(); i++){
            Node n = conns.item(i);
            Element e = (Element)n;
            String from = e.getAttribute("from");
            String to = e.getAttribute("to");
            List<String> route = Arrays.asList(e.getTextContent().split(" "));
            Port portFrom = seaPorts.get(from);
            Port portTo = mAirPorts.get(to);
            if(portFrom == null)
                throw new MapInitException("Specific Harbour does not exist!", MapInitException.ErrorType.FILE_NOT_COHERENT);
            if(portTo == null)
                throw new MapInitException("Specific Military airport does not exist!", MapInitException.ErrorType.FILE_NOT_COHERENT);
            Map<Port,List<Cross>> map = result.get(portFrom);

            if(map == null) {
                map = new HashMap<>();
                result.put(portFrom, map);
            }
            if(map.containsKey(portTo))
                throw new MapInitException("Destination port duplicate!", MapInitException.ErrorType.FILE_NOT_COHERENT);
            List<Cross> crosses = new LinkedList<>();
            route.forEach((s)->crosses.add(airCrossings.get(s)));
            crosses.add(portTo);
            map.put(portTo,crosses);
        }
        return result;
    }

    /**
     * Returns map of Harbours
     * @return map of Harbours
     */
    public Map<String, Harbour> getSeaPorts(){
        return seaPorts;
    }
    /**
     * Returns map of Civilian airports
     * @return map of Civilian Airports
     */
    public Map<String, CivilianAirport> getCAirports(){
        return airPorts;
    }
    /**
     * Returns map of military airports
     * @return map of miliatry airports
     */
    public Map<String, MilitaryAirport> getMAirPorts() {return mAirPorts;}

    /**
     * Returns set of Harbour Buttons.
     * @return set of Harbour Buttons.
     */
    public Set<PortButton<Harbour>> getHarboursBtns() {
        return harboursBtns;
    }

    /**
     * Returns set of Civilian airport buttons.
     * @return set of Civilian airport buttons.
     */
    public Set<PortButton<CivilianAirport>> getcAirportBtns() {
        return cAirportBtns;
    }

    /**
     * Returns set of Military airports Button.
     * @return set of Military airports Button.
     */
    public Set<PortButton<MilitaryAirport>> getmAirportBtns() {
        return mAirportBtns;
    }
}

