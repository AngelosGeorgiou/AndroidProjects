package com.example.angelosgeorgiou.timetrack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;

    private NoteViewModel noteViewModel;
    public Calendar calendar;
    private final NoteAdapter adapter = new NoteAdapter();


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = Calendar.getInstance();


        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        setTitle(currentDate);

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
//        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
//            @Override
//            public void onChanged(@Nullable List<Note> notes) {
//                Toast.makeText(MainActivity.this,"mainActivity", Toast.LENGTH_LONG).show();
//                adapter.submitList(notes);
//            }
//        });

        noteViewModel.getSearchResults().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                adapter.submitList(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {



            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                noteViewModel.getDateNotes(getIntDate(getCalendar()));
                Toast.makeText(MainActivity.this,"Note deleted", Toast.LENGTH_SHORT ).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID,note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE,note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION,note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_TIME,note.getTime());
                startActivityForResult(intent,EDIT_NOTE_REQUEST);

            }
        });

        adapter.setOnItemLongClickListener(new NoteAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(Note note) {
                noteViewModel.delete(note);
                noteViewModel.getDateNotes(getIntDate(getCalendar()));
                Toast.makeText(MainActivity.this,"Note deleted", Toast.LENGTH_SHORT ).show();
                return false;
            }
        });
        noteViewModel.getDateNotes(getIntDate(getCalendar()));
    }

    public int getIntDate(Calendar c){
        //java calendar months begin with 0 ¯\_(ツ)_/¯
        return c.get(Calendar.YEAR) *10000 + (c.get(Calendar.MONTH)+1)*100 + c.get(Calendar.DAY_OF_MONTH);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int time = data.getIntExtra(AddEditNoteActivity.EXTRA_TIME,1);
            int date = getIntDate(getCalendar());

            Note note = new Note(title, description, time, date);
            noteViewModel.insert(note);

            Toast.makeText(this,"Note saved",Toast.LENGTH_SHORT).show();
        }else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID,-1);

            if (id == -1){
                Toast.makeText(this,"Note can't be updated",Toast.LENGTH_LONG);
                return;
            }
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int time = data.getIntExtra(AddEditNoteActivity.EXTRA_TIME,1);
            int date = getIntDate(getCalendar());

            Note note = new Note(title, description, time, date);
            note.setId(id);
            noteViewModel.update(note);

            Toast.makeText(this,"Note updated",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(this,"Note not saved",Toast.LENGTH_SHORT).show();
        }
        noteViewModel.getDateNotes(getIntDate(getCalendar()));


    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        setCalendar(year, month,dayOfMonth);

        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(getCalendar().getTime());
        setTitle(currentDate);
        noteViewModel.getDateNotes(getIntDate(getCalendar()));
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(int year, int month, int day) {
        this.calendar.set(Calendar.YEAR,year);
        this.calendar.set(Calendar.MONTH,month);
        this.calendar.set(Calendar.DAY_OF_MONTH,day);
    }

    public void selectDate() {
        Bundle bundleCalendar = new Bundle();
        bundleCalendar.putInt("calendar",getIntDate(calendar));
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.setArguments(bundleCalendar);
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.select_date_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.select_date:
                selectDate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
