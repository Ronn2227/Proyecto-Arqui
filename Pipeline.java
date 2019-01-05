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
    
    public void run(){
        if(Thread.currentThread().getName().equals("IF"))
            {
                hiloIF();
            }
            else if(Thread.currentThread().getName().equals("ID"))
            {           
                 hiloID();
            }
            else if(Thread.currentThread().getName().equals("EX")){
                hiloEX();
            }
            else if(Thread.currentThread().getName().equals("ME")){
                hiloME();
            }
            else if (Thread.currentThread().getName().equals("WB")){
               hiloWB();
            }
    }

    /**
     * Constructor for objects of class Hilos
     */
    public Pipeline(CyclicBarrier bar1, CyclicBarrier bar2, Memoria mem)
    {
        this.barrera1 = bar1;
        this.barrera2 = bar2;
        this.memoria = mem;
    }

    /**
     * Funcion de Instruction Fetch
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void hiloIF(){
       System. out. println("Soy IF\n");
    }
    
     /**
     * funcion de Instruction Decode
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void hiloID(){
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
        
}
