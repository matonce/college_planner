package emdogan.projekt;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

import static emdogan.projekt.R.id.editText;
import static emdogan.projekt.R.id.homeButton;

public class MainActivity extends AppCompatActivity {

    DBAdapter db;
    int color = Color.parseColor("#c98300");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db =  new DBAdapter(this);

        //za font awesome
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);

        Button button = (Button)findViewById(R.id.homeButton);
        button.setTextColor(Color.parseColor("#c98300"));
    }

    public void DodajPredmet(View view) {
        //dodaj predmet
        db.open();
        EditText mEdit = (EditText)findViewById(R.id.editText);
        long id = db.insertSubject(mEdit.getText().toString(), String.format("#%06X", (0xFFFFFF & color)));
        db.close();
        mEdit.setText("");
/*

        //---get a contact---
        db.open();
        Cursor cu = db.getContact(2);
        if (cu.moveToFirst())
            DisplayContact(cu);
        else
            Toast.makeText(this, "No contact found", Toast.LENGTH_LONG).show();
        db.close();



        //---update contact---
        db.open();
        if (db.updateContact(1, "Wei-Meng Lee", "weimenglee@gmail.com"))
            Toast.makeText(this, "Update successful.", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Update failed.", Toast.LENGTH_LONG).show();
        db.close();



        //---delete a contact---
        db.open();
        if (db.deleteContact(1))
            Toast.makeText(this, "Delete successful.", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Delete failed.", Toast.LENGTH_LONG).show();
        db.close();*/
    }

    public void IspisiPredmete(View v){
        db.open();
        Cursor c = db.getAllSubjects();
        //dohvati sve predmete
        if (c.moveToFirst())
        {
            do {
                DisplaySubject(c);

            } while (c.moveToNext());
        }
        db.close();
    }

    public void DisplaySubject(Cursor c)
    {
        Toast.makeText(this,
                "id: " + c.getString(0) + "\n" +
                        "Name: " + c.getString(1) + "\n",
                Toast.LENGTH_LONG).show();
    }
    
    
    // OPTIONS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        CreateMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return MenuChoice(item);
    }

    private void CreateMenu(Menu menu)
    {
        menu.setQwertyMode(true);
        MenuItem mnu1 = menu.add(0, 0, 0, "Pomodoro timer");
        {
            mnu1.setAlphabeticShortcut('T');
            mnu1.setIcon(R.mipmap.ic_launcher);
        }
    }


    private boolean MenuChoice(MenuItem item)
    {
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(this, Timer.class);
                startActivity(intent);
                Toast.makeText(this, "Odabran je timer",
                        Toast.LENGTH_LONG).show();
                return true;
        }
        return false;
    }

    public void OpenTimetable(View view) {
        Intent intent = new Intent(MainActivity.this, TimetableActivity.class);
        startActivity(intent);
    }
    
    
    public void openTimer (View view){
        Intent intent = new Intent(this, Timer.class);
        startActivity(intent);
    }

    public void changeColorOfButton(View view) {
        Button button = (Button)findViewById(R.id.homeButton);
        button.setTextColor(Color.parseColor("#6e0f94"));
    }

    public void onClickChooseColor(View view) {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, 0, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {}

            @Override
            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                color = i;
            }
        });
        ambilWarnaDialog.show();
    }
}
