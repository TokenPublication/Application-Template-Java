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

        MenuItemClickListener listener = (MenuItemClickListener<InfoDialogItem>) item -> showPopup(item);

        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Confirmed, "Confirmed", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Warning, "Warning", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Error, "Error", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Info, "Info", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Declined, "Declined", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Connecting, "Connecting", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Downloading, "Downloading", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Uploading, "Uploading", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Processing, "Processing", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.Progress, "Progress", listener, null));
        menuItems.add(new InfoDialogItem(InfoDialog.InfoType.None, "None", listener, null));

        ListMenuFragment mListMenuFragment = ListMenuFragment.newInstance(menuItems, "Info Dialog" , true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, mListMenuFragment, false);
    }

    private void showPopup(InfoDialogItem item) {
        InfoDialog dialog = mainActivity.showInfoDialog(item.mType, item.mText, true);
        //Dismiss dialog by calling dialog.dismiss() when needed.
    }

    public void onPosTxnResponse() {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        //bundle.putString("ResponseCode", PosTxnResponse);
        resultIntent.putExtras(bundle);
        mainActivity.setResult(Activity.RESULT_OK, resultIntent);//PosTxn_Request_Code:13
        mainActivity.finish();
    }

}