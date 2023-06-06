package uz.gita.contactappcompose.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import uz.gita.contactappcompose.R
import uz.gita.contactappcompose.data.common.ContactData
import uz.gita.contactappcompose.ui.component.ContactItem
import uz.gita.contactappcompose.ui.theme.ContactAppComposeTheme
import uz.gita.contactappcompose.utils.logger

class HomeScreen : AndroidScreen() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: HomeContract.ViewModel = getViewModel<HomeViewModelImpl>()
        val uiState = viewModel.uiState.collectAsState()
        ContactAppComposeTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    topBar = { TopBar() }
                ) {
                    HomeContactScreenContent(
                        uiState = uiState,
                        viewModel::onEventDispatcher,
                        Modifier.padding(it)
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Cyan)
            .height(56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Contacts", fontSize = 24.sp, color = Color.Black)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContactScreenContent(
    uiState: State<HomeContract.UiState>,
    onEventDispatcher: (intent: HomeContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val showDialog = remember { mutableStateOf(false) }
    val data = remember { mutableStateOf(ContactData(-1, "", "", "")) }

    if (showDialog.value) {
        AlertDialogComponent(
            onEventDispatcher = onEventDispatcher,
            data = data.value,
            true,
            showDialog
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
            content = {
                items(uiState.value.contacts) {
                    Spacer(modifier = Modifier.size(8.dp))

                    ContactItem(
                        fname = it.firstName,
                        lname = it.lastName,
                        phone = it.phone,
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                logger("Item click")
                                onEventDispatcher(HomeContract.Intent.OpenEditContact(it))
                            },
                            onLongClick = {
                                data.value = it
                                showDialog.value = true
                            }
                        )
                    )
                }
            })

        FloatingActionButton(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.BottomEnd),
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.Blue,
            onClick = {
                logger("Action button")
                onEventDispatcher(HomeContract.Intent.OpenAddContact)
            }) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
    }
}

@Composable
fun AlertDialogComponent(
    onEventDispatcher: (intent: HomeContract.Intent) -> Unit,
    data: ContactData,
    show: Boolean,
    showDialog: MutableState<Boolean>
) {
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(show) }

    if (openDialog.value) {
        AlertDialog(
            properties = DialogProperties(dismissOnClickOutside = false),
            onDismissRequest = { openDialog.value = false },
            title = { Text(text = "Warning", color = Color.White) },
            text = { Text("Do you want to delete ?", color = Color.White) },

            confirmButton = {
                TextButton(
                    onClick = {
                        onEventDispatcher(HomeContract.Intent.Delete(data))
                        openDialog.value = false
                        showDialog.value = false
                        Toast.makeText(context, "Item deleted", Toast.LENGTH_LONG).show()
                    }
                ) { Text("Confirm", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    showDialog.value = false
                })
                { Text("Dismiss", color = Color.White) }
            },
            containerColor = colorResource(id = R.color.teal_200),
            textContentColor = Color.White
        )
    }
}