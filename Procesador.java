
/**
 * Este es el procesador que solo invoca al hilo principal
 * 
 * @author Diana Arias B50656 
 * @version 1.0
 */

import java.util.concurrent.*;
import java.util.*;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;
import java.io.*;

public class Procesador{
    public static Semaphore mutex = new Semaphore(1, true);
    String nomArch = "0.txt";
    static Memoria memoria = new Memoria();
    static CyclicBarrier barrera1;
    static CyclicBarrier barrera2;
    int quantum, quanTemp = 0;
    
    /**
     * Este metodo realiza una lectura de los hilillos a procesar
     * posteriormente cuando ha leido todo el contenido del hilillo, llama
     * al metodo de Memoria llenarMemoria.
     */

    public String recibirHilillos() throws FileNotFoundException, IOException{
        String cadena="";
        String instrucciones = "";
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        for(int i =0; i <9; i++){  
            instrucciones = "";

            try {
                nomArch = "Hilillos/"+i+".txt";
                archivo = new File (nomArch);

                fr = new FileReader (archivo);
                br = new BufferedReader(fr);

                // Lectura de los hilillos
                while((cadena=br.readLine())!=null){
                    instrucciones += cadena + " ";            
                }
                memoria.llenarMatrizInstr(instrucciones, i);

            }
            catch(Exception e){
                //e.printStackTrace();
            }finally{
                //Se cierra el fichero
                try{                    
                    if( null != fr ){   
                        fr.close();     
                    }                  
                }catch (Exception e2){ 
                    e2.printStackTrace();
                }
            }

        }
        return instrucciones;
    }
    
    public static void grabarFicheroTexto(){
    
        mutex.acquireUninterruptibly();
        char c;
        String contenido = "";
        try{
            System.out.println("Vas a escribir en un fichero de texto en Java\n");
            System.out.print("Escribe aqui: ");
            FileWriter fichero = new FileWriter("Archivo.txt");
            StringBuffer str = new StringBuffer();
            while (true){
                c=(char)System.in.read();
                if(c == ' ')
                    contenido = "";
                if(c == '\n')
                    if(contenido.trim().equalsIgnoreCase("fin"))
                        break;
                    else contenido = "";
                contenido += c;
                str.append(c);    
            }
            String cadena = new String(str); 
            cadena = cadena.replace("fin", "");
            fichero.write(cadena);          

            if (fichero!=null)
                fichero.close();
        }catch(IOException ex){}
        System.out.println("FICHERO ESCRITO CORRECTAMENTE");

        mutex.release();
    }

    public static void leerFicheroTexto()throws IOException{

        mutex.acquireUninterruptibly();

        System.out.println("Estas leyendo un fichero de texto en Java\n");
        FileReader fr = new FileReader("Archivo.txt");
        BufferedReader br = new BufferedReader(fr);
        String s;

        while((s = br.readLine()) != null) {
            System.out.println(s);
        }
        fr.close();  

        mutex.release();

    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y
     */
    void correr()throws IOException { 
        String quantumS = JOptionPane.showInputDialog("Por favor introduzca el Quantum a dar a cada hilillo");

        quantum = Integer.parseInt(quantumS);
        
        recibirHilillos();
        memoria.imprimirTabContexto();
        Runnable barrierAction = new Runnable() { public void run() {quanTemp++; if (quanTemp == quantum) cambioContexto();}};
        barrera1 = new CyclicBarrier(5);
        barrera2 = new CyclicBarrier(5, barrierAction);
        List<Thread> threads = new ArrayList<Thread>(5);
        for(int i = 0; i < 5; ++i){
            Thread thread = new Thread(new Pipeline(barrera1, barrera2, memoria));
            threads.add(thread);
            thread.start();
        }
        // Espera hasta que se mueran los pipeline
        for (Thread thread : threads){
          try {thread.join();}
          catch(InterruptedException e){
              e.printStackTrace();
          }
        }
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y
     */
    public void cambioContexto()
    {
        quanTemp = 0;
        
    }


    public static void main(String[] args) throws IOException {
        Procesador poi = new Procesador();
        poi.correr();
    }
}
