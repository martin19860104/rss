package com.exemple.eac2_2017s1;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.exemple.eac2_2017s1.XmlParser.Entrada;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // RSS FEED
    private static final String URL = "http://upr.mr/feed/";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Adaptador adapter;
    private DBInterface db;
    private List<Entrada> listSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.rView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adaptador(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        db = new DBInterface(this);
        listSearch = new ArrayList<>();
        ImageButton imageButtonBusqueda = (ImageButton) findViewById(R.id.imageButtonBusqueda);
        imageButtonBusqueda.setOnClickListener(this);
        cargaNoticias();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Pressing the update button updates the status of the network and uploads the news
        if (id == R.id.action_actualizar) {
            cargaNoticias();
            return true;
            //If you press the search button, filter the list
        } else if (id == R.id.action_buscar) {
            mostrarBarraBusqueda();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarBarraBusqueda() {
        LinearLayout barraBusqueda = (LinearLayout) findViewById(R.id.barraBusqueda);
        if (barraBusqueda.getVisibility() == View.GONE) {
            barraBusqueda.setVisibility(View.VISIBLE);
        } else {
            barraBusqueda.setVisibility(View.GONE);
        }
    }

    public void cargaNoticias() {
        // If we have a connection to the device
        if (Util.hayConexion(this)) {
            new DownloadTask().execute(URL);
        } else {
            Toast.makeText(this, "no connect", Toast.LENGTH_LONG).show();
            List<Entrada> lista = cargarDB();
            adapter.setList(lista);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            // update the local listing for searches
            listSearch = lista;
        }
    }
//
//    private void downloadImages(List<Entrada> result) {
//        for (Entrada entrada : result) {
//            try {
//                URL imatge = new URL(entrada.imagen);
//                InputStream inputstream = (InputStream) imatge.getContent();
//                byte[] bufferImatge = new byte[1024];
//                String path = getCacheDir().toString() + File.separator
//                        + entrada.imagen.substring(entrada.imagen.lastIndexOf('/') + 1, entrada.imagen.length());
//                OutputStream outputstream = new FileOutputStream(path);
//                int count;
//                while ((count = inputstream.read(bufferImatge)) != -1) {
//                    outputstream.write(bufferImatge, 0, count);
//                }
//                inputstream.close();
//                outputstream.close();
//            } catch (IOException exception) {
//                Log.d("ERR", "Error!");
//            }
//        }
//    }

    private List<Entrada> carregaXMLdelaXarxa(String urlString) throws XmlPullParserException, IOException {
        List<Entrada> entradas = null;
        InputStream stream = null;
        XmlParser analitzador = new XmlParser();
        StringBuilder htmlString = new StringBuilder();
        try {
            stream = ObreConnexioHTTP(urlString);
            entradas = analitzador.analitza(stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return entradas;
    }

    private InputStream ObreConnexioHTTP(String adrecaURL) throws IOException {
        InputStream in = null;
        int resposta = -1;
        URL url = new URL(adrecaURL);

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        try {

            httpConn.setReadTimeout(10000);
            httpConn.setConnectTimeout(15000);
            httpConn.setRequestMethod("GET");
            httpConn.setDoInput(true);

            httpConn.connect();

            resposta = httpConn.getResponseCode();
            if (resposta == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            throw new IOException("Error connectant");
        }
        return in;
    }

    public void dbInsertAll(List<Entrada> result) {
        db.dropAndRecreateTable();
        db.open();
        for (Entrada r : result) {
            String titulo = r.titulo;
            String enlace = r.enlace;
            String autor = r.autor;
            String descripcion = r.descripcion;
            String fecha = r.fecha;
            String categoria = r.categoria;
//            String imagen = r.imagen;
            db.insert(titulo, descripcion, enlace, autor, fecha, categoria);
        }
        db.close();
    }

    protected List<Entrada> cargarDB() {
        List<Entrada> entradas = new ArrayList<>();

        db.open();
        Cursor cursor = db.getAll();
        
        while (cursor.moveToNext()) {
            String titulo = cursor.getString(cursor.getColumnIndex("titulo"));
            String enlace = cursor.getString(cursor.getColumnIndex("enlace"));
            String autor = cursor.getString(cursor.getColumnIndex("autor"));
            String descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));
            String fecha = cursor.getString(cursor.getColumnIndex("fecha"));
            String categoria = cursor.getString(cursor.getColumnIndex("categoria"));
//            String imagen = cursor.getString(cursor.getColumnIndex("imagen"));
            entradas.add(new Entrada(titulo, descripcion, enlace, autor, fecha, categoria));
        }
        return entradas;
    }

    @Override
    public void onClick(View v) {
        List<Entrada> listaFiltrada = new ArrayList<>();
        TextView editTextBusqueda = (TextView) findViewById(R.id.editTextBusqueda);
        String busqueda = editTextBusqueda.getText().toString().toLowerCase();
        for (Entrada e : listSearch) {
            if (e.titulo.toLowerCase().contains(busqueda))
                listaFiltrada.add(e);
        }
        adapter.setList(listaFiltrada);
        adapter.notifyDataSetChanged();
    }

    private class DownloadTask extends AsyncTask<String, Void, List<Entrada>> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected List<Entrada> doInBackground(String... urls) {
            List<Entrada> lista = null;
            try {
                lista = carregaXMLdelaXarxa(urls[0]);
                dbInsertAll(lista);
            } catch (IOException | XmlPullParserException e) {
                //Error
            }
            return lista;
        }


        @Override
        protected void onPostExecute(List<Entrada> lista) {
            adapter.setList(lista);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            listSearch = lista;
        }

    }

}
