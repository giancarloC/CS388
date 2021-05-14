package com.example.duckdating.ui.match;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duckdating.data.LoginRepository;
import com.example.duckdating.data.model.LoggedInUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MatchViewModel extends ViewModel {
    LoginRepository loginRepository;
    LoggedInUser user;

    private MutableLiveData<String> email = new MutableLiveData<>();

    private MutableLiveData<List<ParseObject>> listDucks = new MutableLiveData<>();


    public MatchViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        user = this.loginRepository.getUser();
        String userEmail = user.getEmail();
        email.setValue(userEmail);

        //grabs list of other ducks
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Match");
        query.whereEqualTo("email", userEmail);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){

                    //grabs emails that you have matched with
                    List<String> otherEmails = new ArrayList<>();
                    for (ParseObject o: objects){
                        otherEmails.add(o.get("otherEmail").toString());
                    }

                    //performs another query to find if they have matched you
                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Match");
                    query2.whereEqualTo("otherEmail", userEmail);
                    query2.whereContainedIn("email", otherEmails);
                    query2.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects2, ParseException e2) {
                            if (e2 == null){

                                //grabs emails that you have watched with for data
                                List<String> emailsIWant = new ArrayList<>();
                                for(ParseObject o: objects2){
                                    emailsIWant.add(o.get("email").toString());
                                }

                                //PERFORMS ANOTHER QUERY TO GET ALL THE DUCK INFO
                                ParseQuery<ParseObject> query3 = ParseQuery.getQuery("Duck");
                                query3.whereContainedIn("email", emailsIWant);
                                query3.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects3, ParseException e3) {
                                        if (e3 == null){

                                            //finally finds data
                                            listDucks.setValue(objects3);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<List<ParseObject>> getListDucks() {
        return listDucks;
    }
}