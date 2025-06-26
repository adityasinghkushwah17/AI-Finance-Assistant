package com.adityacodes.financebuddy.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.adityacodes.financebuddy.BottomNavBar
import com.adityacodes.financebuddy.data.ChatMessage
import com.adityacodes.financebuddy.viewmodels.AuthViewModel
import com.adityacodes.financebuddy.viewmodels.ChatViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel,
    onNavigateToHistory: () -> Unit,
    navController: NavHostController
) {
    val messages by chatViewModel.messages
    val isLoading by chatViewModel.isLoading
    val errorMessage by chatViewModel.errorMessage
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFE7F4F7))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You AI Finance Expert ",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontSize = 22.sp
                )
            }

            LazyColumn(
                state = listState,
                reverseLayout = false,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI is thinking...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Input Row
            InputRow(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClicked = {
                    chatViewModel.sendMessage(messageText)
                    messageText = ""
                },
                isLoading = isLoading
            )
        }
    }

    errorMessage?.let { error ->
        LaunchedEffect(error) {
            chatViewModel.clearError()
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (message.isUser) Color(0xFF41B8C9) else Color.White,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = if (message.isUser) 24.dp else 0.dp,
                        bottomEnd = if (message.isUser) 0.dp else 24.dp
                    )
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.message,
                color = if (message.isUser) Color.White else Color.Black
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputRow(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClicked: () -> Unit,
    isLoading: Boolean
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .heightIn(min = 46.dp),

            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = messageText,
                onValueChange = onMessageChange,
                maxLines = 3,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (messageText.isEmpty()) {
                        Text(
                            "Send message...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray

                            ),
                            modifier= Modifier.padding()
                        )
                    }
                    innerTextField()
                }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                if (messageText.isNotBlank()) {
                    onSendClicked()
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            },
            enabled = messageText.isNotBlank() && !isLoading,
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF41B8C9), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }


}


