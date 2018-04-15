package com.example.adaiboad.realtravelapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import com.example.adaiboad.realtravelapp.Adapters.CustomInfoAdapter;
import com.example.adaiboad.realtravelapp.Adapters.HolidayData;
import com.example.adaiboad.realtravelapp.Adapters.ImageAdapter;
import com.example.adaiboad.realtravelapp.Adapters.MyHolidayRecyclerViewAdapter;
import com.example.adaiboad.realtravelapp.Adapters.PlaceAutoCompleteAdapter;
import com.example.adaiboad.realtravelapp.GalleryActivity;
import com.example.adaiboad.realtravelapp.MainActivity;
import com.example.adaiboad.realtravelapp.MapsActivity;
import com.example.adaiboad.realtravelapp.Model.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.common.api.GoogleApiClient;

import com.example.adaiboad.realtravelapp.Model.Holiday;
import com.example.adaiboad.realtravelapp.Model.HolidayDatePicker;
import com.example.adaiboad.realtravelapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HolidayDetailsFragment.OnHolidayDetailsInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HolidayDetailsFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class HolidayDetailsFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String HOLIDAY = "Holiday";
    public static final String ADDNEW = "AddNew";
    private static final String TAG = "HolidayDetailsFragment" ;

    // TODO: Rename and change types of parameters
    private Holiday holiday;
    private boolean addNew;


    //****** Add capture picture or select photo function ******//
    Button holidayPicture;
    ImageView ivImage;
    private Uri photoUri;
    private GridView holidayPhotoGrid;
    private ImageAdapter imageAdapter;
    private String mCurrentPhotoPath;
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;
    Integer DELETE_FILE=2, SHARE_FILE=3;





    //****** Create a map and add the location on the map ******//
    private GoogleMap holidayMap;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false;
    private Marker mMarker;
    private FusedLocationProviderClient mLocationClient;




    String locationName;

    //****** Create autocomplete places ******//
    private OnHolidayDetailsInteractionListener mListener;
    private Button startDate;
    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView location;
    private Button endDate;
    private PlaceAutoCompleteAdapter mAdapter;
    private PlaceInfo mPlace;
    private MapView holidayMapView;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private EditText title;
    private  EditText notes;
    private Calendar newStartDate;
    private Calendar newEndDate;





    public HolidayDetailsFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment HolidayDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static HolidayDetailsFragment newInstance(String param1, String param2) {
//        HolidayDetailsFragment fragment = new HolidayDetailsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("INFO", "Check if we have a holiday passed to us");
        if (getArguments() != null) {
            Log.i("INFO", "We have a holiday passed to us");
            holiday = (Holiday) getArguments().getSerializable(HOLIDAY);
            addNew = getArguments().getBoolean(ADDNEW);
            Log.i("INFO", "The holoday's title is" + holiday.getTitle());
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (holidayMapView != null) {
            holidayMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            final View view = inflater.inflate(R.layout.fragment_holiday_details, container, false);

            //back to previous page
            Button backButton = view.findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(view.getContext(), MainActivity.class);
                    startActivity(i);
                }
            });




        Button dateButton = (Button)view.findViewById(R.id.dateButton);
        dateButton.setPaintFlags(dateButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Button endDateButton = (Button)view.findViewById(R.id.endDate);
        endDateButton.setPaintFlags(endDateButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        //create editable title
        title = view.findViewById(R.id.editTitle);
        title.setText(holiday.getTitle());

        //create editable notes
        notes = view.findViewById(R.id.editNotes);
        notes.setText(holiday.getNotes());


        //set places auto complete when a user picks a place
        location = (AutoCompleteTextView)view.findViewById(R.id.locationPicker);
        location.setText(holiday.getLocation());

        //Change the date via the calendar
        startDate = (Button)view.findViewById(R.id.dateButton);
        startDate.setText(holiday.formatStartDate());
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v, true);
            }
        });

        endDate = (Button)view.findViewById(R.id.endDate);
        endDate.setText(holiday.formatEndDate());
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v, false);
            }
        });



        FloatingActionButton saveButton = view.findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addNew)
                    HolidayData.addHoliday(holiday);


                holiday.setTitle(title.getText().toString());
                holiday.setNotes(notes.getText().toString());
                holiday.setLocation(location.getText().toString());
                holiday.setStartDate(newStartDate);
                holiday.setEndDate(newEndDate);
            }
        });





        holidayMapView  = view.findViewById(R.id.holidaymap);
        if(holidayMapView != null)
        {
            holidayMapView.onCreate(savedInstanceState);
            holidayMapView.onResume();
            holidayMapView.getMapAsync(this);
        }

        //take a picture
        holidayPicture = view.findViewById(R.id.holidayPicBtn);
        holidayPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();

            }
        });

        ivImage = (ImageView)view.findViewById(R.id.ivImage);

        holidayPhotoGrid = (GridView)view.findViewById(R.id.gridview);

        /* The image adapter that contains a list of filenames to be displayed in the grid */
        imageAdapter = new ImageAdapter(getContext());
        /* Read the file names from the app's Picture folder */
        /* notify the data changed */
        imageAdapter.notifyDataSetChanged();
        /* now set the adapter for the grid view */




        init();

        return view;
    }







    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnHolidayDetailsInteractionListener(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHolidayDetailsInteractionListener) {
            mListener = (OnHolidayDetailsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnHolidayDetailsInteractionListener {
        // TODO: Update argument type and name
        void OnHolidayDetailsInteractionListener(Uri uri);
    }




    public void showDatePickerDialog(View v, Boolean isStartDate) {
        HolidayDatePicker newFragment = new HolidayDatePicker();
        newFragment.setHoliday(holiday);
        if(isStartDate) {
            Log.i("INFO", "setting startdate button");
            newFragment.setDateButton(startDate);

        }
        else{
            Log.i("INFO", "setting enddate button");
            newFragment.setDateButton(endDate);
        }
        newFragment.setStartDate(isStartDate);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

     /*
        --------------------------- google map activity to show holiday location on the map -----------------
 */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        holidayMap = googleMap;


        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            holidayMap.setMyLocationEnabled(true);
            holidayMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();

        }


    }




    private void getDeviceLocation() {

        mLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (mLocationPermissionsGranted) {
                Task location = mLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,"My Location" );

                        } else {
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "onClick: SecurityException: " + e.getMessage() );

        }

    }





    private void init() {

        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        mAdapter = new PlaceAutoCompleteAdapter(getContext(), mGoogleApiClient, LAT_LNG_BOUNDS, null);
        location.setAdapter(mAdapter);
        location.setOnItemClickListener(mAutocompleteClickListener);

        location.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocation();
                }
                return false;
            }

        });




    }








    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);}


    private void moveCamera(LatLng latLng, float zoom, String title) {
        holidayMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //add marker to the map
        if(!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            holidayMap.addMarker(options);
        }

        hideSoftKeyboard();


    }

    private void moveCamera(LatLng latLng, float zoom, Holiday holiday) {
        holidayMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        holidayMap.clear();

        holidayMap.setInfoWindowAdapter(new CustomInfoAdapter(getContext()));

        if(holiday != null){
            try{

                String snippet =holiday.formatStartDate() + " " + "-" + " " +  holiday.formatEndDate() + "\n" +
                        mPlace.getAddress() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(holiday.getTitle())
                        .snippet(snippet);
                mMarker = holidayMap.addMarker(options);

            }catch(NullPointerException e){
                Log.e(TAG, "onClick: NullPointerException: " + e.getMessage() );

            }
        }else{
            holidayMap.addMarker(new MarkerOptions().position(latLng));
        }



        hideSoftKeyboard();

    }



    public void geoLocation(){
        String searchString = location.getText().toString();

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "onClick: IOException: " + e.getMessage() );

        }
        if(list.size() > 0){
            Address address = list.get(0);

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));


        }

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }





 /*
        --------------------------- google places API autocomplete suggestions -----------------
 */

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            hideSoftKeyboard();

            final AutocompletePrediction item = mAdapter.getItem(i);
            final String placeId = item.getPlaceId();



            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


        }

    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id:" + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());

                //get latitude and longitude


            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }
            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, holiday);

            places.release();
        }
    };




    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();

        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }



     /*
        --------------------------- Capture pictures and add to the holiday details-----------------
 */

    private void SelectImage(){

        final CharSequence[] items={"Camera","Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if(resultCode== Activity.RESULT_OK){

            handlePhoto();

            if(requestCode==REQUEST_CAMERA){

                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");

                ivImage.setImageBitmap(bmp);

            }else if(requestCode==SELECT_FILE){

                Uri selectedImageUri = data.getData();
                ivImage.setImageURI(selectedImageUri);
            }

        }
    }

    private void handlePhoto() {
        Log.i("AJB", "Handle BIG photo");
        if (mCurrentPhotoPath != null) {
            Log.i("AJB", "BIG photo located at " + mCurrentPhotoPath);
            imageAdapter.addImage(mCurrentPhotoPath);
            imageAdapter.notifyDataSetChanged();

            mCurrentPhotoPath = null;
        } else {
            Log.i("AJB", "BIG photo but no path");
        }


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
