package com.patrickgrady.todayslists.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.patrickgrady.todayslists.Adapters.SwipeAndDragHelper;
import com.patrickgrady.todayslists.Adapters.TaskListAdapter;
import com.patrickgrady.todayslists.Managers.Objects.ListOfTasks;
import com.patrickgrady.todayslists.Managers.TasksManager;
import com.patrickgrady.todayslists.R;

public class TasksListActivity extends AppCompatActivity {

    // recycler view stuff
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // data
    private ListOfTasks task_list;

    //app bar
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_list);

        //Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
        String filename = getIntent().getStringExtra("filename");
        task_list = TasksManager.getInstance(getApplicationContext()).get(filename);
        //task_list = new String[] {""};

        mRecyclerView = (RecyclerView) findViewById(R.id.task_recycler_view);

        // set recycler view to fixed size since changes in content do not affect layout size
        // improves performance
        mRecyclerView.setHasFixedSize(true);


        // using a layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // using an adapter
        mAdapter = new TaskListAdapter(filename, task_list);
        SwipeAndDragHelper swipeAndDragHelper = new SwipeAndDragHelper((TaskListAdapter) mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        ((TaskListAdapter) mAdapter).setTouchHelper(touchHelper);
        mRecyclerView.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(mRecyclerView);

        // set up toolbar
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
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
        else if(id == R.id.action_add) {
            task_list.add("");
            mRecyclerView.getAdapter().notifyDataSetChanged();
            ((TaskListAdapter) mRecyclerView.getAdapter()).toggleLast();
            return true;
        }
        else if(id == R.id.action_save) {
            finish();
        }
        else if(id == R.id.action_sort) {
            task_list.sortInPlace();
            mRecyclerView.getAdapter().notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
