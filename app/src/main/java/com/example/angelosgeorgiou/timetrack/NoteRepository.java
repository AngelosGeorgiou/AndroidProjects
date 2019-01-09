package com.example.angelosgeorgiou.timetrack;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NoteRepository implements AsyncResult{
    private MutableLiveData<List<Note>> searchResults = new MutableLiveData<>();
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    public void insert(Note note) {
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void update(Note note) {
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note) {
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAllNotes() {
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    public LiveData<List<Note>> getAllNotes() {

        return allNotes;
    }

    public void getDateNotes(int date){
        queryAsyncTask task = new queryAsyncTask(noteDao);
        task.delegate = this;
        task.execute(date);
    }

    public MutableLiveData<List<Note>> getSearchResults(){
        return searchResults;
    }

    @Override
    public void asynFinished(List<Note> results) {
        searchResults.setValue(results);
    }

    private static class queryAsyncTask extends AsyncTask<Integer, Void, List<Note>>{

        private NoteDao asyncNoteDao;
        private NoteRepository delegate = null;

        queryAsyncTask(NoteDao noteDao){
            asyncNoteDao = noteDao;
        }

        @Override
        protected List<Note> doInBackground(Integer... integers) {
            return asyncNoteDao.getDateNotes(integers[0]);
        }

        @Override
        protected void onPostExecute(List<Note> result){
            delegate.asynFinished(result);
        }
    }

    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        private InsertNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;

        private DeleteAllNotesAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }
}