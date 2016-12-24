package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.isec.lj.galleon.API.PostRequest;
import pt.isec.lj.galleon.models.User;

public class LoginActivity extends Activity {

    private ProgressDialog progress;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(((GalleonApp) getApplication()).isSetSharedPreferencesSessId()){
            Intent i = new Intent(this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }


        setContentView(R.layout.activity_login);
    }

    public void register(View v){
        Intent i = new Intent(this, RegisterActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void onLogin(View v){
        email = ((TextView) findViewById(R.id.txtLoginEmail)).getText().toString();
        password = ((TextView) findViewById(R.id.txtLoginPassword)).getText().toString();

        if (!((GalleonApp) getApplication()).isNetworkAvailable(this)) {
            Toast.makeText(this, "There is no internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.equals("") || password.equals("")){
            Toast.makeText(this, "Empty fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("email", email)
                .appendQueryParameter("password", password);
        String query = builder.build().getEncodedQuery();

        new LoginTask(this).execute("/login", query);
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        private final Context context;
        PostRequest postReq;

        public LoginTask (Context c){
            this.context = c;
        }

        @Override
        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Logging in...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            postReq = new PostRequest(strings[0],strings[1]);

            if (postReq.isError()){
                String msg = postReq.getMessage();
                return (msg.isEmpty()) ? "Connection error!" : ("" + postReq.getResponseCode() + " " + msg);
            }else {
                saveUserData(postReq.getJsonResult());
                return "Login Success!";
            }
        }

        @Override
        protected void onPostExecute(String message) {
            progress.dismiss();
            Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();

            // Arranca uma nova activity e apaga as posteriores
            Intent i = new Intent(this.context, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        private void saveUserData(JSONObject obj){
            try {
                User user = new User(
                        obj.getInt("id"),
                        obj.getString("name"),
                        obj.getString("email"),
                        obj.getString("apiKey"));
                ((GalleonApp) getApplication()).setUser(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
