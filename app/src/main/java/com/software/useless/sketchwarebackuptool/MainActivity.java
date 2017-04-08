package com.software.useless.sketchwarebackuptool;

import java.util.*;
import java.text.*;
import eu.chainfire.libsuperuser.Shell;

import android.content.*;
import android.os.*;
import android.app.Activity;
import android.app.*;
import android.util.*;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.widget.*;
import java.lang.Object;
import android.widget.Button;
//
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.view.View.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Intent myintent = new Intent();
    public Boolean suIsInstalled;
    public Boolean bbIsInstalled;
    private AlertDialog.Builder mydialog;

    private class Startup extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog = null;
        private Context context = null;
        private boolean suAvailable = false;
        private String suVersion = null;
        private String suVersionInternal = null;
        private List<String> suResult = null;

        public Startup setContext(Context context) {
            this.context = context;
            return this;
        }

        @Override
        protected void onPreExecute() {
            // We're creating a progress dialog here because we want the user to wait.
            // If in your app your user can just continue on with clicking other things,
            // don't do the dialog thing.

            dialog = new ProgressDialog(context);
            dialog.setTitle("Analysing system");
            dialog.setMessage("Please wait a few seconds ...\n The program is checking \nif your device is rooted");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Let's do some SU stuff
            suAvailable = Shell.SU.available();
            if (suAvailable) {
                suVersion = Shell.SU.version(false);
                suVersionInternal = Shell.SU.version(true);
                suResult = Shell.SU.run(new String[]{
                        //        "id",
                        //        "ls -l /"
                        "busybox"});
                suIsInstalled = true;

                if (suResult.get(0).length()!=0) {
                    bbIsInstalled = true;
                }
            }

            // This is just so you see we had a progress dialog,
            // don't do this in production code
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();

            // output
            StringBuilder sb = (new StringBuilder()).
                    append("Root? ").append(suAvailable ? "Yes" : "No").append((char) 10).
                    append("Version: ").append(suVersion == null ? "N/A" : suVersion).append((char) 10).
                    append("Version (internal): ").append(suVersionInternal == null ? "N/A" : suVersionInternal).append((char) 10).
                    append((char) 10).
                    append("Busybox installed: ").append(suResult == null ? "N/A" : suResult.get(0)).append((char) 10);
            //if (suResult != null) {
                //for (String line : suResult) {
                //    sb.append(line).append((char) 10);
                //}
            //}
            ((TextView) findViewById(R.id.text)).setText(sb.toString());
            if (!suIsInstalled)  {
                mydialog.setIcon(android.R.drawable.ic_menu_info_details);
                mydialog.setTitle("Sorry");
                mydialog.setMessage("Your device does not seam to be rooted\n" +
                        "Please consider installing root if you need this tool");
                mydialog.setPositiveButton("OK",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface _dialog,int _which){
                        finish();

                    }
                });
                mydialog.create().show();



            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        initialize();
        initializeLogic();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MainActivity.this, "not available yet",
                        Toast.LENGTH_LONG).show();
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        ((TextView) findViewById(R.id.text)).setMovementMethod(new ScrollingMovementMethod());

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeLogic()   {
        suIsInstalled = false;
        bbIsInstalled = false;
    }

    private void initialize() {
        mydialog = new AlertDialog.Builder(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_check_su) {
            //myintent.setClass(getApplicationContext(),Main2Activity.class);
            //startActivity(myintent);
            (new Startup()).setContext(this).execute();



        } else if (id == R.id.nav_search_bu) {
            Toast.makeText(MainActivity.this, "not available yet",
                    Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_bu) {
            Toast.makeText(MainActivity.this, "not available yet",
                    Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_restore) {
            Toast.makeText(MainActivity.this, "not available yet",
                    Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_about) {
            Toast.makeText(MainActivity.this, "not available yet",
                    Toast.LENGTH_LONG).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
