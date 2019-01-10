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
            // 0 es seguir, 1 es error. Si procesador envio la señal de 
            // fin de quantum, entonces tendría un 1 en vec IF_ID [5]
            if(memoria.vecIF_ID[6] == 0){

                if(memoria.vecIF_ID[5] == 0 ){
                    // copiar instruccion
                    for(int i = 0; i < 4; ++i){
                        memoria.vecIF_ID[i] = inst[i];
                    }

                    // copiar el NPC
                    memoria.vecIF_ID[4] = PCTemp;

                    //Cambio el PC General
                    memoria.PC = PCTemp;
                }
                if(memoria.vecIF_ID[5] == -1){
                    
                    // si tengo un -1 es porque necesito matar a la
                    // instruccion que tengo
                    for(int i = 0; i < 4; ++i){
                        memoria.vecIF_ID[i] = 0;
                    }
                    // NPC
                    memoria.vecIF_ID[4] = 0;

                    //Cambio el PC 
                    memoria.PC = 0;

                }
                else{

                    // copiar instruccion con 0s por fin de quantum
                    for(int i = 0; i < 4; ++i){
                        memoria.vecIF_ID[i] = 0;
                    }
                }
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

            // Obtengo la instruccion
            for(int a=0; a<4; a++){

                IR[a] = memoria.vecIF_ID[a];
            }
            //Decodifica la instrución
            switch (IR[0]){
                case 5 :  //lw

                case 19:  //addi

                case 51: //lr

                case 103: // jalr

                case 111: // jal

                // Obtengo los valores de la instruccion
                RD = IR[1];
                RF1 = IR[2];
                Inm = IR[3];
                NPC = memoria.vecIF_ID[4];

                // Verifico si el registro fuente esta libre
                if(revisarRegistros(RF1,-1)){

                    // En caso de que ambos esten libres, entonces
                    // le agrego un "candado" al destino
                    memoria.vecRegMaquina[RD][1] ++;
                }
                else{

                    // Si los registros no estan libres, tengo
                    // un conflicto de datos, por lo que no 
                    // puedo avanzar, por ende marco el error
                    // en los dos vectores adyacentes
                    memoria.vecIF_ID[6] = 1;
                    memoria.vecID_EX[8] = 1;
                }

                break;

                case 71:  //add

                case 83:  //sub

                case 72:  //mul

                case 56:  //div

                // Obtengo los valores de la instruccion
                RD = IR[1];
                RF1 = IR[2];
                RF2 = IR[3];
                NPC = memoria.vecIF_ID[4];

                // Verifico si el registro fuente esta libre
                if(revisarRegistros(RF1,RF2)){

                    // En caso de que ambos esten libres, entonces
                    // le agrego un "candado" al destino
                    memoria.vecRegMaquina[RD][1] ++;
                }
                else{

                    // Si los registros no estan libres, tengo
                    // un conflicto de datos, por lo que no 
                    // puedo avanzar, por ende marco el error
                    // en los dos vectores adyacentes
                    memoria.vecIF_ID[6] = 1;
                    memoria.vecID_EX[8] = 1;
                }

                break;

                case 37:  //sw

                case 52:  //sc

                case 99:  //beq

                case 100: //bne

                // Obtengo los valores de la instruccion
                RF1 = IR[1];
                RF2 = IR[2];
                Inm = IR[3];
                NPC = memoria.vecIF_ID[4];

                // Verifico si el registro fuente esta libre
                if(!revisarRegistros(RF1,RF2)){

                    // Si los registros no estan libres, tengo
                    // un conflicto de datos, por lo que no 
                    // puedo avanzar, por ende marco el error
                    // en los dos vectores adyacentes
                    memoria.vecIF_ID[6] = 1;
                    memoria.vecID_EX[8] = 1;
                }
                break;

                case 999: // fin

                case 000: //NOPE (para cambio de contexto)          

                    for(int i = 0; i < 4; ++i){
    
                        IR[i] = 0;
                        
                    }
                
                    break;
                
                default:  System.out.println("No se reconoce la instrucción");
                break;
            }

            //Llego a la primera barrera
            try {
                barrera1.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            } 

            //Evaluo si hay error. Ambos en 0 sigifica que no hay.
            if(memoria.vecID_EX[8] == 0 && memoria.vecID_EX[9] == 0){

                // Copio los valores al vector intermedio
                // Copio la instruccion
                for(int i = 0; i < 4; ++i){

                    memoria.vecID_EX[i] = IR[i];
                }

                // Copio el NPC
                memoria.vecID_EX[4] = NPC;
                //Copio el A
                memoria.vecID_EX[5] = memoria.vecRegMaquina[RF1][0];
                // Copio el B
                memoria.vecID_EX[6] = memoria.vecRegMaquina[RF2][0];
                // Copi el inmediato
                memoria.vecID_EX[7] = Inm;

            }
            // si hay error y necesito matar la instruccion
            if(memoria.vecID_EX[8] == -1){
            
                for(int i = 0; i < 4; ++i){

                    memoria.vecID_EX[i] = 0;
                }

                // Copio el NPC
                memoria.vecID_EX[4] = 0;
                //Copio el A
                memoria.vecID_EX[5] = 0;
                // Copio el B
                memoria.vecID_EX[6] = 0;
                // Copi el inmediato
                memoria.vecID_EX[7] = 0;
            }

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
    public void hiloEX() {
        while(!finHilillos()){

            int IR [] = new int [4];
            int NPC = 0;
            int A = 0;
            int B = 0;
            int Inm = 0;
            int RD = 0;
            int AluOut = 0;

            // Obtengo la instruccion
            for(int a=0; a<4; a++){

                IR[a] = memoria.vecID_EX[a];
            }
            //Decodifica la instrución
            switch (IR[0]){

                case 5 :  //lw

                case 19:  //addi

                    // Sumo el valor del registro fuente con el inmediato
                    AluOut = memoria.vecID_EX[5] + memoria.vecID_EX[7];

                    break;

                case 51: //lr
                    
                    // Guardo el valor del registro fuente para acceder
                    // a memoria.
                    AluOut = memoria.vecID_EX[5];
                
                    break;

                case 103: // jalr
                
                    // guardo el valor del PC para ponerlo en el registro
                    // destino
                    AluOut = memoria.PC;
                    // cargo el PC nuevo 
                    memoria.PC = memoria.vecID_EX[5] + memoria.vecID_EX[7];
                    // asigno en error para eliminar las instrucciones 
                    // anteriores ya que hay que matarlas
                    memoria.vecIF_ID[5] = -1;
                    memoria.vecID_EX[8] = -1;
                    
                    break;
                
                case 111: // jal
                    
                    // guardo el valor del PC para ponerlo en el registro
                    // destino
                    AluOut = memoria.PC;    
                    
                    // cargo el PC nuevo 
                    memoria.PC += memoria.vecID_EX[7];
                    // asigno en error para eliminar las instrucciones 
                    // anteriores ya que hay que matarlas
                    memoria.vecIF_ID[5] = -1;
                    memoria.vecID_EX[8] = -1;
                    
                    break;

                case 71:  //add
                
                    //ejecuto la suma
                    AluOut = memoria.vecID_EX[5] + memoria.vecID_EX[6];
                    
                    break;

                case 83:  //sub
                
                    //ejecuto la resta
                    AluOut = memoria.vecID_EX[5] - memoria.vecID_EX[6];
                    
                    break;

                case 72:  //mul
                
                    //ejecuto la multiplicación
                    AluOut = memoria.vecID_EX[5] * memoria.vecID_EX[6];
                    
                    break;

                case 56:  //div
                
                    //ejecuto la división
                    AluOut = memoria.vecID_EX[5] / memoria.vecID_EX[6];
                    
                    break;

                case 37:  //sw
                
                    //calculo la dirección de memoria con el inmediato 
                    // y el registro fuente 1
                    AluOut = memoria.vecID_EX[7] + memoria.vecID_EX[5];

                case 52:  //sc
                
                    // comparo el RL con el registro fuente 1
                    if(memoria.vecRegMaquina[32][0] == memoria.vecID_EX[5]){
                        
                        // guardo la direccion de memoria para guardar
                        // el valor del registro fuente 2 y el mismo
                        // se mantiene
                        AluOut = 0 + memoria.vecID_EX[5];
                    }
                    else{
                        
                        // Si no, entonces guardo el valor que voy a 
                        //guardar en el registro fuente 2
                        AluOut = 0;
                    }
                
                    break;

                case 99:  //beq
                    
                   // comparo el registro fuente 1 con el 2
                    if(memoria.vecID_EX[5] == memoria.vecID_EX[6]){
                        
                        // cambio el PC
                        memoria.PC += memoria.vecID_EX[7] * 4;
                    } 
                
                    break;
                
                case 100: //bne
                    
                    // comparo el registro fuente 1 con el 2
                    if(memoria.vecID_EX[5] != memoria.vecID_EX[6]){
                        
                        // cambio el PC
                        memoria.PC += memoria.vecID_EX[7] * 4;
                    }
                    
                    break;

                case 999: // fin

                case 000: //NOPE (para cambio de contexto)   
                
                    for(int i = 0; i < 4; ++i){
    
                        IR[i] = 0;
                    }

                    break;
                
                default:  System.out.println("No se reconoce la instrucción");
                break;
            }

            //Llego a la primera barrera
            try {
                barrera1.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            } 

            //Evaluo si hay error. Ambos en 0 sigifica que no hay.
            if(memoria.vecEX_ME[5] == 0 && memoria.vecEX_ME[6] == 0){

                // Copio los valores al vector intermedio
                // Copio la instruccion
                for(int i = 0; i < 4; ++i){

                    memoria.vecEX_ME[i] = IR[i];
                }

                // Copio el NPC
                memoria.vecEX_ME[4] = AluOut;

            }

            try {
                barrera2.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }

        }

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
