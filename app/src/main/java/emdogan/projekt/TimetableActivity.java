package emdogan.projekt;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        db = new DBAdapter(this);

        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayoutId);

        for(int i = 1; i < 13; ++i)
            for (int j = 1; j < 6; ++j)
                ((TableRow)tableLayout.getChildAt(i)).getChildAt(j).setOnCreateContextMenuListener(this);

        ShowTimetable();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // inicijaliziram id-jeve - samo ako je kliknuto na ona valjana polja daj mogućnost unosa termina

        columnId = ((TableRow) view.getParent()).indexOfChild(view);
        rowId = ((TableLayout) view.getParent().getParent()).indexOfChild((TableRow) view.getParent());

        // Toast.makeText(this, String.valueOf(columnId) + " " + String.valueOf(rowId), Toast.LENGTH_SHORT).show();

        super.onCreateContextMenu(menu, view, menuInfo);
        CreateMenu(menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        return MenuChoice(item);
    }


    private void CreateMenu(Menu menu)
    {
        menu.setQwertyMode(true);

        //dobavi sve predmete iz baze i stavi ih kao iteme

        List<String> stringList = DohvatiPredmete();

        for (String s : stringList)
        {
            MenuItem mnu = menu.add(0, 0, 0, s);
        }
    }

    public List<String> DohvatiPredmete(){ // parametar View??
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

        db.insertInTimetable(item.getTitle().toString(),columnId,rowId+7,rowId+8);

        ShowTimetableEntry(item.getTitle().toString(),columnId,rowId,rowId);

        db.close();
        return true;
    }

    public void ShowTimetableEntry(String nazivPredmeta, int day, int from, int to)
    {
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayoutId);

        TextView textView = (TextView)((TableRow)tableLayout.getChildAt(from)).getChildAt(day);

        textView.setText(nazivPredmeta);
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
                ShowTimetableEntry(subject.getString(1),Integer.parseInt(c.getString(1)),Integer.parseInt(c.getString(2))-7,Integer.parseInt(c.getString(3))-8);
            } while (c.moveToNext());
        }
        db.close();
    }
}