package com.example.medicinestore.presentation.dashboard.admin

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentAdminHomeBinding
import com.example.medicinestore.presentation.MainActivity
import com.example.medicinestore.util.MSActivityUtil
import com.example.medicinestore.util.SharePreferenceUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class AdminHomeFragment : Fragment() {
    var actionAddMedicin = Navigation.createNavigateOnClickListener(R.id.action_adminHomeFragment_to_addMedicinFragment)
    val actionMedicinList = Navigation.createNavigateOnClickListener(R.id.action_adminHomeFragment_to_medicinListFragment)
    val actionExpireMedicin = Navigation.createNavigateOnClickListener(R.id.action_adminHomeFragment_to_expireMedicinFragment)
    val actionEmployee = Navigation.createNavigateOnClickListener(R.id.action_adminHomeFragment_to_employeeFragment)

    @Inject
    lateinit var sharedPrefs: SharePreferenceUtil
    @Inject
    lateinit var activityUtil : MSActivityUtil
    private lateinit var viewModel: AdminHomeViewModel
    private lateinit var binding:FragmentAdminHomeBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_admin_home, container, false)
        binding.model = this
        activityUtil.hideBottomNavigation(false)
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminHomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}