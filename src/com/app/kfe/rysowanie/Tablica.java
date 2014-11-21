package com.app.kfe.rysowanie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

import com.app.kfe.R;
import com.app.kfe.wifi.DeviceDetailFragment;
import com.app.kfe.wifi.DeviceListFragment;
import com.app.kfe.wifi.WiFiDirectActivity;
import com.app.kfe.wifi.WiFiDirectBroadcastReceiver;
import com.app.kfe.wifi.DeviceListFragment.DeviceActionListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Toast;


public class Tablica extends Activity implements OnSeekBarChangeListener, OnClickListener, ChannelListener, DeviceActionListener {
	
	static PaintView paintView;
	private Button yellowButton;
	private Button greenButton;
	private Button blueButton;
	private Button redButton;
	private Button whiteButton;
	private Button blackButton;
	private Paint drawPaint;
	private Paint canvasPaint;
	private ImageButton saveButton;
	private ImageButton brushTool;
	private ImageButton lineTool;
	private ImageButton rectangleTool;
	private ImageButton squareTool;
	private ImageButton circleTool;
	private ImageButton triangleTool;
	private ImageButton eraserTool;
	private ImageButton newImageTool;
	private AlertDialog.Builder saveDialog;
	private AlertDialog.Builder newImageDialog;
	private int brushColor;	
	
	public static Tablica tablica = null;
	
	//czêœæ dla WIFI
	
		private ImageButton atn_direct_enable;
		private ImageButton atn_direct_discover;
	
		public static final String TAG = "wifidirectdemo";
	    private WifiP2pManager manager;
	    private boolean isWifiP2pEnabled = false;
	    private boolean retryChannel = false;
	
	    private final IntentFilter intentFilter = new IntentFilter();
	    private Channel channel;
	    private BroadcastReceiver receiver = null;
	
	//koniec czêœci dla WIFI

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tablica);			
		
		SlidingDrawer toolsPanel = (SlidingDrawer) findViewById(R.id.toolsPanel);
		final ImageButton handle = (ImageButton) findViewById(R.id.handle);		
		
		SlidingDrawer connectionPanel = (SlidingDrawer) findViewById(R.id.connctionPanel);
		final ImageButton handle2 = (ImageButton) findViewById(R.id.handle2);
		
		paintView = (PaintView) findViewById(R.id.drawing);
		paintView.setTablica(this);
		
		drawPaint = paintView.getDrawPaint();
		canvasPaint = paintView.getCanvasPaint();
		redButton = (Button) findViewById(R.id.redButton);
		yellowButton = (Button) findViewById(R.id.yellowButton);
		greenButton = (Button) findViewById(R.id.greenButton);
		blueButton = (Button) findViewById(R.id.blueButton);
		whiteButton = (Button) findViewById(R.id.whiteButton);
		blackButton = (Button) findViewById(R.id.blackButton);
		saveButton = (ImageButton) findViewById(R.id.saveButton);
		brushTool = (ImageButton) findViewById(R.id.brushTool);
		brushColor = drawPaint.getColor();
		lineTool = (ImageButton) findViewById(R.id.lineTool);
		rectangleTool = (ImageButton) findViewById(R.id.rectangleTool);
		squareTool = (ImageButton) findViewById(R.id.squareTool);
		circleTool = (ImageButton) findViewById(R.id.circleTool);
		triangleTool = (ImageButton) findViewById(R.id.triangleTool);
		eraserTool = (ImageButton) findViewById(R.id.eraserTool);
		newImageTool = (ImageButton) findViewById(R.id.newImageTool);
		
		redButton.setOnClickListener(this);
		yellowButton.setOnClickListener(this);
		blueButton.setOnClickListener(this);
		whiteButton.setOnClickListener(this);
		blackButton.setOnClickListener(this);
		greenButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		brushTool.setOnClickListener(this);
		lineTool.setOnClickListener(this);
		rectangleTool.setOnClickListener(this);
		squareTool.setOnClickListener(this);
		circleTool.setOnClickListener(this);
		triangleTool.setOnClickListener(this);
		eraserTool.setOnClickListener(this);
		newImageTool.setOnClickListener(this);
		
		SeekBar brashSize = (SeekBar) findViewById(R.id.brushSize);
		brashSize.setOnSeekBarChangeListener(this);
		
		toolsPanel.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
            	handle.setBackgroundResource(R.drawable.right);
            	paintView.setIsEnabled(false);
            }
        });
 
		toolsPanel.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
            	handle.setBackgroundResource(R.drawable.left);
            	paintView.setIsEnabled(true);
            }
        });
		
		connectionPanel.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
            	handle2.setBackgroundResource(R.drawable.left);
            	paintView.setIsEnabled(false);
            }
        });
 
		connectionPanel.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
            	handle2.setBackgroundResource(R.drawable.right);
            	paintView.setIsEnabled(true);
            }
        });
		
		saveDialog = new AlertDialog.Builder(this);
		saveDialog.setTitle("Zapis obrazka");
		saveDialog.setMessage("Czy zapisaæ obrazek do galerii?");
		saveDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				saveImage();
				dialog.cancel();
			}
		});
		saveDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		
		newImageDialog = new AlertDialog.Builder(this);
		newImageDialog.setTitle("Czyszczenie tablicy");
		newImageDialog.setMessage("Czy czy wyczyœciæ tablicê?");
		newImageDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				newImage();
				dialog.cancel();
			}
		});
		newImageDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		
		//czêœæ dla WIFI
			
			intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	
	        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	        channel = manager.initialize(this, getMainLooper(), null);
	        
	        atn_direct_discover = (ImageButton) findViewById(R.id.atn_direct_discover);
	        atn_direct_enable = (ImageButton) findViewById(R.id.atn_direct_enable);
	        atn_direct_discover.setOnClickListener(this);
			atn_direct_enable.setOnClickListener(this);
		
		//koniec czêœci dla wifi
			
		tablica = this;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
		drawPaint.setStrokeWidth((float) progress);
		canvasPaint.setStrokeWidth((float) progress);
		
		paintView.setDrawPaint(drawPaint);
		paintView.setCanvasPaint(canvasPaint);
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
			case R.id.redButton:
				drawPaint.setColor(Color.RED);
				canvasPaint.setColor(Color.RED);
				brushColor = drawPaint.getColor();
				break;
			case R.id.yellowButton:
				drawPaint.setColor(Color.YELLOW);
				canvasPaint.setColor(Color.YELLOW);
				brushColor = drawPaint.getColor();
				break;
			case R.id.greenButton:
				drawPaint.setColor(Color.GREEN);
				canvasPaint.setColor(Color.GREEN);
				brushColor = drawPaint.getColor();
				break;
			case R.id.blueButton:
				drawPaint.setColor(Color.BLUE);
				canvasPaint.setColor(Color.BLUE);
				brushColor = drawPaint.getColor();
				break;
			case R.id.whiteButton:
				drawPaint.setColor(Color.WHITE);
				canvasPaint.setColor(Color.WHITE);
				brushColor = drawPaint.getColor();
				break;
			case R.id.blackButton:
				drawPaint.setColor(Color.BLACK);
				canvasPaint.setColor(Color.BLACK);
				brushColor = drawPaint.getColor();
				break;			
			case R.id.saveButton:				
				saveDialog.show();
				break;
			case R.id.brushTool:
				setBrushTool();
				break;
			case R.id.lineTool:
				setLineTool();
				break;
			case R.id.rectangleTool:
				setRectangleTool();
				break;
			case R.id.squareTool:
				setSquareTool();
				break;
			case R.id.circleTool:
				setCircleTool();
				break;
			case R.id.triangleTool:
				setTriangleTool();
				break;
			case R.id.eraserTool:
				setEraserTool();
				break;
			case R.id.newImageTool:				
				newImageDialog.show();
				break;
			case R.id.atn_direct_enable:
                if (manager != null && channel != null) {

                    // Since this is the system wireless settings activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.

                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                } else {
                    Log.e(TAG, "channel or manager is null");
                }
                break;

            case R.id.atn_direct_discover:
	            if (!isWifiP2pEnabled) {
	                Toast.makeText(Tablica.this, R.string.p2p_off_warning,
	                        Toast.LENGTH_SHORT).show();
	            }
	            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
	                    .findFragmentById(R.id.frag_list);
	            fragment.onInitiateDiscovery();
	            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
	
	                @Override
	                public void onSuccess() {
	                    Toast.makeText(Tablica.this, "Discovery Initiated",
	                            Toast.LENGTH_SHORT).show();
	                }
	
	                @Override
	                public void onFailure(int reasonCode) {
	                    Toast.makeText(Tablica.this, "Discovery Failed : " + reasonCode,
	                            Toast.LENGTH_SHORT).show();
	                }
	            });
	            break;
		}		
		paintView.setDrawPaint(drawPaint);
		
	}
	
	
	
	public void saveImage(){
		paintView.setDrawingCacheEnabled(true);
		Bitmap stara = paintView.getDrawingCache();
		
//		int bytes = stara.getByteCount();
//
//
//		 ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
//		 stara.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stara.compress(Bitmap.CompressFormat.PNG, 100, stream);
		 byte[] yourBytes = stream.toByteArray();
		 
		 InputStream is = null;
         
        
        is = new ByteArrayInputStream(yourBytes);
        byte[] array = convertInputStreamToByteArray(is);
        
		Bitmap nowa = BitmapFactory.decodeByteArray(array , 0, array.length);
		
		String imgSaved = MediaStore.Images.Media.insertImage(
				getContentResolver(), nowa,
				UUID.randomUUID().toString()+".png", "drawing");
		
		if(imgSaved != null){
			Toast saveToast = Toast.makeText(getApplicationContext(), "Zapisano do galerii", Toast.LENGTH_SHORT);
			saveToast.show();
		}
		else{
			Toast unsavedToast = Toast.makeText(getApplicationContext(), "Wyst¹pi³ problem podczas zapisu", Toast.LENGTH_SHORT);
			unsavedToast.show();
		}
		
		paintView.destroyDrawingCache();
	}
	
	public static byte[] convertInputStreamToByteArray(InputStream inputStream)
	 {
	 byte[] bytes= null;
	 
	 try
	 {
	 ByteArrayOutputStream bos = new ByteArrayOutputStream();
	 
	 byte data[] = new byte[1024];
	 int count;
	 
	 while ((count = inputStream.read(data)) != -1)
	 {
	 bos.write(data, 0, count);
	 }
	 
	bos.flush();
	 bos.close();
	 inputStream.close();
	 
	bytes = bos.toByteArray();
	 }
	 catch (IOException e)
	 {
	 e.printStackTrace();
	 }
	 return bytes;
	 }
	
	public void setColor(){
		drawPaint.setColor(brushColor);
		canvasPaint.setColor(brushColor);
	}
	
	public void setBrushTool(){
		setColor();
		paintView.setMCurrentShape(paintView.SMOOTHLINE);
	}
	
	public void setLineTool(){
		setColor();
		paintView.setMCurrentShape(paintView.LINE);
	}
	
	public void setRectangleTool(){
		setColor();
		paintView.setMCurrentShape(paintView.RECTANGLE);
	}
	
	public void setCircleTool(){
		setColor();
		paintView.setMCurrentShape(paintView.CIRCLE);
	}
	
	public void setSquareTool(){
		setColor();
		paintView.setMCurrentShape(paintView.SQUARE);
	}
	
	public void setTriangleTool(){
		setColor();
		paintView.setMCurrentShape(paintView.TRIANGLE);
		paintView.resetTriangle();
	}
	
	public void setEraserTool(){
		drawPaint.setColor(Color.WHITE);	
		canvasPaint.setColor(Color.WHITE);
		paintView.setMCurrentShape(paintView.SMOOTHLINE);
	}
	
	public void newImage(){
		paintView.newImage();
	}
	
	//czêœæ dla WIFI
	
		/**
	     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
	     */
	    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
	        this.isWifiP2pEnabled = isWifiP2pEnabled;
	    }
	    
	    /** register the BroadcastReceiver with the intent values to be matched */
	    @Override
	    public void onResume() {
	        super.onResume();
	        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
	        registerReceiver(receiver, intentFilter);
	        
	        tablica = this;
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	        unregisterReceiver(receiver);
	        
	        tablica = null;
	    }

	    /**
	     * Remove all peers and clear all fields. This is called on
	     * BroadcastReceiver receiving a state change event.
	     */
	    public void resetData() {
	        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
	                .findFragmentById(R.id.frag_list);
	        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
	                .findFragmentById(R.id.frag_detail);
	        if (fragmentList != null) {
	            fragmentList.clearPeers();
	        }
	        if (fragmentDetails != null) {
	            fragmentDetails.resetViews();
	        }
	    }
	    
	    @Override
	    public void showDetails(WifiP2pDevice device) {
	        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
	                .findFragmentById(R.id.frag_detail);
	        fragment.showDetails(device);

	    }

	    @Override
	    public void connect(WifiP2pConfig config) {
	        manager.connect(channel, config, new ActionListener() {

	            @Override
	            public void onSuccess() {
	                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
	        
	            	
	            	
	            }

	            @Override
	            public void onFailure(int reason) {
	                Toast.makeText(Tablica.this, "Connect failed. Retry.",
	                        Toast.LENGTH_SHORT).show();
	            }
	        });
	    }

	    @Override
	    public void disconnect() {
	        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
	                .findFragmentById(R.id.frag_detail);
	        fragment.resetViews();
	        manager.removeGroup(channel, new ActionListener() {

	            @Override
	            public void onFailure(int reasonCode) {
	                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

	            }

	            @Override
	            public void onSuccess() {
	                fragment.getView().setVisibility(View.GONE);
	            }

	        });
	    }

	    @Override
	    public void onChannelDisconnected() {
	        // we will try once more
	        if (manager != null && !retryChannel) {
	            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
	            resetData();
	            retryChannel = true;
	            manager.initialize(this, getMainLooper(), this);
	        } else {
	            Toast.makeText(this,
	                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
	                    Toast.LENGTH_LONG).show();
	        }
	    }

	    @Override
	    public void cancelDisconnect() {

	        /*
	         * A cancel abort request by user. Disconnect i.e. removeGroup if
	         * already connected. Else, request WifiP2pManager to abort the ongoing
	         * request
	         */
	        if (manager != null) {
	            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
	                    .findFragmentById(R.id.frag_list);
	            if (fragment.getDevice() == null
	                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
	                disconnect();
	            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
	                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

	                manager.cancelConnect(channel, new ActionListener() {

	                    @Override
	                    public void onSuccess() {
	                        Toast.makeText(Tablica.this, "Aborting connection",
	                                Toast.LENGTH_SHORT).show();
	                    }

	                    @Override
	                    public void onFailure(int reasonCode) {
	                        Toast.makeText(Tablica.this,
	                                "Connect abort request failed. Reason Code: " + reasonCode,
	                                Toast.LENGTH_SHORT).show();
	                    }
	                });
	            }
	        }

	    }
		
	//koniec czêœci dla WIFI

}
