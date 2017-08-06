package es.pnia.espmote;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView t;
    NsdManager.DiscoveryListener mDiscoveryListener;

    public static final String TAG = MainActivity.class.getSimpleName();
    private NsdManager mNsdManager;

    public static final String SERVICE_TYPE = "_espmote._tcp.";

    private ListView listView;
    private List items;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display a indeterminate progress bar on title bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main);

        this.listView = (ListView) findViewById(es.pnia.espmote.R.id.list);

        items = new ArrayList();
        try {
            items.add(new Item(R.drawable.esp12e, "ESP-12E", InetAddress.getByName("127.0.0.1") ));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        items.add(new Item(R.drawable.esp12e, "Memento", "http://www.imdb.com/title/tt0209144/"));
//        items.add(new Item(R.drawable.esp12e, "Batman Begins", "http://www.imdb.com/title/tt0372784/"));
//        items.add(new Item(R.drawable.esp12e, "The Prestige", "http://www.imdb.com/title/tt0482571/"));
//        items.add(new Item(R.drawable.esp12e, "The Dark Knight", "http://www.imdb.com/title/tt0468569/"));
//        items.add(new Item(R.drawable.esp12e, "Inception", "http://www.imdb.com/title/tt1375666/"));
//        items.add(new Item(R.drawable.esp12e, "The Dark Knight Rises", "http://www.imdb.com/title/tt1345836/"));

        // Sets the data behind this ListView
        this.adapter = new ItemAdapter(this, items);
        this.listView.setAdapter(this.adapter);

        mNsdManager = (NsdManager) this.getSystemService(Context.NSD_SERVICE);

        initializeDiscoveryListener();
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            NsdServiceInfo foundServiceInfo;

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d(TAG, "Service discovery success " + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else{
                    mNsdManager.resolveService(service, new NsdManager.ResolveListener() {

                                @Override
                                public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                                    Log.e(TAG, "Resolve failed" + errorCode);
                                }

                                @Override
                                public void onServiceResolved(NsdServiceInfo serviceInfo) {
                                    Log.e(TAG, "Resolve Succeeded. " + serviceInfo);
                                    foundServiceInfo = serviceInfo;

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            items.add(new Item(R.drawable.esp12e, foundServiceInfo.getServiceName(), foundServiceInfo.getHost()));
                                            adapter.notifyDataSetChanged();
                                        }

                                    });


                                    Log.e(TAG, "Resolve Succeeded. " + serviceInfo);
                                }
                            }
                    );
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost" + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

}
