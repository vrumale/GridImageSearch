package com.codepath.gridimagesearch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.gridimagesearch.R;
import com.codepath.gridimagesearch.adapters.ImageResultsAdapter;
import com.codepath.gridimagesearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity {
    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    String query;
    private final int REQUEST_CODE = 20;
    String searchURL;
    String paImageSize;
    String paColorFilter;
    String paType;
    String paSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        //Creates the data source
        imageResults = new ArrayList<ImageResult>();
        //Attaches the data source to an adapter
        aImageResults = new ImageResultsAdapter(this, imageResults);
        //Link the adapter to the adapterview (gridview)
        gvResults.setAdapter(aImageResults);
        gvResults.setOnScrollListener(new EndlessScrollListener(){
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Lets add 8

                customLoadMoreDataFromApi(totalItemsCount);
            }
        });
    }
    //Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        String localSearchURL = new String(searchURL +"&start="+offset);


        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(localSearchURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Debug", response.toString());
                JSONArray imageResultsJson = null;

                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                   // imageResults.clear(); //clear the existing images from the array( in cases where its a new search)
                    //when you make changes to the adapter, it does modify the underlying data
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                    // aImageResults.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Info", imageResults.toString());
            }
        });
    }
    private void setupViews () {
        gvResults = (GridView) findViewById(R.id.gvImages);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //Launch the image display activity (Most common activity in Android)
                //Creating an intent
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                //Get the image result to display
                ImageResult result = imageResults.get(pos);
                //Pass the image result into the intent
                i.putExtra("result", result); // need to be serializable or parcelable
                //Launch the new activity
                startActivity(i);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_display, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = new String(query);
                onImageSearch(searchView, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public void onImageSearch(View v, String query) {
       // query = etQuery.getText().toString();
        Toast.makeText(this, "Search for this: "+ query, Toast.LENGTH_SHORT).show();
        AsyncHttpClient client = new AsyncHttpClient();
        searchURL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&rsz=8";
        if(paColorFilter != null)
            searchURL = new String(searchURL + "&imgc="+paColorFilter);
        if(paType != null)
            searchURL = new String(searchURL + "&imgtype"+paType);
        if(paImageSize != null )
            searchURL = new String(searchURL + "&imgsz"+paImageSize);
        if(paSite != null)
            searchURL = new String(searchURL+ "&as_sitesearch"+paSite);
        client.get(searchURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Debug", response.toString());
                JSONArray imageResultsJson = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    //imageResults.clear(); //clear the existing images from the array( in cases where its a new search)
                    //when you make changes to the adapter, it does modify the underlying data
                    aImageResults.clear(); // Needed inorder to generate more images for subsequent searches
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                   // aImageResults.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Info", imageResults.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this,"Click Settings", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, ConfigureFilters.class);
            startActivityForResult(i, REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            paImageSize = data.getExtras().getString("imageSize");
            paColorFilter = data.getExtras().getString("colorFilter");
            paType = data.getExtras().getString("type");
            paSite = data.getExtras().getString("site");
        }
    }
}
