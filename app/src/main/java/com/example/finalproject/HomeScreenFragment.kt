package com.example.finalproject
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class HomeScreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homescreen, container, false)

        val fragment1 = FavoriteRestaurantsFragment()
        val fragment2 = AllRestaurantsFragment()


        childFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container_favorite, fragment1)
            add(R.id.fragment_container_all, fragment2)
            commit()
        }
        return view
    }
}







