package com.example.application_template_jmvvm.Uicomponents;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelStoreOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.Viewmodels.SaleViewModel;

public class SaleFragment extends Fragment {

    private SaleViewModel mViewModel;
    private MainActivity main;

    public SaleFragment(MainActivity mainActivity) {
        this.main = mainActivity;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(SaleViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        mViewModel.setActionName(getString(R.string.Sale_Action));
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView actionNameTextView = view.findViewById(R.id.action_name_text_view);
        mViewModel.getActionName().observe(getViewLifecycleOwner(), actionNameTextView::setText);
    }

}