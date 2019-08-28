package baskentdogalgaz.com.tr.baskentgazmobil.KacakGazTaramaProjectClasses.Factories;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.List;
import java.util.Set;

import baskentdogalgaz.com.tr.baskentgazmobil.AppMainClasses.Uygulama;

/**
 * Created by vahit.durmus on 18.02.2019.
 */

public class BluetoothFactory {

    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private BluetoothDevice pmdBluetoothDevice;
    private boolean pairingsuccess=false;
    private IntentFilter filter;
    private String bluetoothDeviceName;

    /**
     * the broadcastreceiver is to making pair with specific bluetooth device.
     */
    public final BroadcastReceiver receiver=new BroadcastReceiver() {
        ProgressDialog dialog;
        @Override
        public void onReceive(Context context, Intent intent) {

           try {
               String action = intent.getAction();

               switch (action){
                   case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                       dialog=new ProgressDialog(context);
                       dialog.setTitle("Cihaz Eşleştirme");
                       dialog.setMessage("Sensit PMD Cihazı aranıyor...");
                       dialog.show();
                       break;
                   case BluetoothDevice.ACTION_FOUND:
                       BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                       if(device.getName().matches(getBluetoothDeviceName())){
                           boolean x= device.createBond();
                           setPairingsuccess(x);
                       }
                       break;
                   case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                       if(isPairingsuccess()){
                           Uygulama.MesajGoster(mContext, "BİLGİ", "Sensit PMD Cihazı bulunmuştur.", null);
                       }
                       else{
                           Uygulama.MesajGoster(mContext, "BİLGİ", "Sensit PMD Cihazı bulunamamıştır.", null);
                       }
                       stopDiscovery();
                       dialog.cancel();
                       break;
                   default:
                       break;
               }
           }
           catch (Exception e){
               e.printStackTrace();
           }
        }
    };


    public BluetoothFactory(BluetoothAdapter bluetoothAdapter, Context context,String bluetoothDeviceName){
        setmContext(context);
        setmBluetoothAdapter(bluetoothAdapter);
        setPairingsuccess(false);
        setBluetoothDeviceName(bluetoothDeviceName);
        setPMDDeviceFromPairedDevices();
    }

    private void setBluetoothDeviceName(String bluetoothDeviceName){
        this.bluetoothDeviceName=bluetoothDeviceName;
    }

    private String getBluetoothDeviceName(){
        return this.bluetoothDeviceName;
    }

    private void setPmdBluetoothDevice(BluetoothDevice device){
        this.pmdBluetoothDevice=device;
    }

    public BluetoothDevice getPmdBluetoothDevice(){
        return this.pmdBluetoothDevice;
    }

    private void setPairingsuccess(boolean status){
        this.pairingsuccess=status;
    }

    private boolean isPairingsuccess(){
        return  this.pairingsuccess;
    }

    /**
     * gives back whethet Bluetooth is enabled
     * @return
     * @throws NullPointerException
     */

    public boolean isAdapterEnabled() throws NullPointerException{
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * gives back whethet sensit pmd is paired with device
     * @return
     */
    public boolean isPairedPMDDevice(){
        return getPmdBluetoothDevice()==null?false:true;
    }

    /**
     * setting to default BluetoothAdapter
     * @param bluetoothAdapter
     */
    private void setmBluetoothAdapter(BluetoothAdapter bluetoothAdapter){
        this.mBluetoothAdapter=bluetoothAdapter;
    }

    private void setmContext(Context context){
        this.mContext=context;
    }
    /**
     * is used to enable Bluetooth feature of device if it is exist.
     */
    public void setBluetoothAdapterEnable(){
        Intent enableBluetooh=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity)(mContext)).startActivityForResult(enableBluetooh,0);
    }
    /**
     * the method is to scan enabled bluetooth devices
     */
    public void startBluetoothDevicesScan(){
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ((Activity)(mContext)).registerReceiver(receiver, filter);
        startDiscovery();
    }
    /**
     * start discovery to find enabled bluetooth devices around.
     */
    private void startDiscovery(){
        if (mBluetoothAdapter!=null){
            if(!mBluetoothAdapter.isEnabled()){
                setBluetoothAdapterEnable();
            }
            else
                mBluetoothAdapter.startDiscovery();
        }
    }
    /**
     * stop discovery to find enabled bluetooth devices around.
     */
    private void stopDiscovery(){
        if (mBluetoothAdapter!=null && mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            mContext.unregisterReceiver(receiver);
        }
    }
    /**
     * gives pmd bluetooth devices if it exist or null
     * @return BluetoothDevice
     */
    private void setPMDDeviceFromPairedDevices(){
        try {
            pmdBluetoothDevice=null;
            Set<BluetoothDevice> pairedDevices=mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device:pairedDevices){
                if (device.getName().matches(getBluetoothDeviceName()))
                    setPmdBluetoothDevice(device);
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}