package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.CasesScreen
import com.example.ui.screens.GeneralSettingsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MedicalVaultScreen
import com.example.ui.screens.NewCaseScreen
import com.example.ui.theme.MedicaAccentBlue
import com.example.ui.theme.MedicaBorderDark
import com.example.ui.theme.MedicaCardDark
import com.example.ui.theme.MedicaTextMuted
import com.example.ui.theme.MedicaTheme
import com.example.viewmodel.MainTab
import com.example.viewmodel.MedicaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MedicaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by viewModel.isDarkTheme.collectAsState()
            MedicaTheme(darkTheme = isDark) {
                MedicaMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MedicaMainApp(viewModel: MedicaViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showNewCase by viewModel.showNewCaseScreen.collectAsState()

    // Handle back button
    BackHandler(enabled = showNewCase || currentTab != MainTab.HOME) {
        if (showNewCase) {
            viewModel.closeNewCaseScreen()
        } else {
            viewModel.switchTab(MainTab.HOME)
        }
    }

    if (showNewCase) {
        NewCaseScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .border(width = 0.8.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        .testTag("medica_bottom_navigation"),
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp
                ) {
                    // 1. Home Tab
                    NavigationBarItem(
                        selected = currentTab == MainTab.HOME,
                        onClick = { viewModel.switchTab(MainTab.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Home",
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == MainTab.HOME) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MedicaAccentBlue,
                            selectedTextColor = MedicaAccentBlue,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    // 2. Cases Tab
                    NavigationBarItem(
                        selected = currentTab == MainTab.CASES,
                        onClick = { viewModel.switchTab(MainTab.CASES) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.CASES) Icons.Filled.MedicalServices else Icons.Outlined.MedicalServices,
                                contentDescription = "Cases",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Cases",
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == MainTab.CASES) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MedicaAccentBlue,
                            selectedTextColor = MedicaAccentBlue,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_cases")
                    )

                    // 3. Vault Tab
                    NavigationBarItem(
                        selected = currentTab == MainTab.VAULT,
                        onClick = { viewModel.switchTab(MainTab.VAULT) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.VAULT) Icons.Filled.Folder else Icons.Outlined.Folder,
                                contentDescription = "Vault",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Vault",
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == MainTab.VAULT) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MedicaAccentBlue,
                            selectedTextColor = MedicaAccentBlue,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_vault")
                    )

                    // 4. Settings Tab
                    NavigationBarItem(
                        selected = currentTab == MainTab.SETTINGS,
                        onClick = { viewModel.switchTab(MainTab.SETTINGS) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == MainTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Settings",
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == MainTab.SETTINGS) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MedicaAccentBlue,
                            selectedTextColor = MedicaAccentBlue,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_settings")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    MainTab.HOME -> HomeScreen(viewModel = viewModel)
                    MainTab.CASES -> CasesScreen(viewModel = viewModel)
                    MainTab.VAULT -> MedicalVaultScreen(viewModel = viewModel)
                    MainTab.SETTINGS -> GeneralSettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
