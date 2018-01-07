package Hilos;


public class Hilo extends Thread{
	
	int seg=2;
	boolean control=true;
	static boolean controlHilo=false;
	private String nombre;
	
	public Hilo(String nombre) {
}

	//@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			//System.out.println("> # Esperando "+seg+" Seg");
			Thread.sleep(seg);
			Serial.control=false;
			//System.out.println("> # Finalizaron "+seg+"  Seg ");

		
			
			//System.out.println(nombre+": "+c+" seg.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
	
	
	}

	
}
