package com.app.tesis.eduardo.tesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tesis.eduardo.tesis.services.AccessServiceAPI;
import com.app.tesis.eduardo.tesis.utils.Constants;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Eduardo on 9/09/2016.
 */
public class UploadFragment extends Fragment {
    private static final int PICK_FILE_REQUEST = 1;
    private String historicalEventId;
    Integer userId;
    EditText title_txt;
    TextView title_error_lbl;
    ImageButton file_chooser;
    TextView file_error_lbl;
    ImageView file_preview;
    Button add_button;
    private String selectedFilePath;
    private Uri uri;
    File file;
    String content_type;
    String file_path;
    // Webservices
    private AccessServiceAPI m_ServiceAccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historicalEventId = getArguments().getString("historicalEventId");
        userId = getArguments().getInt("userId");
        m_ServiceAccess = new AccessServiceAPI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_he_upload, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        title_txt = (EditText)getView().findViewById(R.id.title_txt);
        title_error_lbl = (TextView)getView().findViewById(R.id.title_error_lbl);
        file_error_lbl = (TextView)getView().findViewById(R.id.file_error_lbl);
        file_preview = (ImageView)getView().findViewById(R.id.file_preview);
        file_chooser = (ImageButton)getView().findViewById(R.id.file_chooser);
        file_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
                //new MaterialFilePicker().withActivity(getActivity()).withRequestCode(PICK_FILE_REQUEST).start();
            }
        });
        add_button = (Button)getView().findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate_form()){
                    //file = new File(String.valueOf(uri));
                    //upload(file);
                    new TaskHEUpload(file,content_type,file_path,uri).execute(title_txt.getText().toString(),historicalEventId,String.valueOf(userId));
                }
            }
        });
    }

    private boolean validate_form(){
        boolean is_correct = true;
        title_error_lbl.setText(null);
        file_error_lbl.setText(null);
        if(title_txt.getText().length() == 0){
            title_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        if(file == null){
            file_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }
        return is_correct;
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //sets the select file to all types of files
        intent.setType("*/*");
        //starts new activity to select file and return data
        startActivityForResult(intent,PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_FILE_REQUEST){
            if(data == null){
                file = null;
                content_type = null;
                file_path = null;
                Toast.makeText(getContext(),R.string.no_file_selected,Toast.LENGTH_LONG).show();
                return;
            }

            //file = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
            uri = data.getData();
            file = new File(data.getData().getPath());
            content_type  = getContext().getContentResolver().getType(uri);
            file_path = file.getAbsolutePath();
            Log.d("onActivityResult",uri.getPath());
            Log.d("onActivityResult",file.getPath());
            Log.d("onActivityResult",file.getAbsolutePath());
            Log.d("onActivityResult",getContext().getContentResolver().getType(uri));
            Picasso.with(getContext()).load(uri).into(file_preview);
        }
        Log.d("onActivityResult", String.valueOf(resultCode));
        Log.d("onActivityResult", String.valueOf(requestCode));
    }
/*
    public void upload(File file){
        OkHttpClient client = new OkHttpClient();
        String type = getContext().getContentResolver().getType(uri);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", title_txt.getText().toString())
                .addFormDataPart("file", file.getName(),RequestBody.create(MediaType.parse(type), file))
                .build();

        Request request = new Request.Builder()
                .url(Constants.ENDPOINT_URL+Constants.ADD_PRE_PHOTO)
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
*/
    public class TaskHEUpload extends AsyncTask<String, Void, Integer> {
        JSONObject jObjResult;
        File file;
        String content_type;
        String file_path;
        Uri uri;
        public TaskHEUpload(File file, String content_type, String file_path,Uri uri){
            this.file = new File(file_path);
            this.content_type = content_type;
            this.file_path = file_path;
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            //System.out.println("ABSOLUTE PATH: "+file.getAbsolutePath());
            OkHttpClient client = new OkHttpClient();
            //String type = getContext().getContentResolver().getType(uri);
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type),file);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", params[0])
                    //.addFormDataPart("file", file_path.substring(file_path.lastIndexOf("/")+1),file_body)
                    .addFormDataPart("file", file.getName(),file_body)
                    .addFormDataPart("historical_event_id", params[1])
                    .addFormDataPart("citizen_id", params[2])
                    .build();

            Request request = new Request.Builder()
                    .url(Constants.ENDPOINT_URL+Constants.ADD_PRE_PHOTO)
                    .post(requestBody)
                    .build();

            //Response response = null;
            try {
                Response response = client.newCall(request).execute();
                //System.out.println(response.body().string());
                return Constants.ENDPOINT_SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
                return Constants.ENDPOINT_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(result == Constants.ENDPOINT_ERROR){
                Toast.makeText(getContext(), R.string.service_connection_error, Toast.LENGTH_LONG).show();
            }else if(result == Constants.ENDPOINT_SUCCESS){
                Toast.makeText(getContext(), "UPLOAD SUCCESS", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
