package cr.ac.itcr.josuemorales.toppeliculas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private GridView gridPelis;
    private final static String link = "http://www.imdb.com/list/ls064079588/";
    private ArrayList<Pelicula> peliculas = new ArrayList<Pelicula>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridPelis = findViewById(R.id.gridPelis);
        gridPelis.setNumColumns(3);
        gridPelis.setAdapter(new MoviesAdapter());

        try {
            String req = new HttpGetRequest().execute(link).get();

            Document doc = Jsoup.parse(req);

            Elements titulo = doc.select("h3.lister-item-header a");
            Elements puntuaciones = doc.select("div.inline-block.ratings-imdb-rating strong");
            Elements metascores = doc.select("span.metascore.favorable");
            Elements dirImagenes = doc.select("img.loadlate");

            for(int i = 0; i < 20; i++){
                peliculas.add(new Pelicula(titulo.get(i).text(), Double.parseDouble(puntuaciones.get(i).text()), Integer.parseInt(metascores.get(i).text()), dirImagenes.get(i).attr("loadlate")));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();

        }
    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        private static final String REQUEST_METHOD = "GET";
        private static final int READ_TIMEOUT = 15000;
        private static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... urls) {
            String stringUrl = urls[0];
            String data;
            String inputLine;

            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);

                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();

                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();

                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());

                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }

                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();

                //Set our result equal to our stringBuilder
                data = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                data = null;
            }
            return data;
        }
    }

    public class HttpGetBitmap extends AsyncTask<Integer, Void, Bitmap> {
        private static final String REQUEST_METHOD = "GET";
        private static final int READ_TIMEOUT = 15000;
        private static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected Bitmap doInBackground(Integer... index){

            int i = index[0];
            Bitmap cover;

            Pelicula movie = peliculas.get(i);
            String address = movie.getDirImagen();

            try {

                //Create a URL object holding our url
                URL myUrl = new URL(address);

                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();

                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.setDoInput(true);
                connection.connect();

                //Create a new InputStream
                InputStream input = connection.getInputStream();
                cover = BitmapFactory.decodeStream(input);

            }
            catch(IOException e){
                e.printStackTrace();
                cover = null;
            }
            return cover;
        }
    }

    public class MoviesAdapter extends BaseAdapter {

        public MoviesAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return peliculas.size();
        }

        @Override
        public Object getItem(int i) {
            return peliculas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.movie_info, null);
            }

            ImageView imgMovie = view.findViewById(R.id.imgMovie);
            TextView txtTitulo = view.findViewById(R.id.txtTitulo);
            TextView txtRating = view.findViewById(R.id.txtRating);
            TextView txtMetascore = view.findViewById(R.id.txtMetascore);
            TextView meta = view.findViewById(R.id.txtMeta);
            //RatingBar ratingBar = view.findViewById(R.id.ratingBar);
            //TextView metaScoreView = view.findViewById(R.id.metaScoreTextView);

            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap coverImage = null;
            try {
                coverImage = request.execute(i).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            imgMovie.setImageBitmap(coverImage);

            txtTitulo.setText(peliculas.get(i).getTitulo());
            txtRating.setText(Double.toString(peliculas.get(i).getPuntuacion()));
            txtMetascore.setText(Integer.toString(peliculas.get(i).getMetascore()));
            meta.setText("Metascore");
            //ratingBar.setRating(movies.get(i).getRating() / 2);
            //metaScoreView.setText(String.valueOf(movies.get(i).getMetaScore()));

            return view;
        }
    }
}