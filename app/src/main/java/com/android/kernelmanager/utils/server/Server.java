
package com.android.kernelmanager.utils.server;



public class Server {

    private String mAddress;

    Server(String address) {
        mAddress = address;
    }

    class Query {

        private String mKey;
        private String mValue;

        Query(String key, String value) {
            mKey = key;
            mValue = value;
        }
    }

    String getAddress(String url, Query... queries) {
        StringBuilder parsedUrl = new StringBuilder(mAddress + url + "?");
        for (Query query : queries) {
            if (query.mValue == null) continue;
            parsedUrl.append(query.mKey).append("=").append(query.mValue).append("&");
        }
        parsedUrl.setLength(parsedUrl.length() - 1);
        return parsedUrl.toString();
    }
}
