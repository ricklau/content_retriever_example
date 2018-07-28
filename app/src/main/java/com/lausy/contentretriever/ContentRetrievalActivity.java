package com.lausy.contentretriever;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * <h1>ContentRetrievalActivity</h1>
 *
 * Copyright 2018:  Rick Lau
 *
 * This is the primary activity for user interaction to fetch and sort the content list
 * retrieved from the server.  This activity was meant to be kept simple and to have all the
 * business logic in other non-activity classes.
 *
 * @author Rick Lau
 * @version 1.0
 */
public class ContentRetrievalActivity extends AppCompatActivity {
    static private final String TAG = ContentRetrievalActivity.class.getName();

    private Context mContext;
    private ContentListDataAdapter mContentList;

    static private final String DEFAULT_BASEURI = "http://eng-assets.s3-website-us-west-2.amazonaws.com/";
    static private final String DEFAULT_SERVER_PATH = "fixture/movies.json";

    private String mBaseUri;
    private String mServerPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        // setup the view layouts and toolbar
        setContentView(R.layout.activity_content_retrieval);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContentList = new ContentListDataAdapter(mContext);
        mBaseUri = DEFAULT_BASEURI;
        mServerPath = DEFAULT_SERVER_PATH;

        // floating button.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.refresh);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Fetching content list...");

                mContentList.setBaseUri(mBaseUri);
                mContentList.setServerPath(mServerPath);
                mContentList.getList();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content_retrieval, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_ascending) {
            Log.d(TAG, "Sorting in ascending order...");
            mContentList.sort(true);
            return true;
        } else if (id == R.id.action_sort_descending) {
            Log.d(TAG, "Sorting in descending order...");
            mContentList.sort(false);
            return true;
        } else if (id == R.id.action_load_testdata) {
            Log.d(TAG, "loading test data...");

            JSONArray arr = null;
            try {
                arr = new JSONArray(TestData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mContentList.setList(arr);
        }

        return super.onOptionsItemSelected(item);
    }

    @VisibleForTesting
    static final private String TestData =
            "[{\"title\":\"Joe\",\"image\":\"http://images.adrise.tv/JkC_Cw7UD2jcJtmg7dAAArPGXiE=/214x306/smart/img.adrise.tv/d57031bb-61c9-499e-bb7a-4461e76db235.jpg\",\"id\":\"369854\"}," +
                    "{\"title\":\"Man On A Ledge\",\"image\":\"http://images.adrise.tv/q4v7JUQPPHqn8nTmYiudW6l8w_0=/214x306/smart/img.adrise.tv/1c31dfce-5338-4a09-bcb0-f68789153f33.png\",\"id\":\"302437\"}," +
                    "{\"title\":\"The Hunted\",\"image\":\"http://images.adrise.tv/0K0omTIr4w7jyjK5I18cvW5ljWg=/214x306/smart/img.adrise.tv/c79d639a-de5f-44d4-86d9-40e5a892551e.png\",\"id\":\"307852\"}," +
                    "{\"title\":\"Igor\",\"image\":\"http://images.adrise.tv/mLPoP2m45bK3Rpa92yXQL6anRf8=/214x306/smart/img.adrise.tv/a04564c2-ddd7-4d3a-bcb5-8b1b07317683.jpg\",\"id\":\"334155\"}," +
                    "{\"title\":\"Teeth\",\"image\":\"http://images.adrise.tv/WMI7_mLShEgyngHjnrk2rGZWGnw=/214x306/smart/img.adrise.tv/ac8b5a44-072f-4eca-b9d5-ad708c43b880.jpg\",\"id\":\"376133\"}," +
                    "{\"title\":\"The Town That Dreaded Sundown\",\"image\":\"http://images.adrise.tv/vzLhWbZFqKMNZRK6mKRxSUSwUNg=/0x0:800x1143/214x306/smart/img.adrise.tv/d177a000-89bc-4ebc-9d90-e401c37f1161.jpg\",\"id\":\"348457\"}," +
                    "{\"title\":\"Wild Card\",\"image\":\"http://images.adrise.tv/vq973deEapxkIORH-dPW3wWwNzA=/214x306/smart/img.adrise.tv/677ae470-d187-4c92-bdac-273fe6ee2abd.jpg\",\"id\":\"376177\"}," +
                    "{\"title\":\"Girl Most Likely\",\"image\":\"http://images.adrise.tv/6VNmkcQ77t5RkRvVJ2Jripuvb8s=/214x306/smart/img.adrise.tv/fee2984a-5b20-4788-891f-9e9c94cbb691.jpg\",\"id\":\"376162\"}," +
                    "{\"title\":\"All Is Lost\",\"image\":\"http://images.adrise.tv/sRSBG-FO24ni7VwnNWU1ZwAMKgU=/214x306/smart/img.adrise.tv/97a216ea-846a-4709-af56-df32f0e9bcb8.jpg\",\"id\":\"348089\"}," +
                    "{\"title\":\"Outlander\",\"image\":\"http://images.adrise.tv/dT8e5ntd3GkWTYPlN6E43ZFG4FM=/214x306/smart/img.adrise.tv/48934d06-b841-4e63-b1c3-1defc9d078c6.jpg\",\"id\":\"328041\"}," +
                    "{\"title\":\"Frozen\",\"image\":\"http://images.adrise.tv/86vwXVv9IT0DVoooJQ4UVlOuS9g=/214x306/smart/img.adrise.tv/16197a49-a93d-4ccf-89a7-5dfb03b6b83b.jpg\",\"id\":\"348935\"}," +
                    "{\"title\":\"The Possession\",\"image\":\"http://images.adrise.tv/l1B4b41-v0ikhai0gjs0BuOZttw=/214x306/smart/img.adrise.tv/e381d14d-1ef9-4f5f-a485-943786448ff1.jpg\",\"id\":\"348949\"}," +
                    "{\"title\":\"Sleepover\",\"image\":\"http://images.adrise.tv/i7vGckfYleR4c-uhByVnZQZU6Pk=/214x306/smart/img.adrise.tv/dac1e328-a242-4884-a5b5-8af984467748.png\",\"id\":\"310151\"}," +
                    "{\"title\":\"Bebe's Kids\",\"image\":\"http://images.adrise.tv/gCWMS3AdLCE-8t3DKlyOKukQpHk=/10x0:569x800/214x306/smart/img.adrise.tv/2c0aa33f-0e04-4c76-87b4-035f5d8804e2.jpg\",\"id\":\"306801\"}," +
                    "{\"title\":\"Imagine That\",\"image\":\"http://images.adrise.tv/MeEoHeMLduztZNz-NsLRHO1cb-c=/214x306/smart/img.adrise.tv/b140a32a-3934-4ea6-bf1c-97520e1b5c20.png\",\"id\":\"305457\"}," +
                    "{\"title\":\"School of Rock\",\"image\":\"http://images.adrise.tv/6sjdZy7rGz23YZ62_diTF26BfgE=/214x306/smart/img.adrise.tv/4b85521c-c3af-41d5-bf52-40b698c6d56d.jpg\",\"id\":\"322939\"}," +
                    "{\"title\":\"It Takes Two\",\"image\":\"http://images.adrise.tv/gMH0Ut7gZiu_9sLazRLzeWuu0b0=/9x3:796x1127/214x306/smart/img.adrise.tv/b7cd9ee3-6f97-4ed5-b556-b1c5887ecba3.png\",\"id\":\"307561\"}," +
                    "{\"title\":\"The Last Stand\",\"image\":\"http://images.adrise.tv/9g8zEhkb1dbK5w_hbwgifi5EyKE=/214x306/smart/img.adrise.tv/aa4bd3c7-5425-4b05-9ae4-d8eb0f7e24a7.jpg\",\"id\":\"369857\"}]";
}
