package com.example.application_template_jmvvm.utils.objects;

import androidx.annotation.Nullable;

import com.token.uicomponents.ListMenuFragment.IAuthenticator;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.MenuItemClickListener;

import java.util.List;

public class MenuItem implements IListMenuItem {
    private String mTitle;
    private List<IListMenuItem> subMenuItemList;
    private MenuItemClickListener mListener;
    private IAuthenticator mAuthenticator;

    public MenuItem(String title, MenuItemClickListener listener) {
        this(title, listener, null, null);
    }

    public MenuItem(String title, MenuItemClickListener listener, @Nullable IAuthenticator authenticator) {
        this(title, listener, null, authenticator);
    }

    public MenuItem(String title, List<IListMenuItem> subMenuItemList, @Nullable IAuthenticator authenticator) {
        this(title, null, subMenuItemList, authenticator);
    }

    public MenuItem(String title, @Nullable MenuItemClickListener listener, @Nullable List<IListMenuItem> subMenuItemList, @Nullable IAuthenticator authenticator) {
        this.mTitle = title;
        this.mListener = listener;
        this.subMenuItemList = subMenuItemList;
        this.mAuthenticator = authenticator;
    }

    @Override
    public String getName() {
        return mTitle;
    }

    @Nullable
    @Override
    public List<IListMenuItem> getSubMenuItemList() {
        return subMenuItemList;
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
