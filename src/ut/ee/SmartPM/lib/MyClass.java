package ut.ee.SmartPM.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MyClass implements LibInterface{

	private final String libName = "Arduino";
	private final String libType = "String";
	List<rulesObject<Double, Double, String>> volList = new ArrayList<rulesObject<Double, Double, String>>();
	String sensor_type = "";
	
	private static final String TAG = "ArduinoBT";
	  
	int readBufferPosition;
	byte[] readBuffer;
	Thread workerThread;
    private TextView mAutoLabel;
    private Context context;
    
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = new ByteArrayInputStream( "null".getBytes() );
    
    // Well known SPP UUID
    private static final UUID MY_UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Insert your bluetooth devices MAC address
    private static ArrayList<String> addresses = new ArrayList<String>();
	    
   	@Override
	public String useMyLib(Context context, TextView mAutoLabel, String rules) {
   		this.mAutoLabel = mAutoLabel;
   		this.context = context;
		new parseXML(volList, sensor_type).execute(rules);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	    checkBTState();
	    
	    // add MAC addresses of trusted Arduino BT devices here! 
	    // TODO: Dynamic device selection!
	    addresses.add("00:0E:EA:CF:18:AB");
	    addresses.add("00:0E:EA:CF:16:FD");
	    
		//private static String address = "00:0E:EA:CF:16:FD";
		for (int i = 0; i < addresses.size(); i++){
		    	// Set up a pointer to the remote node using it's address.
			iterate: {
			    BluetoothDevice device = btAdapter.getRemoteDevice(addresses.get(i));
			    Log.d(TAG, addresses.get(i));
			    Log.d(TAG, device.toString());
			    
			    // Two things are needed to make a connection:
			    //   A MAC address, which we got above.
			    //   A Service ID or UUID.  In this case we are using the
			    //     UUID for SPP.
			    try {
			      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
			    } catch (IOException e) {
				    Log.d(TAG, "E1");
				    break iterate;
			      //errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
			    }
			  
			    // Discovery is resource intensive.  Make sure it isn't going on
			    // when you attempt to connect and pass your message.
			    btAdapter.cancelDiscovery();
			  
			    // Establish the connection.  This will block until it connects.
			    Log.d(TAG, "...Connecting to Remote...");
			    try {
			      btSocket.connect();
			      Log.d(TAG, "...Connection established and data link opened...");
			    } catch (IOException e) {
			      try {
			        btSocket.close();
					Log.d(TAG, "E2");
			        break iterate;
			      } catch (IOException e2) {
					  Log.d(TAG, "E3");
					  break iterate;
			        //errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
			      }
			    }
			    
			    // Create a data stream so we can talk to server.
			    Log.d(TAG, "...Creating Socket...");
	
			    try {
			      outStream = btSocket.getOutputStream();
			    } catch (IOException e) {
				    Log.d(TAG, "E4");
				    break iterate;
			      //errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
			    }
			    Log.d(TAG, "BREAK");
			    break;
			    
		    }
	    }
	    
	    beginListenForData();
		
		return null;
   	}

	private void updateDisplay(String status) {
		Boolean isListed = false;
		sensor_type = volList.get(0).getName();
//		Log.d("Sensor type", sensor_type + ".");
		String[] statusList = status.trim().split("=");
//		Log.d("statuslist", statusList[0] + " and " + statusList[1] + " and ." + sensor_type + ".");
		if((statusList[0].toString()).equals(sensor_type)){
			for (rulesObject<Double, Double, String> rulesObject : volList) {
				if ((Double.parseDouble(statusList[1]) > rulesObject.getLow()) && (Double.parseDouble(statusList[1]) < rulesObject.getHigh())) {
					mAutoLabel.setText(rulesObject.getName());
					isListed = true;
					return;
				}
			}
		    if(!isListed){
		    	String outVol = "NotMapped level: " + status;
		    	mAutoLabel.setText(outVol);
		    }
		    
		    mAutoLabel.setText(String.valueOf(status));
		}
	    	
	        
	}

	@Override
	public String getName() {
		return libName;
	}

	@Override
	public String getType() {
		return libType;
	}
	
	private void checkBTState() {
	    // Check for Bluetooth support and then check to make sure it is turned on

	    // Emulator doesn't support Bluetooth and will return null
	    if(btAdapter==null) { 
	      errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
	    } else {
	      if (btAdapter.isEnabled()) {
	        Log.d(TAG, "...Bluetooth is enabled...");
	      } else {
	    	errorExit("Fatal Error", "Bluetooth Not available. Aborting.");
	        //Prompt user to turn on Bluetooth
//	        Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
//	        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	      }
	    }
	  }
	
	private void errorExit(String title, String message){
	    Toast msg = Toast.makeText(context,
	        title + " - " + message, Toast.LENGTH_SHORT);
	    msg.show();
//	    finish();
	  }
	
	void beginListenForData()
	  {
		  try {
			inStream = btSocket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  Log.d(TAG, "...beginListenForData...");
	      final Handler handler = new Handler(); 
	      final byte delimiter = 10; //This is the ASCII code for a newline character
	      
	      readBufferPosition = 0;
	      readBuffer = new byte[1024];
	      workerThread = new Thread(new Runnable()
	      {
	          public void run()
	          {                
	        	  Log.d(TAG, "...beginListenForData running...");

	             while(!Thread.currentThread().isInterrupted())
	             {
	                  try 
	                  {
	                      int bytesAvailable = inStream.available();                        
	                      if(bytesAvailable > 0)
	                      {
	                          byte[] packetBytes = new byte[bytesAvailable];
	                          inStream.read(packetBytes);
	                          for(int i=0;i<bytesAvailable;i++)
	                          {
	                              byte b = packetBytes[i];
	                              if(b == delimiter)
	                              {
	                                  byte[] encodedBytes = new byte[readBufferPosition];
	                                  System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
	                                  final String data = new String(encodedBytes, "US-ASCII");
//	                                  
//	                            	  Log.d(TAG, data);

	                                  readBufferPosition = 0;
	                                  
	                                  handler.post(new Runnable()
	                                  {
	                                      public void run()
	                                      {
	                                    	  updateDisplay(data);
	                                      }
	                                  });
	                              }
	                              else
	                              {
	                                  readBuffer[readBufferPosition++] = b;
	                              }
	                          }
	                      }
	                  } 
	                  catch (IOException ex) 
	                  {
	                  }
	             }
	          }
	      });

	      workerThread.start();
	  }

}
