package com.example.getapet.ComBaseDatos;

import android.os.AsyncTask;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

public class FTPServidorDelete extends AsyncTask<String, Void, String>
{

    private ArrayList<String> arrayImagenes;
    private String directorio;
    private String ok = "";

    public FTPServidorDelete(ArrayList<String> arrayImagenes, String directorio) {
        this.arrayImagenes = arrayImagenes;
        this.directorio = directorio;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... strings) {

        FTPClient client = new FTPClient();

        try {
            client.connect("192.168.1.52");
            client.login("admin","1234");
            client.setType(FTPClient.TYPE_BINARY);
            client.setPassive(true);
            client.changeDirectory(directorio);
            System.out.println("SOY CURRENT DIRECTORY ANTES "+client.currentDirectory());

            for (int i = 0; i < arrayImagenes.size(); i++) {
                client.deleteFile(arrayImagenes.get(i));
            }

            System.out.println("SOY DIRECTORIO "+directorio);
            System.out.println("SOY CURRENT DIRECTORY "+client.currentDirectory());
            client.deleteDirectory(client.currentDirectory());

            client.disconnect(true);

            ok = "ok";

        } catch (IOException e) {
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
        } catch (FTPException e) {

            e.printStackTrace();
        }

        return ok;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
