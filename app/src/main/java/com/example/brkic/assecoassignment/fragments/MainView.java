package com.example.brkic.assecoassignment.fragments;

/**
 * Created by brka on 19.08.2017..
 */
public interface MainView {

    void showSuccessMessage(String successType, String webPageUrl, byte[] hash);
    void showAlreadySavedMessage(String storageType, String webPageUrl, byte[] hash);
    void showErrorMessage(String errorType);
}
