package com.qr.shimloaf.spicyclamatapp.ToolActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

import java.io.File;
import java.util.ArrayList;

public class NotesScreen extends AppCompatActivity {


    ClamatoUtils c;
    RecyclerView notesList;
    LinearLayoutManager notesManager;
    NotesAdapter nAdapter;

    boolean viewingNote = false;
    ClamatoNote currentNote = null;

    ArrayList<ClamatoNote> notes;

    final String NoteTitle = "New Note";
    final String NoteMessage = "Tap anywhere to edit this note!";

    public static class TitleDuplicateException extends Exception {
        public TitleDuplicateException(String errorMessage) {
            super(errorMessage);
        }
    }

    private static class ClamatoNote {
        String title;
        String note;
        String encoding;

        ClamatoNote(String p_title, String p_note, String p_encoding) {
            title = p_title;
            note = p_note;
            encoding = p_encoding;
        }

        ClamatoNote(File f, String formattedData) {
            title = f.getName();
            encoding = formattedData.substring(0, 7);
            note = formattedData.substring(7);
        }

        public int getPos() {
            return Integer.parseInt(encoding.substring(1, 3));
        }

        public String getTitleNoPos() {

            int pos = getPos();
            if (pos < 10) {
                return title.substring(0, title.length() - 1);
            } else {
                return title.substring(0, title.length() - 2);
            }
        }

        public boolean getBumper() {
            return encoding.substring(0, 1).equals("1");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        c = new ClamatoUtils(this.getApplication());

        notesList = (RecyclerView) findViewById(R.id.notes_list);
        notesList.setHasFixedSize(true);

        notesManager = new LinearLayoutManager(this);
        notesList.setLayoutManager(notesManager);

        pairAdapter();

        FloatingActionButton b = findViewById(R.id.save_note_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.quickVibe(50);
                swapScreen();
            }
        });

        ConstraintLayout cl = findViewById(R.id.noteDisplayConstraintLayout);
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.quickVibe(50);
                clearTrash();
            }
        });
    }

    private boolean clearTrash() {
        boolean cleared = false;
        for (int n = 0; n < notesManager.getChildCount(); n++) {
            if (notesManager.findViewByPosition(n).findViewById(R.id.trash_display).getVisibility() == View.VISIBLE) {
                cleared = true;
            }
            notesManager.findViewByPosition(n).findViewById(R.id.trash_display).setVisibility(View.INVISIBLE);
            notesManager.findViewByPosition(n).findViewById(R.id.note_title).setVisibility(View.VISIBLE);
        }
        pairAdapter();
        return cleared;
    }

    private ArrayList<ClamatoNote> initializeNotesList() {
        File notesDir = getDir("notes", MODE_PRIVATE);
        File[] noteFiles = notesDir.listFiles();
        ArrayList<ClamatoNote> newNotesList = new ArrayList<>();
        if (noteFiles == null || noteFiles.length == 0) {

            ClamatoNote n = new ClamatoNote("Bumper", "", "1000000");
            newNotesList.add(n);

            return newNotesList;
        }

        int index = 0;
        for (File f : noteFiles) {
            String noteContent = c.readFromFile(f);
            ClamatoNote n = new ClamatoNote(f.getName(), noteContent.substring(7), noteContent.substring(0, 7));
            newNotesList.add(n);
            index++;
        }
        ClamatoNote n = new ClamatoNote("Bumper", "", "1000000");
        newNotesList.add(n);

        return newNotesList;
    }

    private void pairAdapter() {
        notes = initializeNotesList();
        nAdapter = new NotesAdapter(notes.toArray(new ClamatoNote[0]));
        notesList.setAdapter(nAdapter);
    }

    //This function creates a new note with a given Title and Note
    @SuppressLint("UseValueOf")
    private ClamatoNote createNote(String newTitle, String newNote) throws TitleDuplicateException {
        File notesDir = getDir("notes", MODE_PRIVATE);
        File[] noteFiles = notesDir.listFiles();

        ArrayList<Integer> potentialPos = new ArrayList<>();
        for (int n = 0; n < 100; n++) {
            potentialPos.add(n);
        }

        int currentMax = 0;
        if (noteFiles != null) {

            //Loop through all current files, and if a title is the same, remove it's extended file path pos from the list of candidates
            for (File f : noteFiles) {

                String noteContent = c.readFromFile(f);
                ClamatoNote curNote = new ClamatoNote(f, noteContent);
                int pathPos = curNote.getPos();

                // If the Current Note's Title is the same as the new Title, and the Pos is already used, remove the potential pos from the list.
                if (curNote.title.length() >= newTitle.length() && curNote.getTitleNoPos().equals(newTitle) && potentialPos.contains(pathPos)) {
                    Log.println(Log.VERBOSE, "Blah", "Removed Pos: " + pathPos);
                    potentialPos.remove(new Integer(pathPos));
                }
            }

        }

        if (potentialPos.size() == 0) {
            throw new TitleDuplicateException("Too many of the same title. Note could not be created.");
        }

        String newPos = Integer.toString(potentialPos.get(0));
        Log.println(Log.VERBOSE, "Note Created at: ", newPos);
        if (potentialPos.get(0) < 10) {
            newPos = "0" + newPos;
        }
        return new ClamatoNote(newTitle + potentialPos.get(0), newNote, "0" + newPos + "0000");
    }

    private boolean deleteNote(ClamatoNote noteDelete) {
        File d = getApplicationContext().getDir("notes", Context.MODE_PRIVATE);
        File fdelete = new File(d, noteDelete.title);
        Log.println(Log.VERBOSE, "Note Deleted at: ", fdelete.getAbsolutePath());

        if (fdelete.exists()) {
            return fdelete.delete();
        }
        return false;
    }

    //This function saves a created note to the correct file path
    private void encodeNote(ClamatoNote note) {
        String path = note.title;
        String data = note.encoding + note.note;

        c.writeToFile(data, path, "notes");
    }

    private void swapScreen() {
        RecyclerView r = findViewById(R.id.notes_list);
        TextView notesTitle = findViewById(R.id.notes_title);
        EditText viewNote = findViewById(R.id.noteViewNote);
        EditText viewTitle = findViewById(R.id.noteViewTitle);
        LinearLayout l = findViewById(R.id.noteView);
        FloatingActionButton b = findViewById(R.id.save_note_button);
        if (viewingNote) {
            String newTitle = (String) viewTitle.getText().toString();
            String newNote = (String) viewNote.getText().toString();
            try {
                deleteNote(currentNote);
                encodeNote(createNote(newTitle, newNote));
                currentNote = null;
            } catch (TitleDuplicateException e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            pairAdapter();

            viewingNote = false;
            notesTitle.setVisibility(View.VISIBLE);
            r.setVisibility(View.VISIBLE);
            l.setVisibility(View.INVISIBLE);
            b.setVisibility(View.INVISIBLE);
        } else {
            viewingNote = true;
            notesTitle.setVisibility(View.INVISIBLE);
            r.setVisibility(View.INVISIBLE);
            l.setVisibility(View.VISIBLE);
            b.setVisibility(View.VISIBLE);
        }
    }

    private void setNoteContent(ClamatoNote note) {
        TextView titleText = findViewById(R.id.noteViewTitle);
        TextView noteText = findViewById(R.id.noteViewNote);
        titleText.setText(note.getTitleNoPos());
        noteText.setText(note.note);
    }

    @Override
    public void onBackPressed() {
        if (viewingNote) {
            swapScreen();
        } else if (clearTrash()) {
            Log.d("Trash", "Trash Taken Out");
        } else {
            super.onBackPressed();
        }
    }


    public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder> {
        private ClamatoNote[] noteDatabase;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class NoteHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            private TextView textView;
            private ImageView imageView;
            private ImageView trashIcon;

            private boolean trashtivated;

            public NoteHolder(View v) {
                super(v);
                imageView = v.findViewById(R.id.note_border);
                textView = v.findViewById(R.id.note_title);
                trashIcon = v.findViewById(R.id.trash_display);
                trashtivated = false;
            }
        }

        public NotesAdapter(ClamatoNote[] myDataset) {
            noteDatabase = myDataset;
        }

        @Override
        public NotesAdapter.NoteHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.note_row_item, parent, false);

            NoteHolder vh = new NoteHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final NoteHolder holder, final int position) {
            holder.textView.setText(noteDatabase[position].getTitleNoPos());
            holder.trashIcon.setVisibility(View.INVISIBLE);

            if (noteDatabase[position].getBumper()) {
                holder.textView.setVisibility(View.INVISIBLE);
                holder.imageView.setImageDrawable(getDrawable(R.drawable.add_note));

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        c.quickVibe(50);
                        try {
                            ClamatoNote n = createNote(NoteTitle, NoteMessage);
                            encodeNote(n);
                            pairAdapter();
                        } catch (TitleDuplicateException e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                holder.textView.setVisibility(View.VISIBLE);
                holder.imageView.setImageDrawable(getDrawable(R.drawable.note_border));

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!holder.trashtivated) {
                            c.quickVibe(50);
                            setNoteContent(noteDatabase[position]);
                            currentNote = (noteDatabase[position]);
                            swapScreen();
                        } else {
                            deleteNote(noteDatabase[position]);
                            pairAdapter();
                        }
                    }
                });

                holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        c.quickVibe(50);
                        if (!holder.trashtivated) {
                            holder.trashIcon.setVisibility(View.VISIBLE);
                            holder.textView.setVisibility(View.INVISIBLE);
                            holder.trashtivated = true;
                        } else {
                            holder.trashIcon.setVisibility(View.INVISIBLE);
                            holder.textView.setVisibility(View.VISIBLE);
                            holder.trashtivated = false;
                        }
                        return true;
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return noteDatabase.length;
        }
    }

}




