package test;

import javafx.geometry.Point2D;
import world.ports.CivilianPort;
import world.ports.Harbour;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PassengerGeneratorTest {
    private enum Klocek implements Serializable{
        INSTANCE;
        private List<String> lista;
        Klocek(){
            lista = new ArrayList<>();
        }
        public void dodajDoKlocka(String a){
            lista.add(a);
        }
        public void rm(String a){
            lista.remove(a);
        }
        public void pisz(){
            lista.forEach(e -> System.out.println(e));
        }
    }
    public static void main(String[] args){
        String a ="Cebula";
        Klocek.INSTANCE.dodajDoKlocka(a);
        Klocek.INSTANCE.dodajDoKlocka("MIĘSO");
        try(ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("test.out")))){
            out.writeObject(Klocek.INSTANCE);
        }catch (IOException ex){}
        Klocek.INSTANCE.rm(a);
        Klocek x = null;
        try(ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("test.out")))){
            x     = (Klocek)in.readObject();
        }catch (IOException | ClassNotFoundException ex){}
        Klocek.INSTANCE.pisz();
        x.pisz();
    }
}
