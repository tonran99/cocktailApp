package com.example.cocktailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String mUrl = "https://thecocktaildb.com/api/json/v1/1/";
    private RadioGroup radioGroup;
    private RequestQueue mQueue;
    private TextView textView ;
    private int counter = 0;
    private String results = "";


    static final String STATE_DRINK_TEXT ="";
    String resultsTextBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(this);

        //estetään error jos radionappi ei ole valittu ja haetaan jotain
        radioGroup = findViewById(R.id.options);
        radioGroup.check(R.id.searchByName);

        //asetetaan tekstikenttä siten että sitä voi scrollata alaspäin
        textView = findViewById(R.id.results);
        textView.setMovementMethod(new ScrollingMovementMethod());

        if (savedInstanceState != null) {
            results = savedInstanceState.getString("savText", "");
        } else {}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TÄMÄ ON TODELLA TYPERÄÄ ETTÄ NÄIN JOUTUU TEKEMÄÄN EN YMMÄRRÄ MIKSI TEKSTIKENTTÄ PALAUTTAA SPANNABLE STRINGIN EIKÄ NORMAALI STRINGIÄ
        //KIRJAIMELLISESTI TUSKAILIN TÄMÄN KANSSA MONTA TUNTIA MIETTIEN MIKSI TÄMÄ Ei TOIMI!!!!!!!!!!!
        SpannableString jaa;
        textView = findViewById(R.id.results);
        jaa = (SpannableString) textView.getText();
        String yourString = Html.fromHtml(String.valueOf(jaa), Html.FROM_HTML_MODE_LEGACY).toString();
        outState.putString("savText", yourString);

    }

    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
       results = savedInstanceState.getString("savText","");
       textView = textView.findViewById(R.id.results);
        textView.setText(""+results);
    }

    @Override
    protected void onStart(){
        super.onStart();
        //aktiviteettti on käynnistymässä
    }

    @Override
    protected void onStop(){
        super.onStop();
        //aktiviteetti on pois näkyvistä
    }

    @Override
    protected void onResume(){
        super.onResume();

        //aktiviteetti on tulee näkyviin
    }

    @Override
    protected void onPause(){
        super.onPause();
        //aktiveetti on menossa pois näkyvistä
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //aktiviteetti on tuhottu, siivottu muistista.
        // Esim. kun sovellus menee portrait tilaankin
    }

    // Vie settings näkymään intentin avulla
    public void settingsButton(View view){
        Intent intent = new Intent(this, Settings.class);
        intent.putExtra("INT_COUNTER",counter);
        startActivity(intent);
    }



// Nappiin liitetty funktio joka kutsuu kaikki loput alifunktiot
    //Ottaa valitun napin ID:n ja passaa sitä eteenpäin jotta oikeat toimenpiteet voidaan suorittaa
    @SuppressLint("SetTextI18n")
    public void searchForItem(View view) {
        mUrl = "https://thecocktaildb.com/api/json/v1/1/";
        radioGroup = findViewById(R.id.options);
        TextView searchText = findViewById(R.id.searchText);
        // get selected radio button from radioGroup
        int selectedId = radioGroup.getCheckedRadioButtonId();

        switch(selectedId) {
            case R.id.searchByName:
                mUrl = mUrl+"search.php?s="+searchText.getText();
                fetchDrinkData(R.id.searchByName);
                break;
            case R.id.searchByIngridient:
                mUrl = mUrl+"filter.php?i="+searchText.getText();
                fetchDrinkData(R.id.searchByIngridient);
                break;
            case R.id.random:
                mUrl = mUrl+"random.php";
                fetchDrinkData(R.id.random);
                break;
            case R.id.showAllIngredients:
                mUrl = mUrl+"list.php?i=list";
                fetchDrinkData(R.id.showAllIngredients);
                break;
            case R.id.showAllDrinks:
                mUrl = mUrl+"filter.php?a=Alcoholic";
                fetchDrinkData(R.id.showAllDrinks);
                break;
        }}

    public void fetchDrinkData(int id) {
        //haetaan drinkki data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, mUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(getApplicationContext(),response.toString(), Toast.LENGTH_LONG).show();
                        parseJsonAndUpdateUi(response, id);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(MainActivity.this, "Encountered problem with request", Toast.LENGTH_SHORT).show();

                    }
                });
        mQueue.add(jsonObjectRequest);
    }

    // parsetaan data haluttuun muotoon
    @SuppressLint("SetTextI18n")
    private  void parseJsonAndUpdateUi(JSONObject drinkData, int id){
        TextView results = findViewById(R.id.results);
        int max = 100000;
        String name;
        ArrayList<String> names = new ArrayList<>();
        // listOfThings yhdistää ainekset ja mitat yhteen stringiin jotta tieto saadaan puskettua etiäpäin
        String listOfThings = "";
        ArrayList<String> ingredients = new ArrayList<>();
        ArrayList<String> measurements = new ArrayList<>();
        String  description;

        try {
            //parse optiot id:n mukaan
            switch(id) {
                case R.id.random:
                case R.id.searchByName:

                    //Toast.makeText(MainActivity.this, drinkImage, Toast.LENGTH_SHORT).show();

                    name = drinkData.getJSONArray("drinks").getJSONObject(0).getString("strDrink");
                    description = drinkData.getJSONArray("drinks").getJSONObject(0).getString("strInstructions");
                    //aivan hirveä toteutus mutta toimii tällä hetkellä
                for(int i = 1; i < max; i++){
                    //lopettaa datan lisäämisen jos null arvo tulee vastaan
                    if(drinkData.getJSONArray("drinks").getJSONObject(0).getString("strIngredient"+i) != "null"){
                        ingredients.add(drinkData.getJSONArray("drinks").getJSONObject(0).getString("strIngredient"+i));
                    }
                    //Sortataan tuthat null arvot pois. Esim jäällä ei ole määrää
                    if(drinkData.getJSONArray("drinks").getJSONObject(0).getString("strMeasure"+i)!="null"){
                        measurements.add(drinkData.getJSONArray("drinks").getJSONObject(0).getString("strMeasure"+i));
                    } else{measurements.add("");}

                    if(drinkData.getJSONArray("drinks").getJSONObject(0).getString("strIngredient"+i) == "null"){
                        for (int a = 0; a < ingredients.size(); a++) {
                            listOfThings += ""+ ingredients.get(a)+" " + measurements.get(a) +"\n";
                        }
                        results.setText("Name: "+ name +
                                "\nInstructions: " + description +
                                "\n Ingredients: " + listOfThings);
                        break;
                    }}
                    break;
                case R.id.showAllDrinks:
                case R.id.searchByIngridient:
                    for(int i =0; i < drinkData.getJSONArray("drinks").length(); i++){
                        names.add( drinkData.getJSONArray("drinks").getJSONObject(i).getString("strDrink"));
                    }
                    results.setText("Name: "+ names);
                    break;
                case R.id.showAllIngredients:
                    for(int i =0; i < drinkData.getJSONArray("drinks").length(); i++){
                        names.add( drinkData.getJSONArray("drinks").getJSONObject(i).getString("strIngredient1"));
                    }
                    results.setText("Name: "+ names);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void counterButton(View view) {
        Button button = findViewById(R.id.counterButton);
        counter++;
        Toast.makeText(MainActivity.this,""+ counter, Toast.LENGTH_SHORT).show();


    }
}