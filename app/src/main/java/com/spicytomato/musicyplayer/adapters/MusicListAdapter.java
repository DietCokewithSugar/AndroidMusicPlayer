package com.spicytomato.musicyplayer.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.spicytomato.musicyplayer.R;
import com.spicytomato.musicyplayer.model.Music;

public class MusicListAdapter extends ListAdapter<Music, MusicListAdapter.InnerHolder> {


    private OnItemClickListener onItemClickListener;

    public MusicListAdapter() {
        super(new DiffUtil.ItemCallback<Music>() {
            @Override
            public boolean areItemsTheSame(@NonNull Music oldItem, @NonNull Music newItem) {
                if (oldItem == newItem) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Music oldItem, @NonNull Music newItem) {
                if (oldItem.getMusicName() == newItem.getMusicName() &&
                        oldItem.getContentUri().equals(newItem.getContentUri()) ) {
                    return true;
                }
                return false;
            }
        });
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cell_recyclerview_layout, null, false);

        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        holder.mTextView.setText(getItem(position).getMusicName());

        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: ");
                if (null != onItemClickListener) {
                    Log.d("TAG", "onClick: 1");
                    onItemClickListener.onClick(position);
                }
            }
        });
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private final ConstraintLayout mConstraintLayout;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.musicName);
            mConstraintLayout = itemView.findViewById(R.id.itemView);

        }
    }

    public interface OnItemClickListener {
        public void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
    }


}
