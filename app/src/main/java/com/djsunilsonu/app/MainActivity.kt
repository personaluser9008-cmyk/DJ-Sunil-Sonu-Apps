package com.djsunilsonu.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DjSunilSonuApp()
        }
    }
}

@Composable
fun DjSunilSonuApp() {
    var currentScreen by remember { mutableStateOf("Mixer") }
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 40.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("DJ SUNIL SONU", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Premium Mixing Studio", color = Color.Cyan, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(30.dp))
            
            if (currentScreen == "Mixer") {
                MixerScreen()
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("$currentScreen Screen (Coming Soon)", color = Color.White)
                }
            }
        }

        // Navigation Bar
        Box(
            modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp).fillMaxWidth().height(70.dp)
                .clip(RoundedCornerShape(35.dp)).background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(35.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                NavItem("Home", currentScreen) { currentScreen = "Home" }
                NavItem("Library", currentScreen) { currentScreen = "Library" }
                NavItem("Mixer", currentScreen) { currentScreen = "Mixer" }
                NavItem("Settings", currentScreen) { currentScreen = "Settings" }
            }
        }
    }
}

@Composable
fun NavItem(title: String, currentScreen: String, onClick: () -> Unit) {
    val isSelected = currentScreen == title
    Text(
        text = title, 
        color = if (isSelected) Color.Cyan else Color.Gray, 
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier.clickable { onClick() }.padding(10.dp)
    )
}

@Composable
fun MixerScreen() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        DeckUI(modifier = Modifier.weight(1f), title = "DECK A")
        Spacer(modifier = Modifier.width(10.dp))
        DeckUI(modifier = Modifier.weight(1f), title = "DECK B")
    }
}

@Composable
fun DeckUI(modifier: Modifier, title: String) {
    val context = LocalContext.current
    var volume by remember { mutableStateOf(0.5f) }
    var isPlaying by remember { mutableStateOf(false) }
    var trackName by remember { mutableStateOf("No Track") }

    // DJ Deck ka apna personal Music Player
    val player = remember { ExoPlayer.Builder(context).build() }

    // File Manager se gaana uthane ka tool
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            trackName = "Track Loaded ✅"
            val mediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            isPlaying = false // Load hone par gaana pause rahega
        }
    }

    // Play aur Pause ka logic
    LaunchedEffect(isPlaying) {
        if (isPlaying) player.play() else player.pause()
    }

    // Volume change ka logic
    LaunchedEffect(volume) {
        player.volume = volume
    }

    // App band hone par music band karne ke liye
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    // UI Design
    Box(
        modifier = modifier.clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp)).padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(title, color = Color.Cyan, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            
            // Load Track Button
            Button(
                onClick = { launcher.launch("audio/*") }, // Sirf audio files dikhayega
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.height(30.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
            ) {
                Text("LOAD MP3", color = Color.White, fontSize = 10.sp)
            }
            
            Spacer(modifier = Modifier.height(5.dp))
            Text(trackName, color = if (trackName == "No Track") Color.Gray else Color.Green, fontSize = 10.sp)
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Play/Pause Button
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape)
                    .background(if (isPlaying) Color.Cyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f))
                    .border(1.dp, if (isPlaying) Color.Cyan else Color.Gray, CircleShape)
                    .clickable { isPlaying = !isPlaying },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = if (isPlaying) Color.Cyan else Color.White)
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Volume Slider
            Text("VOL", color = Color.White, fontSize = 10.sp)
            Slider(
                value = volume,
                onValueChange = { volume = it },
                colors = SliderDefaults.colors(thumbColor = Color.Cyan, activeTrackColor = Color.Cyan)
            )
        }
    }
}
