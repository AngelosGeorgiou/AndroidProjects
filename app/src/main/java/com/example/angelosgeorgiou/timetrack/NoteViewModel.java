package com.example.angelosgeorgiou.timetrack;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NoteViewModel extends AndroidViewModel {

    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;
    private MutableLiveData<List<Note>> searchResults;
    private LiveData<List<String>> allTitles;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
        searchResults = repository.getSearchResults();
        allTitles = repository.getAllTitles();
    }

    public void insert(Note note){
        repository.insert(note);
    }

    public void update(Note note){
        repository.update(note);
    }

    public void delete(Note note){
        repository.delete(note);
    }

    public void deleteAllNotes(){
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    public LiveData<List<String>> getAllTitles(){ return allTitles;}

    MutableLiveData<List<Note>> getSearchResults(){
        return searchResults;
    }

    public void getDateNotes(int date){ repository.getDateNotes(date);}

//    public void updateTitles() { repository.updateTitles();}
}
