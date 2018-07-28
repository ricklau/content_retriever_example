package com.lausy.contentretriever;

import org.json.JSONArray;

/**
 * <h1>ContentServiceCallback</h1>
 *
 * Copyright 2018:  Rick Lau
 *
 * Interface class to that listens for data coming back from the server.
 *
 * @author Rick Lau
 * @version 1.0
 */
public interface ContentServiceCallback {
    public void onReceiveContent(JSONArray arr);
    public void onReceiveError(int errorCode);
}
