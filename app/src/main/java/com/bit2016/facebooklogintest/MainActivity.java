package com.bit2016.facebooklogintest;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager; // Activity또는 Fragment의 onActivityResult() 메소드에서 FacebookSdk로 콜백을 관리한다.
    Button share,details;
    ShareDialog shareDialog; // 대화상자를 통해 콘텐츠를 공유하는 기능 제공
    LoginButton login;
    ProfilePictureView profile; // 사용자가 지정한 차원을 준수하면서 제공된 프로파일ID의 프로필 사진을 표시한다.
    Dialog details_dialog;
    TextView details_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext()); // Facebook SDK 초기화

        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create(); // Callback method 생성
        login = (LoginButton)findViewById(R.id.login_button);
        profile = (ProfilePictureView)findViewById(R.id.picture);
        shareDialog = new ShareDialog(this);
        share = (Button)findViewById(R.id.share);
        details = (Button)findViewById(R.id.details);
        login.setReadPermissions("public_profile email");
        share.setVisibility(View.INVISIBLE); //share 버튼 안보이기(로그인안되있을시)
        details.setVisibility(View.INVISIBLE); //details 버튼 안보이기(로그인안되있을시)

        details_dialog = new Dialog(this);
        details_dialog.setContentView(R.layout.dialog_details);
        details_dialog.setTitle("Details");

        details_txt = (TextView)details_dialog.findViewById(R.id.details);

        details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    details_dialog.show();
                }
        });

        if(AccessToken.getCurrentAccessToken() != null){
            RequestData();
            share.setVisibility(View.VISIBLE);
            details.setVisibility(View.VISIBLE);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AccessToken.getCurrentAccessToken() != null) {
                    share.setVisibility(View.INVISIBLE);
                    details.setVisibility(View.INVISIBLE);
                    profile.setProfileId(null);
                }
            }
        });

        /*share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkContent content = new ShareLinkContent.Builder().build();
                shareDialog.show(content);
            }
        });*/

        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if(AccessToken.getCurrentAccessToken() != null){
                    RequestData(); // 로그인성공시 데이터요구 함수 호출
                    share.setVisibility(View.VISIBLE); //로그인성공시 share 버튼 보이기
                    details.setVisibility(View.VISIBLE); //로그인성공시 details 버튼 보이기
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }
        });
    }

    public void RequestData(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object,GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if(json != null){
                        String text = "Name : "+json.getString("name")+"\n\n\nEmail :\n "+json.getString("email")+"\n\n\nProfile link :\n "+json.getString("link");
                        //json으로 이름, 이메일, 페이스북프로필 링크 가져옴
                        details_txt.setText(text); //가져온데이터 출력
                        profile.setProfileId(json.getString("id")); // 프로필 사진셋팅
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}