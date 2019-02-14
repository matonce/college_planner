package emdogan.projekt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Kalendar extends AppCompatActivity {

    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalendar);

        db = new DBAdapter(this);

        CalendarView c = (CalendarView) findViewById(R.id.calendarView);
        c.setFirstDayOfWeek(2);

        c.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                //kad se klikne na određeni dan
                Toast.makeText(getApplicationContext(), ""+dayOfMonth + " " + month + " " + year, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Kalendar.this);

                Context context = view.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText text = new EditText(context);
                text.setHint("Obavijest");
                layout.addView(text);

                final TextView tv1 = new TextView(context);
                tv1.setText("Unesite vrijeme početka: ");
                layout.addView(tv1);

                final TimePicker tp = new TimePicker(context);
                //broj.setHint("Maksimalan broj bodova");
                layout.addView(tp); // Another add method

                alertDialog.setView(layout);

                alertDialog.setPositiveButton("Potvrdi",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

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
        });

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);

        Button button = (Button)findViewById(R.id.calendarButton);
        button.setTextColor(Color.parseColor("#c98300"));

    }

    public void openTimetable(View view) {
        Intent intent = new Intent(this, TimetableActivity.class);
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
