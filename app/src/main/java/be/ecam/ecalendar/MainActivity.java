package be.ecam.ecalendar;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ViewFlipper;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.emory.mathcs.backport.java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int itemThatWasClicked = item.getItemId();
        Context context = this;
        if (itemThatWasClicked == R.id.settings) {
            Class destinationClass = SettingsActivity.class;
            Intent intent = new Intent(context, destinationClass);
            startActivity(intent);

            return true;
        }
        if (itemThatWasClicked == R.id.search) {
            Class destinationClass = SearchActivity.class;
            Intent intent = new Intent(context, destinationClass);
            startActivity(intent);

            return true;
        }

        //Place here others menu items with an if

        return super.onOptionsItemSelected(item);
    }

}
