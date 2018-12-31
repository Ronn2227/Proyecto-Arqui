
/**
 * Este es el procesador que solo invoca al hilo principal
 * 
 * @author Diana Arias B50656 
 * @version 1.0
 */

import java.io.IOException;
public class Procesador
{
    public static void main(String[] args) throws IOException
    {
        String instrucciones;
        
        Hilo_Principal hPrincipal = new Hilo_Principal();
        hPrincipal.principal();
    }
}
