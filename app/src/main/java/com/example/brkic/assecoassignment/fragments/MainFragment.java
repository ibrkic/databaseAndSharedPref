package com.example.brkic.assecoassignment.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.brkic.assecoassignment.R;
import com.example.brkic.assecoassignment.db.DBHelper;
import com.example.brkic.assecoassignment.presenter.MainPresenter;
import com.example.brkic.assecoassignment.presenter.MainPresenterImpl;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by brka on 18.08.2017..
 */
public class MainFragment extends Fragment implements MainView {

    public static final String TAG = MainFragment.class.getSimpleName();
    private SharedPreferences sharedpreferences;
    private MainPresenter mPresenter;

    @BindView(R.id.et_web_url)
    EditText webUrlEt;
    @BindView(R.id.btn_generate_hash)
    Button generateHashBtn;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBHelper db = new DBHelper(getActivity());
        sharedpreferences = getActivity().getSharedPreferences("HashPreferences", Context.MODE_PRIVATE);
        mPresenter = new MainPresenterImpl(this, sharedpreferences, db);
        if (mPresenter != null) {
            mPresenter.bind();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.unbind();
        }
    }

    @OnClick(R.id.btn_generate_hash)
    void generateHash() {
        generateHashBtn.setEnabled(false);
        mPresenter.parseWebPage(webUrlEt.getText().toString().trim());
    }

    /**
     * If web page content is successfully parsed and saved inside SP or DB we want to show success message
     *
     * @param successType Is it saved inside SP or DB
     * @param webPageUrl  Url of saved page
     * @param hash        Hash value of saved page
     */
    @Override
    public void showSuccessMessage(String successType, String webPageUrl, byte[] hash) {
        StringBuilder sb = new StringBuilder(webPageUrl);
        sb.append(" with hash:");
        sb.append(System.getProperty("line.separator"));
        sb.append(Arrays.toString(hash));
        sb.append(System.getProperty("line.separator"));
        sb.append("was SUCCESSFULLY saved to ");
        switch (successType) {
            case MainPresenterImpl.SAVED_TO_DB:
                sb.append("database.");
                showAlertDialog(sb.toString());
                break;
            case MainPresenterImpl.SAVED_TO_SP:
                sb.append("SharedPreferences.");
                showAlertDialog(sb.toString());
                break;
        }
        generateHashBtn.setEnabled(true);
    }

    /**
     * If web page is already parsed and saved inside SP or DB display AlertDialog with message
     *
     * @param storageType Is it saved inside SP or DB
     * @param webPageUrl  Url of already saved web page
     * @param hash        Hash value of already saved page
     */
    @Override
    public void showAlreadySavedMessage(String storageType, String webPageUrl, byte[] hash) {
        disableButtonForFiveSeconds();
        if (storageType.equals(MainPresenterImpl.SAVED_TO_DB)) {
            storageType = "database";
        } else {
            storageType = "SharedPreferences";
        }
        showAlertDialog(getString(R.string.data_already_saved, webPageUrl, Arrays.toString(hash), storageType));//%1s with hash: %2s \n was already saved to %3s.
    }

    /**
     * Disable submit button for five seconds
     */
    private void disableButtonForFiveSeconds() {
        generateHashBtn.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                generateHashBtn.setEnabled(true);
            }
        }, 5000);
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss
                    }
                })
                .show();
    }

    /**
     * This method is used to display various error messages
     *
     * @param errorType Flag that indicates error type
     */
    @Override
    public void showErrorMessage(final String errorType) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (errorType) {
                    case MainPresenterImpl.INVALID_URL:
                        Toast.makeText(getActivity(), getString(R.string.invalid_url), Toast.LENGTH_SHORT).show();
                        break;
                    case MainPresenterImpl.ERROR_SAVING_TO_DB:
                        Toast.makeText(getActivity(), getString(R.string.error_saving_to_db), Toast.LENGTH_SHORT).show();
                        break;
                    case MainPresenterImpl.ERROR_SAVING_TO_SP:
                        Toast.makeText(getActivity(), getString(R.string.error_saving_to_sp), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getActivity(), getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
                        break;
                }
                generateHashBtn.setEnabled(true);
            }
        });
    }
}
