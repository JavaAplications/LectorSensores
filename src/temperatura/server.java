package temperatura;

import Hilos.Serial;

public class server {
	
	static Serial com;
    static int pooling=5;
    static String sistemaOP;
  
    public static void main(String[] args) {
    	 String sSistemaOperativo = System.getProperty("os.name");
    	 if (sSistemaOperativo.contains("indow")){
    		 
    		 sistemaOP="w";
		 }else{
			
			 sistemaOP="l";
		 }    
    	System.out.println();
    	System.out.println(sSistemaOperativo+" detectado");
    	System.out.println();
    	System.out.println("****************************************************");
        System.out.println("**********      INICIANDO SERVICIO       ***********");
        System.out.println("**********    NO CERRAR ESTA VENTANA     ***********");
        System.out.println("****************************************************");
        System.out.println();
		com=new Serial(pooling,sistemaOP);
		com.run();

}

	
	}


