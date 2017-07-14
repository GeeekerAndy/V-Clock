package com.example.dell.v_clock.util;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by andy on 7/10/17.
 */

public class JSONObjectRequestMapParams extends Request<JSONObject> {

    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;

    public JSONObjectRequestMapParams(String url, Map<String, String> params,
                                      Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    public JSONObjectRequestMapParams(int method, String url, Map<String, String> params,
                                      Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse (NetworkResponse response) {
        try {
            String utf8String = new String(response.data, "UTF-8");
            return Response.success(new JSONObject(utf8String), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            // log error
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            // log error
            return Response.error(new ParseError(e));
        } catch (OutOfMemoryError e) {
            Log.e("ERROR", e.getMessage());
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}
