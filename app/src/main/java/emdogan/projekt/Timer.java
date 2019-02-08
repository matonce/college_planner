
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Timer extends AppCompatActivity {

    CountDownTimer ucenjeTimer;
    CountDownTimer pauzaTimer;

    boolean trenutnoUcenje;     // true - ucenje, false - pauza

    int brojIntervala;
    int trenutniInterval;
    int duljinaIntervala;
    int duljinaPauze;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        ((Button) findViewById(R.id.stopTimer)).setClickable(false);    // ovo sam morala dodati jer mi nije htjelo otpocetka blokirati Stop gumb
    }




    // OK - OPTIONS MENU (samo povrataka na pocetnu stranicu)
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
        MenuItem mnu1 = menu.add(0, 0, 0, "Početna stranica");
        {
            mnu1.setAlphabeticShortcut('T');
            mnu1.setIcon(R.mipmap.ic_launcher);
        }
    }

    //druga pomocna fukcija
    private boolean MenuChoice(MenuItem item)
    {
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Povratak na MainActivity",
                        Toast.LENGTH_LONG).show();
                return true;
        }
        return false;
    }





    // OK - POKRETANJE TIMERA
    public void pokreniTimer (View view){

        String duljina_intervala_min = ((EditText)  findViewById(R.id.duljinaIntervalaMin)).getText().toString();
        String duljina_intervala_sec = ((EditText)  findViewById(R.id.duljinaIntervalaSec)).getText().toString();

        String duljina_pauze_min =((EditText)  findViewById(R.id.duljinaPauzeMin)).getText().toString();
        String duljina_pauze_sec =((EditText)  findViewById(R.id.duljinaPauzeSec)).getText().toString();

        String broj_intervala = ((EditText)  findViewById(R.id.brojIntervala)).getText().toString();



        if(duljina_intervala_min.length() == 0 || duljina_intervala_sec.length() == 0 ||
                duljina_pauze_min.length() == 0 ||  duljina_pauze_sec.length() == 0 || broj_intervala.length() == 0) {

            Toast.makeText(this, "Nisu uneseni dobri podaci", Toast.LENGTH_LONG).show();

            ((EditText) findViewById(R.id.duljinaIntervalaMin)).setText("00");
            ((EditText) findViewById(R.id.duljinaIntervalaSec)).setText("30");
            ((EditText)  findViewById(R.id.duljinaPauzeMin)).setText("00");
            ((EditText)  findViewById(R.id.duljinaPauzeSec)).setText("05");
            ((EditText)  findViewById(R.id.brojIntervala)).setText("4");
        }
        else {
            brojIntervala = Integer.parseInt(broj_intervala);
            duljinaIntervala = Integer.parseInt(duljina_intervala_min)*60 + Integer.parseInt(duljina_intervala_sec);
            duljinaPauze = Integer.parseInt(duljina_pauze_min)*60 + Integer.parseInt(duljina_pauze_sec);
            trenutniInterval = 1;


            if (brojIntervala == 0 || duljinaIntervala == 0 || duljinaPauze == 0) {

                Toast.makeText(this, "Nisu uneseni dobri podaci", Toast.LENGTH_LONG).show();

                ((EditText) findViewById(R.id.duljinaIntervalaMin)).setText("00");
                ((EditText) findViewById(R.id.duljinaIntervalaSec)).setText("30");
                ((EditText)  findViewById(R.id.duljinaPauzeMin)).setText("00");
                ((EditText)  findViewById(R.id.duljinaPauzeSec)).setText("05");
                ((EditText)  findViewById(R.id.brojIntervala)).setText("4");

            }

            else {
                // postavimo clickable
                ((Button) findViewById(R.id.startTimer)).setClickable(false);
                ((Button) findViewById(R.id.stopTimer)).setClickable(true);

                ((EditText) findViewById(R.id.duljinaPauzeMin)).setFocusable(false);
                ((EditText) findViewById(R.id.duljinaPauzeMin)).setFocusable(false);
                ((EditText) findViewById(R.id.duljinaPauzeSec)).setFocusable(false);
                ((EditText) findViewById(R.id.duljinaIntervalaMin)).setFocusable(false);
                ((EditText) findViewById(R.id.duljinaIntervalaSec)).setFocusable(false);
                ((EditText) findViewById(R.id.brojIntervala)).setFocusable(false);


                ((TextView) findViewById(R.id.kojiInterval)).setText("UČENJE. Trenutni interval: " + trenutniInterval + "/" + brojIntervala);
                trenutnoUcenje = true;
                pokreniUcenjeTimer();
            }

        }

    }



    public void pokreniPauzaTimer() {
        pauzaTimer = new CountDownTimer(duljinaPauze*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int minuta = (int) ((millisUntilFinished / 1000) / 60);
                int sekunda = (int)((millisUntilFinished / 1000) - minuta*60) ;
                String min = "";
                if (minuta < 10)
                    min = "0" + minuta;
                else
                    min = Integer.toString(minuta);

                String sec = "";
                if (sekunda < 10)
                    sec = "0" + sekunda;
                else
                    sec = Integer.toString(sekunda);



                ((TextView) findViewById(R.id.timerTextView)).setText("PAUZA: " + min + ":" + sec);
            }

            public void onFinish() {
                trenutniInterval++;
                ((TextView) findViewById(R.id.kojiInterval)).setText("Trenutni interval: " + trenutniInterval +"/" + brojIntervala);
                trenutnoUcenje = true;
                pokreniUcenjeTimer();
            }
        }.start();
    }

    public void pokreniUcenjeTimer (){
        ucenjeTimer = new CountDownTimer(duljinaIntervala*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int minuta = (int) ((millisUntilFinished / 1000) / 60);
                int sekunda = (int)((millisUntilFinished / 1000) - minuta*60) ;

                String min = "";
                if (minuta < 10)
                    min = "0" + minuta;
                else
                    min = Integer.toString(minuta);

                String sec = "";
                if (sekunda < 10)
                    sec = "0" + sekunda;
                else
                    sec = Integer.toString(sekunda);

                ((TextView) findViewById(R.id.timerTextView)).setText("UČENJE: " + min + ":" + sec);

            }

            public void onFinish() {
                if (trenutniInterval == brojIntervala)
                    ((TextView) findViewById(R.id.timerTextView)).setText("ODBROJAVANJE JE ZAVRŠENO");
                else {
                    ((TextView) findViewById(R.id.kojiInterval)).setText("Odrađeno je: " + trenutniInterval +"/" + brojIntervala + " intervala.");
                    trenutnoUcenje = false;
                    pokreniPauzaTimer();
                }
            }
        }.start();
    }


    // OK - ZAUSTAVLJANE TIMERA
    public void zaustaviTimer(View view) {

        if (trenutnoUcenje)
            ucenjeTimer.cancel();
        else
            pauzaTimer.cancel();


        ((Button) findViewById(R.id.startTimer)).setClickable(true);
        ((Button) findViewById(R.id.stopTimer)).setClickable(false);

        ((EditText) findViewById(R.id.duljinaPauzeMin)).setFocusableInTouchMode(true);
        ((EditText) findViewById(R.id.duljinaPauzeSec)).setFocusableInTouchMode(true);
        ((EditText) findViewById(R.id.duljinaIntervalaMin)).setFocusableInTouchMode(true);
        ((EditText) findViewById(R.id.duljinaIntervalaSec)).setFocusableInTouchMode(true);
        ((EditText) findViewById(R.id.brojIntervala)).setFocusableInTouchMode(true);


        TextView mTextField = (TextView) findViewById(R.id.timerTextView);
        TextView kojiInterval = (TextView) findViewById(R.id.kojiInterval);
        mTextField.setText("");
        kojiInterval.setText("");

        Toast.makeText(this, "Zaustavljen timer", Toast.LENGTH_SHORT).show();
    }

}
