package me.opia.note.noteopia.Models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import me.opia.note.noteopia.R;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {

    public CommunityAdapter(RealmResults<CommunityList> communityList, Context context) {
        this.communityList = communityList;
        this.context = context;
    }

    RealmResults<CommunityList> communityList;
    Context context;

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommunityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.community_row_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CommunityViewHolder holder, int position) {
        final CommunityViewHolder cholder = (CommunityViewHolder) holder;
        CommunityList list = communityList.get(position);

        cholder.communitytxtTitle.setText(list.getTitle()); //user Notes Title
        cholder.communitytxtNotes.setText(list.getNote()); // user Notes
        cholder.communtiytxtUserName.setText(list.getUserName()); // user Name from the Firebase
        Picasso.get()                                           // user Profile Picture
                .load(list.getUserProfilePic())
                .placeholder(R.drawable.user)
                .into(cholder.userImage);

        cholder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder != null) {
                    cholder.expandableLayout.collapse();
                }else {

                    cholder.expandableLayout.expand();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public class CommunityViewHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.cviewTitle) TextView communitytxtTitle;
        @BindView(R.id.userName) TextView communtiytxtUserName;
        @BindView(R.id.ctxtNote) TextView communitytxtNotes;
        @BindView(R.id.cCardview) MaterialRippleLayout cardView;
        @BindView(R.id.userProfilePicture) CircularImageView userImage;
        @BindView(R.id.expandable_layout) ExpandableLayout expandableLayout;

        public CommunityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);


        }
    }


}
