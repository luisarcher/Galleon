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
    }

    public void onRegister(View v) {

        txtName = (EditText) findViewById(R.id.txtRegName);
        txtEmail = (EditText) findViewById(R.id.txtRegEmail);
        txtPassword = (EditText) findViewById(R.id.txtRegPassword);
        txtPassword2 = (EditText) findViewById(R.id.txtRegPassword2);
        txtBDate = (EditText) findViewById(R.id.txtRegBDate);

        /*if (checkAllFields()) {
            if (comparePasswords()) {*/

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("name", "luisFromAppTestFinal")
                        .appendQueryParameter("email", "luismcjordao@hotmail.com")
                        .appendQueryParameter("password", "myPassword");
                String query = builder.build().getEncodedQuery();

                new APICall(this).execute("/register",query);

            /*} else Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "Empty fields!", Toast.LENGTH_SHORT).show();*/
    }

    private boolean checkAllFields() {
        if (isEmpty(txtEmail)
                || isEmpty(txtEmail)
                || isEmpty(txtPassword)
                || isEmpty(txtPassword2)
                || isEmpty(txtBDate))
            return false;
        return true;
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

    private boolean comparePasswords() {
        if (txtPassword.getText().toString().equals(
                txtPassword2.getText().toString()
        )) return true;
        return false;
    }

    private class APICall extends AsyncTask<String, Void, String> {
        private final Context context;

        public APICall(Context c){
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
        protected void onPostExecute(String result) {
            progress.dismiss();
            Toast.makeText(this.context, result, Toast.LENGTH_SHORT).show();

            // Arranca uma nova activity e apaga as posteriores
            Intent i = new Intent(this.context, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }
}
