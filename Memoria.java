
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
    int cachInst [] = new int[68];

    //Memoria de Datos
    int menDatos [] = new int[96]; 
    //Cache de Datos
    int cachDatos [][] = new int[6][4]; 

    //Vector de registros de maquina, x0...x31 y contemplando el registro RL
    int vecRegMaquina [][] = new int[33][2];

    //Tabla de contextos 
    int tabContextos[][] = new int[9][37];

    //Estructuras entre etapas
    //4 IR, NPC, RIz, RDer 
    int vecIF_ID [] = new int[7];
    //4 IR, NPC, A, B, Inm, RIz, RDer
    int vecID_EX [] = new int[10];
    //4 IR, ALU Ourput, RIz, RDer
    int vecEX_ME [] = new int[7];
    //4 IR, ALU Output, LMD, RIz, RDer
    int vecME_WB [] = new int[8];

    int posicion=0;

    /**
     * Constructor for objects of class Memoria
     */
    public Memoria()
    {
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
            cachInst[c*17]=-1;
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
    public void llenarMatrizInstr(String instruc, int hilillo)
    {
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
}
