package com.example.medicinestore.presentation

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.medicinestore.R
import com.example.medicinestore.databinding.ActivityMainBinding
import com.example.medicinestore.util.MSActivityUtil
import com.example.medicinestore.util.SharePreferenceUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MSActivityUtil.ActivityListener {
    @Inject
    lateinit var sharedPrefs: SharePreferenceUtil

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }
        val authedUser: Boolean = try {
            !sharedPrefs.getAuthToken().isNullOrEmpty()
        } catch (e: Exception) {
            false
        }
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment
        with(navHostFragment) {
            val inflater = findNavController().navInflater
            if (authedUser) {
                findNavController().graph = (inflater.inflate(R.navigation.dashboard_navigation))
            } else {
                findNavController().graph = (inflater.inflate(R.navigation.login_navigation))
            }
        }
        navController = navHostFragment.navController
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> navHostFragment.findNavController().navigate(R.id.adminHomeFragment)
                R.id.order -> navHostFragment.findNavController().navigate(R.id.orderMedicineFragment)
                R.id.profile -> navHostFragment.findNavController().navigate(R.id.profileFragment)
            }
            true
        }
    }

    override fun hideBottomNavigation(hide: Boolean) {
        if (hide) {
            binding.bottomNavigationView.visibility = View.GONE
        } else {
            binding.bottomNavigationView.visibility = View.VISIBLE
        }
    }

    override fun setFullScreenLoading(short: Boolean) {
        if (short) {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            binding.fullscreenLoading.visibility = View.VISIBLE
        } else {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            binding.fullscreenLoading.visibility = View.GONE
        }
    }

    override fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        if (binding.fullscreenLoading.visibility != View.GONE){
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){

            }else{
                super.onBackPressed()
            }
        }
    }

    companion object {
        fun getLaunchIntent(context: Context) = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }
}