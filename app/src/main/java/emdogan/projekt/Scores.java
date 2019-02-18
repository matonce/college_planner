package emdogan.projekt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
        FontManager.markAsIconContainer(findViewById(R.id.include), iconFont);

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

        //menu za izbor predmeta za unos ljestvice za ocjene
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
        final EditText tri = new EditText(this);
        final EditText cetiri = new EditText(this);
        final EditText pet = new EditText(this);

        db.open();
        Cursor c = db.getBounds(naziv);
        //ako ljestvica jos nije unesena, prikazuje se ovo u textboxu
        if (c.getCount() == 0)
        {
            dva.setHint("Bodovi za 2");
            tri.setHint("Bodovi za 3");
            cetiri.setHint("Bodovi za 4");
            pet.setHint("Bodovi za 5");
        }
        //ali ako je unesena, onda se prikazu trenutne vrijednosti
        else
        {
            dva.setHint(c.getString(0));
            tri.setHint(c.getString(1));
            cetiri.setHint(c.getString(2));
            pet.setHint(c.getString(3));
        }

        //dopustam samo unos intova
        dva.setInputType(2);
        tri.setInputType(2);
        cetiri.setInputType(2);
        pet.setInputType(2);

        layout.addView(dva); // Notice this is an add method
        layout.addView(tri); // Another add method
        layout.addView(cetiri); // Another add method
        layout.addView(pet); // Another add method

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Potvrdi",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //ne smije biti prazan
                        if(dva.getText().toString().matches("") || tri.getText().toString().matches("")
                                || cetiri.getText().toString().matches("") || pet.getText().toString().matches(""))
                        {
                            Toast.makeText(Scores.this, "Unesite brojeve u sva polja", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                        //provejravam jesu li granie dobro postavljene (npr prag za dva mora biti manji od praga za tri, nista ne smije biti 0)
                        else if(provjeri(Integer.parseInt(dva.getText().toString()), Integer.parseInt(tri.getText().toString()),
                                Integer.parseInt(cetiri.getText().toString()), Integer.parseInt(pet.getText().toString()))) {
                            db.open();
                            db.insertBounds(naziv, Integer.parseInt(dva.getText().toString()), Integer.parseInt(tri.getText().toString()),
                                    Integer.parseInt(cetiri.getText().toString()), Integer.parseInt(pet.getText().toString()));
                            db.close();
                            finish();
                            startActivity(getIntent());
                        }
                        else Toast.makeText(Scores.this, "Nepravilan unos", Toast.LENGTH_LONG).show();
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

    public boolean provjeri(int dva, int tri, int cetiri, int pet)
    {
        if (dva < tri && tri < cetiri && cetiri < pet && dva != 0) return true;
        else return false;
    }

    private void showSubjects() {
        db.open();
        Cursor c = db.getAllSubjects();
        //nema jos unesenih predmeta, nemam sto prikazati
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
        }
        //za svaki predmet pozivam fju i kojoj cu dodati sve sto je uneseno za taj predmet
        else {
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

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

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
        FontManager.markAsIconContainer(bod, iconFont);
        bod.setTag(R.id.name, name);
        bod.setId(id++);
        bod.setTag(R.id.type, type);

        bod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_bodove(v);
            }
        });
        bod.setText(R.string.edit);

        Button brisi = new Button(this);
        brisi.setTag(R.id.name, name);
        brisi.setId(id++);
        brisi.setTag(R.id.type, type);
        params5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, bod.getId());

        brisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brisi_bodove(v);
            }
        });
        brisi.setText(R.string.fa_trash_2);
        FontManager.markAsIconContainer(brisi, iconFont);

        params3.addRule(RelativeLayout.RIGHT_OF, tv2.getId());
        TextView tv3 = new TextView(this);
        tv3.setId(id++);
        tv3.setText(earned + "/" + total + "  ");
        tv3.setTextSize(25);
        params4.addRule(RelativeLayout.LEFT_OF, brisi.getId());

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

            //omogucim unos tipa bodova i max broja za taj dio
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

                //nakon svih bodova ide gumb za dodavanje jos kojeg elementa
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
            //ako je upisana neka ljestvica onda imam poruke ispisane statistike nakon gumba, inace ne
            if (c2.getCount() != 0) {

                TextView ocjena = new TextView(this);
                RelativeLayout.LayoutParams params_oc = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                params_oc.addRule(RelativeLayout.BELOW, zadnji_rez_id);
                zadnji_rez_id = id; //ispod ocjene ce ici progress bar pa moram ovo zapamtiti
                ocjena.setId(id++);
                int trenutna = findGrade(name, ukupni_earned);

                if (trenutna != 5) {
                    int fali = needPoints(name, ukupni_earned, trenutna + 1);
                    ocjena.setText("Trenutna ocjena: " + trenutna + " (" + fali + " do više ocjene)");
                } else ocjena.setText("Trenutna ocjena: 5");
                ocjena.setTextSize(20);

                layout.addView(ocjena, params_oc);
            }

            //progress bar na kraju
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

    public void dodaj_btn(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Scores.this);
        final String naziv = ((Button) v).getTag().toString();
        alertDialog.setTitle(naziv);

        Context context = v.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText tip = new EditText(context);
        tip.setHint("Tip bodova");
        layout.addView(tip);

        final EditText broj = new EditText(context);
        broj.setHint("Maksimalan broj bodova");
        layout.addView(broj);

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

        db.open();
        final Cursor c = db.getEarnedForType(predmet, tip);
        final Cursor tot = db.getTotalForType(predmet, tip);
        unos.setHint(c.getString(0));

        unos.setInputType(2);

        layout.addView(unos);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Potvrdi",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (unos.getText().toString().matches(""))
                            Toast.makeText(Scores.this, "Prazan unos" ,Toast.LENGTH_LONG).show();
                        else if(Integer.parseInt(tot.getString(0)) < Integer.parseInt(unos.getText().toString()))
                            Toast.makeText(Scores.this, "Nepravilan unos" ,Toast.LENGTH_LONG).show();
                        else {
                            db.open();
                            db.updateEarned(predmet, tip, Integer.parseInt(unos.getText().toString()));
                            db.close();
                            finish();
                            startActivity(getIntent());
                        }
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
        Intent intent = new Intent(this, Raspored.class);
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

    public void openCalendar (View view){
        Intent intent = new Intent(this, Kalendar.class);
        startActivity(intent);
    }

}

