package com.pgpsvoz;

/**
 * pgpsvoz: Basic app with ASR using a RecognizerIntent
 * 
 * Simple demo in which the user speaks and the recognition results.
 * The result will be used to set location
 * 
 * Sample based on  Zoraida Callejas's & Michael McTear asrwithintent sample
 * 
 * @author Julio Rodríguez Martínez
 * @author Javier Escobar Cerezo
 * @version 0.1
 *
 */

import java.util.ArrayList;

import com.pgpsvoz.R;

import android.app.Activity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Build;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class Main_pgpsvoz extends Activity 
{
	
	// Default values for the language model and maximum number of recognition results
	// They are shown in the GUI when the app starts, and they are used when the user selection is not valid
	private final static int DEFAULT_NUMBER_RESULTS = 10;
	private final static String DEFAULT_LANG_MODEL = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM; 
	private int numberRecoResults = DEFAULT_NUMBER_RESULTS; 
	private String languageModel = DEFAULT_LANG_MODEL;
	private static final String LOGTAG = "ASRBEGIN";
	private static int ASR_CODE = 123;
	//String con la longitud y la laitud corregidas
	private String latitud="latitud";
	private String longitud="longitud";
	
	//Arraylist coordenadas
	ArrayList<String> coordenadas = new ArrayList<String>();
	
	//Cerrojos
	private boolean b_latitud;
	private boolean b_longitud;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_pgpsvoz);
		//Shows in the GUI the default values for the language model and the maximum number of recognition results
		setSpeakButtonLongitud();
		setSpeakButtonLatitud();
		coordenadas.add(longitud);
		coordenadas.add(latitud);
		coordenadas.add("0.0");
		coordenadas.add("-0.0");
		
	}
	
	private void listen()  
	{
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// Specify language model
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, languageModel);
		// Specify how many results to receive
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, numberRecoResults);  
		// Start listening
		startActivityForResult(intent, ASR_CODE);
    }
	
	/**
	 * Sets up the listener for the button that the user
	 * must click to start talking. Boton Latitud
	 */
	@SuppressLint("DefaultLocale")
	private void setSpeakButtonLatitud() 
	{
	Button speak= (Button) findViewById(R.id.latitud);
	speak.setOnClickListener(new View.OnClickListener() 
	{
			@Override
			public void onClick(View v) {
				//Speech recognition does not currently work on simulated devices,
				//it the user is attempting to run the app in a simulated device
				//they will get a Toast
				if("generic".equals(Build.BRAND.toLowerCase())){
					Toast toast = Toast.makeText(getApplicationContext(),"ASR is not supported on virtual devices", Toast.LENGTH_SHORT);
					toast.show();
					Log.d(LOGTAG, "ASR attempt on virtual device");						
				}
				else{
					numberRecoResults = 2;  //Cogemos sólo dos resultados
					listen(); 				//Set up the recognizer with the parameters and start listening
					b_latitud=true;
				}
			}
		});
	}
	
	/**
	 * Sets up the listener for the button that the user
	 * must click to start talking. Boton Longitud
	 */
	@SuppressLint("DefaultLocale")
	private void setSpeakButtonLongitud() 
	{
		//Gain reference to speak button
		Button speak = (Button) findViewById(R.id.longitud);
		

		//Set up click listener
		speak.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				//Speech recognition does not currently work on simulated devices,
				//it the user is attempting to run the app in a simulated device
				//they will get a Toast
				if("generic".equals(Build.BRAND.toLowerCase()))
				{
					Toast toast = Toast.makeText(getApplicationContext(),"ASR is not supported on virtual devices", Toast.LENGTH_SHORT);
					toast.show();
					Log.d(LOGTAG, "ASR attempt on virtual device");						
				}
				else{
					numberRecoResults = 2;  //Cogemos sólo dos resultados
					listen(); 				//Set up the recognizer with the parameters and start listening
					b_longitud = true;
				}
			}
		});
			
		
	}
	
	/**
	 *  Shows the formatted best of N best recognition results (N-best list) from
	 *  best to worst in the <code>ListView</code>. 
	 *  For each match, it will render the recognized phrase and the confidence with 
	 *  which it was recognized.
	 */
	@SuppressLint("InlinedApi")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ASR_CODE)  {
            if (resultCode == RESULT_OK)  {            	
            	if(data!=null) {
	            	//Retrieves the N-best list and the confidences from the ASR result
	            	ArrayList<String> nBestList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	            	float[] nBestConfidences = null;
	            	
	            	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)  //Checks the API level because the confidence scores are supported only from API level 14
	            		nBestConfidences = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
	            	
					//Creates a collection of strings, each one with a recognition result and its confidence
	            	//following the structure "Phrase matched (conf: 0.5)"
					ArrayList<String> nBestView = new ArrayList<String>();
					
					for(int i=0; i<nBestList.size(); i++){
						if(nBestConfidences!=null){
							if(nBestConfidences[i]>=0)
								nBestView.add(nBestList.get(i));
							else
								nBestView.add(nBestList.get(i));
						}
						else
							nBestView.add(nBestList.get(i) + " (no confidence value available)");
					}
					
					//Includes the collection in the ListView of the GUI
					setListView(nBestView);	
					Log.i(LOGTAG, "There were : "+ nBestView.size()+" recognition results");
            	}
            }
            else {       	
	    		//Reports error in recognition error in log
	    		Log.e(LOGTAG, "Recognition was not successful");
            }
        }
	}
	
	private float tranforma_grados_a_int(String coord)
	{
		float dec_coord;
		String[] grados=coord.split("º");
		String[] minutos=grados[1].split("'");
		String[] segundos=minutos[1].split("''");
		
		float f_grados=Float.parseFloat(grados[0]);
		float f_minutos=(Float.parseFloat(minutos[0])/60);
		float f_segundos=(Float.parseFloat(segundos[0])/3600);
	
		dec_coord=f_grados+f_minutos+f_segundos;
		if(coord.contains("W"))
		{
			dec_coord=dec_coord*(-1);
		}
		return dec_coord;
		
		
	}
	private String transforma_coordenadas(String cadena)
	{
		String aux;
		//Cadena sin espacio
		aux=cadena.replaceAll("coma", ".");
		aux=aux.replaceAll("con",".");
		aux=aux.replaceAll(",",".");
		aux=aux.replaceAll("norte","N");
		aux=aux.replaceAll("oeste", "W");
		aux=aux.replaceAll("grados", "º");
		aux=aux.replaceAll("grado", "º");
		aux=aux.replaceAll("minutos", "'");
		aux=aux.replaceAll("minuto", "'");
		aux=aux.replaceAll("segundos", "''");
		aux=aux.replaceAll("segundo", "''");
		aux=aux.replaceAll(" " ,"");
		aux=aux.trim();
		return aux;
	}
	
	/**
	 * Includes the recognition results in the list view
	 * @param nBestView list of matches
	 */
	private void setListView(ArrayList<String> nBestView)
	{
			if(b_longitud==true)
			{
				// Instantiates the array adapter to populate the listView
				//Transformar coordenadas al formato estandar
				longitud=transforma_coordenadas(nBestView.get(1));
				coordenadas.set(0, longitud);
				coordenadas.set(2,Float.toString(tranforma_grados_a_int(longitud)));
				
			
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, coordenadas);
				ListView listView = (ListView) findViewById(R.id.lista_coordenadas);
				listView.setAdapter(adapter);
	    	
				b_longitud=false;
			}
			else
			{
				if(b_latitud==true)
				{
					//Transformar coordenadas al formato estandar
					latitud=transforma_coordenadas(nBestView.get(0));
					coordenadas.set(1, latitud);
					coordenadas.set(3,Float.toString(tranforma_grados_a_int(latitud)));
				
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, coordenadas);
				
					ListView listView = (ListView) findViewById(R.id.lista_coordenadas);
					listView.setAdapter(adapter);
					b_longitud=false;
			    
				}
			}
		
		//Comprobación para lanzar el activity
		if(coordenadas.get(0)!= "longitud" && coordenadas.get(1)!="latitud")
		{
			//Carga el activity al tener la latitud y longitud correctas
			Intent i = new Intent(this, Main_navegacion.class);
			i.putExtra(coordenadas.get(2),coordenadas.get(3));
			//reseteo
			coordenadas.set(0,"longitud");
			coordenadas.set(1,"latitud");
			coordenadas.set(3,"0.0");
			coordenadas.set(4,"-0.0");
		//	startActivity(i);
		}
	}	
}


