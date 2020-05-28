package com.example.getapet.ComBaseDatos;

import android.os.AsyncTask;

import com.example.getapet.pojo.Animal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

public class FTPServidorDownload extends AsyncTask<Object[], Void, String>
{
    private ArrayList<Animal> arrayAnimal;
    private ArrayList<String> directorios;

    private String ok = "";

    public FTPServidorDownload(ArrayList<Animal> arrayAnimal, ArrayList<String> directorios) {
        this.arrayAnimal = arrayAnimal;
        this.directorios = directorios;
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
    protected String doInBackground(Object[]... objects) {
        FTPClient client = new FTPClient();

        try {
            client.connect("192.168.1.52");
            client.login("admin","1234");
            client.setType(FTPClient.TYPE_BINARY);
            client.setPassive(true);

            for (int i = 0; i < directorios.size(); i++) {

                System.out.println("SOY DIRECTORIOODSFDASFASF "+directorios.get(i));
            }



            for (int j = 0; j < directorios.size(); j++)
            {
                System.out.println("DIRECTORIO DENTRO DE FTP "+directorios.get(j));
                client.changeDirectory("/");
                client.changeDirectory(directorios.get(j));

                for (int h = 0; h < arrayAnimal.get(j).getNombreImagenes().size(); h++)
                {
                    System.out.println("dasfasf "+arrayAnimal.get(j).getNombreImagenes().size());
                    System.out.println("SOY INFO LISTA PROVISIONAL GET I "+arrayAnimal.get(j).getNombreImagenes().get(h));
                    String nombreImagen = arrayAnimal.get(j).getNombreImagenes().get(h);
                    System.out.println("SOY NOMBRE IMAGEN DENTRO DEL FTP "+nombreImagen);
                    File fileImagen = arrayAnimal.get(j).getImagenes().get(h);
                    System.out.println("SOY FILE IMAGEN DENTRO DEL FTP  "+fileImagen);
                    client.download(nombreImagen,fileImagen);
                }
            }


            client.disconnect(true);

            ok = "ok";

        } catch (IOException e) {
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
        } catch (FTPException e) {
            e.printStackTrace();
        } catch (FTPAbortedException e) {
            e.printStackTrace();
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
        }

        return ok;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
