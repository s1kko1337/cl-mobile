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
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Главная Activity приложения электронной коммерции.
 *
 * Инициализирует навигацию и определяет стартовый экран на основе
 * статуса аутентификации пользователя и его роли (admin/customer).
 *
 * @property authRepository Репозиторий для проверки статуса аутентификации пользователя
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authRepository: AuthRepository

    /**
     * Создает и настраивает UI приложения при запуске Activity.
     *
     * Определяет начальный экран навигации в зависимости от:
     * - Статуса входа пользователя (залогинен/не залогинен)
     * - Роли пользователя (admin/customer)
     *
     * @param savedInstanceState Сохраненное состояние Activity
     */
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
