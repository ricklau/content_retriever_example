package com.lausy.contentretriever;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <h1>ContentListDataAdapter</h1>
 *
 * Copyright 2018:  Rick Lau
 *
 * Contains the list view and elements that are shown to the user.  The list is populated through
 * a well known JSON object.  Images of the list view are updated accordingly through addition
 * REST calls after the initial list is setup.
 *
 * @author Rick Lau
 * @version 1.0
 */

class ContentListDataAdapter extends BaseAdapter implements ContentServiceCallback {
    private static final String TAG = ContentListDataAdapter.class.getName();

    private Context mContext;

    /**
     * Class structure for the listview elements and layout for each list item.
     */
    public class ViewHolder {
        ImageView image;
        TextView title;
        TextView id;
    }

    /**
     * Class structure that holds the data for the listview/adapter.  Added the capbility
     * to sort the data structure by using the content ID is the natural sort order.
     *
     * Could not use lambdas because my phone is 6.0.1/API 23.  Java 8 requires API24.
     */
    public class ViewData implements Comparable<ViewData> {
        String title;
        public String getTitle() { return title; }
        public void setTitle(String t) { title = t; }

        int contentID;
        public int getId() {return contentID; }
        public void setId(int id) { contentID = id; }

        String imgUrl;
        public String getImgUrl() { return imgUrl; }
        public void setImgUrl(String url) { imgUrl = url; }

        @Override
        public int compareTo(@NonNull ViewData o) {
            return this.contentID - o.contentID;
        }
    }

    private ArrayList<ViewData> mViewDataList;

    /**
     * Listview that complements the list adapter.
     */
    private ListView mListView;

    /**
     * mActivity is used to access the UI elements of the calling activity.  Could have casted
     * mContext but defined here to make the code more readable.
     */
    private Activity mActivity;

    /**
     * mBaseUri and mPath are used to generate the overall URL.  Retrofit requires these
     * components to be broken up.  Perhaps later a more generic version can be used.  But
     * since retrofit reformats the slash to a literal character, this presents some challenges.
     */
    private String mBaseUri;
    private String mPath;


    /**
     * Constructor for the class.
     *
     * @param context The calling activity passes in its context.
     */
    ContentListDataAdapter(Context context) {
        mContext = context;
        mActivity = (Activity) context;
        mListView = (ListView) mActivity.findViewById(R.id.content_listview);

        // array needs to be initialized first before setting the adapter.
        mViewDataList = new ArrayList<ViewData>();

        mListView.setAdapter(this);
    }

    @Override
    public int getCount() {
        return mViewDataList.size();
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item, null);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.id = (TextView) convertView.findViewById(R.id.content_id);
        holder.image = (ImageView) convertView.findViewById(R.id.content_image);
        holder.title = (TextView) convertView.findViewById(R.id.content_title);

        holder.id.setText(String.valueOf(mViewDataList.get(position).getId()));
        holder.title.setText(mViewDataList.get(position).getTitle());

        // Use Picasso to load the image to imageview.  Use a placeholder image where
        // necessary.
        if ((mViewDataList.get(position).getImgUrl() != null && mViewDataList.get(position).getImgUrl().length() > 0)) {
            Picasso.with(mContext).load(mViewDataList.get(position).getImgUrl()).placeholder(R.drawable.placeholder).into(holder.image);
        } else {
            Picasso.with(mContext).load(R.drawable.placeholder).into(holder.image);
        }

        return convertView;
    }

    /**
     * mBaseUri needs to be set before fetch can be called.  This format falls inline with
     * retrofit formats.  Getter and setter are implemented here.
     */

    public void setBaseUri(String uri) { mBaseUri = uri; }
    public String getBaseUri() { return mBaseUri; }

    /**
     * mPath needs to be set before fetch can be called.  This format falls inline with
     * retrofit formats.  Getter and setter are implemented here.
     */
    public void setServerPath(String url) { mPath = url; }
    public String getServerPath() { return mPath; }


    /**
     * This is a network call to the server to retrieve a list of content.
     * setBaseUri and setUrl must called to set a valid overall URL for the server request.
     *
     * @param none
     */
    public void getList()
    {
        NetworkService net = new NetworkService(mContext, this);

        net.setBaseUri(mBaseUri);
        net.setPath(mPath);
        net.fetchContentList();
    }

    /**
     * setList will overwrite the existing list of items.  If format of JSON object is not
     * known, this method call will be ignored.
     *
     * @param jsonArray: takes in a JSON array to parse.
     * @return Nothing
     */
    public void setList(JSONArray jsonArray)
    {
        clearList();

        for (int i=0; i<jsonArray.length(); i++)
        {
            try {
                JSONObject item = jsonArray.getJSONObject(i);

                ViewData data = new ViewData();

                data.setId(Integer.parseInt(item.getString("id")));
                data.setImgUrl(item.getString("image"));
                data.setTitle(item.getString("title"));

                mViewDataList.add(data);
            } catch (JSONException e) {
                Log.e(TAG, "Error in parsing JSON object.");
                //e.printStackTrace();
            }

        }

        this.notifyDataSetChanged();
    }

    /**
     * clearList will delete all items in the ListView.  The UI will show no items.
     *
     * @param none
     */
    public void clearList()
    {
        mViewDataList.clear();
        this.notifyDataSetChanged();
    }

    /**
     * Callback method from the network when a list of content is retrieved.
     *
     * @param arr An array of JSON objects that contains the content.
     */
    public void onReceiveContent(JSONArray arr)
    {
        setList(arr);
    }

    /**
     * If an error occurs when retrieving content, this callback method will be called.
     *
     * @param errorCode Network status messages are returned.  If -1 is passed, this is a general
     *                  error.
     */
    public void onReceiveError(int errorCode)
    {
        Log.e(TAG, "Could not retrieve data from server.  Error " + errorCode + "received.");
    }

    /**
     * filterList will filter the list according to the enum type FilterType.
     * FilterType is used to control what types of filtering can be done.
     *
     * @param ascending If true, then list is sorted in ascending order.  Else list
     *                  is sorted in descending order.
     */
    public void sort(Boolean ascending)
    {
        if (ascending) {
            Collections.sort(mViewDataList);
        } else {
            // Only good for API24 and above devices.  Cannot use on 6.0.1 device.
            //Collections.sort(mViewDataList, Comparator.reverseOrder());
            Collections.sort(mViewDataList, new Comparator<ViewData>() {
                @Override
                public int compare(ViewData o1, ViewData o2) {
                    return o2.compareTo(o1);
                }
            });
        }

        this.notifyDataSetChanged();
    }

    public List<ViewData> getAllData() {
        return Collections.unmodifiableList(mViewDataList);
    }
}
