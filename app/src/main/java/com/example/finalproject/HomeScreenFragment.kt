package com.example.finalproject
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeScreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homescreen, container, false)

        // Create instances of your fragments
        val fragment1 = FavoriteRestaurantsFragment()
        val fragment2 = AllRestaurantsFragment()

        // Add the fragments to your HomeScreenFragment
        childFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container_favorite, fragment1)
            add(R.id.fragment_container_all, fragment2)
            commit()
        }
        return view
    }
}







