package com.example.application_template_jmvvm.Uicomponents;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.Viewmodels.PosTxnViewModel;
import com.example.application_template_jmvvm.Viewmodels.SaleViewModel;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;

import java.util.ArrayList;
import java.util.List;

public class PosTxnFragment extends Fragment {

    private FragmentManager fragmentManager;
    private PosTxnViewModel mViewModel;
    private Button dummyButton;

    private ListMenuFragment mListMenuFragment;
    private MainActivity main;

    public PosTxnFragment(MainActivity mainActivity) {
        this.main = mainActivity;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(PosTxnViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postxn, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<IListMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.transactions), iListMenuItem -> {
        }));
        menuItems.add(new MenuItem(getString(R.string.refund), iListMenuItem -> {
        }));
        menuItems.add(new MenuItem(getString(R.string.batch_close), iListMenuItem -> {
        }));
        menuItems.add(new MenuItem(getString(R.string.examples), iListMenuItem -> {
        }));

        ListMenuFragment mListMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.pos_operations), true, R.drawable.token_logo_png);
        //mViewModel.getMenuItems().observe(getViewLifecycleOwner(), mListMenuFragment::setListItems);
        // Add ListMenuFragment to the view hierarchy of PosTxnFragment

        getChildFragmentManager().beginTransaction()
                .add(R.id.container, mListMenuFragment)
                .commit();
    }
}