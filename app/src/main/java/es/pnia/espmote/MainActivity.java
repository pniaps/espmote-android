package es.pnia.espmote;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView t;
    NsdManager.DiscoveryListener mDiscoveryListener;

    public static final String TAG = "ESPMote"; //MainActivity.class.getSimpleName();
    private NsdManager mNsdManager;

    public static final String SERVICE_TYPE = "_espmote._tcp.";

    private ListView listView;
    private List items;
    private ItemAdapter adapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display a indeterminate progress bar on title bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapter, View view, int position, long arg) {

                final Item mote = (Item) listView.getAdapter().getItem(position);
                final View rowView = view;

                final String JsonURL = "http://"+mote.getIp()+(mote.isEnabled() ? "/disable" : "/enable");
                JsonObjectRequest enabledReq = new JsonObjectRequest(Request.Method.GET, JsonURL, null,
                        // The third parameter Listener overrides the method onResponse() and passes
                        //JSONObject as a parameter
                        new Response.Listener<JSONObject>() {

                            // Takes the response from the JSON request
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean enabled = response.getBoolean("enabled");
                                    mote.setEnabled(enabled);

                                    ImageView ivStatus =  (ImageView) rowView.findViewById(R.id.ivStatus);
                                    ivStatus.setImageResource(mote.isEnabled() ? R.drawable.light_on : R.drawable.light_off);
//                                    Log.e(TAG, JsonURL + " - " + (enabled ? "enabled" : "disabled"));
                                }
                                // Try and catch are included to handle any errors due to JSON
                                catch (JSONException e) {
                                    // If an error occurs, this prints the error to the log
                                    e.printStackTrace();
                                }
                            }
                        },
                        // The final parameter overrides the method onErrorResponse() and passes VolleyError
                        //as a parameter
                        new Response.ErrorListener() {
                            @Override
                            // Handles errors that occur due to Volley
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                            }
                        }
                );
                // Adds the JSON object request "obreq" to the request queue
                requestQueue.add(enabledReq);
            }
        });

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
                                    final Item mote = new Item(R.drawable.esp12e, foundServiceInfo.getServiceName(), foundServiceInfo.getHost());

                                    // Creating the JsonObjectRequest class called obreq, passing required parameters:
                                    //GET is used to fetch data from the server, JsonURL is the URL to be fetched from.
                                    final String JsonURL = "http://"+foundServiceInfo.getHost();
                                    JsonObjectRequest enabledReq = new JsonObjectRequest(Request.Method.GET, JsonURL, null,
                                            // The third parameter Listener overrides the method onResponse() and passes
                                            //JSONObject as a parameter
                                            new Response.Listener<JSONObject>() {

                                                // Takes the response from the JSON request
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        boolean enabled = response.getBoolean("enabled");
                                                        mote.setEnabled(enabled);
//                                                        Log.e(TAG, JsonURL + " - " + (enabled ? "enabled" : "disabled"));
                                                    }
                                                    // Try and catch are included to handle any errors due to JSON
                                                    catch (JSONException e) {
                                                        // If an error occurs, this prints the error to the log
                                                        e.printStackTrace();
                                                    }
                                                }
                                            },
                                            // The final parameter overrides the method onErrorResponse() and passes VolleyError
                                            //as a parameter
                                            new Response.ErrorListener() {
                                                @Override
                                                // Handles errors that occur due to Volley
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.e("Volley", "Error");
                                                }
                                            }
                                    );
                                    // Adds the JSON object request "obreq" to the request queue
                                    requestQueue.add(enabledReq);

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            items.add(mote);
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
