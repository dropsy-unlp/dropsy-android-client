package com.fuentesfernandez.dropsy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.glassfish.tyrus.client.ClientManager;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

/**
 * A connect screen that offers connection via host/port/path.
 */
public class ConnectActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private ServerConnectTask mAuthTask = null;

    // UI references.
    private EditText mHostView;
    private EditText mPortView;
    private EditText mPathView;
    private View mProgressView;
    private View mConnectionFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        mHostView = (EditText) findViewById(R.id.host);
        mPortView = (EditText) findViewById(R.id.port);
        mPathView = (EditText) findViewById(R.id.path);

        Button mConnectButton = (Button) findViewById(R.id.connect_button);
        mConnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptConnection();
            }
        });

        mConnectionFormView = findViewById(R.id.connection_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to connect using the data provided in the connection form.
     * If there are form errors, the
     * errors are presented and no actual connection attempt is made.
     */
    private void attemptConnection() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mHostView.setError(null);
        mPortView.setError(null);
        mPathView.setError(null);

        // Store values at the time of the login attempt.
        String host = mHostView.getText().toString();
        String port = mPortView.getText().toString();
        String path = mPathView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid host, if the user entered one.
        if (!TextUtils.isEmpty(host)) {
            mHostView.setError(getString(R.string.error_field_required));
            focusView = mHostView;
            cancel = true;
        }

        // Check for a valid port, if the user entered one.
        if (!TextUtils.isEmpty(port)) {
            mHostView.setError(getString(R.string.error_field_required));
            focusView = mHostView;
            cancel = true;
        }

        // Check for a valid path, if the user entered one.
        if (!TextUtils.isEmpty(path)) {
            mHostView.setError(getString(R.string.error_field_required));
            focusView = mHostView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt connection and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the connection attempt.
            showProgress(true);
            mAuthTask = new ServerConnectTask(host, port,path);
            mAuthTask.execute((Void) null);
        }
    }


    /**
     * Shows the progress UI and hides the connection form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mConnectionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mConnectionFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mConnectionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mConnectionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    /**
     * Represents an asynchronous connection task used to connect to the server.
     */
    public class ServerConnectTask extends AsyncTask<Void, Void, Boolean> {

        private final String url;

        ServerConnectTask(String host, String port, String path) {
            url = host + ":" + port + "/" + path;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                final ClientManager client = ClientManager.createClient();
                client.connectToServer(new Endpoint() {
                    @Override
                    public void onOpen(Session session, EndpointConfig EndpointConfig) {

                        try {
                            session.addMessageHandler(new MessageHandler.Whole<String>() {
                                @Override
                                public void onMessage(String message) {
                                    Log.i("TYRUS-TEST", "### 3 Tyrus Client onMessage: " + message);
                                }
                            });

                            Log.i("TYRUS-TEST", "### 2 Tyrus Client onOpen");
                            session.getBasicRemote().sendText("Do or do not, there is no try.");
                        } catch (IOException e) {
                            // do nothing
                        }
                    }
                }, ClientEndpointConfig.Builder.create().build(), URI.create(url));
            } catch (DeploymentException | IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {

        }
    }
}

