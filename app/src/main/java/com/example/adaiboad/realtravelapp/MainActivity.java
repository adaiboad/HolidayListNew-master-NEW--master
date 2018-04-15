package com.example.adaiboad.realtravelapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.design.widget.BottomNavigationView;

import com.example.adaiboad.realtravelapp.Fragments.HolidayDetailsFragment;
import com.example.adaiboad.realtravelapp.Fragments.HolidayListFragment;
import com.example.adaiboad.realtravelapp.Model.Holiday;

public class MainActivity extends AppCompatActivity implements  HolidayListFragment.OnAddNewHolidayListener, HolidayListFragment.OnEditHolidayListener,
        HolidayDetailsFragment.OnHolidayDetailsInteractionListener {

    ImageView coverPhoto;
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavViewHelper.disableShiftMode(navigation);

        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:

                        break;
                    case R.id.navigation_map:
                        Intent intent2 = new Intent(MainActivity.this, MapsActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_gallery:
                        Intent intent3 = new Intent(MainActivity.this, GalleryActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });



        HolidayListFragment holidayListFragment = new HolidayListFragment();
        // Create a new Fragment to be placed in the activity layout
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        holidayListFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, holidayListFragment).commit();

        //take a picture
        coverPhoto = findViewById(R.id.coverPhoto);
        coverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();

            }
        });

}

    private void SelectImage(){

        final CharSequence[] items={"Camera","Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);


                } else if (items[i].equals("Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }

    private void goToHoldayDetailsPage(Holiday item, boolean addNew, boolean removeHoliday)
    {
        Toast.makeText(this, "You clicked " + item.toString(), Toast.LENGTH_LONG).show();
        // Create the new fragment,

        HolidayDetailsFragment holidayDetailsFragment = new HolidayDetailsFragment();
        // add an argument specifying the item it should show
        // note that the DummyItem class must implement Serializable
        Bundle args = new Bundle();
        Log.i("INFO", "storing the holiday in the arguments using the key Item");
        args.putSerializable(HolidayDetailsFragment.HOLIDAY, item);
        args.putBoolean(HolidayDetailsFragment.ADDNEW, addNew);
        args.putBoolean(HolidayDetailsFragment.REMOVEHOLIDAY, removeHoliday);

        holidayDetailsFragment.setArguments(args);

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, holidayDetailsFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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

    @Override
    public void OnEditHolidayListener(Holiday item) {
        goToHoldayDetailsPage(item,false, false);
    }

    @Override
    public void OnAddNewHolidayListener() {
        Holiday item = new Holiday("", "", "");
        goToHoldayDetailsPage(item,true, true);
    }

    @Override
    public void OnHolidayDetailsInteractionListener(Uri uri) {

    }

    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if(resultCode== Activity.RESULT_OK){


            if(requestCode==REQUEST_CAMERA){

                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");

                coverPhoto.setImageBitmap(bmp);

            }else if(requestCode==SELECT_FILE){

                Uri selectedImageUri = data.getData();
                coverPhoto.setImageURI(selectedImageUri);
            }

        }
    }

}
