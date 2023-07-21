package com.example.application_template_jmvvm.ui.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.token.uicomponents.ListMenuFragment.IAuthenticator;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.ListMenuFragment.MenuItemClickListener;
import com.token.uicomponents.infodialog.InfoDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment includes InfoDialog types and methods
 */
public class InfoDialogFragment extends Fragment {
    static class InfoDialogItem implements IListMenuItem {

        private InfoDialog.InfoType mType;
        private String mText;
        private MenuItemClickListener mListener;
        private IAuthenticator mAuthenticator;

        public InfoDialogItem(InfoDialog.InfoType type, String text, MenuItemClickListener listener, IAuthenticator authenticator) {
            mType = type;
            mText = text;
            mAuthenticator = authenticator;
            mListener = listener;
        }

        @Override
        public String getName() {
            return mText;
        }

        @Nullable
        @Override
        public List<IListMenuItem> getSubMenuItemList() {
            return null;
        }

        @Nullable
        @Override
        public MenuItemClickListener getClickListener() {
            return mListener;
        }

        @Nullable
        @Override
        public IAuthenticator getAuthenticator() {
            return mAuthenticator;
        }
    }

    List<IListMenuItem> menuItems = new ArrayList<>();
    private MainActivity mainActivity;

    public InfoDialogFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_dialog, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MenuItemClickListener listener = (MenuItemClickListener<InfoDialogItem>) this::showPopup;

        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Confirmed, getString(R.string.confirmed), listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Warning, getString(R.string.warning), listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Error, getString(R.string.error_example), listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Info, getString(R.string.info), listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Declined, getString(R.string.declined_example), listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Connecting, getString(R.string.connecting_example), listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Downloading, getString(R.string.downloading), listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Uploading, getString(R.string.uploading), listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Processing, getString(R.string.processing), listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Progress, getString(R.string.progress), listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.None, getString(R.string.none), listener, null));

        ListMenuFragment mListMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.info_dialog) , true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, mListMenuFragment, false);
    }

    private void showPopup(InfoDialogItem item) {
        InfoDialog dialog = mainActivity.showInfoDialog(item.mType, item.mText, true);
        //Dismiss dialog by calling dialog.dismiss() when needed.
    }
}
