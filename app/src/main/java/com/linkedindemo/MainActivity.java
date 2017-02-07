package com.linkedindemo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getKeyHash();
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initLinkedin();
            }
        });
    }

    /**
     * get KeyHash to add in the Linkedin developer console
     */

    private void getKeyHash()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    /**
     * authentication and to get the access
     */
    private void initLinkedin() {
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                shareOnLinkedIn();// once you get successfully authenticated, do your desired code like to share
            }

            @Override
            public void onAuthError(LIAuthError error) {
                System.out.println("Sorry Failed to get connection");
            }
        }, true);
    }




    /**
     * Build the list of member permissions our LinkedIn session requires
     */
    private static Scope buildScope()
    {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);
    }





    /**
     * sharing on Linked account
     */
    public void shareOnLinkedIn() {
        String url = "https://api.linkedin.com/v1/people/~/shares";
        /*
        *here payload is te string content that you want to share on your Linked in account
        * */
        String payload = "{" +
                "\"comment\":\"Check out developer.linkedin.com! " +
                "http://linkd.in/1FC2PyG\"," +
                "\"visibility\":{" +
                "    \"code\":\"anyone\"}" +
                "}";
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.postRequest(this, url, payload, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                Toast.makeText(getApplicationContext(), "Successfully posted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                Toast.makeText(getApplicationContext(), "Error  posted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * handle the resulting responses.  This is done by calling LISessionManager's implementation of onActivityResult()
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Add this line to your existing onActivityResult() method
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }
}