package emdogan.projekt;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static emdogan.projekt.R.id.editText;

public class MainActivity extends AppCompatActivity {

    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db =  new DBAdapter(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Toast.makeText(this, width, Toast.LENGTH_LONG).show();
/*
        RelativeLayout.LayoutParams layoutparams;

        ImageButton im = (ImageButton) findViewById(R.id.imageButton1);
        layoutparams = (RelativeLayout.LayoutParams) im.getLayoutParams();
        layoutparams.height = 70;
        layoutparams.width = width/4;
        im.setLayoutParams(layoutparams);
        im.setScaleType(ImageView.ScaleType.FIT_XY);

        im = (ImageButton) findViewById(R.id.imageButton2);
        layoutparams = (RelativeLayout.LayoutParams) im.getLayoutParams();
        layoutparams.height = 70;
        layoutparams.width = width/4;
        im.setLayoutParams(layoutparams);
        im.setScaleType(ImageView.ScaleType.FIT_XY);

        im = (ImageButton) findViewById(R.id.imageButton3);
        layoutparams = (RelativeLayout.LayoutParams) im.getLayoutParams();
        layoutparams.height = 70;
        layoutparams.width = width/4;
        im.setLayoutParams(layoutparams);
        im.setScaleType(ImageView.ScaleType.FIT_XY);

        im = (ImageButton) findViewById(R.id.imageButton4);
        layoutparams = (RelativeLayout.LayoutParams) im.getLayoutParams();
        layoutparams.height = 70;
        layoutparams.width = width/4;
        im.setLayoutParams(layoutparams);
        im.setScaleType(ImageView.ScaleType.FIT_XY);*/
    }

    public void DodajPredmet(View view) {
        //dodaj predmet
        db.open();
        EditText mEdit = (EditText)findViewById(R.id.editText);
        long id = db.insertSubject(mEdit.getText().toString());
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
        db.open();
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


}
