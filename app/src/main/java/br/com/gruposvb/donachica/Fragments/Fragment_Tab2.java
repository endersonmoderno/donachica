package br.com.gruposvb.donachica.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.gruposvb.donachica.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_Tab2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Tab2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Tab2 extends Fragment {
    public Fragment_Tab2() {
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
        return inflater.inflate(R.layout.fragment_tab2, container, false);
    }
}
