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
import com.patrickgrady.todayslists.Adapters.SwipeAndDragHelper;
import com.patrickgrady.todayslists.Managers.QuoteManager;
import com.patrickgrady.todayslists.Managers.TasksManager;
import com.patrickgrady.todayslists.Managers.UpdateManager;
import com.patrickgrady.todayslists.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // ui
    private RecyclerView mRecyclerView;
    private MainAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView quoteImage;
    private TextView quoteView;
    private Toolbar toolbar;

    // data
    private ArrayList<String> groupings;

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

        initializeData();

        // using an adapter
        mAdapter = new MainAdapter(groupings);
        mRecyclerView.setAdapter(mAdapter);

        SwipeAndDragHelper swipeAndDragHelper = new SwipeAndDragHelper((MainAdapter) mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        ((MainAdapter) mAdapter).setTouchHelper(touchHelper);
        mRecyclerView.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(mRecyclerView);

        // set up toolbar
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }

    private void initializeData() {
        TasksManager tm = TasksManager.getInstance(true, getApplicationContext());
        QuoteManager qm = QuoteManager.getInstance(getApplicationContext(), quoteView, quoteImage);
        UpdateManager.init(getApplicationContext(), tm, qm);
        groupings = tm.getTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();

        UpdateManager.getInstance().updateListeners();
        TasksManager.getInstance().getTasks();
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
                TasksManager tm = TasksManager.getInstance(true, getApplicationContext());
                tm.addList();   // add a new list
                mRecyclerView.getAdapter().notifyDataSetChanged();

                startLast();
                break;
            case R.id.action_trash:
                getApplicationContext().getSharedPreferences("time", Context.MODE_PRIVATE).edit().clear().apply();
                break;
            case R.id.action_info:
                System.out.println("Dataset size: " + groupings.size());
                System.out.println("Elements: ");
                for(String name : groupings) {
                    System.out.println("\tg:" + name);
                    for(String item : TasksManager.getInstance(true, getApplicationContext()).get(name).getUnderlyingElements()) {
                        System.out.println("\t\te:" + item);
                    }
                    System.out.println("\t\td:" + TasksManager.getInstance().getTask(name));
                }
                break;
            case R.id.action_clear:
                TasksManager.getInstance(true, getApplicationContext()).clear();
                mRecyclerView.getAdapter().notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(mItem);
    }

    public void startLast() {
        String key = groupings.get(groupings.size()-1);
        Context context = getApplicationContext();
        Intent intent = new Intent(context, TasksListActivity.class);
        intent.putExtra("filename", key);

        context.startActivity(intent);
    }
}