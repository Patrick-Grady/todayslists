package com.patrickgrady.todayslists.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.patrickgrady.todayslists.Adapters.MainAdapter;
import com.patrickgrady.todayslists.Adapters.MyListAdapter;
import com.patrickgrady.todayslists.Adapters.SwipeAndDragHelper;
import com.patrickgrady.todayslists.Managers.ListManager;
import com.patrickgrady.todayslists.Managers.QuoteManager;
import com.patrickgrady.todayslists.Managers.TasksManager;
import com.patrickgrady.todayslists.Managers.UpdateManager;
import com.patrickgrady.todayslists.Objects.ListElement;
import com.patrickgrady.todayslists.R;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    // ui
    private RecyclerView mRecyclerView;
    private MyListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView quoteImage;
    private TextView quoteView;
    private Toolbar toolbar;

    // data
    private ArrayList<String> elements;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // quote text view
        quoteView = findViewById(R.id.quote_text_view);
        quoteImage = findViewById(R.id.quote_image);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // set recycler view to fixed size since changes in content do not affect layout size
        // improves performance
        mRecyclerView.setHasFixedSize(false);


        // using a layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(getIntent().getExtras() != null) {
            key = getIntent().getStringExtra("key");
        }
        else {
            key = ListElement.ROOT;
        }
        initializeData();

        // using an adapter
        mAdapter = new MyListAdapter(elements);
        mRecyclerView.setAdapter(mAdapter);

        SwipeAndDragHelper swipeAndDragHelper = new SwipeAndDragHelper((MyListAdapter) mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        ((MyListAdapter) mAdapter).setTouchHelper(touchHelper);
        mRecyclerView.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(mRecyclerView);

        // set up toolbar
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }

    private void initializeData() {
        ListManager lm = ListManager.getInstance();
        try {
            lm.setFocusList(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        QuoteManager qm = QuoteManager.getInstance(getApplicationContext(), quoteView, quoteImage);
        UpdateManager.init(getApplicationContext(), qm);
        elements = lm.getElementKeys();
    }

    @Override
    protected void onResume() {
        super.onResume();

        UpdateManager.getInstance().updateListeners();
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = mItem.getItemId();

        switch(id) {
            case R.id.action_settings:
                break;
            case R.id.action_add:
                ListManager.getInstance().addNewString();   // add a new element
                break;
            case R.id.action_add_folder:
                ListManager.getInstance().addNewFolder();   // add a new list
                break;
            case R.id.action_trash:
                getApplicationContext().getSharedPreferences("time", Context.MODE_PRIVATE).edit().clear().apply();
                break;
            case R.id.action_info:
                System.out.println("Dataset size: " + elements.size());
                System.out.println("Elements: ");
                for(String key : elements) {
                    System.out.println("\tg:" + key);
                }
                break;
            case R.id.action_clear:
                ListManager.getInstance().removeAllChildren();
                break;
        }

        return super.onOptionsItemSelected(mItem);
    }
}