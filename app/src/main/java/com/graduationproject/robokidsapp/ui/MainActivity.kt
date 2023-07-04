package com.graduationproject.robokidsapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.databinding.ActivityMainBinding
import com.graduationproject.robokidsapp.ui.parentsFragments.auth.AuthViewModel
import com.graduationproject.robokidsapp.ui.parentsFragments.info.InfoViewModel
import com.graduationproject.robokidsapp.ui.parentsFragments.info.ParentsHomeFragmentDirections
import com.graduationproject.robokidsapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

//                              بسم الله الرحمن الرحيم
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        lateinit var binding: ActivityMainBinding
    }

    private lateinit var navController: NavController

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var infoViewModel: InfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // هذه ال if علشان لما يفتح ال navigation drawer يلاقي الداتا الي سجل بيها تكون موجوده
        if (authViewModel.currentUser != null) {
            infoViewModel = ViewModelProvider(this).get(InfoViewModel::class.java)
            infoViewModel.getParentData()
            lifecycleScope.launch {
                infoViewModel.getParent.observe(this@MainActivity) { resource ->
                    when (resource) {
                        is Resource.Loading -> {}
                        is Resource.Failure -> {
                            showToast("" + resource.error)
                        }
                        is Resource.Success -> {
                            val view = binding.navigationView.getHeaderView(0)
                            val name = view.findViewById<TextView>(R.id.tv_userName)
                            val email = view.findViewById<TextView>(R.id.tv_email)
                            name.text = resource.data.name
                            email.text = resource.data.email
                        }
                    }
                }
            }
        }


        // this code is Permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        setSupportActionBar(binding.customToolbarMainActivity)
        binding.drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        // لكي يستدعي الداله الي تحت الي اسمها  onNavigationItemSelected
        binding.navigationView.setNavigationItemSelectedListener(this)


        val actionToggle = ActionBarDrawerToggle(
            this, binding.drawLayout, binding.customToolbarMainActivity,
            R.string.draw_open, R.string.draw_close
        )

        binding.drawLayout.addDrawerListener(actionToggle)
        actionToggle.syncState()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.myKids -> {
                val action =
                    ParentsHomeFragmentDirections.actionParentsHomeFragmentToHomeKidsFragment()
                navController.navigate(action)
                binding.drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            R.id.knowApp -> {
                val action =
                    ParentsHomeFragmentDirections.actionParentsHomeFragmentToGetToKnowTheAppFragment2()
                navController.navigate(action)
                binding.drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            R.id.commonQuestions -> {
                val action =
                    ParentsHomeFragmentDirections.actionParentsHomeFragmentToCommonQuestionsFragment()
                navController.navigate(action)
                binding.drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            R.id.setting -> {
                val action =
                    ParentsHomeFragmentDirections.actionParentsHomeFragmentToSettingFragment()
                navController.navigate(action)
                binding.drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            R.id.signOut -> {
                authViewModel.logout {}
                val action =
                    ParentsHomeFragmentDirections.actionParentsHomeFragmentToWelcomeFragment()
                navController.navigate(action)

                binding.drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

        }
        closeDrawer() // this method to end or hide the Navigation drawer
        return true
    }


    fun closeDrawer() {
        binding.drawLayout.closeDrawer(GravityCompat.START)
    }

    fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }

    // هذه الدله علشان لو انت كنت فاتح ال navigation drawer ضغط علي زر الرجوع ميخرجش من التطبيق كاكل
    // لا هي لو مفتوحه هيعملها close عادي ومش هيخرجك من التطبيق
    override fun onBackPressed() {
        if (binding.drawLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer()
        else
            super.onBackPressed()
    }
}

