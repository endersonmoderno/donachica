package br.com.gruposvb.donachica;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_Tab1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Tab1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Tab1 extends Fragment {
    public Fragment_Tab1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab1, container, false);
    }
}
