package com.djsunilsonu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    // Ye variable yaad rakhega ki aap kis page par hain
    var currentScreen by remember { mutableStateOf("Mixer") }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        
        // Upar ka Header aur Screens
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 40.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DJ SUNIL SONU",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(text = "Premium Mixing Studio", color = Color.Cyan, fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Yahan screen change hogi Navigation dabane par
            when (currentScreen) {
                "Mixer" -> MixerScreen()
                "Home" -> CenterTextScreen("Home Dashboard")
                "Library" -> CenterTextScreen("Music Library (Aapke Gaane Yahan Aayenge)")
                "Settings" -> CenterTextScreen("App Settings & Themes")
            }
        }

        // Niche ka Touchable Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(35.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(35.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem("Home", currentScreen) { currentScreen = "Home" }
                NavItem("Library", currentScreen) { currentScreen = "Library" }
                NavItem("Mixer", currentScreen) { currentScreen = "Mixer" }
                NavItem("Settings", currentScreen) { currentScreen = "Settings" }
            }
        }
    }
}

// Navigation Button ka Design
@Composable
fun NavItem(title: String, currentScreen: String, onClick: () -> Unit) {
    val isSelected = currentScreen == title
    val textColor = if (isSelected) Color.Cyan else Color.Gray
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

    Text(
        text = title, 
        color = textColor, 
        fontWeight = fontWeight,
        modifier = Modifier
            .clickable { onClick() }
            .padding(10.dp)
    )
}

// Dusri screens ka dummy design
@Composable
fun CenterTextScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = Color.White, fontSize = 20.sp)
    }
}

// Asli Mixer Screen (Deck A & B with Play Buttons)
@Composable
fun MixerScreen() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), 
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DeckUI(modifier = Modifier.weight(1f), title = "DECK A")
        Spacer(modifier = Modifier.width(12.dp))
        DeckUI(modifier = Modifier.weight(1f), title = "DECK B")
    }
}

// Deck ka Box Jisme Play Button aur Volume Slider hai
@Composable
fun DeckUI(modifier: Modifier, title: String) {
    var volume by remember { mutableStateOf(0.5f) }
    var isPlaying by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = Color.Cyan, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            
            Text("No Track Loaded", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(20.dp))
            
            // Circular Play/Pause Button
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) Color.Cyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f))
                    .border(1.dp, if (isPlaying) Color.Cyan else Color.Gray, CircleShape)
                    .clickable { isPlaying = !isPlaying },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow, 
                    contentDescription = "Play", 
                    tint = if (isPlaying) Color.Cyan else Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Volume Slider
            Text("VOL", color = Color.White, fontSize = 10.sp)
            Slider(
                value = volume,
                onValueChange = { volume = it },
                colors = SliderDefaults.colors(
                    thumbColor = Color.Cyan,
                    activeTrackColor = Color.Cyan,
                    inactiveTrackColor = Color.Gray
                )
            )
        }
    }
}
