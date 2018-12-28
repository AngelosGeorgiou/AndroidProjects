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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final int ADD_NOTE_REQUEST = 1;

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
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                Toast.makeText(MainActivity.this,"mainActivity", Toast.LENGTH_LONG).show();
                adapter.setNotes(notes);
            }
        });
    }

    public int getIntDate(Calendar c){
        //java calendar months begin with 0 ¯\_(ツ)_/¯
        return c.get(Calendar.YEAR) *10000 + (c.get(Calendar.MONTH)+1)*100 + c.get(Calendar.DAY_OF_MONTH);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION);
            int time = data.getIntExtra(AddNoteActivity.EXTRA_TIME,1);
            int date = getIntDate(getCalendar());

            Note note = new Note(title, description, time, date);
            noteViewModel.insert(note);

            Toast.makeText(this,"Note saved",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Note not saved",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        setCalendar(year, month,dayOfMonth);

        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(getCalendar().getTime());
        setTitle(currentDate);
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
