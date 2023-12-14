package com.example.finalproject
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text

class HomeScreenFragment : Fragment() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homescreen, container, false)
        drawerLayout = view.findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = view.findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(activity, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    drawerLayout.closeDrawers()
                }

                R.id.nav_recent_orders -> {
                    //Navigate to recent orders
                }

                R.id.nav_sign_out -> {
                    val auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
                // Add more navigation actions here
            }
            drawerLayout.closeDrawer(Gravity.LEFT, true)
            true
        }

        val signOutItem = navigationView.menu.findItem(R.id.nav_sign_out)
        val actionView = signOutItem.actionView
        val params = actionView?.layoutParams
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        actionView?.layoutParams = params

        actionView?.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        val fragment1 = FavoriteRestaurantsFragment()
        val fragment2 = AllRestaurantsFragment()

        childFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container_favorite, fragment1)
            add(R.id.fragment_container_all, fragment2)
            commit()
        }

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button: ImageButton = view.findViewById(R.id.menuButton)
        button.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }




        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        val im = FirebaseStorage.getInstance()
        val id = user?.uid
        val imref = im.reference.child("profileImages/$id/$id")
        val ref = db.collection("users")
        ref.document(user?.uid.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val name = drawerLayout.findViewById<TextView>(R.id.name)
                val email = drawerLayout.findViewById<TextView>(R.id.email)
                val pfp = drawerLayout.findViewById<ImageButton>(R.id.profile_image)
                val document = task.result
                if (document != null && document.exists()) {
                    imref.downloadUrl.addOnSuccessListener { uri ->
                        print(uri)
                        Glide.with(requireContext())
                            .load(uri)
                            .into(pfp)
                        val fname = document.getString("Name")
                        name.text = fname
                        email.text = user?.email
                    }
                }
            }
        }

    }
        }








