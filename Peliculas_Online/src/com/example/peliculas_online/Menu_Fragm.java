package com.example.peliculas_online;


import java.io.ByteArrayOutputStream;
import java.io.IOException;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Menu_Fragm extends ListFragment {
   
    private static final String API_KEY = "wpggaks2tjdt7egsgujxbfxw"; 
    String[] movieTitles;
    String[] Informacion_Pelicula;
    
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        
    	new Peticion_Async().execute("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?apikey=" + API_KEY );
    	View view =inflater.inflate(R.layout.list_fragment, container, false);
        return view;

    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        
    	Vista_Frag txt = (Vista_Frag)getFragmentManager().findFragmentById(R.id.fragment2);
        txt.change("Titulo: "+movieTitles[position],"Sinopsis: "+Informacion_Pelicula[position]);
        getListView().setSelector(android.R.color.holo_blue_dark);
    }
    
    private void Actualiza(String[] movieTitles)
    {
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
    			android.R.layout.simple_list_item_1, movieTitles);
        setListAdapter(adapter);
        
    }

    private class Peticion_Async extends AsyncTask<String, String, String>
    {
        // Peticion a la URL  http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json
        @Override
        protected String doInBackground(String... uri)
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try
            {
                // Peticion HTTP
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK)
                {
                    // lee respuesta y cierra conexion
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                }
                else
                {
                    // Si falla, cierra conexion
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            }
            catch (Exception e)
            {
                Log.d("Test", "Error "+e);
            }
            return responseString;
        }

        // Si la solicitud es exitosa, se ejecurara el on post
        @Override
        public void onPostExecute(String response)
        {
            super.onPostExecute(response);

            if (response != null)
            {
                try
                {
                   
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray movies = jsonResponse.getJSONArray("movies");

                    movieTitles = new String[movies.length()];
                    Informacion_Pelicula = new String[movies.length()];
                    
                    for (int i = 0; i < movies.length(); i++)
                    {
                        JSONObject movie = movies.getJSONObject(i);
                        
                        //movieTitles[i] = movie.getString("title")+"  "+movie.getJSONObject("posters").getString("thumbnail");
                        movieTitles[i] = movie.getString("title");
                        Informacion_Pelicula[i] = "A–o: "+movie.getString("year")+" Calificacion de Criticos: "+movie.getJSONObject("ratings").getInt("critics_score")+"% "+"Audiencia: "+movie.getJSONObject("ratings").getInt("audience_score")+"% "+movie.getString("synopsis")+"  ";

                    }
                    Actualiza(movieTitles);
                }
                catch (JSONException e)
                {
                    Log.d("Test", "Error2:  "+e);
                }
            }
         }
     }
    

    
}