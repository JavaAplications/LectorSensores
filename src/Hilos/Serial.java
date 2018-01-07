package Hilos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.regex.*;

import BBDD.Conexion;

import com.fazecast.jSerialComm.SerialPort;

public class Serial extends Thread {
	
	Conexion conectar;
	SerialPort comPort;
	Hilo hilito;

	int tiempoPooling;//seg
	String dato;
	static boolean control=true;
	private String os;
	InputStream in;
	OutputStream out;
	StringBuffer txt = new StringBuffer(); 
	int contador =0;
	
	public Serial(int pooling, String sistemaOP) {
		
		conectar=new Conexion();
		this.tiempoPooling=pooling;
		this.os=sistemaOP;
		if(os.equals("w")){
			comPort = SerialPort.getCommPorts()[0];//windows
			
			System.out.println("Accediendo al puerto serial com[0] ...");
		}else{
			comPort = SerialPort.getCommPort("/dev/ttyS0");//linux
			System.out.println("Accediendo al puerto serial /dev/ttyS0 ...");
			
		}
	}

	//@Override
	public void run() {
		super.run();
		
		char char_dat = 0;
		StringBuffer txt = new StringBuffer(); 
	try {
			comPort.openPort();
	    //	comPort.setComPortTimeouts(SerialPort.LISTENING_EVENT_DATA_RECEIVED, 100, 0);
	    	comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
	    
	    	in = comPort.getInputStream();
			out = comPort.getOutputStream();
		} catch (Exception e) {
			System.out.println("Error serial: "+e);
		}
	
	
	int CantidadSensores=conectar.ConsultarCantidadSensores();
	System.out.println("> Cantidad Sensores:"+CantidadSensores);
	String content ;
	byte[] bytes ; 

	while(true){
			control=true;
			System.out.println("************ INICIA BLUCLE DE CONSULTAS ********************");
			System.out.println();
			for(int i=1;i<=CantidadSensores;i++){
				
				content=i+"\r";
				System.out.println("> Consulta al sensor N°:"+i+" "+conectar.ConsultarNombre(i));
			   	bytes=content.getBytes();		

				try {	
					control=true;
					out.write(bytes);
					try {Thread.sleep(350);
					}
				catch (InterruptedException e1) {
					e1.printStackTrace();
				}
					bytes=null;
					hilito=new Hilo(conectar.ConsultarNombre(i));
					hilito.start();
				}catch (IOException e) {
					System.out.print("> Error al escribir en el puerto");e.printStackTrace();
					System.out.println();
					}
			    control=true;
			    
			    while(control){					
					while(comPort.bytesAvailable() != 0){
						
						try {
								char_dat=(char)in.read();
					   	  		txt.append(char_dat);
					   		}catch (IOException e) {
					   			System.out.print("> Error al leer en el puerto");e.printStackTrace();
								System.out.println();
					   			}					      
						if((char_dat=='\n')){
							dato=txt.toString();
						
					        ProcesarMensaje();
						
					        txt.delete(0, txt.length());
					        control=false;
					    }
					}
		     	   }//while(control)
				control=true;	
				System.out.println();
				System.out.print("> Espera "+tiempoPooling+" seg");
				
				for(int c =0;c<tiempoPooling;c++){
					try {Thread.sleep(1000);
				      System.out.print(".");
						}
					catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				}
				   System.out.println();
				
			  }//for
		}//while
	}

	public void ProcesarMensaje(){//expresion regular para controlar caracteres ingresados.
		
		
		boolean valido=false;
		String[] parts ;
		String[] parteUno ;
		String[] partefinal ;
		int numParts=4;
		String id = null ; 
		int id_f = 0;
		int bal_f =0;
		String temp ; 
		String hum ; 
		String bal ;
		Calendar calendario = Calendar.getInstance();
		int hora = 0, minutos = 0, segundos = 0,dia = 0,mes = 0,ano = 0;
		float t_f = 0,h_f = 0;
		
		Pattern pat = Pattern.compile("\\d{1,3}\\;\\d{2}\\.\\d{2}\\;\\d{2}\\.\\d{2}\\;\\d{1,4}\\r\\n");
	   
		Matcher mat = pat.matcher(dato);
		
		if(mat.matches()){
			 System.out.print("> Dato valido:");
			 valido=true;
		}else{
			 System.out.print("> Dato Invalido:");
			 valido=false;
		 }
		System.out.println(dato);
	
		if (valido) {//comprueba caracteres validos
			
		try{	
			
			parts = dato.split(";");// separa por espacio debe ser ;
		    if(parts.length==numParts){// comprueba numero de datos separados
			
			    parteUno = parts[0].split("\r");// separa por espacio debe ser ;
				
				id = parteUno[0]; 
			
				temp = parts[1]; 
				hum = parts[2]; 
				partefinal=parts[3].split("\r");
				bal = partefinal[0];
					
				id_f=Integer.parseInt(id);
				t_f=Float.parseFloat(temp);
				h_f=Float.parseFloat(hum);
				bal_f= Integer.parseInt(bal);
				
				dia =calendario.get(Calendar.DAY_OF_MONTH);
				mes =calendario.get(Calendar.MONTH);
				ano =calendario.get(Calendar.YEAR);
				hora =calendario.get(Calendar.HOUR_OF_DAY);
				minutos = calendario.get(Calendar.MINUTE);
				segundos = calendario.get(Calendar.SECOND);
				System.out.print("> "+dia+"-"+mes+"-"+ano+" "+hora + ":" + minutos + ":" + segundos);
				System.out.println(" Id:"+id+"\t T:"+t_f+"\t H:"+h_f+"\t bal: "+bal_f);
				conectar.InsertarDato(id_f,t_f,h_f,bal_f,valido);
				System.out.println("> Dato almacenado.");
				
		    	}
			else{
		    	 System.out.println("no concuerda el numero de partes");
		    	
		    	}
		    
		 	}catch (Exception e) {
				System.out.println("Error comprueba caracteres validos "+e);
		 		}
			
		 }else{//dato invalido
			 
			 System.out.print("> "+dia+"-"+mes+"-"+ano+" "+hora + ":" + minutos + ":" + segundos);
			 System.out.println(" Id:"+id+"\t T:"+t_f+"\t H:"+h_f+"\t bal: "+bal_f);
			 conectar.InsertarDato(id_f,0,0,0,false);
			 
		 }
	}		
}
