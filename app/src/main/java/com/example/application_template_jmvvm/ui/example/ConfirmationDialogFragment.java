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

        menuItems.add(new MenuItem("Confirmed", (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Confirmed, "Confirmed", "Confirmation: Confirmed", InfoDialog.InfoDialogButtons.Both, 99, this)));

        menuItems.add(new MenuItem("Warning", (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Warning, "Warning", "Confirmation: Warning", InfoDialog.InfoDialogButtons.Both, 98, this)));

        menuItems.add(new MenuItem("Error", (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Error, "Error", "Confirmation: Error", InfoDialog.InfoDialogButtons.Both, 97, this)));

        menuItems.add(new MenuItem("Info", (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Info, "Info", "Confirmation: Info", InfoDialog.InfoDialogButtons.Both, 96, this)));

        menuItems.add(new MenuItem("Declined", (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Declined, "Declined", "Confirmation: Declined", InfoDialog.InfoDialogButtons.Both, 95, this)));

        menuItems.add(new MenuItem("Connecting", (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Connecting, "Connecting", "Confirmation: Connecting", InfoDialog.InfoDialogButtons.Both, 94, this)));

        menuItems.add(new MenuItem("Downloading", (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Downloading, "Downloading", "Confirmation: Downloading", InfoDialog.InfoDialogButtons.Both, 93, this)));

        menuItems.add(new MenuItem("Uploading", (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Uploading, "Uploading", "Confirmation: Uploading", InfoDialog.InfoDialogButtons.Both, 92, this)));

        menuItems.add(new MenuItem("Processing", (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Processing, "Processing", "Confirmation: Processing", InfoDialog.InfoDialogButtons.Both, 91, this)));

        menuItems.add(new MenuItem("Progress", (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Progress, "Progress", "Confirmation: Progress", InfoDialog.InfoDialogButtons.Both, 90, this)));

        menuItems.add(new MenuItem("None", (menuItem) ->
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.None, "None", "Confirmation: None", InfoDialog.InfoDialogButtons.Both, 89, this)));

        ListMenuFragment mListMenuFragment = ListMenuFragment.newInstance(menuItems, "Confirmation Dialog", true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, mListMenuFragment, false);
    }

    public void confirmed(int arg) {
        if (arg == 99) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Confirmed, "Confirmed!", true);
        }
        //else if (arg == ***) { Do something else... }
        if (arg == 98) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Warning, "Warning!", true);
        }
        if (arg == 97) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Error, "Error!", true);
        }
        if (arg == 96) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Info, "Info!", true);
        }
        if (arg == 95) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Declined, "Declined!", true);
        }
        if (arg == 94) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Connecting, "Connecting!", true);
        }
        if (arg == 93) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Downloading, "Downloading!", true);
        }
        if (arg == 92) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Uploading, "Uploading!", true);
        }
        if (arg == 91) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Processing, "Processing!", true);
        }
        if (arg == 90) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Progress, "Progress!", true);
        }
        if (arg == 89) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.None, "None!", true);
        }
    }

    @Override
    public void canceled(int arg) {
        if (arg <= 99|| arg >= 89) {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Error, "Canceled", true);
        }
        //else if (arg == ***) { Do something else... }
    }

}