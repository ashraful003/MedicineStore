package com.example.medicinestore.presentation.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.example.medicinestore.R
import com.example.medicinestore.databinding.FragmentLoginLandingBinding


class LoginLandingFragment : Fragment() {
    val actionLogin = Navigation.createNavigateOnClickListener(R.id.action_loginLandingFragment_to_loginInputFragment)
    val actionSignUp = Navigation.createNavigateOnClickListener(R.id.action_loginLandingFragment_to_loginCreateFragment)
    private lateinit var binding:FragmentLoginLandingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login_landing, container, false)
        binding.model = this
        val pageOneView = LayoutInflater.from(activity).inflate(R.layout.login_slider_page_one,null)
        val pageTwoView = LayoutInflater.from(activity).inflate(R.layout.login_slider_page_two,null)
        val pageThreeView = LayoutInflater.from(activity).inflate(R.layout.login_slider_page_three,null)
        val views = listOf(pageOneView,pageTwoView,pageThreeView)
        val adapter = ViewPageAdapter(views)

        binding.sliderViewPager.adapter = adapter
        binding.sliderViewPager.registerOnPageChangeCallback(object :
        ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                changeColor()
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position:Int){
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                changeColor()
            }
        })

        return binding.root
    }
    private fun changeColor(){
        val context = requireContext()
        when(binding.sliderViewPager.currentItem){
            0 ->{
                binding.dotOneIv.setBackgroundColor(ContextCompat.getColor(context,R.color.blue_500))
                binding.dotTwoIv.setBackgroundColor(ContextCompat.getColor(context,R.color.slidePage))
                binding.dotThreeIv.setBackgroundColor(ContextCompat.getColor(context,R.color.slidePage))

            }
            1 ->{
                binding.dotOneIv.setBackgroundColor(ContextCompat.getColor(context,R.color.slidePage))
                binding.dotTwoIv.setBackgroundColor(ContextCompat.getColor(context,R.color.blue_500))
                binding.dotThreeIv.setBackgroundColor(ContextCompat.getColor(context,R.color.slidePage))

            }
            2 ->{
                binding.dotOneIv.setBackgroundColor(ContextCompat.getColor(context,R.color.slidePage))
                binding.dotTwoIv.setBackgroundColor(ContextCompat.getColor(context,R.color.slidePage))
                binding.dotThreeIv.setBackgroundColor(ContextCompat.getColor(context,R.color.blue_500))

            }
        }
    }
}