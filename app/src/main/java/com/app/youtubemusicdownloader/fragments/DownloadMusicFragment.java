package com.app.youtubemusicdownloader.fragments;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.youtubemusicdownloader.R;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import okio.BufferedSink;
import okio.Okio;


public class DownloadMusicFragment extends Fragment{

    public DownloadMusicFragment() {
        // Required empty public constructor
    }

    Button download_button;
    final String yt_download_server_url = "http://10.0.2.2:1337/download/json";
    final String android_filename = "songs.zip";
    final String android_path = "/storage/emulated/0/Music";
    String url;
    ArrayList<String> url_list = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_downloadmusic, container, false);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        View root_view = getView();


        if(root_view != null)
        {
            download_button = (Button)root_view.findViewById(R.id.download_songs);

            download_button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    View createDLList_view = getFragmentManager().getFragments().get(0).getView();
                    TextView url_counter = (TextView)createDLList_view.findViewById(R.id.url_counter);


                    for(int i = 0; i < Integer.parseInt(url_counter.getText().toString()); i++)
                    {
                        url = ((EditText)createDLList_view.findViewWithTag("edit_text_input_url" + i)).getHint().toString();
                        if(!url_list.contains("https://www." + url))
                        {
                            url_list.add("https://www." + url);
                        }
                    }
                    // https://www.youtube.com/watch?v=ERlBHyOjeLI
                    // https://www.youtube.com/watch?v=7zDkniNTeZg

                    //String url_list = "{\"encoder\" : \"mp3\", \"url_list\" : [\"https://www.youtube.com/watch?v=ES9vRfs2rbA\", \"https://www.youtube.com/watch?v=85CoKLuxTzY\"]}";

                    String json_input = "{\"encoder\" : \"mp3\", \"url_list\" : \"" + url_list + "\"}";
                    Log.d("jsonInput", json_input);
                    Log.d("jsonInput", url_list.toString());

                    ConnectionBuilder conBuild = new ConnectionBuilder();
                    conBuild.post(yt_download_server_url, json_input, new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.d("onFailure", "failed: " + e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            if (response.isSuccessful()) {

                                //Log.d("path", Environment.getExternalStorageDirectory() + "");
                                try
                                {
                                    //Log.d("resp", response.toString());
                                    zipBytes(getContext().getFilesDir().getPath() + "/" + android_filename, response.body().source().readByteArray());
                                    //saveText(getContext().getFilesDir().getPath() + "/", response.body().source());
                                    //Log.d("savedInZip", response.body().string());
                                    Log.d("savedInZip", "SUCCESS to save in zip");
                                }
                                catch (IOException ioe)
                                {
                                    Log.d("inputIOE", ioe.getMessage());
                                }

                                //String responseStr = response.body().string();
                                //Log.d("POSTresp", "SUCCESS");
                            } else {
                                Log.d("POSTresp", "ERROR");
                            }
                        }
                    });
                }
            });
        }
    }

    public static byte[] zipBytes(String filename, byte[] input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        ZipEntry entry = new ZipEntry(filename);
        entry.setSize(input.length);
        zos.putNextEntry(entry);
        zos.write(input);
        zos.closeEntry();
        zos.close();
        return baos.toByteArray();
    }

    public static void saveText(String path, String input) throws IOException {
        File root = new File(path);
        File gpxfile = new File(root, "test.txt");
        FileWriter writer = new FileWriter(gpxfile);
        writer.append(input);
        writer.flush();
        writer.close();
    }

}
