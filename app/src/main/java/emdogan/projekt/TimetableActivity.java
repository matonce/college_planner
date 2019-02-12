package emdogan.projekt;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TimetableActivity extends AppCompatActivity {
    DBAdapter db;
    int rowId;
    int columnId;
    final String delete = "Delete?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.spaceView2), iconFont);

        db = new DBAdapter(this);

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayoutId);

        for(int i = 1; i < 13; ++i)
            for (int j = 1; j < 6; ++j)
                ((TableRow)tableLayout.getChildAt(i)).getChildAt(j).setOnCreateContextMenuListener(this);

        ShowTimetable();

        Button button = (Button)findViewById(R.id.timetableButton);
        button.setTextColor(Color.parseColor("#c98300"));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayoutId);

        columnId = ((TableRow) view.getParent()).indexOfChild(view);
        rowId = ((TableLayout) view.getParent().getParent()).indexOfChild((TableRow) view.getParent());
        // Toast.makeText(this, String.valueOf(columnId) + " " + String.valueOf(rowId), Toast.LENGTH_SHORT).show();

        super.onCreateContextMenu(menu, view, menuInfo);

        TextView textView = (TextView) view;

        if (textView.getText() == "")
            CreateMenu1(menu);
        else
            CreateMenu2(menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if (item.getTitle() == delete)
            return DeleteTimetableEntry();
        return MenuChoice(item);
    }

    private boolean DeleteTimetableEntry()
    {
        db.open();

        boolean res =  db.deleteTimetableEntry(rowId, columnId);

        db.close();

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayoutId);

        TextView textView = (TextView)(((TableRow)tableLayout.getChildAt(rowId)).getChildAt(columnId));
        textView.setText("");
        textView.setBackgroundColor(0);

        if (res)
            return true;
        else
            return false;
    }

    private void CreateMenu1(Menu menu)
    {
        menu.setQwertyMode(true);

        List<String> stringList = GetAllSubjects();

        for (String s : stringList)
            menu.add(0, 0, 0, s);
    }

    private void CreateMenu2(Menu menu)
    {
        menu.setQwertyMode(true);

        menu.add(0, 0, 0,  delete);
    }

    public List<String> GetAllSubjects()
    {
        db.open();
        Cursor c = db.getAllSubjects();
        List<String> stringList = new ArrayList<>();

        if (c.moveToFirst())
        {
            do {
                stringList.add(c.getString(1));
            } while (c.moveToNext());
        }
        db.close();
        return stringList;
    }

    private boolean MenuChoice(MenuItem item)
    {
        db.open();
        // ovdje mozda napraviti ako se je kliknulo na isti da se nista ne dogada, a inace promijeni

        Cursor c = db.getSubjectByName(item.getTitle().toString());

        db.insertInTimetable(c.getInt(0), columnId,rowId+7);

        ShowTimetableEntry(item.getTitle().toString(), c.getString(2),columnId,rowId);

        db.close();
        return true;
    }

    public void ShowTimetableEntry(String nazivPredmeta, String color, int day, int when)
    {
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayoutId);

        TextView textView = (TextView)((TableRow)tableLayout.getChildAt(when)).getChildAt(day);

        textView.setText(nazivPredmeta);

        textView.setBackgroundColor(Color.parseColor(color));
    }

    public void ShowTimetable()
    {
        db.open();
        Cursor c = db.getAllTimetableEntries();

        if (c.moveToFirst())
        {
            do {
                Cursor subject = db.getSubject(c.getInt(0));
                subject.moveToFirst();
                // Toast.makeText(this, "ime: " + subject.getString(1) + ", dan: " + c.getString(1) + ", sat: " + String.valueOf(c.getInt(2)-7), Toast.LENGTH_SHORT).show();
                ShowTimetableEntry(subject.getString(1), subject.getString(2), c.getInt(1),c.getInt(2)-7);
            } while (c.moveToNext());
        }
        db.close();
    }

    public void onClickDeleteTimetable(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(TimetableActivity.this).create();
        alertDialog.setMessage("Delete all entries?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllTimetableEntries();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void deleteAllTimetableEntries() {
        db.open();
        Cursor c = db.getAllTimetableEntries();

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayoutId);

        if (c.moveToFirst())
        {
            do {
                db.deleteTimetableEntry(c.getInt(2)-7, c.getInt(1));

                TextView textView = (TextView)(((TableRow)tableLayout.getChildAt(c.getInt(2)-7)).getChildAt(c.getInt(1)));
                textView.setText("");
                textView.setBackgroundColor(0);

            } while (c.moveToNext());
        }
        db.close();
    }

    public void openTimer(View view) {
        Intent intent = new Intent(this, Timer.class);
        startActivity(intent);
    }

    public void openHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openTimetable(View v){

    }

    public void openScores(View view) {
        Intent intent = new Intent(this, Scores.class);
        startActivity(intent);
    }

    public void changeColorOfButton(View view) {
        Button button = (Button)findViewById(R.id.timetableButton);
        button.setTextColor(Color.parseColor("#6e0f94"));
    }
}