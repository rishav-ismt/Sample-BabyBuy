package np.com.rishavchudal.myapplication.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import np.com.rishavchudal.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PurchasedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PurchasedFragment extends Fragment {

    public static PurchasedFragment newInstance() {
        PurchasedFragment fragment = new PurchasedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_purchased, container, false);
    }
}