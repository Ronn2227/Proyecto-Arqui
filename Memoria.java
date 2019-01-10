
/**
 * Esta clase posee todas las funcionalidades relacionadas con Memoria, y Cache 
 * 
 * @author Dina Arias B50656 
 * @version 1.0
 */
import java.util.*;

public class Memoria
{
    // Estructuras de Memoria

    //Memoria de Instruccion
    int menInst [] = new int[640] ;    
    //Cache de Instruccion 
    int cachInst [][] = new int[4][17];

    //Memoria de Datos
    int menDatos [] = new int[96]; 
    //Cache de Datos
    int cachDatos [][] = new int[6][4]; 

    //Vector de registros de maquina, x0...x31 y contemplando el registro RL
    int vecRegMaquina [][] = new int[33][2];

    //Tabla de contextos 
    int tabContextos[][] = new int[9][36];

    //Estructuras entre etapas
    //4 IR, NPC, RIz, RDer 
    int vecIF_ID [] = new int[7];
    //4 IR, NPC, A, B, Inm, RIz, RDer
    int vecID_EX [] = new int[10];
    //4 IR, NPC, ALU Output, RIz, RDer
    int vecEX_ME [] = new int[8];
    //4 IR, NPC, ALU Output, LMD, RIz, RDer
    int vecME_WB [] = new int[9];

    int posicion = 0;
    int PC = 0;
    int ciclos = 0;
    int hililloTemp = 1;
    boolean CC = false;

    /**
     * Constructor for objects of class Memoria
     */
    public Memoria(){
        inicializar();
    }

    /**
     * Inicializamos las estructuras
     * Tabla de contexto
     * 
     */
    public void inicializar(){
        //Se dice que no hay ningun hillillo en la tabla de contexto especificando su ID en -1
        for (int f = 0; f < 9; f++ ){
            tabContextos[f][0] = -1;
            for(int c =3; c< 37; c++){//llenamos el campo de cliclos y de registros con 0
                tabContextos[f][c]=0;

            }
        }

        //Se inicializa CI con campo de etiqueta en -1
        for(int c= 0; c<4; c++){
            cachInst[c][0]=-1;
        }    

        /*Inicializamos la memoria de datos con un 1 para simular.Esto
         * es para simular que todos los candados fueron puestos por el “hilillo principal
         */
        for(int i=0; i<96; i++){
            menDatos [i]=1;
        }
        //Se inicializa CD con etiqueta en -1
        for(int c = 0; c < 4; c++){
            cachDatos[4][c]=-1;
        }
        
        /*Se inicializan los registros de la máquina*/
        for(int c=0; c<33; c++){
        
            vecRegMaquina [c][0] = 0;
            vecRegMaquina [c][1] = 0;
        }
        
        for(int i = 0; i < 7; ++i){
            vecIF_ID [i] = 0;
            vecID_EX [i] = 0;
            vecEX_ME [i] = 0;
            vecME_WB [i] = 0;
        }
        vecME_WB [7] = 0;
        for(int i = 7; i < 10; ++i)
            vecID_EX [i] = 0;
    }

    /**
     * Este metodo llena la matriz de instrucciones,
     * segun cantidad de hilillos a procesar, cada vez que recibe un hilillo,
     * llena la tabla de contexto con su respectivo PC
     * 
     * @param  recibe instruc que es uno de los hilillos y 
     * hilillo que es el identificador de hilillo
     * @return     the sum of x and y 
     */
    public void llenarMatrizInstr(String instruc, int hilillo){
        String instruccion;
        int count=0;
        instruccion = instruc;

        llenarTabContexto(hilillo, posicion+384);
        String[] split = instruccion.split(" ");

        // System.out.println("Esta es la memoria: "+ instruccion);
        while (split.length > count)
        {
            menInst[posicion] = Integer.parseInt( split[count]);
            //System.out.println("Memoria: " + menInst[count]);
            count++;
            posicion++;  
        }

    }

    /**
     * Este método nos permite llenar la tabla de contextos
     * 
     * @param  recibe el identificador de hilillo y el PC de inicio
     */
    public void llenarTabContexto(int hilillo, int pc){
        tabContextos [hilillo][0]= hilillo;
        tabContextos [hilillo][1]= pc;
        //Aqui podriamos llenar tambien el campo de hora de entrada
        //los otros campos de este hilillo ya se habian llenado con 0 en el constructor
    }

    /**
     * Metodo para imprimir tabla de Contexto, de ser necesario
     * 
     */
    public void imprimirTabContexto(){
        System.out.println("Hilo\tPC\tHora\tCiclo\tx0\tx1\tx2\tx3\tx4\tx5\tx6\tx7\tx8\tx9\tx10\tx11\tx12\tx13\tx14\tx15\tx16\tx17\tx18\tx19\tx20\tx21\tx22\tx23\tx24\tx25\tx26\tx27\tx28\tx29\tx30\tx31\tLR");
        for(int f =0; f< 9; f++){
            for(int c = 0; c< 37; c++){
                System.out.print(tabContextos[f][c]+ "\t"); 
            }
            System.out.println("");
        }
    }

    /**
     * Método que obtiene la instrucción que voy a ejecutar y revisa
     * si hay fallo de cache. En caso de que exista el fallo, se representa
     * el retraso al sumarle 48 ciclos a la variable ciclos
     */
    public int[] obtenerInstruccion(int PC)
    {
        // Obtengo el numero de bloque donde esta la instruccion
        int numBloque = PC/16;
        // Obtengo el # de bloque de cache donde debo poner el bloque de meoria
        int posCache = numBloque % 4;
        // Obtengo el # de palabra que necesito
        int posBloque = (PC % 16) / 4;
        // Defino el arreglo donde voy a guardar las instruccion
        int instr [] = new int [4];
        
        // Pregunto si el bloque que necesito está en cache
        if(cachInst[posCache][0] != numBloque){
            
            // poner el tag del bloque en cache
            cachInst[posCache][0] = numBloque;
            
            // Meter a cache el bloque desde memoria
            for(int i =0; i < 16; ++i){
                cachInst[posCache][i+1] = menInst[((numBloque - 24)*16) + i];
            }
            
            // Agrego los ciclos al contador de retraso para simular el fallo
            // de cache
            ciclos += 48;
        }
        
        // Cargo en la variable designada la instruccion que necesito
        for(int i =0; i < 4; ++i){
            instr[i] = cachInst[posCache][(posBloque*4)+i+1];
        }
        return instr;
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public int obtenerDato(int pos)
    {
        // Obtengo el numero de bloque donde esta la instruccion
        int numBloque = pos/16;
        // Obtengo el # de bloque de cache donde debo poner el bloque de meoria
        int posCache = numBloque % 4;
        // Obtengo el # de palabra que necesito
        int posBloque = (pos % 16) / 4;
        
        int dato = 0;
        
        if(cachDatos[0][posCache] != numBloque){
            moverACacheD(numBloque, posCache);
        }
        
        dato = cachDatos[posBloque+2][posCache];
        return dato;
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void escribirDato(int pos, int dato)
    {
        // Obtengo el numero de bloque donde esta la instruccion
        int numBloque = pos/16;
        // Obtengo el # de bloque de cache donde debo poner el bloque de meoria
        int posCache = numBloque % 4;
        // Obtengo el # de palabra que necesito
        int posBloque = (pos % 16) / 4;
        
        if(cachDatos[0][posCache] != numBloque){
            moverACacheD(numBloque, posCache);
        }
        
        cachDatos[posBloque+2][posCache] = dato;
    }

    
    /**
     * Metodo que se encarga de resolver el conflicto de cache de instrucciones
     */
    public void moverACacheD(int bloque, int pos){
        if(cachDatos[1][pos] == 1){
            for(int i = 0; i < 4; ++i){
                menDatos[cachDatos[0][pos]*4 + i] = cachDatos[i+2][pos];
            }
            ciclos+=48;
        }
        cachDatos[0][pos] = bloque;
        cachDatos[1][pos] = 0;
        for(int i = 0; i < 4; ++i){
             cachDatos[i+2][pos] = menDatos[bloque*4 + i];
        }
        ciclos+=48;
    }
    
    /**
     * Cuenta la cantidad de Hilillos corriendo
     *
     * @return    la cantidad de hilos
     */
    public int cantHilillos()
    {
        int respuesta = 0;
        for(int i = 0; i < 9; ++i){
            if(tabContextos[i][0] != -1){
                respuesta++;
            }else{
                break;
            }
        }
        return respuesta;
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y
     */
    public boolean noFinHilillos()
    {
        boolean respuesta = true;
        if(tabContextos[0][1] == 1 && tabContextos[1][1] == 1 && tabContextos[2][1] == 1
           && tabContextos[3][1] == 1 && tabContextos[4][1] == 1 && tabContextos[5][1] == 1
           && tabContextos[6][1] == 1 && tabContextos[7][1] == 1 && tabContextos[8][1] == 1){
               respuesta = false;
        }
        return respuesta;
    }
}
