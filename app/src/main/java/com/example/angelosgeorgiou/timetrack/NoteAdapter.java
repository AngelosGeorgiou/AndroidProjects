package com.example.angelosgeorgiou.timetrack;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends ListAdapter<Note,NoteAdapter.NoteHolder> {
    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;

    public NoteAdapter() {
        super(DIFF_CALLBACK);

    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getTime( ) == newItem.getTime();
        }
    };

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)  {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = getItem(position);
        int minutes = currentNote.getTime() % 100;
        String sZero = "0";
        String sMinute = String.valueOf(minutes);
        if (minutes < 10)
            sMinute = sZero.concat(sMinute);

        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getDescription());
        holder.textViewTime.setText(String.valueOf(currentNote.getTime() / 100) + ":" + sMinute);
    }


    public Note getNoteAt(int position) {
        return getItem(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewTime;

        public NoteHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewTime = itemView.findViewById(R.id.text_view_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (longListener!= null && position != RecyclerView.NO_POSITION) {
                        longListener.onItemLongClicked(getItem(position));
                        return true;
                    }
                    return false;
                }
            });


        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(this.getAdapterPosition(),121,0,"Delete");
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longListener){ this.longListener= longListener;
    }
}