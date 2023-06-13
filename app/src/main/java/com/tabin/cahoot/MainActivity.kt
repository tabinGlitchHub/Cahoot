package com.tabin.cahoot

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.tabin.cahoot.destinations.CommonPlayerScreenDestination
import com.tabin.cahoot.destinations.CommonWaitingScreenDestination
import com.tabin.cahoot.destinations.GuestInputScreenDestination
import com.tabin.cahoot.destinations.HomeScreenDestination
import com.tabin.cahoot.destinations.HostInputScreenDestination
import com.tabin.cahoot.ui.animations.CommonTransition
import com.tabin.cahoot.ui.components.PlainDialog
import com.tabin.cahoot.ui.components.SolidButton
import com.tabin.cahoot.ui.theme.*
import com.tabin.cahoot.utils.Constants
import com.tabin.cahoot.utils.UtilFuncs
import com.tabin.cahoot.viewmodels.GlobalStateModel
import com.tabin.cahoot.viewmodels.GlobalStateModel.guestViewModel
import com.tabin.cahoot.viewmodels.GlobalStateModel.hostViewModel
import com.tabin.cahoot.viewmodels.GlobalStateModel.navController
import kotlinx.coroutines.delay
import java.lang.Exception
import kotlin.concurrent.thread

lateinit var rootViewModel: GlobalStateModel
private const val TAG: String = "MainActivity"

private lateinit var dialogOpen: MutableState<Boolean>

class MainActivity : ComponentActivity() {
//    private lateinit var navController: NavController

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            rootViewModel = GlobalStateModel
            val activity = LocalContext.current as Activity
            LaunchedEffect(activity) {
                activity.window.setBackgroundDrawable(getDrawable(R.drawable.primary_red_gradient))
            }
            CahootTheme {
                fetchUserData(this)

                navController = rememberAnimatedNavController()

                val navHostEngine = rememberAnimatedNavHostEngine(
                    navHostContentAlignment = Alignment.TopCenter,
                    rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
                    defaultAnimationsForNestedNavGraph = mapOf(
                        NavGraphs.root to NestedNavGraphDefaultAnimations(
                            enterTransition = { slideInHorizontally() },
                            exitTransition = { fadeOut() }
                        ),
                    ))
                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    navController = navController as NavHostController,
                    engine = navHostEngine
                )
            }
        }
    }

    override fun onBackPressed() {
        try {
            val currentScreen = navController.currentBackStackEntry?.destination?.route
            if (!currentScreen.isNullOrEmpty() &&
                (currentScreen == Constants.SCREEN_GUEST_INPUT
                        || currentScreen == Constants.SCREEN_HOST_INPUT)
            ) {
                if (rootViewModel.userData.value.userRole == Constants.USER_GUEST) {
                    guestViewModel.sendTerminationMessage(
                        rootViewModel.userData.value
                    )
//                    guestViewModel.shouldConnect = true
                } else {
                    hostViewModel.sendTerminationMessage(
                        rootViewModel.userData.value
                    )
                }
                rootViewModel.updateUserDataModel(userRole = "")
                rootViewModel.connectedUsers.clear()
                super.onBackPressed()
            } else {
                super.onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}


fun fetchUserData(context: Context) {
    val userName = UtilFuncs.getSharedPrefsForKey(Constants.SHARED_PREF_KEY_USER_NAME, context)
    if (userName.isNotEmpty()) {
        rootViewModel.updateUserDataModel(userName = userName)
    }
}

@RootNavGraph(start = true)
@Destination(style = CommonTransition::class)
@Composable
fun HomeScreen(navigator: DestinationsNavigator? = null) {
    val nameInput = rememberSaveable {
        mutableStateOf(rootViewModel.userData.value.userName)
    }
    dialogOpen = rememberSaveable {
        mutableStateOf(false)
    }
    val currentContext = LocalContext.current
    // A surface container using the 'background' color from the theme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primary_red,
                        secondary_red
                    )
                )
            )
    ) {
        Column {
            Header()
            HomeContent(navigator, nameInput)
            if (dialogOpen.value) {
                PlainDialog("Enter a name your friends can identify you with.",
                    onDismiss = { dialogOpen.value = false }) {
                    TextField(
                        value = nameInput.value,
                        onValueChange = {
                            nameInput.value = it
                        },
                        label = { Text("Your name here", color = Color.DarkGray) },
                        textStyle = TextStyle(color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(modifier = Modifier.fillMaxWidth(), Arrangement.End) {
                        SolidButton(
                            text = "Save",
                            onClick = {
                                if (nameInput.value.isNotEmpty()) {
                                    dialogOpen.value = false
                                    rootViewModel.updateUserDataModel(userName = nameInput.value)
                                    UtilFuncs.setSharedPrefsForKey(
                                        Constants.SHARED_PREF_KEY_USER_NAME,
                                        nameInput.value,
                                        currentContext
                                    )
                                    if (rootViewModel.userData.value!!.userRole == Constants.USER_HOST) {
                                        navigator!!.navigate(HostInputScreenDestination)
                                    } else if (rootViewModel.userData.value!!.userRole == Constants.USER_GUEST) {
                                        navigator!!.navigate(GuestInputScreenDestination)
                                    }
                                    //else do nothing
                                }
                            },
                            textColor = Color.White,
                            buttonBackgroundColor = secondary_red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Header() {
    val localContext = LocalContext.current
    TopAppBar(elevation = 0.dp, backgroundColor = primary_red) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(id = R.string.app_name).uppercase(),
                fontWeight = FontWeight.SemiBold,
                color = primary_yellow, modifier = Modifier.padding(20.dp, 0.dp)
            )

            val user = remember {
                rootViewModel.userData
            }

            if (user.value.userName.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            color = Purple200,
                            shape = CircleShape
                        )
                        .clickable {
                            if (navController.currentDestination?.route.equals(HomeScreenDestination.route)) {
                                dialogOpen.value = true
                            } else {
                                Toast
                                    .makeText(
                                        localContext,
                                        "Cannot rename during a live-session!",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }
                        }
                ) {
                    Text(
                        text = user.value.userName[0].uppercase(),
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier
                            .align(
                                Alignment.Center
                            )
                    )
                }
            }

        }
    }
}

@Composable
fun HomeContent(
    navigator: DestinationsNavigator? = null,
    userName: MutableState<String>
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "What role are you playing today?",
            color = primary_yellow,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth(0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(100.dp))

        SolidButton(
            buttonModifier = Modifier.width(120.dp),
            text = "HOST",
            onClick = {
                rootViewModel.updateUserDataModel(userRole = Constants.USER_HOST)
                if (userName.value.isEmpty()) {
                    dialogOpen.value = true
                } else {
                    navigator!!.navigate(HostInputScreenDestination)
//                    navigator!!.navigate(CommonPlayerScreenDestination(uri = "/storage/emulated/0/Download/SampleVideo_1280x720_2mb.mp4"))
                }
            },
            textColor = null
        )

        Spacer(modifier = Modifier.height(20.dp))

        SolidButton(
            buttonModifier = Modifier.width(120.dp),
            text = "GUEST",
            onClick = {
                rootViewModel.updateUserDataModel(userRole = Constants.USER_GUEST)
                if (userName.value.isEmpty()) {
                    dialogOpen.value = true
                } else {
                    navigator!!.navigate(GuestInputScreenDestination)
                }
            },
            textColor = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CahootTheme {
        HomeScreen()
    }
}