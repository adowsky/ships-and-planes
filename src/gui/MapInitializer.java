package gui;

import javafx.geometry.Point2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import world.Cross;
import world.Crossing;
import world.ports.CivilianAirport;
import world.ports.Harbour;
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
    public MapInitializer(String path) throws ParserConfigurationException {
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        try {
            doc = builder.parse(new File(path));
            doc.getDocumentElement().normalize();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        seaPorts = new HashMap<>();
    }
    public Set<PortButton<Harbour>> getHarbours(){
        Set<PortButton<Harbour>> set = new HashSet<>();
        List<PortButton<Harbour>> list = new ArrayList<>();
        List<Map<String,String[]>> routes= new ArrayList<>();
        if(doc == null)
            return set;
        if(seaCrossings == null)
            gatherSeaCrossings();
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
        for(int i = 0; i<list.size();++i){
            Map<Port,List<Cross>> route = new HashMap<>();
            Map<String,String[]> stringRoutes = routes.get(i);
            for(String s : stringRoutes.keySet()){
                List<Cross> rt = new LinkedList<>();
                String[] stringRoute = stringRoutes.get(s);
                for(String crs : stringRoute){
                    Cross tmp = seaCrossings.get(crs);
                    if(tmp!= null)
                        rt.add(tmp);
                }
                rt.add(seaPorts.get(s));
                route.put(seaPorts.get(s),rt);
            }
            list.get(i).getModel().setWays(route);
        }
        return set;
    }
    private void gatherSeaCrossings(){
        seaCrossings = new HashMap<>();
        NodeList nodes = doc.getElementsByTagName("sea-crossing");
        for(int i=0; i<nodes.getLength(); ++i){
            Node node = nodes.item(i);
            Element elem = (Element)node;
            int x = Integer.valueOf(elem.getAttribute("x"));
            int y = Integer.valueOf(elem.getAttribute("y"));
            int r = Integer.valueOf(elem.getAttribute("radius"));
            String name = elem.getAttribute("name");
            seaCrossings.put(name,new Crossing(x,y,r));
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
        if(doc == null)
            return set;
        NodeList buttons = doc.getElementsByTagName("harbour");
        for(int i = 0; i< buttons.getLength(); i++){
            Node node = buttons.item(i);
            el = (Element)node;
            PortButton<CivilianAirport> btn = new PortButton<>();
            setLayouts(btn);
            btn.setModel(gatherCivilianAirport(new Point2D(btn.getLayoutX(),btn.getLayoutY())));
            set.add(btn);
        }
        return set;
    }
    private CivilianAirport gatherCivilianAirport(Point2D location){
        String att =  el.getAttribute("sleep-time");
        int sleepTime;
        if(att.equals(""))
            sleepTime = DEFAULT_SLEEP_TIME;
        else
            sleepTime = Integer.valueOf(att);
        att = el.getAttribute("sleep-time");
        int capacity;
        if(att.equals(""))
            capacity = DEFAULT_CAPACITY;
        else
            capacity = Integer.valueOf(att);
        return new CivilianAirport(sleepTime, capacity, location);
    }
    public Map<String, Harbour> getSeaPorts(){
        return seaPorts;
    }
}

