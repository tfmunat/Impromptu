package com.laserscorpion.impromptu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ExampleAttendanceActivity extends AppCompatActivity {

    private ArrayAdapter<Person> adapter;
    private ArrayList<Person> people;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        people = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.list_item, people);
        ListView list = (ListView)findViewById(R.id.name_list);
        list.setAdapter(adapter);
    }


    public void submit(View view) {
        TextView nameField = (TextView)findViewById(R.id.textfield);
        TextView uniField = (TextView)findViewById(R.id.unifield);
        String name = nameField.getText().toString();
        String uni = uniField.getText().toString();
        DatabaseHelper helper = new DatabaseHelper(this);
        long id = helper.add(name, uni);

    }

    public void updateList(View view) {
        DatabaseHelper helper = new DatabaseHelper(this);
        ArrayList<Person> currentPeople = helper.getAllNames();


        for (Person person : currentPeople) {
            boolean alreadyInList = people.contains(person);
            if (alreadyInList)
                adapter.remove(person);
        }
        adapter.notifyDataSetChanged();

        adapter.addAll(currentPeople);

        adapter.notifyDataSetChanged();
        people = currentPeople;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
