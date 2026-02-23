@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.cs501_hw3pr4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ResponsiveLayoutScreen()
                }
            }
        }
    }
}


data class NavItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val emoji: String
)


@Composable
fun ResponsiveLayoutScreen() {
    val navItems = remember {
        listOf(
            NavItem(0, "Overview",  "Summary & status",     "📊"),
            NavItem(1, "Projects",  "Active workstreams",   "🧰"),
            NavItem(2, "Tasks",     "To-do and backlog",    "✅"),
            NavItem(3, "Messages",  "Team communication",   "✉️"),
            NavItem(4, "Files",     "Docs and assets",      "📁"),
            NavItem(5, "Settings",  "Preferences",          "⚙️"),
        )
    }

    var selectedId by rememberSaveable { mutableIntStateOf(0) }
    val selected = navItems.first { it.id == selectedId }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val isWide = screenWidthDp >= 600

    if (isWide) {
        WideTwoPane(
            navItems = navItems,
            selectedId = selectedId,
            onSelect = { selectedId = it },
            selected = selected
        )
    } else {
        PhoneSingleColumn(
            navItems = navItems,
            selectedId = selectedId,
            onSelect = { selectedId = it },
            selected = selected
        )
    }
}

@Composable
private fun PhoneSingleColumn(
    navItems: List<NavItem>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
    selected: NavItem
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Responsive Layout") },
                actions = {
                    IconButton(onClick = {}) { Text("🔍") }
                    IconButton(onClick = {}) { Text("⋯") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) { Text("＋") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                text = "Navigation",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            // Requirement: LazyColumn / LazyVerticalGrid / verticalScroll (we use LazyColumn here)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(navItems) { item ->
                    val isSelected = item.id == selectedId

                    ListItem(
                        headlineContent = { Text(item.title) },
                        supportingContent = { Text(item.subtitle) },
                        leadingContent = { Text(item.emoji, style = MaterialTheme.typography.titleLarge) },
                        trailingContent = { if (isSelected) Text("›", style = MaterialTheme.typography.titleLarge) },
                        colors = ListItemDefaults.colors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(item.id) }
                    )
                    Divider()
                }
            }

            // Detail content below list (Box + Column mixed)
            PhoneDetailCard(item = selected, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PhoneDetailCard(
    item: NavItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            Text(item.subtitle, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(12.dp))

            // M3 components: AssistChip + SuggestionChip
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text("Quick Action") },
                    leadingIcon = { Text("⚡") }
                )
                SuggestionChip(
                    onClick = {},
                    label = { Text("Tip") },
                    icon = { Text("💡") }
                )
            }

            Spacer(Modifier.height(12.dp))

            // M3 component: LinearProgressIndicator
            LinearProgressIndicator(
                progress = { 0.62f },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // M3 component: Button
            Button(onClick = {}, modifier = Modifier.align(Alignment.End)) {
                Text("Open ↗")
            }
        }
    }
}

@Composable
private fun WideTwoPane(
    navItems: List<NavItem>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
    selected: NavItem
) {
    // Requirement: Row with two panes
    Row(modifier = Modifier.fillMaxSize()) {

        // Left pane: navigation/options list (Column)
        Surface(
            tonalElevation = 1.dp,
            modifier = Modifier
                .weight(0.35f)      // weight allocation
                .fillMaxHeight()    // fillMaxHeight
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                CenterAlignedTopAppBar(
                    title = { Text("Menu") },
                    navigationIcon = {
                        IconButton(onClick = {}) { Text("☰") }
                    }
                )

                Divider()

                // LazyColumn in wide left pane
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(navItems) { item ->
                        val isSelected = item.id == selectedId

                        ListItem(
                            headlineContent = { Text(item.title) },
                            supportingContent = { Text(item.subtitle) },
                            leadingContent = { Text(item.emoji, style = MaterialTheme.typography.titleLarge) },
                            trailingContent = { if (isSelected) Text("✓") },
                            colors = ListItemDefaults.colors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(item.id) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }

        // Right pane: detail content (Box + Column mixed)
        Box(
            modifier = Modifier
                .weight(0.65f)      // weight allocation
                .fillMaxHeight()    // fillMaxHeight
                .padding(16.dp)
        ) {
            DetailPane(item = selected)
        }
    }
}

@Composable
private fun DetailPane(item: NavItem) {
    val scrollState = rememberScrollState()

    // right pane uses Box + Column mixed (Box outside, Card+Column inside)
    Card(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState), // demonstrate verticalScroll
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📄", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(10.dp))
                Text(item.title, style = MaterialTheme.typography.headlineSmall)
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Wide mode keeps the navigation pane visible on the left while details stay on the right.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))

            // M3 component: ElevatedCard
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Settings Snapshot", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))

                    // M3 component: Switch
                    var enabled by rememberSaveable { mutableStateOf(true) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable notifications", modifier = Modifier.weight(1f))
                        Switch(checked = enabled, onCheckedChange = { enabled = it })
                    }

                    Spacer(Modifier.height(12.dp))

                    // M3 component: Slider
                    var slider by rememberSaveable { mutableFloatStateOf(0.35f) }
                    Text("Priority level: ${(slider * 10).toInt()}/10")
                    Slider(value = slider, onValueChange = { slider = it })
                }
            }

            Spacer(Modifier.height(16.dp))

            // M3 component: OutlinedTextField
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Notes") },
                placeholder = { Text("Write a quick note...") },
                leadingIcon = { Text("✍️") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // M3 components: Button + OutlinedButton
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = {}) { Text("Save 💾") }
                OutlinedButton(onClick = {}) { Text("Reset ↺") }
            }

            Spacer(Modifier.height(24.dp))
            Divider()
            Spacer(Modifier.height(12.dp))

            Text(
                text = List(25) { "• Detail line ${it + 1}" }.joinToString("\n"),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}