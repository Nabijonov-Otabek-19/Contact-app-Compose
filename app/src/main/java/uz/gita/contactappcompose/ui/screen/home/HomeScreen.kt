package uz.gita.contactappcompose.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import uz.gita.contactappcompose.R
import uz.gita.contactappcompose.data.common.ContactData
import uz.gita.contactappcompose.ui.component.ContactItem
import uz.gita.contactappcompose.ui.screen.addcontact.AddScreen
import uz.gita.contactappcompose.ui.theme.ContactAppComposeTheme
import uz.gita.contactappcompose.utils.logger

class HomeScreen : AndroidScreen() {
    @Composable
    override fun Content() {
        val viewModel: HomeViewContract.ViewModel = getViewModel<HomeViewModelImpl>()
        val uiState = viewModel.uiState.collectAsState().value
        ContactAppComposeTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                HomeContactScreenContent(uiState = uiState, viewModel::onEventDispatcher)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContactScreenContent(
    uiState: HomeViewContract.UiState,
    onEventDispatcher: (intent: HomeViewContract.Intent) -> Unit
) {
    val navigator = LocalNavigator.currentOrThrow

    /* if (uiState.addContactState) {
         navigator.push(AddScreen(null))
         onEventDispatcher(HomeViewContract.Intent.CloseAddContact)
     }
     if (uiState.editContactState) {
         navigator.push(AddScreen(uiState.updateData))
         onEventDispatcher(HomeViewContract.Intent.CloseAddContact)
     }*/

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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
            content = {
                items(uiState.contacts) {
                    Spacer(modifier = Modifier.size(8.dp))

                    ContactItem(
                        fname = it.firstName,
                        lname = it.lastName,
                        phone = it.phone,
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                logger("Item click")
                                onEventDispatcher(HomeViewContract.Intent.OpenEditContact(it))
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
                onEventDispatcher(HomeViewContract.Intent.OpenAddContact) }) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
    }
}

@Composable
fun AlertDialogComponent(
    onEventDispatcher: (intent: HomeViewContract.Intent) -> Unit,
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
                        onEventDispatcher(HomeViewContract.Intent.Delete(data))
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