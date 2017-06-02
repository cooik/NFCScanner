package info.androidhive.materialdesign.activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.fragment.AllTagFragment;
import info.androidhive.materialdesign.fragment.FragmentDrawer;
import info.androidhive.materialdesign.fragment.ReadTagFragment;
import info.androidhive.materialdesign.fragment.RateProductFragment;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG ="tanjin";
    NfcAdapter nfcAdapter;
    public String tagContent;
    private Toolbar mToolbar;
    private boolean TagIsAvaliable;
    private FragmentDrawer drawerFragment;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "MainActivity------>onCreate");
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        Bundle bundle = null;
        switch (position) {
            case 0:
                fragment = new AllTagFragment();
                title = getString(R.string.title_alltags);
                bundle = new Bundle();
                bundle.putBoolean("TagIsAvaliable", TagIsAvaliable);
                bundle.putString("tagContent",tagContent);
                break;
            case 1:
                fragment = new ReadTagFragment();
                title = getString(R.string.title_readtag);
                bundle = new Bundle();
                bundle.putBoolean("TagIsAvaliable", TagIsAvaliable);
                bundle.putString("tagContent",tagContent);
                break;
            case 2:
                fragment = new RateProductFragment();
                title = getString(R.string.title_writetag);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if(bundle!=null)
            {
                fragment.setArguments(bundle);
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            final Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            Toast.makeText(this, "NfcIntent!", Toast.LENGTH_LONG).show();
            final AlertDialog.Builder builder  = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("选择读写标签");
            final String[] readorwrite = {"真伪验证","产品溯源"};
            builder.setItems(readorwrite, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(MainActivity.this,"Chose"+readorwrite[which],Toast.LENGTH_SHORT).show();
                    if(which==1){
                        if (parcelables != null && parcelables.length > 0) {
                            readTextFromMessage((NdefMessage) parcelables[0]);
                        } else {
                            TagIsAvaliable = false;
                            tagContent =  "No NDEF messages found!";
                        }
                        displayView(1);
                    }else if(which==0)
                    {
                        if (parcelables != null && parcelables.length > 0) {
                            readTextFromMessage((NdefMessage) parcelables[0]);
                        } else {
                            TagIsAvaliable = false;
                            tagContent =  "No NDEF messages found!";
                        }
                        displayView(0);
                    }
                }
            });
            builder.show();
        }
    }

    private void  readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {

            NdefRecord ndefRecord = ndefRecords[0];

            tagContent = getTextFromNdefRecord(ndefRecord);
            TagIsAvaliable = true;
        } else {
            tagContent = "No NDEF records found!";
            TagIsAvaliable = false;
        }
//        Toast.makeText(this, tagContent, Toast.LENGTH_SHORT).show();
    }

    private String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding;
            if ((payload[0] & 128) == 0) textEncoding = "UTF-8";
            else textEncoding = "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}