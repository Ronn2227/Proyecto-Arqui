
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
    int cantHilillos, quantum, quanTemp = 0;
    
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
                e.printStackTrace();
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
    
    /* Método que crea los hilos, les asigna las variables necesarias y los
       inicia */
    
    void correr()throws IOException { 
        String quantumS = JOptionPane.showInputDialog("Por favor introduzca el Quantum a dar a cada hilillo");

        quantum = Integer.parseInt(quantumS);
        
        recibirHilillos();
        memoria.imprimirTabContexto();
        memoria.PC = 384;
        cantHilillos = memoria.cantHilillos();
        Runnable barrierAction = new Runnable() { public void run() {quanTemp++; memoria.ciclos ++; if (quanTemp == quantum) cambioContexto();}};
        barrera1 = new CyclicBarrier(5);
        barrera2 = new CyclicBarrier(5, barrierAction);
        //Crea una lista para los hilos del pipeline y la llena
        List<Thread> threads = new ArrayList<Thread>(5);
        for(int i = 1; i < 6; ++i){
            Thread thread = new Thread(new Pipeline(barrera1, barrera2, memoria, i));
            threads.add(thread);
            thread.start();
        }
        // Espera hasta que se mueran los pipeline
        for (Thread thread : threads){
          try {
              thread.join();
          }
          catch(InterruptedException e){
              e.printStackTrace();
          }
        }
    }
    
    public void cambioContexto()
    {
        quanTemp = 0;
        memoria.CC = true;
        //Bloquea hasta que terminen todas las instrucciones actuales en el pipeline
        mutex.acquireUninterruptibly();
        //Llena la tabla de contextos con los registros actuales, la cantidad de ciclos y el ´ultimo pc
        for(int i = 0 ; i < 32; ++i){
            memoria.tabContextos[memoria.hililloTemp-1][i+4] = memoria.vecRegMaquina [i][0];
        }
        memoria.tabContextos[memoria.hililloTemp-1][2] = memoria.ciclos;
        memoria.tabContextos[memoria.hililloTemp-1][3] = memoria.PC;
        memoria.vecRegMaquina [32][0] = -1;
        
        boolean noEncontrado = true;
        int siguiente = memoria.hililloTemp;
        //Revisa que haya algun hilillo que necesite quantum
        if(memoria.noFinHilillos()){
            //Encuentra el siguiente hilillo al que le toca correr
            while(noEncontrado){
                if(siguiente < cantHilillos && memoria.tabContextos[siguiente][1] != 0){
                    siguiente++;
                }
                else if (siguiente == cantHilillos && memoria.tabContextos[siguiente][1] != 0){
                    siguiente = 0;
                }
                else{
                    noEncontrado = false;
                }
            }
            //cargo los registros pc y ciclos el proximo hilillo
            if(siguiente < cantHilillos){
                for(int i = 0 ; i < 32; ++i){
                    memoria.vecRegMaquina [i][0] = memoria.tabContextos[siguiente][i+4];
                }
                memoria.hililloTemp = siguiente + 1;
                memoria.PC = memoria.tabContextos[siguiente][3];
                memoria.ciclos = memoria.tabContextos[siguiente][2];
            }
            else{
                for(int i = 0 ; i < 32; ++i){
                    memoria.vecRegMaquina [i][0] = memoria.tabContextos[0][i+4];
                }
                memoria.hililloTemp = 1;
                memoria.PC = memoria.tabContextos[0][3];
                memoria.ciclos = memoria.tabContextos[0][2];
            }
        }
        mutex.release();
        memoria.CC = false;
    }

    public static void main(String[] args) throws IOException {
        Procesador Risc_V = new Procesador();
        Risc_V.correr();
    }
}
