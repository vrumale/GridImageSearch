package com.codepath.gridimagesearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.gridimagesearch.R;

public class ConfigureFilters extends Activity {
    Spinner spImageSize;
    Spinner spColorFilter;
    Spinner spType;
    EditText etSite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_filters);
        setupSpinners();
        etSite = (EditText) findViewById(R.id.etSiteFilter);
    }

    void setupSpinners() {
        spImageSize = (Spinner) findViewById(R.id.spImageSize);
        spColorFilter = (Spinner) findViewById(R.id.spColorFilter);
        spType = (Spinner) findViewById(R.id.spType);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.imageSize_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spImageSize.setAdapter(adapter);

        ArrayAdapter<CharSequence> aColorFilter = ArrayAdapter.createFromResource(this,
                R.array.colorFilter_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        aColorFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spColorFilter.setAdapter(aColorFilter);


        ArrayAdapter<CharSequence> aType = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        aType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spType.setAdapter(aType);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onConfigurSetup(View view) {
        String imageSize = new String (spImageSize.getSelectedItem().toString());
        String colorFilter = new String( spColorFilter.getSelectedItem().toString());
        String type =  new String(spType.getSelectedItem().toString());
        String site = new String(etSite.getText().toString());
        Toast.makeText(this,imageSize+colorFilter+type,Toast.LENGTH_SHORT).show();
        Intent data = new Intent();
        data.putExtra("imageSize", imageSize);
        data.putExtra("colorFilter", colorFilter);
        data.putExtra("type", type);
        data.putExtra("site", site);
        setResult(RESULT_OK, data);
        finish();
    }
}
