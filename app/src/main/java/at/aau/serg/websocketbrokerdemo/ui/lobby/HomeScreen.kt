package at.aau.serg.websocketbrokerdemo.ui.lobby

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Welcome to HEXABRAWL",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        )

        TextButton(onClick = { /* Settings */},
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Settings")
        }

        Button(onClick = { navController.navigate("game") },
            modifier = Modifier.align(Alignment.Center))
        {
            Text("PLAY")
        }

        Button(onClick = { /* Exit App */ },
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 24.dp))
        {
            Text("Exit")
        }
    }
}