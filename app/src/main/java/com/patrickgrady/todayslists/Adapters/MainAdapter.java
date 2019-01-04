package com.patrickgrady.todayslists.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.patrickgrady.todayslists.Activities.TasksListActivity;
import com.patrickgrady.todayslists.Managers.TasksManager;
import com.patrickgrady.todayslists.R;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> implements SwipeAndDragHelper.ActionCompletionContract {

    // list data
    private ArrayList<String> mDataset;

    // touch control
    ItemTouchHelper touchHelper;

    // provide references to the views for each item in list
    public class MyViewHolder extends RecyclerView.ViewHolder {

        // each data item in the list has a text view
        private View mLayoutView;
        private TextView mTextView;
        private String name;

        public MyViewHolder(View v) {
            super(v);
            mLayoutView = v;
            mTextView = v.findViewById(R.id.textItemView);

            mLayoutView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(MyViewHolder.this);
                    }
                    return false;
                }
            });

            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = mLayoutView.getContext().getApplicationContext();
                    Intent intent = new Intent(context, TasksListActivity.class);
                    intent.putExtra("filename", name);

                    context.startActivity(intent);
                }
            });
        }
    }

    // initalize with data dependent on kind of dataset in list
    public MainAdapter(ArrayList<String> dataset) {
        mDataset = dataset;
    }

    //#region Used by the layout manager


    // create new views
    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a view
        FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);



        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // replace the contents of a view
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from the dataset at this position
        // - replace the contents of the view with that element
        if(position >= mDataset.size()) {
            removeAt(position);
        }

        TasksManager tm = TasksManager.getInstance();
        holder.name = mDataset.get(position);
        holder.mTextView.setText(tm.getTask(holder.name));
    }

    public void removeAt(int position) {
        try {
            TasksManager.getInstance().remove(mDataset.get(position));
        } catch(Error e) {
            System.out.println("Must initialize dataset");
        }

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }

    // return size of dataset
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        try {
            TasksManager tm = TasksManager.getInstance();
            tm.move(oldPosition, newPosition);
        } catch(Error e) {
            System.out.println("Must initialize dataset");
        }

        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        try {
            TasksManager.getInstance().remove(mDataset.get(position));
        } catch(Error e) {
            System.out.println("Must initialize dataset");
        }

        notifyItemRemoved(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    // #endregion Used by the layout manager

}
