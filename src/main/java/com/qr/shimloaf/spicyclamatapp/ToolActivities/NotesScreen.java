package com.qr.shimloaf.spicyclamatapp.ToolActivities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

import java.io.File;
import java.util.ArrayList;

public class NotesScreen extends AppCompatActivity {

    ClamatoUtils c;
    RecyclerView notesList;
    LinearLayoutManager notesManager;
    NotesAdapter nAdapter;

    ArrayList<ClamatoNote> notes;

    final String NoteTitle = "New Note";

    private class ClamatoNote {
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
            note = formattedData.substring(8);
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
            ClamatoNote n = new ClamatoNote(f.getName(), noteContent.substring(8), noteContent.substring(0, 7));
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
    private ClamatoNote createNote(String newTitle, String newNote) throws Exception {
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
                ClamatoNote currentNote = new ClamatoNote(f, noteContent);
                int pathPos = currentNote.getPos();

                // If the Current Note's Title is the same as the new Title, and the Pos is already used, remove the potential pos from the list.
                if (currentNote.title.length() >= newTitle.length() && currentNote.getTitleNoPos().equals(newTitle) && potentialPos.contains(pathPos)) {
                    Log.println(Log.VERBOSE, "Blah", "Removed Pos: " + pathPos);
                    potentialPos.remove(new Integer(pathPos));
                }
            }

        }

        if (potentialPos.size() == 0) {
            throw new Exception();
        }

        String newPos = Integer.toString(potentialPos.get(0));
        Log.println(Log.VERBOSE, "Blah", newPos);
        if (potentialPos.get(0) < 10) {
            newPos = "0" + newPos;
        }
        return new ClamatoNote(newTitle, newNote, "0" + newPos + "0000");
    }

    //This function saves a created note to the correct file path
    private void encodeNote(ClamatoNote note) {
        String path = note.title;
        int extPathPos = note.getPos();
        Log.println(Log.VERBOSE, "SameTitleCount: ", note.encoding.substring(1, 3));
        path = path + extPathPos;
        String data = note.encoding + note.note;

        Log.println(Log.VERBOSE, "Data encoded: ", data);
        Log.println(Log.VERBOSE, "At Path: ", path);
        c.writeToFile(data, path, "notes");
    }

    public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder> {
        private ClamatoNote[] noteDatabase;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class NoteHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView textView;
            public ImageView imageView;

            public NoteHolder(View v) {
                super(v);
                imageView = v.findViewById(R.id.note_border);
                textView = v.findViewById(R.id.note_title);
            }
        }

        public NotesAdapter(ClamatoNote[] myDataset) {
            noteDatabase = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public NotesAdapter.NoteHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.note_row_item, parent, false);

            NoteHolder vh = new NoteHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(NoteHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.textView.setText(noteDatabase[position].getTitleNoPos());

            if (noteDatabase[position].getBumper()) {
                holder.textView.setVisibility(View.INVISIBLE);
                holder.imageView.setImageDrawable(getDrawable(R.drawable.add_note));

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        c.quickVibe(50);
                        try {
                            ClamatoNote n = createNote("New Note", "Tap here to Lol!");
                            encodeNote(n);
                            pairAdapter();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Please Name Your Note Something Else!", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return noteDatabase.length;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}




