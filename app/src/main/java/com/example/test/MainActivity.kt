package com.example.test

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "notes") // Now defined like this

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp(this) // Pass the activity context to MyApp
        }
    }
}

@Composable
fun MyApp(context: Context) { // Receive the context here
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val coroutineScope = rememberCoroutineScope()

    // Load the saved note on composition
    LaunchedEffect(Unit) {
        text = loadNote(context) // Pass the context to loadNote
    }

    // Save the note whenever the text changes
    LaunchedEffect(text) {
        saveNote(context, text) // Pass the context to saveNote
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxSize()
        )
    }
}

suspend fun saveNote(context: Context, note: TextFieldValue) {
    val dataStoreKey = stringPreferencesKey("notes")
    context.dataStore.edit { settings ->
        settings[dataStoreKey] = note.text
    }
}

suspend fun loadNote(context: Context): TextFieldValue {
    val dataStoreKey = stringPreferencesKey("notes")
    val preferences = context.dataStore.data.first()
    return TextFieldValue(preferences[dataStoreKey] ?: "")
}