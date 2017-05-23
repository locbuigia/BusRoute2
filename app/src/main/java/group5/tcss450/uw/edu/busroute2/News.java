package group5.tcss450.uw.edu.busroute2;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jnbui on 5/8/2017.
 */

public class News {
    /**
     * ID from JSON
     */
    private String mAnalyzerID;
    /**
     * Language from JSON
     */
    private String mResult;
//    /**
//     * Kind from JSON
//     */
//    private String mKind;
//    /**
//     * Specifications from JSON
//     */
//    private String mSpecs;
    /**
     * Implementation from JSON
     */
    private String mImpl;
    /**
     * takes in a jsonobject.
     * @param json
     * @throws JSONException
     */
    public News(JSONObject json)  throws JSONException {
        create(json);
    }

    /**
     * parse the jsonobject.
     * include name, url, description, image url.
     * @param json
     * @throws JSONException
     */
    private void create(JSONObject json) throws JSONException{
        mAnalyzerID = json.getString("analyzerId");
        mResult = json.getString("result");
    }

    /**
     * return id
     */
    public String getId(){return mAnalyzerID;}
    /**
     * return result
     */
    public String getResult(){return mResult;}

}
