package me.opia.note.noteopia.Models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import me.opia.note.noteopia.R;

import static android.support.constraint.Constraints.TAG;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NotesViewHolder> {


    Context context;
    RealmResults<NoteList> list;
    public NoteAdapter(Context context, RealmResults<NoteList> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        NotesViewHolder holder = new NotesViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {

        final NoteList listy = list.get(position);

        try {

            if (listy != null) {
                holder.txtTitle.setText(listy.getTitle());
            }
            if (listy != null) {
                holder.txtNote.setText(listy.getNote());
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent intent = new Intent(context,addNoteActivity.class);
//
//                        intent.putExtra("id",listy.getId());
//                        intent.putExtra("title",listy.getTitle());
//                        intent.putExtra("note",listy.getNote());
//
//                    context.startActivity(intent);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onBindViewHolder: " + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.viewTitle) TextView txtTitle;
        @BindView(R.id.viewNote) TextView txtNote;
        @BindView(R.id.Cardview) MaterialRippleLayout cardView;

        public NotesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


        }
    }
}
