package emdogan.projekt;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by emdogan on 2/12/19.
 */

public class Predmeti extends AppCompatActivity {
    DBAdapter db;
    int color = Color.parseColor("#c98300");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predmeti);

        db = new DBAdapter(this);

    }

    public void DodajPredmet(View view) {
        //dodaj predmet
        db.open();
        EditText mEdit = (EditText)findViewById(R.id.editText);
        long id = db.insertSubject(mEdit.getText().toString(), String.format("#%06X", (0xFFFFFF & color)));
        db.close();
        mEdit.setText("");
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