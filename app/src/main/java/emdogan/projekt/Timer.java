package emdogan.projekt;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Timer extends AppCompatActivity {
    // odbrojavanje
    CountDownTimer ucenjeTimer;
    CountDownTimer pauzaTimer;

    boolean pokrenutTimer;  // zbog onResume-a nam je potreban

    boolean trenutnoUcenje;     // true - ucenje, false - pauza

    int brojIntervala;      // broj intervala za ucenje
    int trenutniInterval;   // koji je trenutni interval
    int duljinaIntervala;   // duljina intervala za ucenje (u sekundama)
    int duljinaPauze;       // duljina pauze (u sekundama)

    // podaci potrebni za onResume
    int ukupnoTrajanje;     // = brojIntervala * duljinaIntervala + (brojIntervala-1) * duljinaPauze
    int pauziranaSekunda;  // koja sekunda je bila kad se dogodio onPause

    long systemSekunda;     // da znamo koliko je proslo sekundi od kada se dogodio onPause


    public static final String MYPREFS = "MyPrefsFile";

    // progress bar
    ProgressBar progressBar;
    static int progress;        // progressBar_max = 100, progress je "posototak" progressBara koji je zavrsen

    int progressInterval;       // "za koliko povecamo progressBar u jednom Ticku kad traje ucenje"
    int progressPauza;          // "... kad traje pauza"


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Toast.makeText(this, "onSaveInstanceState", Toast.LENGTH_SHORT).show();

        savedInstanceState.putBoolean("trenutnoUcenje", trenutnoUcenje);

        savedInstanceState.putBoolean("pokrenutTimer", pokrenutTimer);

        savedInstanceState.putInt("brojIntervala" ,brojIntervala );
        savedInstanceState.putInt("duljinaIntervala" , duljinaIntervala);
        savedInstanceState.putInt("trenutniInterval" , trenutniInterval);
        savedInstanceState.putInt("duljinaPauze" , duljinaPauze);

        savedInstanceState.putInt("ukupnoTrajanje", ukupnoTrajanje);
        savedInstanceState.putInt("pauziranaSekunda", pauziranaSekunda);

        savedInstanceState.putInt("progressInterval" , progressInterval );
        savedInstanceState.putInt("progressPauza" , progressPauza);

        savedInstanceState.putLong("systemSekunda", systemSekunda);

        resetiraj();        // resetiramo nakon sto sve spremimo
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Toast.makeText(this, "on RestoreInstanceState", Toast.LENGTH_SHORT).show();

        trenutnoUcenje = savedInstanceState.getBoolean("trenutnoUcenje");

        pokrenutTimer = savedInstanceState.getBoolean("pokrenutTimer");

        brojIntervala = savedInstanceState.getInt("brojIntervala");
        duljinaIntervala = savedInstanceState.getInt("duljinaIntervala");
        trenutniInterval = savedInstanceState.getInt("trenutniInterval");
        duljinaPauze = savedInstanceState.getInt("duljinaPauze");

        ukupnoTrajanje = savedInstanceState.getInt("ukupnoTrajanje" );
        pauziranaSekunda = savedInstanceState.getInt("pauziranaSekunda");

        progressInterval = savedInstanceState.getInt("progressInterval");
        progressPauza = savedInstanceState.getInt("progressPauza");

        systemSekunda = savedInstanceState.getLong("systemSekunda");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.icons_container), iconFont);

        ((Button) findViewById(R.id.stopTimer)).setClickable(false);    // ovo sam morala dodati jer mi nije htjelo otpocetka blokirati Stop gumb

        progress = 0;
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setMax(100);

        Button button = (Button)findViewById(R.id.timerButton);
        button.setTextColor(Color.parseColor("#c98300"));

        if (savedInstanceState != null) {
            // Toast.makeText(this, "onCreate - savedInstanceState", Toast.LENGTH_SHORT).show();

            trenutnoUcenje = savedInstanceState.getBoolean("trenutnoUcenje");
            pokrenutTimer = savedInstanceState.getBoolean("pokrenutTimer");

            brojIntervala = savedInstanceState.getInt("brojIntervala");
            duljinaIntervala = savedInstanceState.getInt("duljinaIntervala");
            trenutniInterval = savedInstanceState.getInt("trenutniInterval");
            duljinaPauze = savedInstanceState.getInt("duljinaPauze");

            ukupnoTrajanje = savedInstanceState.getInt("ukupnoTrajanje" );
            pauziranaSekunda = savedInstanceState.getInt("pauziranaSekunda");

            progressInterval = savedInstanceState.getInt("progressInterval");
            progressPauza = savedInstanceState.getInt("progressPauza");

            systemSekunda = savedInstanceState.getLong("systemSekunda");

            // onCreate poziva onResume, pa ako je timer radio, onResume ce to rijesiti
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();

        // Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        if (pokrenutTimer) {
            pokreniPolovniTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();

        // zaustavimo, da se ne vrte u pozadini (ali zapamtimo u kojoj sekundi su stali -> savePreferences)
        if (ucenjeTimer != null)
            ucenjeTimer.cancel();
        if (pauzaTimer != null)
            pauzaTimer.cancel();

    }




    protected void savePreferences(){
        //stvorimo shared preference
        int mode = MODE_PRIVATE;
        SharedPreferences mySharedPreferences=getSharedPreferences(MYPREFS, mode);

        //editor za modificiranje shared preference
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        //spremamo vrijednosti u shared preference
        editor.putBoolean("trenutnoUcenje", trenutnoUcenje);

        editor.putBoolean("pokrenutTimer", pokrenutTimer);

        editor.putInt("brojIntervala" ,brojIntervala );
        editor.putInt("duljinaIntervala" , duljinaIntervala);
        editor.putInt("trenutniInterval" , trenutniInterval);
        editor.putInt("duljinaPauze" , duljinaPauze);

        editor.putInt("ukupnoTrajanje", ukupnoTrajanje);
        editor.putInt("pauziranaSekunda", pauziranaSekunda);

        editor.putInt("progressInterval" , progressInterval );
        editor.putInt("progressPauza" , progressPauza);

        editor.putLong("systemSekunda", System.currentTimeMillis());

        //commit promjene
        editor.commit();
    }

    public void loadPreferences(){
        // dohvatimo preference
        int mode = MODE_PRIVATE;
        SharedPreferences mySharedPreferences = getSharedPreferences(MYPREFS,mode);

        //dohvatimo vrijednosti
        trenutnoUcenje = mySharedPreferences.getBoolean("trenutnoUcenje", false);

        pokrenutTimer = mySharedPreferences.getBoolean("pokrenutTimer", false);

        brojIntervala = mySharedPreferences.getInt("brojIntervala", 0);
        duljinaIntervala = mySharedPreferences.getInt("duljinaIntervala",0);
        trenutniInterval = mySharedPreferences.getInt("trenutniInterval",0);
        duljinaPauze = mySharedPreferences.getInt("duljinaPauze",0);

        ukupnoTrajanje = mySharedPreferences.getInt("ukupnoTrajanje",0);
        pauziranaSekunda = mySharedPreferences.getInt("pauziranaSekunda",0);

        progressInterval = mySharedPreferences.getInt("progressInterval",0);
        progressPauza = mySharedPreferences.getInt("progressPauza",0);

        systemSekunda = mySharedPreferences.getLong("systemSekunda",0L);
    }



    // pokrece ga onResume - nastavak postojeceg timera
    public void pokreniPolovniTimer() {

        if (pokrenutTimer) {
            long trenutnaSystemSekunda =  System.currentTimeMillis();
            // pomocu systemSekunda i trenutna systemSekunda odredimo koliko je vremena zaista proslo

            if ((trenutnaSystemSekunda - systemSekunda)/1000 >= (ukupnoTrajanje - pauziranaSekunda)) {
                resetirajSve();
                ((TextView) findViewById(R.id.timerTextView)).setText("ZAVRŠENO ODBROJAVANJE");
                pokrenutTimer = false;
            }
            else {  // moramo pokrenuti novi timer

                // START - STOP gumbi
                ((Button) findViewById(R.id.startTimer)).setClickable(false);
                ((Button) findViewById(R.id.stopTimer)).setClickable(true);

                ((EditText) findViewById(R.id.duljinaPauzeMin)).setFocusable(false);
                ((EditText) findViewById(R.id.duljinaPauzeMin)).setFocusable(false);
                ((EditText) findViewById(R.id.duljinaPauzeSec)).setFocusable(false);
                ((EditText) findViewById(R.id.duljinaIntervalaMin)).setFocusable(false);
                ((EditText) findViewById(R.id.duljinaIntervalaSec)).setFocusable(false);
                ((EditText) findViewById(R.id.brojIntervala)).setFocusable(false);


                // odredujemo koji timer moramo pokrenuti
                int interval = 1;
                int cnt = 0;

                // trenVrijeme = koliko je timer odvrtio do trenutka izlaska iz activityja (pauziranaSekunda)
                //              + koliko je vremena bio van activityja ((trenutnaSystemSekunda - systemSekunda)/1000)

                // trenVrijeme nam odreduje od koje sekunde moramo nastaviti brojati
                int trenutnoVrijeme = (int)((pauziranaSekunda) + (trenutnaSystemSekunda - systemSekunda)/1000);

                /*
                Log.d("myTag", "ON RESUME -------------------------------- stari timer se jos vrti");
                Log.d("myTag", "pauzirana sekunda = " + pauziranaSekunda);
                Log.d("myTag", "proslo sekundi vani: " + (trenutnaSystemSekunda - systemSekunda)/1000);
                Log.d("myTag", "trenutnoVrijeme = " +  trenutnoVrijeme);
                Log.d("myTag", "do kraja intervala: " + (duljinaIntervala - trenutnoVrijeme));
                Log.d("myTag", "");
                */
                resetiraj();

                // nadoknadimo pauziranaSekunda za propusteno vrijeme
                pauziranaSekunda += (int) ((trenutnaSystemSekunda - systemSekunda)/1000);

                while (true){
                    if ((cnt + duljinaIntervala) > trenutnoVrijeme) {
                        // pokreni timerUcenje
                        trenutniInterval = interval;

                        ((TextView) findViewById(R.id.kojiInterval)).setText("UČENJE. Trenutni interval: " + trenutniInterval + "/" + brojIntervala);
                        pokreniUcenjeTimer(duljinaIntervala - (trenutnoVrijeme - cnt),
                                        (trenutnoVrijeme - cnt)*progressInterval);
                        break;
                    }
                    else{
                        cnt += duljinaIntervala;
                    }

                    if ((cnt + duljinaPauze) > trenutnoVrijeme){
                        // pokreni timerPauza
                        trenutniInterval = interval;
                        ((TextView) findViewById(R.id.kojiInterval)).setText("Trenutni interval: " + trenutniInterval +"/" + brojIntervala);
                        pokreniPauzaTimer(duljinaPauze - (trenutnoVrijeme - cnt),
                                        (trenutnoVrijeme - cnt)*progressPauza);
                        break;
                    }
                    else{
                        cnt += duljinaPauze;
                        ++interval;
                    }
                }

            }
        }
    }


    // poziva se klikom na gumb START (provjera jesu li dobri podaci i prvi put se poziva pokreniUcenjeTimer)
    public void pokreniTimer (View view) {
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

                if (brojIntervala > 1)
                    ukupnoTrajanje = brojIntervala * duljinaIntervala + (brojIntervala-1) * duljinaPauze;
                else
                    ukupnoTrajanje = duljinaIntervala;

                pauziranaSekunda = 0;

                progressInterval = 100/duljinaIntervala;
                progressPauza = 100/duljinaPauze;

                pokrenutTimer = true;

                pokreniUcenjeTimer(duljinaIntervala, 0);
            }
        }
    }

    public void pokreniPauzaTimer(int duljina_pauze, int trenutni_progres) {
        // progress bar
        progress = trenutni_progres;
        progressBar.setProgress(trenutni_progres);

        // odbrojavanje
        pauzaTimer = new CountDownTimer(duljina_pauze*1000+1000, 1000) {

            public void onTick(long millisUntilFinished) {
                pauziranaSekunda++;

                int minuta = (int) ((millisUntilFinished / 1000) / 60);
                int sekunda = (int)((millisUntilFinished / 1000) - minuta*60) ;
                String min = "";
                if (minuta < 10)
                    min = "0" + minuta;
                else
                    min = Integer.toString(minuta);

                String sec = "";
                if (sekunda < 10)
                    sec = "0" + (sekunda);
                else
                    sec = Integer.toString(sekunda);

                ((TextView) findViewById(R.id.timerTextView)).setText("PAUZA: " + min + ":" + sec);

                progressBar.setProgress(progress + progressPauza);
                progress += progressPauza;
            }

            public void onFinish() {
                trenutniInterval++;
                ((TextView) findViewById(R.id.kojiInterval)).setText("Trenutni interval: " + trenutniInterval +"/" + brojIntervala);
                trenutnoUcenje = true;
                progressBar.setProgress(progress + progressPauza);
                pokreniUcenjeTimer(duljinaIntervala, 0);
            }
        }.start();
    }

    public void pokreniUcenjeTimer (int duljina_ucenja, int trenutni_progres){
        // progress bar
        progress = trenutni_progres;
        progressBar.setProgress(trenutni_progres);


        // odbrojavanje
        ucenjeTimer = new CountDownTimer(duljina_ucenja*1000+1000, 1000) {

            public void onTick(long millisUntilFinished) {
                pauziranaSekunda++;

                int minuta = (int) ((millisUntilFinished / 1000) / 60);
                int sekunda = (int)((millisUntilFinished / 1000) - minuta*60) ;

                String min = "";
                if (minuta < 10)
                    min = "0" + minuta;
                else
                    min = Integer.toString(minuta);

                String sec = "";
                if (sekunda < 10)
                    sec = "0" + (sekunda);
                else
                    sec = Integer.toString(sekunda);

                ((TextView) findViewById(R.id.timerTextView)).setText("UČENJE: " + min + ":" + sec);

                progressBar.setProgress(progress + progressInterval);
                progress += progressInterval;

            }

            public void onFinish() {
                if (trenutniInterval == brojIntervala) {
                    resetirajSve();
                    ((TextView) findViewById(R.id.timerTextView)).setText("ZAVRŠENO ODBROJAVANJE");
                    pokrenutTimer = false;
                }
                else {
                    ((TextView) findViewById(R.id.kojiInterval)).setText("Odrađeno je: " + trenutniInterval +"/" + brojIntervala + " intervala.");
                    trenutnoUcenje = false;
                    progressBar.setProgress(progress + progressInterval);
                    pokreniPauzaTimer(duljinaPauze, 0);
                }
            }
        }.start();
    }



    // poziva se klikom na gumb STOP - sve ponisti i pocisti (omoguci pokretanje novog timera)
    public void zaustaviTimer(View view) {
        resetirajSve();
        pokrenutTimer = false;
    }

    // "pocisti izgled aktivnosti prije nek se pauzira/unisti"
    public void resetiraj(){

        if (ucenjeTimer != null)
            ucenjeTimer.cancel();
        if (pauzaTimer != null)
            pauzaTimer.cancel();

        ((TextView) findViewById(R.id.kojiInterval)).setText("");
        ((TextView) findViewById(R.id.timerTextView)).setText("");

        progress = 0;
        progressBar.setProgress(0);
    }

    // poziva se kad je ODBROJAVANJE gotovo ("oslobodimo tipku Start i dozvolimo upis minuta")
    public void resetirajSve(){
        resetiraj();

        ((Button) findViewById(R.id.startTimer)).setClickable(true);
        ((Button) findViewById(R.id.stopTimer)).setClickable(false);

        ((EditText) findViewById(R.id.duljinaPauzeMin)).setFocusableInTouchMode(true);
        ((EditText) findViewById(R.id.duljinaPauzeSec)).setFocusableInTouchMode(true);
        ((EditText) findViewById(R.id.duljinaIntervalaMin)).setFocusableInTouchMode(true);
        ((EditText) findViewById(R.id.duljinaIntervalaSec)).setFocusableInTouchMode(true);
        ((EditText) findViewById(R.id.brojIntervala)).setFocusableInTouchMode(true);

        duljinaIntervala = 0;
        duljinaPauze = 0;
        brojIntervala = 0;

        progressInterval = 0;
        progressPauza = 0;

        ukupnoTrajanje = 0;
        pauziranaSekunda = 0;

        pokrenutTimer = false;
    }





    // ZA IKONICE DOLJE
    public void openTimetable(View view) {
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
    }
    public void openHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void openTimer(View view){

    }

    public void openScores(View view) {
        Intent intent = new Intent(this, Scores.class);
        startActivity(intent);
    }

    public void openCalendar (View view){
        Intent intent = new Intent(this, Kalendar.class);
        startActivity(intent);
    }
}
