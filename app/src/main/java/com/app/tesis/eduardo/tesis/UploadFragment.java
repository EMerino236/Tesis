package com.app.tesis.eduardo.tesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static com.app.tesis.eduardo.tesis.utils.CustomToast.centeredToast;

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
    TextView file_name_lbl;
    Button add_button;
    private Uri uri;
    File file;
    String content_type;
    String file_path;
    String file_name;
    Boolean isPhoto;
    Long file_size;
    // Webservices
    ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historicalEventId = getArguments().getString("historicalEventId");
        userId = getArguments().getInt("userId");
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_uploading));
        mProgressDialog.setTitle(R.string.progress_dialog_title);
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
        file_name_lbl = (TextView)getView().findViewById(R.id.file_name_lbl);
        file_chooser = (ImageButton)getView().findViewById(R.id.file_chooser);
        file_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        add_button = (Button)getView().findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate_form()){
                    //file = new File(String.valueOf(uri));
                    //upload(file);
                    new TaskHEUpload(isPhoto,mProgressDialog,content_type,file_path).execute(title_txt.getText().toString(),historicalEventId,String.valueOf(userId));
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
        if(file == null || uri == null || content_type == null){
            file_error_lbl.setText(R.string.required_field);
            is_correct = false;
        }else if(!content_type.equals("image/png") && !content_type.equals("image/jpeg") && !content_type.equals("image/jpg") && !content_type.equals("audio/mpeg") && !content_type.equals("audio/mpga")){
            file_error_lbl.setText(R.string.file_accepted_extentions);
            is_correct = false;
        }
        if(content_type != null && file_size>=2097152){
            file_error_lbl.setText(R.string.file_size_cap);
            is_correct = false;
        }
        return is_correct;
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //sets the select file to all types of files
        intent.setType("image/png");
        intent.setType("image/jpeg");
        intent.setType("image/jpg");
        intent.setType("image/mpeg");
        intent.setType("image/mpga");
        //starts new activity to select file and return data
        startActivityForResult(intent,PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(resultCode == RESULT_OK && requestCode == PICK_FILE_REQUEST){
                if(data == null){
                    file = null;
                    content_type = null;
                    file_path = null;
                    file_preview.setImageBitmap(null);
                    file_name_lbl.setText("");
                    file_name = "";
                    file_size = 0L;
                    centeredToast(getContext(),getString(R.string.no_file_selected));
                    //Toast.makeText(getContext(),R.string.no_file_selected,Toast.LENGTH_LONG).show();
                    return;
                }

                uri = data.getData();
                content_type  = getContext().getContentResolver().getType(uri);
                file_path = getRealPathFromURI(uri);
                file = new File(file_path);
                file_size = file.length();
                file_name = file_path.substring(file_path.lastIndexOf("/")+1);
                file_name_lbl.setText(file_name);
                if(content_type.equals("image/png") || content_type.equals("image/jpeg") || content_type.equals("image/jpg")){
                    Picasso.with(getContext()).load(uri).into(file_preview);
                    isPhoto = true;
                }else if(content_type.equals("audio/mpeg") || content_type.equals("audio/mpga")){
                    Picasso.with(getContext()).load(R.drawable.audio_icon).into(file_preview);
                    isPhoto = false;
                }
            }
        }catch (Exception e) {
            centeredToast(getContext(),getString(R.string.general_error));
            //Toast.makeText(getContext(), R.string.general_error, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public class TaskHEUpload extends AsyncTask<String, Void, Integer> {
        JSONObject jObjResult;
        File file;
        String content_type;
        String file_path;
        Response response;
        ProgressDialog mProgressDialog;
        Boolean isPhoto;
        public TaskHEUpload(Boolean isPhoto, ProgressDialog mProgressDialog, String content_type, String file_path){
            this.isPhoto = isPhoto;
            this.mProgressDialog = mProgressDialog;
            this.file = new File(file_path);
            this.content_type = content_type;
            this.file_path = file_path;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
            add_button.setEnabled(false);
        }

        @Override
        protected Integer doInBackground(String... params) {
            String url;
            if (isPhoto) {
                url = Constants.ENDPOINT_URL+Constants.ADD_PRE_PHOTO;
            }else{
                url = Constants.ENDPOINT_URL+Constants.ADD_PRE_AUDIO;
            }
            OkHttpClient client = new OkHttpClient();
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type),new File(file_path));

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", params[0])
                    .addFormDataPart("file", file.getName(),file_body)
                    .addFormDataPart("historical_event_id", params[1])
                    .addFormDataPart("citizen_id", params[2])
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            //Response response = null;
            try {
                response = client.newCall(request).execute();
                //System.out.println(response.body().string());
                if (!response.isSuccessful()){
                    return Constants.ENDPOINT_ERROR;
                }
                return Constants.ENDPOINT_SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
                return Constants.ENDPOINT_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            if(result == Constants.ENDPOINT_ERROR){
                String jsonData = null;
                try {
                    jsonData = response.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    centeredToast(getContext(),Jobject.getString("message"));
                    //Toast.makeText(getContext(), Jobject.getString("message"), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    centeredToast(getContext(),getString(R.string.service_connection_error));
                    //Toast.makeText(getContext(), R.string.service_connection_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    centeredToast(getContext(),getString(R.string.service_connection_error));
                    //Toast.makeText(getContext(), R.string.service_connection_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }else if(result == Constants.ENDPOINT_SUCCESS){
                file = null;
                content_type = null;
                file_path = null;
                file_preview.setImageBitmap(null);
                centeredToast(getContext(),getString(R.string.add_success));
                getActivity().finish();
                //Toast.makeText(getContext(), R.string.upload_success, Toast.LENGTH_LONG).show();
            }
            add_button.setEnabled(true);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContext().getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
