package pt.isec.lj.galleon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends Activity {

    EditText txtName ;
    EditText txtEmail ;
    EditText txtPassword ;
    EditText txtPassword2 ;
    EditText txtBDate ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }

    public void onRegister(View v){
        StringBuilder buff = new StringBuilder();

        txtName = (EditText)findViewById(R.id.txtRegName);
        txtEmail = (EditText)findViewById(R.id.txtRegEmail);
        txtPassword = (EditText)findViewById(R.id.txtRegPassword);
        txtPassword2 = (EditText)findViewById(R.id.txtRegPassword2);
        txtBDate = (EditText)findViewById(R.id.txtRegBDate);

        if(checkAllFields()){
            if(checkPasswords()){
                //Tudo ok...
                buff.append("?");

                buff.append("name=" +     txtName.getText().toString() );
                buff.append("&email=" +   txtEmail.getText().toString());
                buff.append("&password="+ txtPassword.getText().toString());

                APICaller apiCaller = new APICaller();
                Toast.makeText(this,"Creating user...",Toast.LENGTH_SHORT).show();
                String actionResult = apiCaller.createUser(buff.toString());
                Toast.makeText(this,"Processing result...",Toast.LENGTH_SHORT).show();

                //try {
                    Toast.makeText(this,actionResult,Toast.LENGTH_SHORT).show();
                /*} catch (JSONException e) {
                    e.printStackTrace();
                }*/

                if (!apiCaller.isErrorInResult(actionResult)){
                    // Se não houve erros, o registo foi realizado, pode fazer login
                    Intent intent = new Intent(this,LoginActivity.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this,"Passwords do not match!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkAllFields(){
        if(isEmpty(txtEmail)
                || isEmpty(txtEmail)
                || isEmpty(txtPassword)
                || isEmpty(txtPassword2)
                || isEmpty(txtBDate)){
            Toast.makeText(this, "Empty fields!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    private boolean checkPasswords(){
        if(txtPassword.getText().toString().equals(
                txtPassword2.getText().toString()
        )) return true;
        return false;
    }
}
