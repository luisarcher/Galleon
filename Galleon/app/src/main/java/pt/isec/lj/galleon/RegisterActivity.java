package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pt.isec.lj.galleon.API.PostRequest;

public class RegisterActivity extends Activity {

    private ProgressDialog progress;

    EditText txtName;
    EditText txtEmail;
    EditText txtPassword;
    EditText txtPassword2;
    EditText txtBDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtName = (EditText) findViewById(R.id.txtRegName);
        txtEmail = (EditText) findViewById(R.id.txtRegEmail);
        txtPassword = (EditText) findViewById(R.id.txtRegPassword);
        txtPassword2 = (EditText) findViewById(R.id.txtRegPassword2);
        txtBDate = (EditText) findViewById(R.id.txtRegBDate);
    }

    public void onRegister(View v) {

        if (!((GalleonApp) getApplication()).isNetworkAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkAllFields()) {
            Toast.makeText(this, getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!comparePasswords()) {
            Toast.makeText(this, getResources().getString(R.string.not_matching_passwords), Toast.LENGTH_SHORT).show();
            return;
        }

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("name", txtName.getText().toString())
                .appendQueryParameter("email", txtEmail.getText().toString())
                .appendQueryParameter("password", txtPassword.getText().toString());
        String query = builder.build().getEncodedQuery();

        new RegisterTask(this).execute("/register", query);
    }

    private boolean checkAllFields() {
        return !(isEmpty(txtEmail)
                || isEmpty(txtEmail)
                || isEmpty(txtPassword)
                || isEmpty(txtPassword2)
                || isEmpty(txtBDate));
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    private boolean comparePasswords() {
        return txtPassword.getText().toString().equals(
                txtPassword2.getText().toString()
        );
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {
        private final Context context;

        RegisterTask(Context c){
            this.context = c;
        }

        @Override
        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            PostRequest postReq = new PostRequest(strings[0],strings[1]);
            return postReq.getMessage();
        }

        @Override
        protected void onPostExecute(String message) {
            progress.dismiss();
            Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();

            // Arranca uma nova activity e apaga as posteriores
            Intent i = new Intent(this.context, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }
}
