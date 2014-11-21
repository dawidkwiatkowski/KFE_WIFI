// Copyright 2011 Google Inc. All Rights Reserved.

package com.app.kfe.wifi;

import android.R.bool;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.app.kfe.R;
import com.app.kfe.rysowanie.PaintView;
import com.app.kfe.rysowanie.Tablica;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

    private static final int SOCKET_TIMEOUT = 10000;
    public static final String EXTRAS_CANVAS = "CANVAS";
    public static final String ACTION_SEND_FILE = "com.app.kfe.Wifi.SEND_FILE";
    public static final String ACTION_SEND_TEXT = "com.app.kfe.Wifi.SEND_TEXT";
    public static final String ACTION_SEND_CANVAS = "com.app.kfe.Wifi.SEND_CANVAS";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    static Socket socket;
    static Boolean czy_tak=false;
    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {
                Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream is = null;
                try {
                    is = cr.openInputStream(Uri.parse(fileUri));
                } catch (FileNotFoundException e) {
                    Log.d(WiFiDirectActivity.TAG, e.toString());
                }
                DeviceDetailFragment.copyFile(is, stream);
                Log.d(WiFiDirectActivity.TAG, "Client: Data written");
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
        else if (intent.getAction().equals(ACTION_SEND_TEXT)) {
        	String text = "Dupa";
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {
                Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();
//                ContentResolver cr = context.getContentResolver();
                InputStream is = null;
                
                is = new ByteArrayInputStream(text.getBytes());
                
                DeviceDetailFragment.copyFile(is, stream);
                Log.d(WiFiDirectActivity.TAG, "Client: Data written");
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        else if (intent.getAction().equals(ACTION_SEND_CANVAS)) {
        	String text = "Dupa";
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            
//            byte[] canvasInByte = intent.getExtras().getByteArray(EXTRAS_CANVAS);
            PaintView pv = ((PaintView) Tablica.tablica.findViewById(R.id.drawing));
            Canvas canvas = null;
            
            canvas = pv.getDrawCanvas();
//         
//          
          pv.setDrawingCacheEnabled(true);
          Bitmap obrazek = pv.getDrawingCache();
          
          ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
          obrazek.compress(Bitmap.CompressFormat.PNG, 100, streamOut);
  		 byte[] yourBytes = streamOut.toByteArray();
          
          
          
          pv.destroyDrawingCache();

//          byte[] yourBytes = null;
//          
//          int bytes = obrazek.getByteCount();
//
//
//			 ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
//			 obrazek.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
//			 yourBytes = buffer.array(); //Get the underlying array containing the data.
          	
          	     socket = new Socket();
          	     
          	
            
            if( canvas != null)
            	text = "Dostano kanwe";
            else
            	text = "kanva = null";

            try {
                Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
                if(!socket.isConnected())
                {
                	socket.bind(null);
                	socket.connect((new InetSocketAddress(host, port)),SOCKET_TIMEOUT);
                }
               

                Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();
//                ContentResolver cr = context.getContentResolver();
                InputStream is = null;
                
                is = new ByteArrayInputStream(yourBytes);
                
                DeviceDetailFragment.copyFile(is, stream);
                Log.d(WiFiDirectActivity.TAG, "Client: Data written");
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
            } 
            finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
