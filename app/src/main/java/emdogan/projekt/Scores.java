package emdogan.projekt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Scores extends AppCompatActivity {

    static int id = 1;
    DBAdapter db;
    LinearLayout root;
    int zadnji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);

        root = (LinearLayout) findViewById(R.id.rootId);
        db = new DBAdapter(this);

        showSubjects();
    }

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
        int brojac = 1;
        menu.setQwertyMode(true);

        db.open();
        Cursor c = db.getAllSubjects();

        if (c.moveToFirst())
        {
            do
            {
                MenuItem mnu2 = menu.add(0, brojac, brojac, "Dodaj/uredi ljestvicu za " + c.getString(1) );
                {

                }
                brojac++;
            }
            while (c.moveToNext());
        }
        db.close();
    }

    private boolean MenuChoice(MenuItem item)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Scores.this);
        final int naziv_id = item.getItemId();
        db.open();
        String naziv = db.getSubject(naziv_id).getString(1);
        db.close();
        alertDialog.setTitle(naziv);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText dva = new EditText(this);
        dva.setHint("Bodovi za 2");
        layout.addView(dva); // Notice this is an add method

        final EditText tri = new EditText(this);
        tri.setHint("Bodovi za 3");
        layout.addView(tri); // Another add method

        final EditText cetiri = new EditText(this);
        cetiri.setHint("Bodovi za 4");
        layout.addView(cetiri); // Another add method

        final EditText pet = new EditText(this);
        pet.setHint("Bodovi za 5");
        layout.addView(pet); // Another add method

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Potvrdi",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        db.open();
                        db.insertBounds(naziv_id, Integer.parseInt(dva.getText().toString()), Integer.parseInt(tri.getText().toString()), Integer.parseInt(cetiri.getText().toString()), Integer.parseInt(pet.getText().toString()));
                        db.close();
                        finish();
                        startActivity(getIntent());
                    }
                });

        alertDialog.setNegativeButton("Odustani",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();

        return true;
    }

    private void showSubjects()
    {
        db.open();
        Cursor c = db.getAllSubjects();
        if (c.getCount() == 0)
        {
            RelativeLayout layout = new RelativeLayout(this);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            TextView tv = new TextView(this);
            tv.setId(id++);
            tv.setText("Nemate upisan niti jedan kolegij.");
            tv.setTextSize(20);

            layout.addView(tv, params1);

            root.addView(layout);
        }

        else
        {
            if (c.moveToFirst())
            {
                do
                {
                    addSubject(c.getString(1));
                }
                while (c.moveToNext());
            }
        }
        db.close();
    }

    private void addScores(String type, int earned, int total, RelativeLayout layout, String name)
    {
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(100,100);

        params.addRule(RelativeLayout.BELOW, zadnji);
        params3.addRule(RelativeLayout.BELOW, zadnji);
        params4.addRule(RelativeLayout.BELOW, zadnji);

        TextView tv2 = new TextView(this);
        tv2.setId(id++);
        tv2.setText(type + ": ");
        tv2.setTextSize(20);
        zadnji = tv2.getId();

        Button bod = new Button(this);
        bod.setTag(R.id.name, name);
        bod.setTag(R.id.type, type);

        bod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_bodove(v);
            }
        });
        bod.setText("+");

        params3.addRule(RelativeLayout.RIGHT_OF, tv2.getId());
        TextView tv3 = new TextView(this);
        tv3.setId(id++);
        tv3.setText(earned + "/" + total + "  ");
        tv3.setTextSize(20);
        params4.addRule(RelativeLayout.RIGHT_OF, tv3.getId());

        layout.addView(tv2, params);
        layout.addView(tv3, params3);
        layout.addView(bod, params4);
    }

    private void addSubject(String name)
    {
        RelativeLayout layout = new RelativeLayout(this);
        layout.setId(id++);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView tv1 = new TextView(this);
        zadnji = id;
        tv1.setId(id++);
        tv1.setText(name);
        tv1.setTextSize(30);

        layout.addView(tv1, params1);

        db.open();
        Cursor c1 = db.getTypesByName(name);
        if(c1.getCount() == 0)
        {
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params2.addRule(RelativeLayout.BELOW, tv1.getId());
            TextView tv2 = new TextView(this);
            tv2.setId(id++);
            tv2.setText("Niste unijeli način polaganja za kolegij.");

            RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params4.addRule(RelativeLayout.BELOW, tv2.getId());

            Button btn = new Button(this);
            btn.setText("Unesite podatke");
            btn.setTag(name);

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dodaj_btn (v);
                }
            });
            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params3.addRule(RelativeLayout.BELOW, tv2.getId());
            layout.addView(tv2, params2);
            layout.addView(btn, params3);
        }

        else
        {
            if (c1.moveToFirst())
            {
                do
                {
                    addScores(c1.getString(1), c1.getInt(2), c1.getInt(3), layout, name);
                }
                while (c1.moveToNext());

                Button dodaj = new Button(this);
                dodaj.setText("Dodaj podatke");
                dodaj.setTag(name);

                dodaj.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dodaj_btn (v);
                    }
                });

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, zadnji);
                layout.addView(dodaj, params);
            }
        }


        root.addView(layout);
        db.close();
    }

    public void Ispisi(){
        db.open();
        Cursor c = db.getAllBounds();
        if (c.moveToFirst())
        {
            do {
                Display(c);

            } while (c.moveToNext());
        }
        db.close();
    }

    public void Display(Cursor c)
    {
        Toast.makeText(this,
                "id: " + c.getString(0) + "\n" +
                        "dva: " + c.getString(1) + "\n" +
                        "tri: " + c.getString(2) + "\n" +
                        "cetiri: " + c.getString(3) + "\n" +
                        "pet: " + c.getString(4) + "\n",
                Toast.LENGTH_LONG).show();
    }
    public void openTimetable(View view) {
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
    }
    public void openHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void openTimer(View view){
        Intent intent = new Intent(this, Timer.class);
        startActivity(intent);
    }

    public void openScores(View view) {

    }

    public void dodaj_btn(View v)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Scores.this);
        final String naziv = ((Button) v).getTag().toString();
        alertDialog.setTitle(naziv);

        Context context = v.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText tip = new EditText(context);
        tip.setHint("Tip bodova");
        layout.addView(tip); // Notice this is an add method

        final EditText broj = new EditText(context);
        broj.setHint("Maksimalan broj bodova");
        layout.addView(broj); // Another add method

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Potvrdi",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        db.open();
                        db.insertInScores(naziv, tip.getText().toString(), Integer.parseInt(broj.getText().toString()));
                        db.close();
                        finish();
                        startActivity(getIntent());
                    }
                });

        alertDialog.setNegativeButton("Odustani",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void dodaj_bodove(View v)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Scores.this);
        final String predmet = ((Button) v).getTag(R.id.name).toString();
        final String tip = ((Button) v).getTag(R.id.type).toString();
        alertDialog.setTitle(predmet + " - " + tip);

        Context context = v.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText unos = new EditText(context);
        unos.setHint("Unesite bodove");
        layout.addView(unos); // Notice this is an add method

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Potvrdi",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        db.open();
                        db.updateEarned(predmet, tip, Integer.parseInt(unos.getText().toString()));
                        db.close();
                        finish();
                        startActivity(getIntent());
                    }
                });

        alertDialog.setNegativeButton("Odustani",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
}

