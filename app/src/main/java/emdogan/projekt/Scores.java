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
import android.view.View;
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

    private void addScores(String type, int earned, int total)
    {
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView tv2 = new TextView(this);
        tv2.setId(id++);
        tv2.setText(type + ": ");

        TextView tv3 = new TextView(this);
        params3.addRule(RelativeLayout.RIGHT_OF, tv2.getId());
        tv3.setId(id++);
        tv3.setText(earned + "/" + total);

        //layout.addView(tv3, params3);
    }

    private void addSubject(String name)
    {
        RelativeLayout layout = new RelativeLayout(this);
        layout.setId(id++);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView tv1 = new TextView(this);
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


            EditText tip_et = new EditText(this);
            tip_et.setId(id++);

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Scores.this);
                    //String
                    alertDialog.setTitle(((Button) v).getTag().toString());

                    Context context = v.getContext();
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText titleBox = new EditText(context);
                    titleBox.setHint("Tip bodova");
                    layout.addView(titleBox); // Notice this is an add method

                    final EditText descriptionBox = new EditText(context);
                    descriptionBox.setHint("Maksimalan broj bodova");
                    layout.addView(descriptionBox); // Another add method

                    alertDialog.setView(layout);

                    alertDialog.setPositiveButton("Potvrdi",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    //insertInScores((alertDialog.getTitle().toString(), )
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
                    addScores(c1.getString(1), c1.getInt(2), c1.getInt(3));
                }
                while (c1.moveToNext());
            }
        }
        root.addView(layout);
        db.close();
    }

    public void Ispisi(){
        db.open();
        Cursor c = db.getAllScoresEntries();
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
                        "Name: " + c.getString(1) + "\n" +
                        "type: " + c.getString(2) + "\n" +
                        "earned: " + c.getString(3) + "\n" +
                        "total: " + c.getString(4) + "\n",
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
}

