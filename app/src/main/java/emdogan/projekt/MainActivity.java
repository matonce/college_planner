package emdogan.projekt;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

        //za to do
        db.open();
        Cursor c = db.getAllTODO();

        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout1);

        if (c.moveToFirst())
        {
            do {

                RelativeLayout r = new RelativeLayout(getApplicationContext());

                DodajCheckBox(r, c.getInt(2));

                DodajButton(r);

                DodajEditText(r, c.getString(1), c.getInt(2));

                ll.addView(r);


            } while (c.moveToNext());
        }
        db.close();
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
        /*MenuItem mnu1 = menu.add(0, 0, 0, "Pomodoro timer");
        {
            mnu1.setAlphabeticShortcut('T');
            mnu1.setIcon(R.mipmap.ic_launcher);
        }*/
        MenuItem mnu2 = menu.add(0, 0, 0, "Dodaj predmete");
        {

        }

    }


    private boolean MenuChoice(MenuItem item)
    {
        switch (item.getItemId()) {
            /*case 0:
                Intent intent = new Intent(this, Timer.class);
                startActivity(intent);
                //Toast.makeText(this, "Odabran je timer",
                //        Toast.LENGTH_LONG).show();
                return true;*/
            case 0:
                Intent i = new Intent(this, Predmeti.class);
                startActivity(i);
                return true;
        }
        return false;
    }


    public void DodajRed(View view) {

        EditText e = (EditText) findViewById(R.id.editText2);

        if (!e.getText().toString().equals("")){
            LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout1);

            RelativeLayout r = new RelativeLayout(getApplicationContext());

            DodajCheckBox(r, 0);

            DodajButton(r);

            DodajEditText(r, e.getText().toString(), 0);

            ll.addView(r);

            db.open();
            db.insertTODO(e.getText().toString());
            db.close();

            e.setText("");

        }
    }


    public void DodajCheckBox(RelativeLayout r, int checked){
        CheckBox cb = new CheckBox(getApplicationContext());
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout r = (RelativeLayout) v.getParent();
                EditText c = (EditText) r.getChildAt(2);
                if (((CheckBox) v).isChecked()){
                    c.setTextColor(Color.parseColor("#bfbfbf"));
                    c.setPaintFlags(c.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    db.open();
                    db.updateTODOChecked(c.getText().toString(),1);
                    db.close();
                }
                else{
                    c.setTextColor(Color.parseColor("#000000"));
                    c.setPaintFlags(c.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    db.open();
                    db.updateTODOChecked(c.getText().toString(),0);
                    db.close();
                }
            }
        });
        EditText e = (EditText) r.getChildAt(2);
        if (checked == 1){
            cb.setChecked(true);
        }
        else {
            cb.setChecked(false);
        }
        RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lparams.addRule(RelativeLayout.CENTER_VERTICAL);
        cb.setLayoutParams(lparams);
        cb.setId(1);
        //cb.setText(e.getText());

        r.addView(cb);
    }


    public void DodajButton(RelativeLayout r){

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        Button novi = new Button(getApplicationContext());
        novi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout r = (RelativeLayout) v.getParent();
                LinearLayout l = (LinearLayout) r.getParent();
                if (l != null) {
                    EditText c = (EditText) r.getChildAt(2);

                    db.open();
                    db.deleteTODO(c.getText().toString());
                    db.close();

                    l.removeView(r);
                }
            }
        });
        FontManager.markAsIconContainer(novi, iconFont);
        novi.setText(R.string.fa_trash_2);
        RelativeLayout.LayoutParams newP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        newP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        novi.setLayoutParams(newP);
        novi.setId(3);


        r.addView(novi);
    }


    public void DodajEditText(RelativeLayout r, String s, int checked){

        EditText ed = new EditText(getApplicationContext());
        ed.setText(s);
        if (checked == 1){
            ed.setTextColor(Color.parseColor("#bfbfbf"));
            ed.setPaintFlags(ed.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            ed.setTextColor(Color.parseColor("#000000"));
            ed.setPaintFlags(ed.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        ed.addTextChangedListener(new TextWatcher() {

            String prosla;

            @Override
            public void afterTextChanged(Editable s) {
                db.open();
                db.updateTODOPoruka(prosla, s.toString());
                db.close();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                Log.e("greska", s.toString());
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                prosla = s.toString();
                //MainActivity.promijeni();

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
        RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        newParams.addRule(RelativeLayout.RIGHT_OF, 1);
        newParams.addRule(RelativeLayout.LEFT_OF, 3);
        ed.setLayoutParams(newParams);
        ed.setId(2);

        r.addView(ed);
    }

    public void ispišiBazu(View view) {
        db.open();
        Cursor c = db.getAllTODO();
        if (c.moveToFirst())
        {
            do {
                Toast.makeText(this,
                        "id: " + c.getString(0) + "\n" +
                                "poruka: " + c.getString(1) + "\n" +
                                "checked: " + c.getString(2),
                        Toast.LENGTH_LONG).show();

            } while (c.moveToNext());
        }
        db.close();
    }

    public void izbrišiBazu(View view) {
        db.open();
        Cursor c = db.getAllTODO();
        if (c.moveToFirst())
        {
            do {
                db.deleteTODO(c.getString(1));

            } while (c.moveToNext());
        }
        db.close();
    }

    public void openTimetable(View view) {
        Intent intent = new Intent(MainActivity.this, TimetableActivity.class);
        startActivity(intent);
    }


    public void openTimer (View view){
        Intent intent = new Intent(this, Timer.class);
        startActivity(intent);
    }

    public void openHome(View view){

    }

    public void changeColorOfButton(View view) {
        Button button = (Button)findViewById(R.id.homeButton);
        button.setTextColor(Color.parseColor("#6e0f94"));
    }
}
