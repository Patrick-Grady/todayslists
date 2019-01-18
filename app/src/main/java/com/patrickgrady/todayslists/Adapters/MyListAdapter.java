package com.patrickgrady.todayslists.Adapters;

import android.content.Context;
import android.content.Intent;
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

import com.patrickgrady.todayslists.Activities.ListActivity;
import com.patrickgrady.todayslists.Managers.ListManager;
import com.patrickgrady.todayslists.Objects.ListProps;
import com.patrickgrady.todayslists.R;

import java.util.ArrayList;

public class MyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeAndDragHelper.ActionCompletionContract {

    // list data
    private ArrayList<String> mDataset;
    private int toggle = -1;

    // touch control
    ItemTouchHelper touchHelper;

    // provide references to the views for each item in list
    public class FolderViewHolder extends RecyclerView.ViewHolder {

        // each data item in the list has a text view
        private View mLayoutView;
        private TextView mTextView;
        private String key;

        public FolderViewHolder(View v) {
            super(v);
            mLayoutView = v;
            mTextView = v.findViewById(R.id.textItemView);

            mLayoutView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(FolderViewHolder.this);
                    }
                    return false;
                }
            });

            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = mLayoutView.getContext().getApplicationContext();
                    Intent intent = new Intent(context, ListActivity.class);
                    intent.putExtra("key", key);

                    context.startActivity(intent);
                }
            });
        }
    }

    public class StringViewHolder extends RecyclerView.ViewHolder {

        // each data item in the list has a text view
        private View mLayoutView;
        private TextView mTextView;
        private LinearLayout editTextLayout;
        private EditText editTextView;
        private Button save;


        public StringViewHolder(View v) {
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
                        touchHelper.startDrag(StringViewHolder.this);
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

    private void edit(int position, String text) {
        System.out.println("tposition: " + position);
        System.out.println("text: " + text);
        System.out.println("oldText: " + mDataset.get(position));
        ListManager.getInstance().renameChild(mDataset.get(position), text);
        notifyDataSetChanged();
    }

    // initalize with data dependent on kind of dataset in list
    public MyListAdapter(ArrayList<String> dataset) {
        mDataset = dataset;
    }

    //#region Used by the layout manager


    // create new views
    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return ListManager.getInstance(ListManager.FIREBASE).getProps(mDataset.get(position)).type.equals(ListProps.FOLDER) ? 0 : 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a view
        FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);

        switch (viewType) {
            case 0: return new FolderViewHolder(v);
            default: return new StringViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from the dataset at this position
        // - replace the contents of the view with that element
        if(position >= mDataset.size()) {
            removeAt(position);
        }

        ListManager lm = ListManager.getInstance();


        switch (holder.getItemViewType()) {
            case 0:
                FolderViewHolder h0 = (FolderViewHolder)holder;
                h0.key = mDataset.get(position);
                h0.mTextView.setText(lm.getProps(mDataset.get(position)).name);
                break;

            default:
                StringViewHolder h1 = (StringViewHolder) holder;
                h1.mLayoutView.setActivated(!h1.mLayoutView.isActivated());

                if(position == toggle) {
                    h1.mTextView.setVisibility(View.GONE);
                    h1.editTextLayout.setVisibility(View.VISIBLE);
                    h1.editTextView.setText(lm.getProps(mDataset.get(position)).name);
                }
                else {
                    h1.mTextView.setVisibility(View.VISIBLE);
                    h1.editTextLayout.setVisibility(View.GONE);
                    h1.mTextView.setText(lm.getProps(mDataset.get(position)).name);
                }

                break;
        }
    }

    public void removeAt(int position) {
        try {
            ListManager.getInstance().removeChild(mDataset.get(position));
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
            ListManager lm = ListManager.getInstance();
            lm.moveChild(oldPosition, newPosition);
        } catch(Error e) {
            System.out.println("Must initialize dataset");
        }

        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        removeAt(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    // #endregion Used by the layout manager

}
