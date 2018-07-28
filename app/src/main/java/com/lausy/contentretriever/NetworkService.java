package com.lausy.contentretriever;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * <h1>NetworkService</h1>
 *
 * Copyright 2018:  Rick Lau
 *
 * This is a network service specifically to pull a content list from the server.  The host
 * and url are dynamic and can be set to different values.  The user of this class must
 * implement the ContentServiceCallback interface to listen for the data coming back from the
 * server.
 *
 * The user of this class should know the format of the JSON array coming back from the server.
 * This class simply passes on whatever is retrieved from the server.
 *
 * Later on, the format of URL can be made more flexible.  Due to how retrofit formats URLs,
 * this is a larger task left for laer.
 *
 * @author Rick Lau
 * @version 1.0
 */
public class NetworkService {
    private static final String TAG = NetworkService.class.getName();

    private Context mContext;

    private String mPath;
    private String mBaseUri;
    private static ContentServiceCallback mCallbackListAdapter;

    /**
     * Constructuctor for the class.
     *
     * @param context Context of of the caller.  Should be an activity.
     * @param caller The class implementing the ContentServiceCallback interface.
     */
    NetworkService(Context context, Object caller) {
        mContext = context;
        mPath = "";
        mBaseUri = "";

        mCallbackListAdapter = (ContentServiceCallback) caller;
    }

    /**
     * Getters and setters for mPath.
     */
    public void setPath(String url) { mPath = url; }
    public String getPath() { return mPath; }


    /**
     * Getter and setter for mBaseUri
     */
    public void setBaseUri(String base) { mBaseUri = base; }
    public String getBaseUri() { return mBaseUri; }

    /**
     * mPath and mBaseUri must be set before calling this API.  This API will generate a
     * REST call to the server and retrieve a content list.  This content list is then passed
     * to the caller via the callback registered with this class at the time of instantiation.
     *
     * Only 200 and 404 messages are handled right now.
     *
     */
    public void fetchContentList()
    {
        if (mPath.equals("") || mBaseUri.equals("")) {
            Log.e(TAG, "Error invalidate host or url.  host=" + mBaseUri + ", url=" + mPath);
            return;
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(mBaseUri)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        NetworkApiService client = retrofit.create(NetworkApiService.class);

        Call<ResponseBody> call = client.fetchContentList(
                mPath
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                JSONArray jsonArr;

                if (response.raw().code() == 200) {
                    Log.d(TAG, "Received 200 response from server.");

                    try {
                        jsonArr = new JSONArray(response.body().string());
                        mCallbackListAdapter.onReceiveContent(jsonArr);

                        Log.v(TAG, jsonArr.toString());
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException caught onResponse.");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException caught onResponse.");
                        e.printStackTrace();
                    }
                } else {
                    mCallbackListAdapter.onReceiveError(response.raw().code());

                    if (response.raw().code() == 404) {
                        Log.e(TAG, "Received 404 error code from server.");
                    } else {
                        Log.e(TAG, "Received unhandled response code from server:  " + response.raw().code());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mCallbackListAdapter.onReceiveError(-1);
                Log.e(TAG, "Error in sending the request to server.");
            }
        });
    }

    /**
     * Interface class definition for formatting of the REST call to retrofit.
     */
    private interface NetworkApiService {
        @GET("{url}")
        Call<ResponseBody> fetchContentList (
                @Path("url") String url
        );
    }
}
