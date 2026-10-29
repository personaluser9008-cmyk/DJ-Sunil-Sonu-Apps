package com.djsunilsonu.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

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
            modifier = Modifier.fillMaxSize().padding(top = 40.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("DJ SUNIL SONU", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Ultimate Mixing Studio", color = Color.Cyan, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(15.dp))
            
            if (currentScreen == "Mixer") {
                MixerScreen()
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("$currentScreen Screen (Coming Soon)", color = Color.White)
                }
            }
        }

        // Floating Liquid Glass Navigation Bar
        Box(
            modifier = Modifier.align(Alignment.BottomCenter).padding(10.dp).fillMaxWidth().height(60.dp)
                .clip(RoundedCornerShape(30.dp)).background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(30.dp)),
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
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    )
}

@Composable
fun MixerScreen() {
    var crossfader by remember { mutableStateOf(0.5f) }
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).verticalScroll(scrollState)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val volumeA = if (crossfader < 0.5f) 1f else 1f - ((crossfader - 0.5f) * 2f)
            DeckUI(modifier = Modifier.weight(1f), title = "DECK A", crossfadeVol = volumeA)
            
            Spacer(modifier = Modifier.width(8.dp))
            
            val volumeB = if (crossfader > 0.5f) 1f else (crossfader * 2f)
            DeckUI(modifier = Modifier.weight(1f), title = "DECK B", crossfadeVol = volumeB)
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Crossfader UI
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.05f)).border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp)).padding(15.dp)) {
            Text("CROSSFADER", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
            Slider(
                value = crossfader,
                onValueChange = { crossfader = it },
                colors = SliderDefaults.colors(thumbColor = Color.Cyan, activeTrackColor = Color.Cyan, inactiveTrackColor = Color.Gray)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("A", color = Color.Cyan, fontWeight = FontWeight.Bold)
                Text("B", color = Color.Cyan, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AudioVisualizer(isPlaying: Boolean) {
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom, modifier = Modifier.height(30.dp).fillMaxWidth()) {
        (1..8).forEach { _ ->
            var barHeight by remember { mutableStateOf(5f) }
            LaunchedEffect(isPlaying) {
                while(isPlaying) {
                    barHeight = (10..30).random().toFloat()
                    delay((100..250).random().toLong())
                }
                if (!isPlaying) barHeight = 5f 
            }
            Box(modifier = Modifier.padding(horizontal = 2.dp).width(5.dp).height(barHeight.dp).background(Color.Cyan, RoundedCornerShape(2.dp)))
        }
    }
}

@Composable
fun DeckUI(modifier: Modifier, title: String, crossfadeVol: Float) {
    val context = LocalContext.current
    var baseVolume by remember { mutableStateOf(0.8f) }
    var speed by remember { mutableStateOf(1.0f) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLooping by remember { mutableStateOf(false) } 
    var trackName by remember { mutableStateOf("No Track") }
    
    // Song Progress Tracking
    var currentPosition by remember { mutableStateOf(0L) }
    var trackDuration by remember { mutableStateOf(1L) }

    val player = remember { ExoPlayer.Builder(context).build() }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            trackName = "Track Loaded ✅"
            val mediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            isPlaying = false 
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            player.play()
            while (isPlaying) {
                currentPosition = player.currentPosition
                trackDuration = if (player.duration > 0) player.duration else 1L
                delay(500L) // Update progress bar every 0.5 seconds
            }
        } else {
            player.pause()
        }
    }
    
    LaunchedEffect(isLooping) {
        player.repeatMode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
    }

    LaunchedEffect(baseVolume, crossfadeVol) {
        player.volume = baseVolume * crossfadeVol
    }

    LaunchedEffect(speed) {
        player.playbackParameters = PlaybackParameters(speed)
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Box(
        modifier = modifier.clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp)).padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(title, color = Color.Cyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { launcher.launch("audio/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.height(30.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
            ) {
                Text("LOAD MP3", color = Color.White, fontSize = 9.sp)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(trackName, color = if (trackName == "No Track") Color.Gray else Color.Green, fontSize = 9.sp)
            
            Spacer(modifier = Modifier.height(5.dp))
            AudioVisualizer(isPlaying = isPlaying)
            
            // Track Progress Bar (SEEKBAR)
            Slider(
                value = if (trackDuration > 0) currentPosition.toFloat() / trackDuration.toFloat() else 0f,
                onValueChange = { percent ->
                    val seekTo = (percent * trackDuration).toLong()
                    player.seekTo(seekTo)
                    currentPosition = seekTo
                },
                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White, inactiveTrackColor = Color.Gray),
                modifier = Modifier.height(20.dp)
            )
            
            Spacer(modifier = Modifier.height(5.dp))
            
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                        .background(if (isPlaying) Color.Cyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f))
                        .border(1.dp, if (isPlaying) Color.Cyan else Color.Gray, CircleShape)
                        .clickable { isPlaying = !isPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = if (isPlaying) Color.Cyan else Color.White)
                }
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Box(
                    modifier = Modifier.size(30.dp).clip(CircleShape)
                        .background(if (isLooping) Color.Yellow.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f))
                        .border(1.dp, if (isLooping) Color.Yellow else Color.Gray, CircleShape)
                        .clickable { isLooping = !isLooping },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Loop", tint = if (isLooping) Color.Yellow else Color.White, modifier = Modifier.size(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(5.dp))

            Text("SPEED: ${speed.toString().take(3)}x", color = Color.Yellow, fontSize = 9.sp)
            Slider(value = speed, onValueChange = { speed = it }, valueRange = 0.5f..1.5f,
                colors = SliderDefaults.colors(thumbColor = Color.Yellow, activeTrackColor = Color.Yellow, inactiveTrackColor = Color.Gray), modifier = Modifier.height(25.dp))
            
            Text("VOL", color = Color.White, fontSize = 9.sp)
            Slider(value = baseVolume, onValueChange = { baseVolume = it },
                colors = SliderDefaults.colors(thumbColor = Color.Cyan, activeTrackColor = Color.Cyan), modifier = Modifier.height(25.dp))
        }
    }
}
