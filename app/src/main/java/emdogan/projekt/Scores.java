package emdogan.projekt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
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
import android.widget.ProgressBar;
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

        Button button = (Button) findViewById(R.id.scoresButton);
        button.setTextColor(Color.parseColor("#c98300"));

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
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuChoice(item);
    }

    private void CreateMenu(Menu menu) {
        int brojac = 1;
        menu.setQwertyMode(true);

        db.open();
        Cursor c = db.getAllSubjects();

        if (c.moveToFirst()) {
            do {
                MenuItem mnu2 = menu.add(0, brojac, brojac, "Dodaj/uredi ljestvicu za " + c.getString(1));
                {

                }
                brojac++;
            }
            while (c.moveToNext());
        }
        db.close();
    }

    private boolean MenuChoice(MenuItem item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Scores.this);
        final int naziv_id = item.getItemId();
        db.open();
        final String naziv = db.getSubject(naziv_id).getString(1);
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
                    public void onClick(DialogInterface dialog, int which) {
                        db.open();
                        db.insertBounds(naziv, Integer.parseInt(dva.getText().toString()), Integer.parseInt(tri.getText().toString()), Integer.parseInt(cetiri.getText().toString()), Integer.parseInt(pet.getText().toString()));
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

    private void showSubjects() {
        db.open();
        Cursor c = db.getAllSubjects();
        if (c.getCount() == 0) {
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
        } else {
            if (c.moveToFirst()) {
                do {
                    addSubject(c.getString(1));
                }
                while (c.moveToNext());
            }
        }
        db.close();
    }

    private void addScores(String type, int earned, int total, RelativeLayout layout, String name) {
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(100, 100);
        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(100, 100);

        params.addRule(RelativeLayout.BELOW, zadnji);
        params3.addRule(RelativeLayout.BELOW, zadnji);
        params4.addRule(RelativeLayout.BELOW, zadnji);
        params5.addRule(RelativeLayout.BELOW, zadnji);

        TextView tv2 = new TextView(this);
        tv2.setId(id++);
        tv2.setText(type + ": ");
        tv2.setTextSize(25);
        zadnji = tv2.getId();

        Button bod = new Button(this);
        bod.setTag(R.id.name, name);
        bod.setId(id++);
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
        tv3.setTextSize(25);
        params4.addRule(RelativeLayout.RIGHT_OF, tv3.getId());

        Button brisi = new Button(this);
        brisi.setTag(R.id.name, name);
        brisi.setTag(R.id.type, type);
        params5.addRule(RelativeLayout.RIGHT_OF, bod.getId());

        brisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brisi_bodove(v);
            }
        });
        brisi.setText("-");

        layout.addView(tv2, params);
        layout.addView(tv3, params3);
        layout.addView(bod, params4);
        layout.addView(brisi, params5);
    }

    private void addSubject(String name) {
        RelativeLayout layout = new RelativeLayout(this);
        layout.setId(id++);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView tv1 = new TextView(this);
        zadnji = id;
        tv1.setId(id++);
        tv1.setText(name);
        tv1.setTextSize(35);
        tv1.setTextColor(Color.parseColor("#6e0f94"));

        layout.addView(tv1, params1);

        int zadnji_btn_id = 0;
        int zadnji_rez_id = 0;
        int ukupni_total = find_total_sum(name);
        int ukupni_earned = find_earned_sum(name);

        db.open();
        Cursor c1 = db.getTypesByName(name);
        //ako nisu upisani nikakvi bodovi
        if (c1.getCount() == 0) {
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
            btn.setId(id++);

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dodaj_btn(v);
                }
            });
            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params3.addRule(RelativeLayout.BELOW, tv2.getId());
            layout.addView(tv2, params2);
            layout.addView(btn, params3);
        }
        //upisani su bodovi
        else {
            if (c1.moveToFirst()) {
                do {
                    addScores(c1.getString(1), c1.getInt(2), c1.getInt(3), layout, name);
                }
                while (c1.moveToNext());

                //nakon svih bodova ide gumb za dodavanje
                Button dodaj = new Button(this);
                dodaj.setText("Dodaj podatke");
                dodaj.setTag(name);
                zadnji_btn_id = id;
                dodaj.setId(id++);

                dodaj.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dodaj_btn(v);
                    }
                });

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, zadnji);
                layout.addView(dodaj, params);

                TextView rez = new TextView(this);
                zadnji_rez_id = id;
                rez.setId(id++);
                rez.setText("Rezultat: " + ukupni_earned + "/" + ukupni_total);
                rez.setTextSize(20);
                RelativeLayout.LayoutParams params_rez = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                params_rez.addRule(RelativeLayout.BELOW, zadnji_btn_id);

                layout.addView(rez, params_rez);
            }

            db.open();
            Cursor c2 = db.getBounds(name);
            //ako je upisana neka ljestvica onda imam poruke ispisane statistike nakon gumba
            if (c2.getCount() != 0) {

                TextView ocjena = new TextView(this);
                RelativeLayout.LayoutParams params_oc = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                params_oc.addRule(RelativeLayout.BELOW, zadnji_rez_id);
                zadnji_rez_id = id; //ispod ocjene ce ici progress bar
                ocjena.setId(id++);
                int trenutna = findGrade(name, ukupni_earned);

                if (trenutna != 5) {
                    int fali = needPoints(name, ukupni_earned, trenutna + 1);
                    ocjena.setText("Trenutna ocjena: " + trenutna + " (" + fali + " do više ocjene)");
                } else ocjena.setText("Trenutna ocjena: 5");
                ocjena.setTextSize(20);

                layout.addView(ocjena, params_oc);
            }

            if(c1.getCount() != 0)
            {
                ProgressBar progressBar = new ProgressBar(Scores.this, null, android.R.attr.progressBarStyleHorizontal);
                progressBar.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params_progress = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                params_progress.addRule(RelativeLayout.BELOW, zadnji_rez_id);

                progressBar.setMax(ukupni_total);
                progressBar.setProgress(ukupni_earned);

                layout.addView(progressBar, params_progress);
            }
        }

        root.addView(layout);
        db.close();
    }

    public void Ispisi() {
        db.open();
        Cursor c = db.getAllBounds();
        if (c.moveToFirst()) {
            do {
                Display(c);

            } while (c.moveToNext());
        }
        db.close();
    }

    public void Display(Cursor c) {
        Toast.makeText(this,
                "id: " + c.getString(0) + "\n" +
                        "dva: " + c.getString(1) + "\n" +
                        "tri: " + c.getString(2) + "\n" +
                        "cetiri: " + c.getString(3) + "\n" +
                        "pet: " + c.getString(4) + "\n",
                Toast.LENGTH_LONG).show();
    }

    public void dodaj_btn(View v) {
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
                    public void onClick(DialogInterface dialog, int which) {
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

    public void dodaj_bodove(View v) {
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
                    public void onClick(DialogInterface dialog, int which) {
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

    public void brisi_bodove(View v)
    {
        final String predmet = ((Button) v).getTag(R.id.name).toString();
        final String tip = ((Button) v).getTag(R.id.type).toString();
        db.open();
        db.deleteScore(predmet, tip);
        db.close();
        finish();
        startActivity(getIntent());
    }

    int find_total_sum(String name) {
        int sum = 0;
        db.open();
        Cursor c = db.getTotals(name);

        if (c.moveToFirst()) {
            do {
                sum += c.getInt(0);
            }
            while (c.moveToNext());
        }
        db.close();

        return sum;
    }

    int find_earned_sum(String name) {
        int sum = 0;
        db.open();
        Cursor c = db.getEarned(name);

        if (c.moveToFirst()) {
            do {
                sum += c.getInt(0);
            }
            while (c.moveToNext());
        }
        db.close();

        return sum;
    }

    int findGrade(String name, int earned)
    {
        db.open();
        int ret = db.getGrade(name, earned);
        db.close();
        return ret;
    }

    int needPoints(String name, int earned, int ocjena)
    {
        db.open();
        int ret = db.getNumber(name, earned, ocjena);
        db.close();
        return ret;
    }

    public void openTimetable(View view) {
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
    }

    public void openHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openTimer(View view) {
        Intent intent = new Intent(this, Timer.class);
        startActivity(intent);
    }

    public void openScores(View view) {

    }

    public void changeColorOfButton(View view) {
        Button button = (Button) findViewById(R.id.scoresButton);
        button.setTextColor(Color.parseColor("#6e0f94"));
    }
}

