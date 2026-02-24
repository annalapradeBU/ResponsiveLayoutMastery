package com.example.responsivelayoutmastery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.responsivelayoutmastery.ui.theme.ResponsiveLayoutMasteryTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch


val WorkoutDarkPalette = darkColorScheme(
    primary = Color(0xFFCCFF00),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF2E3500),
    onPrimaryContainer = Color(0xFFCCFF00),
    secondary = Color(0xFFB0BEC5),
    surface = Color(0xFF121412),
    background = Color(0xFF0A0B0A),
    outline = Color(0xFF3F443F)
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = WorkoutDarkPalette) {
                WorkoutResponsiveApp()
            }
        }
    }
}

@Composable
fun WorkoutResponsiveApp() {
    // REQUIREMENT: BoxWithConstraints for width-based logic
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // 600dp is the industry standard for adaptive layouts (according to Gemini)
        val isWideScreen = maxWidth > 600.dp

        if (isWideScreen) {
            TabletWorkoutLayout()
        } else {
            PhoneWorkoutLayout()
        }
    }
}

// --- REQUIREMENT: TABLET MODE: DUAL PANE ---
@Composable
fun TabletWorkoutLayout(){
    // REQUIREMENT: On wide screens (tablet/landscape): switch to a Row with two panes
    Row(modifier = Modifier.fillMaxSize()) {
        // REQUIREMENT: M3 component - NavigationRail
        NavigationRail(
            // REQUIREMENT: fillMaxHeight to span the vertical edge
            modifier = Modifier.fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            // workout section
            NavigationRailItem(
                selected = true,
                onClick = {},
                icon = { Icon(Icons.Default.FitnessCenter, "Workouts") },
                label = { Text("Workouts") }
            )
            // workout history section
            NavigationRailItem(
                selected = false,
                onClick = {},
                icon = { Icon(Icons.Default.List, contentDescription = "History") },
                label = { Text("History") }
            )
        }

        // LEFT PANE: Muscle Groups List (Column)
        // REQUIREMENT: Standard Column for vertical stacking
        Column(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text("Muscle Groups", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))

            val groups = listOf("Chest & Triceps", "Back & Biceps", "Legs")

            groups.forEach { group ->
                // REQUIREMENT: M3 component - ListItem for structured lists
                ListItem(
                    headlineContent = { Text(group) },
                    supportingContent = { Text("5 Exercises") },
                    leadingContent = { Icon(Icons.Default.Check, null) }
                )
            }
        }

        // RIGHT PANE: Exercise Details (Box + Column mixed)
        // REQUIREMENT: weight(1f) to allocate remaining width
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            ExerciseDetailContent()
        }
    }
}


// --- REQUIREMENT: PHONE MODE: SINGLE COLUMN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneWorkoutLayout() {
    // state to handle opening/closing the drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // https://developer.android.com/develop/ui/compose/components/drawer
    // as suggested by gemini, helos with the sliding animation and "scrim"
    // REQUIREMENT: M3 Component - ModalNavigationDrawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet {
                // Here is where we put the "Missing" Muscle Groups
                Column(modifier = Modifier.padding(16.dp)) {
                    // --- SECTION 1: MAIN NAVIGATION ---
                    Text("Navigation", style = MaterialTheme.typography.labelMedium)

                    NavigationDrawerItem(
                        label = { Text("Workouts") },
                        selected = true, // Set based on state if building a real app
                        onClick = { scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Default.FitnessCenter, null) }
                    )
                    NavigationDrawerItem(
                        label = { Text("History") },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Default.List, null) }
                    )

                    // REQUIREMENT: M3 component - ListItem for structured lists
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // --- SECTION 2: MUSCLE GROUPS (DRAWER SUB-CONTENT) ---
                    Text("Muscle Groups", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    val groups = listOf("Chest & Triceps", "Back & Biceps", "Legs")
                    groups.forEach { group ->
                        NavigationDrawerItem(
                            label = { Text(group) },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.Default.Check, null) }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                // REQUIREMENT: M3 component - TopAppBar
                TopAppBar(
                    title = { Text("FitTracker") },
                    // open the drawer when the "hamburger" menu icon is clicked
                    navigationIcon = {
                        // as informed by Gemini and ch 37
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            // REQUIREMENT: M3 component - floatingActionButton
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                ExerciseDetailContent()
            }
        }
    }
}

// --- REUSABLE DETAIL CONTENT --- usable between both screen types :]
@Composable
fun ExerciseDetailContent() {
    // REQUIREMENT: verticalScroll for content exceeding screen height
    val scrollState = rememberScrollState()

    // "daily focus" content
    // Demonstrate at least one of: verticalScroll, LazyColumn, or LazyVerticalGrid (your choice)
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        Text("Daily Focus: Chest", style = MaterialTheme.typography.titleLarge)

        // REQUIREMENT: M3 component - LinearProgressIndicator
        Text("Muscle Fatigue", style = MaterialTheme.typography.labelMedium)
        // ch 25.10
        LinearProgressIndicator(
            progress = { 0.45f },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // // REQUIREMENT: M3 component - divider
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)

        // REQUIREMENT: M3 component - Card for grouping content
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Bench Press", style = MaterialTheme.typography.headlineSmall)
                Text("4 sets x 6 reps • 105 lbs")

                // REQUIREMENT: M3 component - OutlinedButton
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Log Set")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Cable Tricep Pushdown", style = MaterialTheme.typography.headlineSmall)
                Text("4 sets x 6 reps • 60 lbs")

                // REQUIREMENT: M3 component - OutlinedButton
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Log Set")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Seated Dumbbell Shoulder Press", style = MaterialTheme.typography.headlineSmall)
                Text("4 sets x 6 reps • 35 lbs")

                // REQUIREMENT: M3 component - OutlinedButton
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Log Set")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Lateral Raises", style = MaterialTheme.typography.headlineSmall)
                Text("4 sets x 12 reps • 10 lbs")

                // REQUIREMENT: M3 component - OutlinedButton
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Log Set")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Assisted Dips", style = MaterialTheme.typography.headlineSmall)
                Text("4 sets x 6 reps • 40 lbs")

                // REQUIREMENT: M3 component - OutlinedButton
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Log Set")
                }
            }
        }
    }
}

