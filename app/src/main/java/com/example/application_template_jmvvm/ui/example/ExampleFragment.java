package com.example.application_template_jmvvm.ui.example;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.application_template_jmvvm.ui.sale.CardViewModel;
import com.example.application_template_jmvvm.utils.printHelpers.PrintHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.utils.objects.MenuItem;
import com.example.application_template_jmvvm.utils.printHelpers.StringHelper;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.ListMenuFragment.MenuItemClickListener;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.numpad.NumPadDialog;
import com.token.uicomponents.numpad.NumPadListener;
import com.tokeninc.deviceinfo.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class ExampleFragment extends Fragment {

    private MainActivity mainActivity;
    private ListMenuFragment listMenuFragment;
    private CardViewModel cardViewModel;
    List<IListMenuItem> menuItems = new ArrayList<>();
    protected int qrAmount = 100;
    protected String qrString = "QR Code Test";

    public ExampleFragment(MainActivity mainActivity, CardViewModel cardViewModel) {
        this.mainActivity = mainActivity;
        this.cardViewModel = cardViewModel;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_postxn, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<IListMenuItem> subList1 = new ArrayList<>();
        subList1.add(new MenuItem("Menu Item 1", (menuItem) -> Toast.makeText(this.mainActivity, "Sub Menu 1", Toast.LENGTH_LONG).show(), null));
        subList1.add(new MenuItem("Menu Item 2", (menuItem) -> Toast.makeText(this.mainActivity, "Sub Menu 2", Toast.LENGTH_LONG).show(), null));
        subList1.add(new MenuItem("Menu Item 3", (menuItem) -> Toast.makeText(this.mainActivity, "Sub Menu 3", Toast.LENGTH_LONG).show(), null));

        menuItems.add(new MenuItem("Sub Menu", subList1, null));

        menuItems.add(new MenuItem(getString(R.string.custom_input_list), (MenuItemClickListener<MenuItem>) menuItem -> {
            CustomInputListFragment CustomInputListFragment = new CustomInputListFragment(this.mainActivity);
            mainActivity.replaceFragment(R.id.container, CustomInputListFragment, true);
        }));

        menuItems.add(new MenuItem(getString(R.string.info_dialog), (menuItem) -> {
            InfoDialogFragment InfoDialogFragment = new InfoDialogFragment(this.mainActivity);
            mainActivity.replaceFragment(R.id.container, InfoDialogFragment, true);
        }));

        menuItems.add(new MenuItem(getString(R.string.confirmation_dialog), (menuItem) -> {
            ConfirmationDialogFragment ConfirmationDialogFragment = new ConfirmationDialogFragment(this.mainActivity);
            mainActivity.replaceFragment(R.id.container, ConfirmationDialogFragment, true);
        }));

        menuItems.add(new MenuItem(getString(R.string.device_info), (menuItem) -> {
            /*    [Device Info](https://github.com/TokenPublication/DeviceInfoClientApp)    */

            DeviceInfo deviceInfo = new DeviceInfo(mainActivity.getApplicationContext());
            deviceInfo.getFields(
                    fields -> {
                        if (fields == null) return;

                        Log.d("Example 0", "Fiscal ID:   "    + fields[0]);
                        Log.d("Example 1", "IMEI Number: "    + fields[1]);
                        Log.d("Example 2", "IMSI Number: "    + fields[2]);
                        Log.d("Example 3", "Modem Version : " + fields[3]);
                        Log.d("Example 4", "LYNX Number: "    + fields[4]);
                        Log.d("Example 5", "POS Mode: "       + fields[5]);

                        mainActivity.showInfoDialog(InfoDialog.InfoType.Info,
                                "Fiscal ID: "     +fields[0] +"\n"
                                        +"IMEI Number: "   +fields[1] +"\n"
                                        +"IMSI Number: "   +fields[2] +"\n"
                                        +"Modem Version: " +fields[3] +"\n"
                                        +"Lynx Version: "  +fields[4] +"\n"
                                        +"Pos Mode: "      +fields[5],true);
                        deviceInfo.unbind();
                    },
                    // write requested fields
                    DeviceInfo.Field.FISCAL_ID,
                    DeviceInfo.Field.IMEI_NUMBER,
                    DeviceInfo.Field.IMSI_NUMBER,
                    DeviceInfo.Field.MODEM_VERSION,
                    DeviceInfo.Field.LYNX_VERSION,
                    DeviceInfo.Field.OPERATION_MODE
            );
        }));

        menuItems.add(new MenuItem("Num Pad", (menuItem) -> {
            NumPadDialog dialog = NumPadDialog.newInstance(new NumPadListener() {
                @Override
                public void enter(String pin) { }
                @Override
                public void onCanceled() {
                    //Num pad canceled callback
                }
            }, "Please enter PIN", 8);
            dialog.show(mainActivity.getSupportFragmentManager(), "Num Pad");
        }));

        menuItems.add(new MenuItem(getString(R.string.show_qr), (menuItem) -> {
            InfoDialog dialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Progress, getString(R.string.qr_loading), true);
            cardViewModel.initializeCardServiceBinding(mainActivity);
            cardViewModel.getIsCardServiceConnect().observe(listMenuFragment.getViewLifecycleOwner(), isConnected -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
                cardViewModel.getCardServiceBinding().showQR(getString(R.string.please_read_qr), StringHelper.getAmount(qrAmount), qrString); // Shows QR on the back screen
                dialog.setQr(qrString, getString(R.string.waiting_qr_read));
            }, 2000));
        }));

        List<IListMenuItem> subListPrint = new ArrayList<>();
        subListPrint.add(new MenuItem("Print Load Success", (menuItem) -> {
            PrintHelper.PrintSuccess(mainActivity.getApplicationContext()); // Message print: Load Success
        }, null));

        subListPrint.add(new MenuItem("Print Load Error", (menuItem) -> {
            PrintHelper.PrintError(mainActivity.getApplicationContext()); // Message print: Load Error
        }, null));

        subListPrint.add(new MenuItem("Print contactless32", (menuItem) -> PrintHelper.PrintContactless(true, mainActivity.getApplicationContext()), null));

        subListPrint.add(new MenuItem("Print  contactless64", (menuItem) -> PrintHelper.PrintContactless(false, mainActivity.getApplicationContext()), null));

        subListPrint.add(new MenuItem("Print Visa", (menuItem) -> PrintHelper.PrintVisa(mainActivity.getApplicationContext()), null));

        menuItems.add(new MenuItem(getString(R.string.print_functions), subListPrint, null));

        listMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.examples), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, listMenuFragment, false);
    }
}