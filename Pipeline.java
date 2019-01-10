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
            
            // Llego a la primera barrera
            try {
               barrera1.await();
             } catch (InterruptedException | BrokenBarrierException e) {
               e.printStackTrace();
             } 
             
             // Revisar si ID reporto algun error o si puede seguir
             // 0 es seguir, 1 es error
            if(memoria.vecIF_ID[6] == 0 && memoria.vecIF_ID[6] == 0){
                
                // copiar instruccion
                for(int i = 0; i < 4; ++i){
                    memoria.vecIF_ID[i] = inst[i];
                }
                
                // copiar el NPC
                memoria.vecIF_ID[4] = PCTemp;
                
                //Cambio el PC General
                memoria.PC = PCTemp;
            }
            
            // Llego a la segunda barrera
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
            
            int IR [] = new int [4];
            int NPC = 0;
            int A = 0;
            int B = 0;
            int Inm = 0;
            int RD = 0;
            int RF1 = 0;
            int RF2 =0;
            
            // Reviso si no hay error proveniente de IF causado por final de
            // quantum
            if(memoria.vecIF_ID[5] == 0 && memoria.vecID_EX[9] == 0){
                // Obtengo la instruccion
                for(int a=0; a<4; a++){
                
                    IR[a] = memoria.vecIF_ID[a];
                }
                //Decodifica la instrución
                switch (IR[0]){
                    case 5 :  //lw
                        
                        // Obtengo los valores de la instruccion
                        RD = IR[1];
                        RF1 = IR[2];
                        Inm = IR[3];
                        
                        // Verifico si el registro fuente este ocupado
                        //if(){}
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
            else {
                
                //reporto el error de final de quantum al siguiente hilo
                // mediante el registro de error izquierdo del otro vector
                memoria.vecID_EX[8] = 1;
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
        // liberar registros
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

    /**
     * Metodo para revisar si los registros fuente estan libres o si no.
     */
    public boolean revisarRegistros(int reg1, int reg2)
    {
        boolean respuesta = true;
        
        // Verifico si el primer registro esta ocupado y en caso de que si
        // devuelvo false
        if(memoria.vecRegMaquina[reg1][1] != 0){
        
            respuesta = false;
            return(respuesta);
        }
        
        // reviso si necesito verificar 2 o solo un registro fuente 
        // -1 en reg2 -> solo un registro
        if(reg2 >= 0){
            
            // Si no es -1, entonces reviso que no este ocupado y si lo esta
            // devuelvo false
            if(memoria.vecRegMaquina[reg2][1] > 0){
            
                respuesta = false;
            }
        }
        
        // Si ambos estan libres, devuelvo true
        return respuesta;
    }
}
