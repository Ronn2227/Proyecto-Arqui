 
/**
 * Esta clase es como el controlador, 
 * que recibe los hilillos para ser procesasados,
 * dando recursos como procesador, memoria, cache
 * 
 * @author Diana Arias B50656 
 * @version 1.0
 */

//import java.util.concurrent.CyclicBarrier;
//import java.util.concurrent.Semaphore;
import java.util.concurrent.*;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.*;

public class Hilo_Principal extends Thread
{
    public static Semaphore mutex = new Semaphore(1, true);
    int vectorRegistros[][] = new int [31][2];
    String nomArch = "0.txt";
    Memoria memoria = new Memoria();
    static CyclicBarrier barrera; 
    Pipeline pipeline;

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

    /**
     * Este metodo es el principal el cual,
     * desencadena el procesamiento de los hilillos en el procesador.
     * Y se encarga de preparar el entorno.
     * 
     */

    public void principal() throws IOException
    {

        String quantumS = JOptionPane.showInputDialog("Por favor introduzca el Quantum a dar a cada hilillo");

        int quantum = Integer.parseInt(quantumS);

        String instrucciones;
        //Hilo_Principal hPrincipal = new Hilo_Principal();
        // Memoria memoria = new Memoria();
        /*Se Inicializan los hilos de las etapas para lograr procesar los hilillos*/
        recibirHilillos();
        memoria.imprimirTabContexto();
        barrera = new CyclicBarrier(5);

        Thread hiloIF = new Hilo_Principal(); hiloIF.setName("IF");
        Thread hiloID = new Hilo_Principal(); hiloID.setName("ID");
        Thread hiloEX = new Hilo_Principal(); hiloEX.setName("EX");
        Thread hiloME = new Hilo_Principal(); hiloME.setName("ME");
        Thread hiloWB = new Hilo_Principal(); hiloWB.setName("WB");

        hiloID.start();
        hiloIF.start();
        hiloEX.start();
        hiloME.start();
        hiloWB.start();
        
    
    

        
    }
    //Main de esta clase no lo estamos usando
    public static void main(String[] args) throws IOException
    {

        String instrucciones;

        Hilo_Principal hPrincipal = new Hilo_Principal();
        hPrincipal.principal();
    }

    public void run()
    {
        // Sincroniza 5 hilos secundarios + el hilo principal

        for(int i=0; i<2; i++){
            
            if(Thread.currentThread().getName().equals("IF"))
            {
                
                pipeline.hiloIF();
            }
    
            else if(Thread.currentThread().getName().equals("ID"))
            {           
                 pipeline.hiloID();
               
                
            }
            else if(Thread.currentThread().getName().equals("EX")){
                pipeline.hiloEX();
            }
            else if(Thread.currentThread().getName().equals("ME")){
                pipeline.hiloME();
            }
            else if (Thread.currentThread().getName().equals("WB")){
               pipeline.hiloWB();
            }
            else{
                System. out. println("Soy Hilo Principal, que debo de hacer\n");
            }
    
        }
    }

    public static void grabarFicheroTexto()
    {

        mutex.acquireUninterruptibly();
        char c;
        String contenido = "";
        try{
            System.out.println("Vas a escribir en un fichero de texto en Java\n");
            System.out.print("Escribe aqui: ");
            FileWriter fichero=new FileWriter("Archivo.txt");
            StringBuffer str=new StringBuffer();
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
            String cadena=new String(str); 
            cadena = cadena.replace("fin", "");
            fichero.write(cadena);          

            if (fichero!=null)
                fichero.close();
        }catch(IOException ex){}
        System.out.println("FICHERO ESCRITO CORRECTAMENTE");

        mutex.release();
    }

    public static void leerFicheroTexto()throws IOException
    {

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
}
