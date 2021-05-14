package com.example.duckdating.ui.login;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duckdating.R;

import java.io.IOException;

public class RegisterFragment extends Fragment {
    private final String TAG = "RegisterFragment";

    private RegisterViewModel registerViewModel;
    private Context context;
    private Uri selectedImage;
    private Bitmap bitmap;
    private int GALLERY_REQUEST = 420;

    private EditText email;
    private EditText firstName;
    private EditText lastName;
    private EditText location;
    private EditText bio;
    private EditText passwordEditText;
    private TextView image;
    private TextView gender;

    private Button registerButton;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory(getActivity().getApplication()))
                .get(RegisterViewModel.class);
        View root = inflater.inflate(R.layout.register_fragment, container, false);

        email = root.findViewById(R.id.email);
        firstName = root.findViewById(R.id.firstName);
        lastName = root.findViewById(R.id.lastName);
        location = root.findViewById(R.id.location);
        bio = root.findViewById(R.id.bio);
        passwordEditText = root.findViewById(R.id.password);

        gender = root.findViewById(R.id.gender);
        image = root.findViewById(R.id.image);

        registerButton = root.findViewById(R.id.register);
        final ProgressBar loadingProgressBar = root.findViewById(R.id.loading);

        //thing for choosing a gender
        String[] genders = {"Male", "Female", "Nonbinary", "Other"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose your Gender");
        builder.setItems(genders, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gender.setText(genders[which]);
            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

        //choosing an image
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });


        registerViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            if (loginFormState.getUsernameError() != null) {
                email.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getFirstError() != null){
                firstName.setError(getString(loginFormState.getFirstError()));
            }
            if (loginFormState.getLastError() != null){
                lastName.setError(getString(loginFormState.getLastError()));
            }
            if (loginFormState.getLocationError() != null){
                location.setError(getString(loginFormState.getLocationError()));
            }
            if (loginFormState.getBioError() != null){
                bio.setError(getString(loginFormState.getBioError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
            Log.v(TAG, "gender: " + gender.getText().toString());
            if (!gender.getText().toString().isEmpty() && !image.getText().toString().isEmpty()){
                registerButton.setEnabled(loginFormState.isDataValid());
            }
        });

        registerViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                Log.v(this.getClass().getSimpleName(), "Got login result");
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                //TODO: do something with the logged in user data?
               /*TODO: Since this example uses Navigation, we don't need to directly work with the
                fragment manager
                * */
                Navigation.findNavController(root).navigate(R.id.nav_home);

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerViewModel.loginDataChanged(email.getText().toString(),
                        firstName.getText().toString(), lastName.getText().toString(),
                        location.getText().toString(), bio.getText().toString(),
                        passwordEditText.getText().toString(), gender.getText().toString(),
                        image.getText().toString());
            }
        };
        email.addTextChangedListener(afterTextChangedListener);
        firstName.addTextChangedListener(afterTextChangedListener);
        lastName.addTextChangedListener(afterTextChangedListener);
        location.addTextChangedListener(afterTextChangedListener);
        bio.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        gender.addTextChangedListener(afterTextChangedListener);
        image.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerUser();
            }
            return false;
        });

        registerButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            registerUser();
        });

        /*final TextView textView = root.findViewById(R.id.text_home);
        registerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
        //return inflater.inflate(R.layout.login_fragment, container, false);
    }

    private void registerUser(){
        Log.v(TAG, "testing");
        registerViewModel.register(email.getText().toString(), firstName.getText().toString(),
                        lastName.getText().toString(), location.getText().toString(),
                        bio.getText().toString(), bitmap,
                        gender.getText().toString(),
                        passwordEditText.getText().toString());

        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                selectedImage = data.getData();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getActivity().getContentResolver(), selectedImage));
                    } else {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    }
                } catch (IOException e) {
                    Log.i(TAG, "Some exception: " + e);
                }

                //change text to file path
                String[] path = selectedImage.getPath().split("/");
                Log.v(TAG, "path: " + selectedImage.getPath());
                image.setText(path[path.length - 1] + "." + path[path.length - 2]);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        context = getContext();
        // TODO: Use the ViewModel
    }
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(context, welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
    }
}
