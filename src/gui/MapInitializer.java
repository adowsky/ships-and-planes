package gui;

import exceptions.gui.MapInitException;
import javafx.geometry.Point2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import world.AvailabilityType;
import world.Cross;
import world.Crossing;
import world.MoveType;
import world.ports.CivilianAirport;
import world.ports.Harbour;
import world.ports.MilitaryAirport;
import world.ports.Port;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.*;

/**
 * Class to initialize map's elements.
 */
public class MapInitializer {
    private DocumentBuilder builder;
    private Document doc;
    private volatile Element el;
    private final int DEFAULT_SLEEP_TIME = 500;
    private final int DEFAULT_CAPACITY = 10;
    private Map<String, Cross> seaCrossings;
    private Map<String, Cross> airCrossings;
    private Map<String, Harbour> seaPorts;
    private Map<String, CivilianAirport> airPorts;
    private Map<String, MilitaryAirport> mAirPorts;
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
    }
    public Set<PortButton<Harbour>> getHarbours(){
        Set<PortButton<Harbour>> set = new HashSet<>();
        List<PortButton> list = new ArrayList<>();
        List<Map<String,String[]>> routes= new ArrayList<>();
        if(doc == null)
            return set;
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
            routes.add(getMapOfRoutesAsStrings());
            set.add(btn);
            seaPorts.put(el.getAttribute("name"),btn.getModel());
        }
        setWays(MoveType.SEA, AvailabilityType.CIVILIAN, list, routes);
        return set;
    }
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
    public synchronized Set<PortButton<CivilianAirport>> getCivilianAirports(){

        Set<PortButton<CivilianAirport>> set = new HashSet<>();
        List<PortButton> list = new ArrayList<>();
        List<Map<String,String[]>> routes= new ArrayList<>();
        if(doc == null)
            return set;
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
            routes.add(getMapOfRoutesAsStrings());
            airPorts.put(el.getAttribute("name"),btn.getModel());
            set.add(btn);
        }
        setWays(MoveType.AIR, AvailabilityType.CIVILIAN, list, routes);
        return set;
    }
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
    public synchronized Set<PortButton<MilitaryAirport>> getMilitaryAirports(){

        Set<PortButton<MilitaryAirport>> set = new HashSet<>();
        List<PortButton> list = new ArrayList<>();
        List<Map<String,String[]>> routes= new ArrayList<>();
        if(doc == null)
            return set;
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
        return set;
    }

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
    public Map<String, Harbour> getSeaPorts(){
        return seaPorts;
    }
    public Map<String, CivilianAirport> getCAirports(){
        return airPorts;
    }
    public Map<String, MilitaryAirport> getMAirPorts() {return mAirPorts;}
}

