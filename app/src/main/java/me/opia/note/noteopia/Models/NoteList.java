package me.opia.note.noteopia.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NoteList extends RealmObject {

    public NoteList() {
    }

    @PrimaryKey private int id;
    private String title;
    private String note;

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

    public NoteList(int id, String title, String note) {
        this.id = id;
        this.title = title;
        this.note = note;
    }

}
