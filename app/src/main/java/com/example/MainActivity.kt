package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
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
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AnalysisScreen
import com.example.ui.screens.CaseDetailScreen
import com.example.ui.screens.CaseHistoryScreen
import com.example.ui.screens.EscalationScreen
import com.example.ui.screens.EvidenceViewerScreen
import com.example.ui.screens.FlowchartViewerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KnowledgeVaultScreen
import com.example.ui.screens.MediaReviewScreen
import com.example.ui.screens.NewCaseScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VaultDetailScreen
import com.example.ui.theme.MedicaBlue
import com.example.ui.theme.MedicaTheme
import com.example.viewmodel.MainTab
import com.example.viewmodel.MedicaViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: MedicaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by viewModel.isDarkTheme.collectAsState()
            MedicaTheme(darkTheme = isDark) {
                MedicaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MedicaApp(viewModel: MedicaViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()

    // Handle Android system back button
    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.navigateBack()
    }

    val isTopLevelScreen = currentScreen is Screen.Home ||
            currentScreen is Screen.CasesList ||
            currentScreen is Screen.VaultList ||
            currentScreen is Screen.Settings

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // Adaptive Tablet / Landscape layout with NavigationRail
            Row(modifier = Modifier.fillMaxSize()) {
                if (isTopLevelScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("tablet_navigation_rail")
                    ) {
                        NavigationRailItem(
                            selected = currentTab == MainTab.HOME,
                            onClick = { viewModel.switchTab(MainTab.HOME) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MedicaBlue,
                                indicatorColor = MedicaBlue.copy(alpha = 0.2f)
                            )
                        )

                        NavigationRailItem(
                            selected = currentTab == MainTab.CASES,
                            onClick = { viewModel.switchTab(MainTab.CASES) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.CASES) Icons.Filled.Folder else Icons.Outlined.Folder,
                                    contentDescription = "Cases"
                                )
                            },
                            label = { Text("Cases", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MedicaBlue,
                                indicatorColor = MedicaBlue.copy(alpha = 0.2f)
                            )
                        )

                        NavigationRailItem(
                            selected = currentTab == MainTab.VAULT,
                            onClick = { viewModel.switchTab(MainTab.VAULT) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.VAULT) Icons.Filled.MedicalServices else Icons.Outlined.MedicalServices,
                                    contentDescription = "Vault"
                                )
                            },
                            label = { Text("Vault", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MedicaBlue,
                                indicatorColor = MedicaBlue.copy(alpha = 0.2f)
                            )
                        )

                        NavigationRailItem(
                            selected = currentTab == MainTab.SETTINGS,
                            onClick = { viewModel.switchTab(MainTab.SETTINGS) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = { Text("Settings", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MedicaBlue,
                                indicatorColor = MedicaBlue.copy(alpha = 0.2f)
                            )
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    ScreenContent(screen = currentScreen, viewModel = viewModel)
                }
            }
        } else {
            // Standard Mobile Phone layout with Bottom NavigationBar
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (isTopLevelScreen) {
                        NavigationBar(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .testTag("bottom_nav_bar"),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 3.dp
                        ) {
                            NavigationBarItem(
                                selected = currentTab == MainTab.HOME,
                                onClick = { viewModel.switchTab(MainTab.HOME) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Home"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "HOME",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MedicaBlue,
                                    selectedTextColor = MedicaBlue,
                                    indicatorColor = MedicaBlue.copy(alpha = 0.2f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            NavigationBarItem(
                                selected = currentTab == MainTab.CASES,
                                onClick = { viewModel.switchTab(MainTab.CASES) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == MainTab.CASES) Icons.Filled.Folder else Icons.Outlined.Folder,
                                        contentDescription = "Cases"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "CASES",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MedicaBlue,
                                    selectedTextColor = MedicaBlue,
                                    indicatorColor = MedicaBlue.copy(alpha = 0.2f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            NavigationBarItem(
                                selected = currentTab == MainTab.VAULT,
                                onClick = { viewModel.switchTab(MainTab.VAULT) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == MainTab.VAULT) Icons.Filled.MedicalServices else Icons.Outlined.MedicalServices,
                                        contentDescription = "Vault"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "VAULT",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MedicaBlue,
                                    selectedTextColor = MedicaBlue,
                                    indicatorColor = MedicaBlue.copy(alpha = 0.2f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            NavigationBarItem(
                                selected = currentTab == MainTab.SETTINGS,
                                onClick = { viewModel.switchTab(MainTab.SETTINGS) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == MainTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                        contentDescription = "Settings"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "SETTINGS",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MedicaBlue,
                                    selectedTextColor = MedicaBlue,
                                    indicatorColor = MedicaBlue.copy(alpha = 0.2f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ScreenContent(screen = currentScreen, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ScreenContent(
    screen: Screen,
    viewModel: MedicaViewModel
) {
    when (screen) {
        is Screen.Home -> HomeScreen(viewModel = viewModel)
        is Screen.CasesList -> CaseHistoryScreen(viewModel = viewModel)
        is Screen.VaultList -> KnowledgeVaultScreen(viewModel = viewModel)
        is Screen.Settings -> SettingsScreen(viewModel = viewModel)
        is Screen.NewCase -> NewCaseScreen(viewModel = viewModel)
        is Screen.MediaReview -> MediaReviewScreen(viewModel = viewModel)
        is Screen.Analysis -> AnalysisScreen(viewModel = viewModel)
        is Screen.Result -> ResultScreen(caseId = screen.caseId, viewModel = viewModel)
        is Screen.EvidenceViewer -> EvidenceViewerScreen(
            assetId = screen.assetId,
            fromCaseId = screen.fromCaseId,
            viewModel = viewModel
        )
        is Screen.FlowchartViewer -> FlowchartViewerScreen(
            assetId = screen.assetId,
            viewModel = viewModel
        )
        is Screen.VaultDetail -> VaultDetailScreen(
            assetId = screen.assetId,
            viewModel = viewModel
        )
        is Screen.CaseDetail -> CaseDetailScreen(
            caseId = screen.caseId,
            viewModel = viewModel
        )
        is Screen.Escalation -> EscalationScreen(
            caseId = screen.caseId,
            viewModel = viewModel
        )
    }
}
