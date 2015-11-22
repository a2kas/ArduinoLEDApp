package com.led.led;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;


public class ledControl extends ActionBarActivity {

    Button btnOn, btnOff, btnDis;
    SeekBar brightness;
    TextView lumn;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    RadioButton device1Radio, device2Radio, device3Radio;
    private RadioGroup radioGroup;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Integer[] status = {0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_led_control);

        //call the widgtes
        btnOn = (Button)findViewById(R.id.button2);
        btnOff = (Button)findViewById(R.id.button3);
        btnDis = (Button)findViewById(R.id.button4);
        brightness = (SeekBar)findViewById(R.id.seekBar);
        lumn = (TextView)findViewById(R.id.lumn);
        device1Radio = (RadioButton) findViewById (R.id.radioButton);
        device2Radio = (RadioButton) findViewById (R.id.radioButton2);
        device3Radio = (RadioButton) findViewById (R.id.radioButton3);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        btnOff.setEnabled(false);

        new ConnectBT().execute(); //Call the class to connect


        //commands to be sent to bluetooth
        btnOn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                turnOnLed();      //method to turn on
                btnOn.setEnabled(false);
                btnOff.setEnabled(true);
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                turnOffLed();   //method to turn off
                btnOn.setEnabled(true);
                btnOff.setEnabled(false);
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    lumn.setText(String.valueOf(progress));
                    try {
                        btSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

                /* Attach CheckedChangeListener to radio group */
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    String selection = (String) rb.getText();
                    if(selection.equals("Device 1"))
                    {
                        if(status[0] == 0)
                        {
                            btnOn.setEnabled(true);
                            btnOff.setEnabled(false);
                        }
                        else
                        {
                            btnOn.setEnabled(false);
                            btnOff.setEnabled(true);
                        }
                    }
                    else if(selection.equals("Device 2"))
                    {
                        if(status[1] == 0)
                        {
                            btnOn.setEnabled(true);
                            btnOff.setEnabled(false);
                        }
                        else
                        {
                            btnOn.setEnabled(false);
                            btnOff.setEnabled(true);
                        }

                    }
                    else
                    {
                        if(status[2] == 0)
                        {
                            btnOn.setEnabled(true);
                            btnOff.setEnabled(false);
                        }
                        else
                        {
                            btnOn.setEnabled(false);
                            btnOff.setEnabled(true);
                        }
                    }
                }

            }
        });
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    private void turnOffLed()
    {
        if (btSocket!=null)
        {
            try
            {
                Integer led = 0;
                if(device1Radio.isChecked())
                {
                    status[0] = 0;  // Save status
                    led = 5;
                }
                else if(device2Radio.isChecked())
                {
                    status[1] = 0;  // Save status
                    led = 3;
                }
                else
                {
                    status[2] = 0;  // Save status
                    led = 6;
                }
                String command = "TF_" + led.toString();

                btSocket.getOutputStream().write(command.toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void turnOnLed()
    {
        if (btSocket!=null)
        {
            try
            {
                Integer led = 0;
                if(device1Radio.isChecked())
                {
                    status[0] = 1;  // Save status
                    led = 5;
                }
                else if(device2Radio.isChecked())
                {
                    status[1] = 1; // Save status
                    led = 3;
                }
                else
                {
                    status[2] = 1; // Save status
                    led = 6;
                }
                String command = "TO_" + led.toString();

                btSocket.getOutputStream().write(command.toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                 myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                 BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                 btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                 btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                //finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
