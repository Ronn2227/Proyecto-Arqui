/**
 * Esta clase contiene las funcionalidades de cada etapa en el pipeline
 * 
 * @author Diana Arias 
 * @version 1.0
 */

import java.util.concurrent.*;
public class Pipeline implements Runnable
{
    // instance variables - replace the example below with your own
    Memoria memoria;
    CyclicBarrier barrera1, barrera2;
    int fase = 0;
    
    
    public void run(){
        switch(fase){
            case 1: hiloIF();
                    break;
            case 2: hiloID();
                    break;
            case 3: hiloEX();
                    break;
            case 4: hiloME();
                    break;
            case 5: hiloWB();
                    break;
        }
    }

    /**
     * Constructor for objects of class Hilos
     */
    public Pipeline(CyclicBarrier bar1, CyclicBarrier bar2, Memoria mem, int f)
    {
        this.barrera1 = bar1;
        this.barrera2 = bar2;
        this.memoria = mem;
        this.fase = f;
    }

    /**
     * Funcion de Instruction Fetch
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void hiloIF(){
        while(!finHilillos()){
            // Obtengo el PC actual de memoria
            int PCAct = memoria.PC; 
            // Calculo el nuevo PC (a ser usado en caso de que el ciclo siga)
            int PCTemp = PCAct + 4;
            // Defino la variable para obtener las instrucción que voy a usar 
            int inst[] = memoria.obtenerInstruccion (PCAct);
            //inst = memoria.obtenerInstruccion (PCAct);
            try {
               barrera1.await();
             } catch (InterruptedException | BrokenBarrierException e) {
               e.printStackTrace();
             } 
             
            for(int i = 0; i < 4; ++i)
                memoria.vecIF_ID[i] = inst[i];
            
            memoria.vecIF_ID[4] = PCAct;
            memoria.vecIF_ID[5] = 0;
            memoria.vecIF_ID[6] = 0;
            memoria.PC = PCTemp;
            try {
               barrera2.await();
             } catch (InterruptedException | BrokenBarrierException e) {
               e.printStackTrace();
             }
        }
        
        System. out. println("Soy IF\n");
    }
    
     /**
     * funcion de Instruction Decode
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void hiloID(){
        while(!finHilillos()){
            
            //Decodifica la instrución
            switch (memoria.vecIF_ID[0]){
                case 5 :  ; //lw
                          break;
                case 19:  //addi
                          break;
                case 37:  //sw
                          break;
                case 51:  //lr
                          break;
                case 52:  //sc
                          break;
                case 56:  //div
                          break;
                case 71:  //add
                          break;
                case 72:  //mul
                          break;
                case 83:  //sub
                          break;
                case 99:  //beq
                          break;
                case 100: //bne
                          break;
                case 103: //jalr
                          break;
                case 111: //jal
                          break;
                case 999: //FIN
                          break;
                case 000: //NOPE (para cambio de contexto)
                          break;
                default:  System.out.println("No se reconoce la instrucción");
                          break;
            }
            // Obtengo el PC actual de memoria
            int PCAct = memoria.PC; 
            // Calculo el nuevo PC (a ser usado en caso de que el ciclo siga)
            int PCTemp = PCAct + 4;
            // Defino la variable para obtener las instrucción que voy a usar 
            int inst[] = memoria.obtenerInstruccion (PCAct);
            //inst = memoria.obtenerInstruccion (PCAct);
            try {
               barrera1.await();
             } catch (InterruptedException | BrokenBarrierException e) {
               e.printStackTrace();
             } 
             
            for(int i = 0; i < 4; ++i)
                memoria.vecIF_ID[i] = inst[i];
            
            memoria.vecIF_ID[4] = PCAct;
            memoria.vecIF_ID[5] = 0;
            memoria.vecIF_ID[6] = 0;
            memoria.PC = PCTemp;
            try {
               barrera2.await();
             } catch (InterruptedException | BrokenBarrierException e) {
               e.printStackTrace();
             }
        }
        
         System. out. println("Soy ID\n");
    }
    
     /**
     * funcion de Execute
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void hiloEX() //a este se le puede meter de parametro lo que esta en hilo principal
    {
         System. out. println("Soy EX\n");
    }
    
     /**
     * funcion de Memory
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void hiloME(){
        System. out. println("Soy ME\n");
        
    }
    
     /**
     * funcion de Write Back
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void hiloWB(){
        System. out. println("Soy WB\n");
        
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y
     */
    public boolean finHilillos()
    {
        boolean respuesta = false;
        if(memoria.tabContextos[0][1] == 1 && memoria.tabContextos[1][1] == 1 && memoria.tabContextos[2][1] == 1
           && memoria.tabContextos[3][1] == 1 && memoria.tabContextos[4][1] == 1 && memoria.tabContextos[5][1] == 1
           && memoria.tabContextos[6][1] == 1 && memoria.tabContextos[7][1] == 1 && memoria.tabContextos[8][1] == 1){
               respuesta = true;
        }
        return respuesta;
    }

}
