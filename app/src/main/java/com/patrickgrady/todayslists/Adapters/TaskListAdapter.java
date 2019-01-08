package com.patrickgrady.todayslists.Adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.patrickgrady.todayslists.Objects.ListOfTasks;
import com.patrickgrady.todayslists.R;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.MyViewHolder> implements SwipeAndDragHelper.ActionCompletionContract {

    // list data
    private ListOfTasks mDataset;
    private String id;
    private int toggle = -1;


    // touch control
    private ItemTouchHelper touchHelper;

    // provide references to the views for each item in list
    public class MyViewHolder extends RecyclerView.ViewHolder {

        // each data item in the list has a text view
        private View mLayoutView;
        private TextView mTextView;
        private LinearLayout editTextLayout;
        private EditText editTextView;
        private Button save;


        public MyViewHolder(View v) {
            super(v);
            mLayoutView = v;
            mTextView = v.findViewById(R.id.textItemView);
            editTextLayout = v.findViewById(R.id.editItemView);
            editTextView = v.findViewById(R.id.textEditView);
            save = v.findViewById(R.id.saveEditButton);

            mLayoutView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(MyViewHolder.this);
                    }
                    return true;
                }
            });

            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle = -1;
                    edit(getAdapterPosition(), editTextView.getText().toString());
                }
            });
        }
    }

    // initalize with data dependent on kind of dataset in list
    public TaskListAdapter(String filename, ListOfTasks dataset) {
        id = filename;
        mDataset = dataset;
    }

    private void edit(int position, String text) {
        System.out.println("tposition: " + position);
        System.out.println("text: " + text);
        System.out.println("oldText: " + mDataset.get(position));
        mDataset.set(position, text);
        notifyDataSetChanged();
    }

    //#region Used by the layout manager


    // create new views
    @Override
    public TaskListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        holder.mLayoutView.setActivated(!holder.mLayoutView.isActivated());

        if(position == toggle) {
            holder.mTextView.setVisibility(View.GONE);
            holder.editTextLayout.setVisibility(View.VISIBLE);
            holder.editTextView.setText(mDataset.get(position));
        }
        else {
            holder.mTextView.setVisibility(View.VISIBLE);
            holder.editTextLayout.setVisibility(View.GONE);
            holder.mTextView.setText(mDataset.get(position));
        }
    }

    public void removeAt(View view, int position) {
        mDataset.remove(position);
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
        mDataset.move(oldPosition, newPosition);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }
    public void toggleLast() {
        toggle = mDataset.size()-1;
        notifyDataSetChanged();
    }

    // #endregion Used by the layout manager

}
