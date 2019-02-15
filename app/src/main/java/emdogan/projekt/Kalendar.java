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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;
import java.util.TimeZone;

public class Kalendar extends AppCompatActivity {

    LinearLayout l;
    DBAdapter db;
    TimePicker tp;
    EditText etext;
    int mj;
    int god;
    int dan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalendar);

        l = (LinearLayout) findViewById(R.id.novi);

        db = new DBAdapter(this);

        CalendarView c = (CalendarView) findViewById(R.id.calendarView);
        c.setFirstDayOfWeek(2);

        c.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                //kad se klikne na određeni dan
                //Toast.makeText(getApplicationContext(), "" + dayOfMonth + " " + month + " " + year, Toast.LENGTH_SHORT).show();
                dan = dayOfMonth;
                god = year;
                mj = month;

                ocisti();
                //Toast.makeText(getApplicationContext(), "" + view.getDate(), Toast.LENGTH_SHORT).show();
                displayTasks(year, month, dayOfMonth);
            }

        });

       /* c.setOnLongClickListener(View v){

        }
*/
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.button3), iconFont);

        Button button = (Button)findViewById(R.id.calendarButton);
        button.setTextColor(Color.parseColor("#c98300"));


        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(c.getDate());

        displayTasks(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));

    }

    private void ocisti() {
        LinearLayout l = (LinearLayout) findViewById(R.id.novi);
        if (l.getChildCount() > 0){
            l.removeAllViews();
        }
    }


    private void displayTasks(int year, int month, int day) {

        //CalendarView c = (CalendarView) findViewById(R.id.calendarView);

        db.open();
        Cursor cursor = db.getOnDay2(year, month, day);

        if (cursor.moveToFirst())
        {
            do {
                LinearLayout l = (LinearLayout) findViewById(R.id.novi);
                TextView txtv1 = new TextView(l.getContext());
                txtv1.setTextColor(Color.parseColor("#6e0f94"));
                txtv1.setTextSize(20);
                txtv1.setTypeface(txtv1.getTypeface(), Typeface.BOLD);

                String min;
                String sat;

                if (cursor.getInt(1) < 10)
                    min = "0" + cursor.getInt(1);
                else
                    min = String.valueOf(cursor.getInt(1));

                if (cursor.getInt(0) < 10)
                    sat = "0" + cursor.getInt(0);
                else
                    sat = String.valueOf(cursor.getInt(0));

                txtv1.setText(sat + " : " + min);

                TextView txtv2 = new TextView(getApplicationContext());
                txtv2.setTypeface(txtv2.getTypeface(), Typeface.BOLD);
                txtv2.setText(cursor.getString(2));
                txtv2.setTextSize(20);

                l.addView(txtv1);
                l.addView(txtv2);

            } while (cursor.moveToNext());
        }
        db.close();
    }


    public void dodaj(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Kalendar.this);

        Context context = view.getContext();
        LinearLayout layout = new LinearLayout(context);
        //LinearLayout layout = (LinearLayout) findViewById(R.id.novi);
        layout.setOrientation(LinearLayout.VERTICAL);

        etext = new EditText(context);
        etext.setHint("Obavijest");
        if(etext.getParent() != null) {
            ((ViewGroup)etext.getParent()).removeView(etext);
        }
        layout.addView(etext);

        final TextView tv1 = new TextView(context);
        tv1.setText("Unesite vrijeme početka: ");
        layout.addView(tv1);

        tp = new TimePicker(context);
        //broj.setHint("Maksimalan broj bodova");

        tp.setIs24HourView(true);
        if(tp.getParent() != null) {
            ((ViewGroup)tp.getParent()).removeView(tp);
        }
        layout.addView(tp);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Potvrdi",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!etext.getText().toString().equals("")) {
                            db.open();
                            CalendarView c = (CalendarView) findViewById(R.id.calendarView);
                            db.insertObaveza2(dan, mj, god, tp.getCurrentHour(), tp.getCurrentMinute(), etext.getText().toString());
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

    public void openTimetable(View view) {
        Intent intent = new Intent(this, Raspored.class);
        startActivity(intent);
    }


    public void openTimer (View view){
        Intent intent = new Intent(this, Timer.class);
        startActivity(intent);
    }

    public void openHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openScores(View view) {
        Intent intent = new Intent(this, Scores.class);
        startActivity(intent);
    }

    public void openCalendar (View view){

    }

}
