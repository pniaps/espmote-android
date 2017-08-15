package es.pnia.espmote;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ItemAdapter extends BaseAdapter {

    private Context context;
    private List<Item> items;

    public ItemAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.e(MainActivity.TAG, "ItemAdapter.getView");
        if (convertView == null) {
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        final View rowView = convertView;

        final Item mote = this.items.get(position);

        // Set data into the view.
        ImageView ivItem = (ImageView) rowView.findViewById(R.id.ivItem);
        ivItem.setImageResource(mote.getImage());

        TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
        tvTitle.setText(mote.getName());

        ImageView ivChannel0 = (ImageView) rowView.findViewById(R.id.ivChannel0);
        ivChannel0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeOutput(mote, 0, !mote.isChannel0(), rowView);
            }
        });

        ImageView ivChannel1 = (ImageView) rowView.findViewById(R.id.ivChannel1);
        ivChannel1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeOutput(mote, 1, !mote.isChannel1(), rowView);
            }
        });

        ImageView ivChannel2 = (ImageView) rowView.findViewById(R.id.ivChannel2);
        ivChannel2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeOutput(mote, 2, !mote.isChannel2(), rowView);
            }
        });

        updateIcons(mote, rowView);

        return rowView;
    }

    public void changeOutput(final Item mote, int channel, boolean enable, final View rowView) {

        final String JsonURL = "http://" + mote.getIp() + (enable ? "/enable" : "/disable") + "?channel=" + channel;
        JsonObjectRequest enabledReq = new JsonObjectRequest(Request.Method.GET, JsonURL, null,
                new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mote.setChannel0(response.isNull("channel0") ? null : response.getBoolean("channel0"));
                            mote.setChannel1(response.isNull("channel1") ? null : response.getBoolean("channel1"));
                            mote.setChannel2(response.isNull("channel2") ? null : response.getBoolean("channel2"));
                            updateIcons(mote, rowView);
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
        MainActivity.requestQueue.add(enabledReq);
    }

    private void updateIcons(final Item mote, View rowView) {
        Log.e(MainActivity.TAG, "ItemAdapter.updateIcons");
        ImageView ivChannel0 = (ImageView) rowView.findViewById(R.id.ivChannel0);
        if(mote.isChannel0() == null){
            ivChannel0.setVisibility(View.GONE);
        }else{
            ivChannel0.setImageResource(mote.isChannel0() ? R.drawable.light_on : R.drawable.light_off);
            ivChannel0.setVisibility(View.VISIBLE);
        }

        ImageView ivChannel1 = (ImageView) rowView.findViewById(R.id.ivChannel1);
        if(mote.isChannel1() == null){
            ivChannel1.setVisibility(View.GONE);
        }else{
            ivChannel1.setImageResource(mote.isChannel1() ? R.drawable.light_on : R.drawable.light_off);
            ivChannel1.setVisibility(View.VISIBLE);
        }

        ImageView ivChannel2 = (ImageView) rowView.findViewById(R.id.ivChannel2);
        if(mote.isChannel2() == null){
            ivChannel2.setVisibility(View.GONE);
        }else{
            ivChannel2.setImageResource(mote.isChannel2() ? R.drawable.light_on : R.drawable.light_off);
            ivChannel2.setVisibility(View.VISIBLE);
        }
    }
}