package com.example.activitytrackerai

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen() {
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(false) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                    (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
    }

    // Έλεγχος permission στην εκκίνηση
    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        hasLocationPermission = (fine == PackageManager.PERMISSION_GRANTED) || (coarse == PackageManager.PERMISSION_GRANTED)
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Λήψη τοποθεσίας αν έχεις permission
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                    }
                }
            } catch (e: SecurityException) {
                // Αν για κάποιο λόγο δεν έχεις permission
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!hasLocationPermission) {
            // Ενημέρωση χρήστη ότι χρειάζεται άδεια
            Text(
                text = "Απαιτείται άδεια τοποθεσίας για να εμφανιστεί ο χάρτης.",
                modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
            )
        } else if (userLocation != null) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(userLocation!!, 15f)
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(userLocation!!),
                    title = "Η θέση μου"
                )
            }
        } else {
            // Περιμένουμε να φορτώσει η τοποθεσία
            CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
        }
    }
}