package br.com.carinatiemiyoshida.twitterdemonumberone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    private static final String TWITTER_KEY = "8QeQVcYpAdqwjIL2InwvRalZj";
    private static final String TWITTER_SECRET = "nF5j2VRv04QBpOntb7CD7dPnZb8dCve09Gm2RMxosDwh3uVAVm";
    private TwitterLoginButton loginButton;
    private ImageView imageView;
    private TextView txtDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        imageView = (ImageView) findViewById(R.id.imageView);
        txtDetails = (TextView) findViewById(R.id.txtDetails);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                final String userName = session.getUserName();
                //String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Call<User> user = Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false);
                user.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        User userInfo = result.data;
                        String email = userInfo.email;
                        String description = userInfo.description;
                        String location = userInfo.location;
                        int friendsCount = userInfo.friendsCount;
                        int favouritesCount = userInfo.favouritesCount;
                        int followersCount = userInfo.followersCount;

                        String profileImageUrl = userInfo.profileImageUrl.replace("normal", "");
                        Picasso.with(getApplicationContext()).load(profileImageUrl).into(imageView);
                        StringBuilder sb = new StringBuilder();
                        sb.append("User Name: "+ userName);
                        sb.append("\n");
                        sb.append("Email: "+ email);
                        sb.append("\n");
                        sb.append("Description: "+ description);
                        sb.append("\n");
                        sb.append("Location: "+ location);
                        sb.append("\n");
                        sb.append("FriendsCount: "+ friendsCount);
                        sb.append("\n");
                        sb.append("FavouritesCount: "+ favouritesCount);
                        sb.append("\n");
                        sb.append("FollowersCount: "+ followersCount);
                        txtDetails.setText(sb.toString());
                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }
                });
                loginButton.setVisibility(View.INVISIBLE);
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
                Log.e("BLA: ", exception.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

}
