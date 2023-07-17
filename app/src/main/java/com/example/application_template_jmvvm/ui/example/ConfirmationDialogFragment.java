package com.example.application_template_jmvvm.ui.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.utils.objects.MenuItem;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.ArrayList;
import java.util.List;

public class ConfirmationDialogFragment extends Fragment implements InfoDialogListener {

    List<IListMenuItem> menuItems = new ArrayList<>();
    private MainActivity mainActivity;

    public ConfirmationDialogFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmation_dialog, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menuItems.add(new MenuItem(getString(R.string.confirmed), (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Confirmed, getString(R.string.confirmed), getString(R.string.confirmation) + ": " + getString(R.string.confirmed), InfoDialog.InfoDialogButtons.Both, 99, this)));

        menuItems.add(new MenuItem(getString(R.string.warning), (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Warning, getString(R.string.warning), getString(R.string.confirmation) + ": " + getString(R.string.warning), InfoDialog.InfoDialogButtons.Both, 98, this)));

        menuItems.add(new MenuItem(getString(R.string.error), (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Error, getString(R.string.error), getString(R.string.confirmation) + ": " + getString(R.string.error), InfoDialog.InfoDialogButtons.Both, 97, this)));

        menuItems.add(new MenuItem(getString(R.string.info), (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Info, getString(R.string.info), getString(R.string.confirmation) + ": " + getString(R.string.info), InfoDialog.InfoDialogButtons.Both, 96, this)));

        menuItems.add(new MenuItem(getString(R.string.declined), (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Declined, getString(R.string.declined), getString(R.string.confirmation) + ": " + getString(R.string.declined), InfoDialog.InfoDialogButtons.Both, 95, this)));

        menuItems.add(new MenuItem(getString(R.string.connecting), (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Connecting, getString(R.string.connecting), getString(R.string.confirmation) + ": " + getString(R.string.connecting), InfoDialog.InfoDialogButtons.Both, 94, this)));

        menuItems.add(new MenuItem(getString(R.string.downloading), (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Downloading, getString(R.string.downloading), getString(R.string.confirmation) + ": " + getString(R.string.downloading), InfoDialog.InfoDialogButtons.Both, 93, this)));

        menuItems.add(new MenuItem(getString(R.string.uploading), (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Uploading, getString(R.string.uploading), getString(R.string.confirmation) + ": " + getString(R.string.uploading), InfoDialog.InfoDialogButtons.Both, 92, this)));

        menuItems.add(new MenuItem(getString(R.string.processing), (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Processing, getString(R.string.processing), getString(R.string.confirmation) + ": " + getString(R.string.processing), InfoDialog.InfoDialogButtons.Both, 91, this)));

        menuItems.add(new MenuItem(getString(R.string.progress), (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Progress, getString(R.string.progress), getString(R.string.confirmation) + ": " + getString(R.string.progress), InfoDialog.InfoDialogButtons.Both, 90, this)));

        menuItems.add(new MenuItem(getString(R.string.none), (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.None, getString(R.string.none), getString(R.string.confirmation) + ": " + getString(R.string.none), InfoDialog.InfoDialogButtons.Both, 89, this)));

        ListMenuFragment mListMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.confirmation_dialog), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, mListMenuFragment, false);
    }

    public void confirmed(int arg) {
        if (arg == 99) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Confirmed, getString(R.string.confirmed) + "!", true);
        }
        //else if (arg == ***) { Do something else... }
        if (arg == 98) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Warning, getString(R.string.warning) + "!", true);
        }
        if (arg == 97) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Error, getString(R.string.error) + "!", true);
        }
        if (arg == 96) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Info, getString(R.string.info) + "!", true);
        }
        if (arg == 95) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Declined, getString(R.string.declined) + "!", true);
        }
        if (arg == 94) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Connecting, getString(R.string.connecting) + "!", true);
        }
        if (arg == 93) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Downloading, getString(R.string.downloading) + "!", true);
        }
        if (arg == 92) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Uploading, getString(R.string.uploading) + "!", true);
        }
        if (arg == 91) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Processing, getString(R.string.processing) + "!", true);
        }
        if (arg == 90) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Progress, getString(R.string.progress) + "!", true);
        }
        if (arg == 89) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.None, getString(R.string.none) + "!", true);
        }
    }

    @Override
    public void canceled(int arg) {
        if (arg <= 99|| arg >= 89) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Error, getString(R.string.cancelled), true);
        }
        //else if (arg == ***) { Do something else... }
    }

}