package com.example.ecommerceapp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.ecommerceapp.data.repository.AuthRepository
import com.example.ecommerceapp.navigation.NavGraph
import com.example.ecommerceapp.navigation.Screen
import com.example.ecommerceapp.ui.theme.ECommerceAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECommerceAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val startDestination = remember {
                        runBlocking {
                            val isLoggedIn = authRepository.isLoggedIn.first()
                            val role = authRepository.userRole.first()

                            when {
                                !isLoggedIn -> Screen.Login.route
                                role == "admin" -> Screen.AdminDashboard.route
                                else -> Screen.Home.route
                            }
                        }
                    }

                    NavGraph(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
