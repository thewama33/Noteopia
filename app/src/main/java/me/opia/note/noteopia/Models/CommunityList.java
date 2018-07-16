package me.opia.note.noteopia.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CommunityList extends RealmObject {

    @PrimaryKey
    private int id;
    private String title;
    private String note;
    private String userName;

    public CommunityList() {
    }

    private String userProfilePic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public CommunityList(int id, String title, String note, String userName, String userProfilePic) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.userName = userName;
        this.userProfilePic = userProfilePic;
    }

}
