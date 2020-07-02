package com.tangs.myapplication.ui.main.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.tangs.myapplication.R;
import com.tangs.myapplication.databinding.ListItemRecordBinding;
import com.tangs.myapplication.ui.main.data.Record;

public class RecordAdapter extends ListAdapter<Record, RecordAdapter.RecordViewHolder> {

    public RecordAdapter() {
        super(new RecordDiffCallback());
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        private ListItemRecordBinding binding;
        // each data item is just a string in this case
        public RecordViewHolder(ListItemRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setClickListener(v -> {
                Navigation.findNavController(this.binding.getRoot()).navigate(R.id.action_view_fragment_to_record_detail_fragment);
            });
        }

        public void bind(Record record) {
            binding.setRecord(record);
            binding.executePendingBindings();
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        return new RecordViewHolder(ListItemRecordBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.textView.setText(mDataset[position]);
        Record record = getItem(position);
        holder.bind(record);
    }
}

class RecordDiffCallback extends DiffUtil.ItemCallback<Record> {

    @Override
    public boolean areItemsTheSame(@NonNull Record oldItem, @NonNull Record newItem) {
        return oldItem.orderId == newItem.orderId;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Record oldItem, @NonNull Record newItem) {
        return oldItem.equals(newItem);
    }
}