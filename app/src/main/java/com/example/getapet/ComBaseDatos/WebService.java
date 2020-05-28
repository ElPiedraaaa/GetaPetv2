package com.example.getapet.ComBaseDatos;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WebService extends AsyncTask<Void,String ,String>
{
    String peticionDIB="";
    String datoDIB="";
    URL url;

    // Clase que recibe un string petición y un string query.
    public WebService(String peticion, String query) {
        peticionDIB = peticion;
        datoDIB = query;
    }

    // Método que creará una conexión, enviará una petición a la api y recibirá los datos de esa petición.
    @Override
    protected String doInBackground(Void... voids) {

        String s="";
        {
            try {
                Map<String,String> datosEnv=new HashMap<String,String>();
                datosEnv.put(peticionDIB,datoDIB);
                Gson gson = new Gson();
                String jsonOutput = gson.toJson(datosEnv);
                jsonOutput = URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(jsonOutput, "UTF-8");
                url = new URL("http://192.168.1.52/GetAPet/Gap.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(jsonOutput);
                wr.flush();
                //Recibimos los datos
                InputStream in = con.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader recv = new BufferedReader(inputStreamReader);
                //Los mostramos por pantalla
                s = recv.readLine();


                String string = "{ \"name\":\"John\", \"age\":30, \"car\":null }";
                recv.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return s;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    // Método desde el cual podremos recoger los datos con un get()
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        datoDIB = s;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}
