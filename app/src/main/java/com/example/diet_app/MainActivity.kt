package com.example.diet_app

import DietPlansScreen
import GenerateMealPlanWithInputsScreen
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diet_app.model.GlobalData
import com.example.diet_app.model.Screen
import com.example.diet_app.model.getFoodIndexFromVariant
import com.example.diet_app.model.getFoodVariantFromIndex
import com.example.diet_app.model.getGoalInt
import com.example.diet_app.model.getSexInt
import com.example.diet_app.screenActivities.*
import com.example.diet_app.screenActivities.components.navigateAndClearStack
import com.example.diet_app.viewModel.DietDayViewModel
import com.example.diet_app.viewModel.DietViewModel
import com.example.diet_app.viewModel.FoodViewModel
import com.example.diet_app.viewModel.UserViewModel
import com.example.diet_app.viewModel.parseUserPlatesResponse

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Forzar íconos oscuros en la barra de estado
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            var foodViewModel = FoodViewModel()
            var userViewModel = UserViewModel()
            var dietViewModel = DietViewModel()

            DietApp(LocalContext.current, userViewModel, foodViewModel, dietViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DietApp(applicationContext: Context, userViewModel: UserViewModel, newFood: FoodViewModel, dietViewModel: DietViewModel) {
    // Usamos NavController para manejar la navegación
    val navController = rememberNavController()
    var dietJson by remember { mutableStateOf<String?>(null) }
    var dietViewModels by remember { mutableStateOf<MutableList<DietViewModel>>(mutableListOf()) }
    var navigationVariable by remember { mutableStateOf(false) }

    // Configuración de la navegación entre pantallas
    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(route = Screen.Home.route
        ) {HomePageFrame(navController, userViewModel)}

        composable(route = Screen.Goal.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            GoalSelectionScreen(
                onSkip = { navController.navigate("welcome") },
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    Log.d("CurrentUser", userViewModel.getUser().toString())
                    userViewModel.updateUser(goal = it)
                    GlobalData.login(userViewModel)
                    createUser(
                        email = userViewModel.getUser().email,
                        password = userViewModel.getUser().password,
                        physicalActivity = 4,
                        sex = getSexInt(userViewModel.getUser().sex),
                        birthday = userViewModel.getUser().age,
                        height = userViewModel.getUser().height,
                        weight = userViewModel.getUser().currentWeight.toInt(),
                        context = applicationContext,
                        goal = getGoalInt(userViewModel.getUser().goal),
                        onResult = {
                            authenticateUser(
                                email = userViewModel.getUser().email,
                                password = userViewModel.getUser().password,
                                context = applicationContext,
                                userViewModel = userViewModel
                            ) { result ->
                                when {
                                    result.isSuccess -> {
                                        Toast.makeText(
                                            applicationContext,
                                            "Usuario autenticado\n" +
                                                    "Bienvenido ${userViewModel.getUser().email}!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    result.isFailure -> {
                                        Toast.makeText(
                                            applicationContext,
                                            "Fallo de autenticación",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                    )

                    navController.navigate(Screen.Home.route)
                },
            )
        }

        composable(route = Screen.Sex.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            SexSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSkip = { navController.navigate("welcome") },
                onNext = {
                    userViewModel.updateUser(sex = it)
                    navController.navigate(Screen.Age.route)
                    printUserInfo(userViewModel)
                }
            )
        }

        composable(route = Screen.Age.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            AgeSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSkip = { navController.navigate("welcome") },
                onNext = {
                    userViewModel.updateUser(age = it)
                    navController.navigate(Screen.Height.route)
                    printUserInfo(userViewModel)
                } // O la siguiente pantalla que corresponda
            )
        }

        composable(route = Screen.Height.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            HeightSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSkip = { navController.navigate("welcome") },
                onNext = {
                    // userViewModel.updateUser(height = it)
                    navController.navigate(Screen.CurrentWeight.route)
                    userViewModel.updateUser(height = it)
                    printUserInfo(userViewModel)
                } // O la siguiente pantalla que corresponda
            )
        }

        composable(route = Screen.CurrentWeight.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            CurrentWeightSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSkip = { navController.navigate("welcome") },
                onNext = {
                    userViewModel.updateUser(currentWeight = it)
                    navController.navigate(Screen.Goal.route)
                    printUserInfo(userViewModel)
                } // O la siguiente pantalla que corresponda
            )
        }

        composable(route = Screen.Meals.route
        ) {

            var showDiets by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                getUserDietPlansCompletePro(userViewModel.getUser().id, applicationContext) { jsonResponse ->
                    dietJson = jsonResponse
                    // Solo actualizamos los ViewModels cuando tengamos el JSON válido
                    if (jsonResponse.isNotEmpty()) {
                        val response = deserializeDietInformation(jsonResponse)
                        dietViewModels = response.toDietViewModels() // Ahora recibe una lista
                        showDiets = true
                    }
                }
            }

            // Muestra la pantalla solo cuando tengamos datos
            DietPlansScreen(
                navController = navController,
                diets = dietViewModels // Pasamos la lista completa
            )

        }

        composable(route = Screen.Welcome.route,
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            InputDesign(
                onNext = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(
            route = Screen.Login.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            LoginScreen(
                applicationContext,
                userViewModel,
                onLoginSuccess = {
                    userViewModel.updateUser(
                        name = it.getUser().name,
                        email = it.getUser().email,
                        password = it.getUser().password,
                        id = it.getUser().id,
                        age = it.getUser().age,
                        sex = it.getUser().sex,
                        height = it.getUser().height,
                        currentWeight = it.getUser().currentWeight,
                        goal = it.getUser().goal,)
                    GlobalData.login(userViewModel)
                    printUserInfo(userViewModel)
                    navController.navigate(Screen.Home.route)
                },
                onRegisterSuccess = {
                    userViewModel.updateUser(name = it.getUser().name)
                    navController.navigate(Screen.Sex.route)
                },
            )
        }

        composable(route = Screen.Password.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            ChangePasswordScreen(
                navController,
                userViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    navController.navigateAndClearStack(Screen.Welcome.route)
                })
        }

        composable(
            route = Screen.PhsysicalData.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            UpdatePhysicalDataScreen(
                navController = navController,
                userViewModel = userViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    navController.navigateAndClearStack(Screen.Welcome.route)
                }
            )
        }

        composable(route = Screen.FoodList.route,
        ) {
            var foodList by remember { mutableStateOf<MutableList<FoodViewModel>>(mutableListOf()) }

            LaunchedEffect(Unit){
                getUserPlatesPro(userViewModel.getUser().id, applicationContext, {
                    foodList = parseUserPlatesResponse(it).toMutableList()
                })
            }
            FoodListViewScreen(navController, foodList)
        }

        composable(route = Screen.AddFood.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            AddNewFoodScreen(navController, onNavigateBack = { navController.popBackStack() },
                onNext = {
                newFood.updateFood(
                    protein = it.getFood().protein,
                    fats = it.getFood().fats,
                    sugar = it.getFood().sugar,
                    salt = it.getFood().salt,
                    carbohydrates = it.getFood().carbohydrates,
                    calories = it.getFood().calories,
                    price = it.getFood().price,
                    foodVariants = it.getFood().foodVariants
                )
                userViewModel.getUser().foodList.add(newFood)
                GlobalData.foodList.add(newFood)
                var foodList = GlobalData.foodList
                printFoodInfo(newFood)
                navController.navigate(Screen.NewFoodType.route)
            })
        }

        composable(route = Screen.NewFoodType.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            FoodTypeSelectionScreen(navController,
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    newFood.updateFood(foodTypes = it)
                    printFoodInfo(newFood)
                    navController.navigate(Screen.NewFoodSummary.route)
                }
            )
        }

        composable(route = Screen.NewFoodSummary.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            var addNewFood by remember { mutableStateOf(false) }
            NewFoodSummaryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    newFood.updateFood(name = it.getFood().name)
                    printFoodInfo(newFood)
                    navController.navigate(Screen.FoodDetail.route)
                    addNewFood = true
                    //llamar a la API para guardar comida
                },
                foodViewModel = newFood
            )

            if (addNewFood) {
                LaunchedEffect(Unit) {
                    createPlateFromViewModel(newFood, userViewModel.getUser().id.toString(), applicationContext, onResult = {})
                }
            }
        }

        composable(route = Screen.GenerateMealPlanWithData.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            GenerateMealPlanWithDataScreen(
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    if (it) {
                        navController.navigate(Screen.TypeOfDietSelection.route)
                    } else {
                        navController.navigate(Screen.PhsysicalData.route)
                    }
                },
                userViewModel = userViewModel,
                navController = navController
            )
        }

        composable(route = Screen.TypeOfDietSelection.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            DietSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    dietViewModel.updateDiet(foodVariant = getFoodVariantFromIndex(it))
                    navController.navigate(Screen.DietDurationSelection.route)
                },
            )
        }

        composable(route = Screen.DietDurationSelection.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            DietDurationScreen(
                navigationVariable = navigationVariable,
                onNavigateBack = { navController.popBackStack() },
                onNextName = {
                    dietViewModel.updateDiet(duration = it)
                    navController.navigate(Screen.DietNameSelection.route)
                },
                onNextDiet = {
                    dietViewModel.updateDiet(duration = it)
                    val dietDays = List(dietViewModel.getDiet().duration) {
                        DietDayViewModel()
                    }
                    dietViewModel.updateDiet(diets = dietDays)
                    navController.navigate(Screen.ChosenDiet.route)
                }
            )
        }

        composable(route = Screen.DietNameSelection.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            DietNameScreen(
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    dietViewModel.updateDiet(name = it)
                    val requirements = listOf(userViewModel.getUser().id, getFoodIndexFromVariant(dietViewModel.getDiet().foodVariant), dietViewModel.getDiet().duration, dietViewModel.getDiet().name)
                    create_diet_with_user_data(requirements, context = applicationContext, onResult = {
                        navController.navigateAndClearStack(Screen.Meals.route)                    })
                },
            )
        }

        composable(route = Screen.FoodDetail.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            FoodDetailScreen(
                foodViewModel = newFood,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(route = Screen.Settings.route
        ) {
            SettingsScreen(
                navController,
                userViewModel,
                onLogout = {
                    // userViewModel = UserViewModel()
                    navController.navigateAndClearStack(Screen.Welcome.route)
                }
            )
        }

        composable(route = Screen.DietInterface.route,
            arguments = listOf(
                navArgument("dietId") {
                    type = NavType.StringType // o IntType si usas números
                    nullable = false // Cambia a true si puede ser nulo
                }
            )
        ) { entry ->
            val dietId = entry.arguments?.getString("dietId") ?: ""
            var diet = getDietViewModelId(dietViewModels, dietId)

            if (diet != null) {
                DietInterface(
                    dietViewModel = diet,
                    navController = navController,// Pasa el ID a tu pantalla
                )
            } else {
                // Muestra un indicador de carga mientras esperamos
                LoadingScreen()
            }
        }

        composable(route = Screen.ChosenDiet.route
        ) {
            ChosenDietInterface(
                context = applicationContext,
                dietViewModel = dietViewModel,
                navController = navController,// Pasa el ID a tu pantalla
                userViewModel = userViewModel,
                onNext = {
                    navController.navigate(Screen.Home.route)

                }
            )
        }

        composable(
            route = Screen.GraphicFrame.route,
            arguments = listOf(
                navArgument("dietId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { entry ->
            val dietId = entry.arguments?.getString("dietId") ?: ""
            val diet = getDietViewModelId(dietViewModels, dietId)
            if (diet != null) {
                GraphicFrame(
                    navController = navController,
                    dietViewModel = diet
                )
            } else {
                Text("No se encontró la dieta con ID $dietId")
            }

        }

        composable(route = Screen.Home.route
        ) {
            HomePageFrame(navController, userViewModel)
        }

        composable(route = Screen.SelectTypeOfDietGeneration.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            DietGeneratorSelectionScreen(
                navController = navController,
                userViewModel = userViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    if (it == DietGeneratorType.USER_DATA) {
                        navigationVariable = false
                        navController.navigate(Screen.GenerateMealPlanWithData.route)
                    } else if (it == DietGeneratorType.MANUAL_INPUT){
                        navigationVariable = false
                        navController.navigate(Screen.GenerateMealPlanWithInputs.route)
                    } else {
                        navigationVariable = true
                        navController.navigate(Screen.DietDurationSelection.route)
                    }
                }
            )
        }

        composable(route = Screen.GenerateMealPlanWithInputs.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            GenerateMealPlanWithInputsScreen(
                context = applicationContext,
                userId = userViewModel.getUser().id,
                onNavigateBack = { navController.popBackStack() },
                onNext = {
                    create_diet_with_inputs(it, applicationContext, onResult = {
                        navController.navigateAndClearStack(Screen.Meals.route)
                    })
                }
            )
        }

        composable(route = Screen.Calendar.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }
        ) {
            CalendarScreen(
                onSkip = { },
                onNavigateBack = { navController.popBackStack() },
                onNext = { }
            )
        }

    }
}

fun printUserInfo(userViewModel: UserViewModel) {
    Log.d("Resumen: ",
        " Email: ${userViewModel.getUser().email},\n" +
        " Contraseña: ${userViewModel.getUser().password},\n" +
        " Edad: ${userViewModel.getUser().age},\n" +
        " Sexo: ${userViewModel.getUser().sex},\n" +
        " Altura: ${userViewModel.getUser().height},\n" +
        " Peso: ${userViewModel.getUser().currentWeight},\n" +
        " Objetivo de dieta: ${userViewModel.getUser().goal}"
    )
}

fun printFoodInfo(foodViewModel: FoodViewModel) {
    Log.d("Resumen: ",
        "Nombre: ${foodViewModel.getFood().name},\n" +
        "Protein: ${foodViewModel.getFood().protein}, \n" +
        "Fats: ${foodViewModel.getFood().fats}, \n" +
        "Sugar: ${foodViewModel.getFood().sugar}, \n" +
        "Salt: ${foodViewModel.getFood().salt}, \n" +
        "Carbohydrates: ${foodViewModel.getFood().carbohydrates}, \n" +
        "Calories: ${foodViewModel.getFood().calories}, \n" +
        "Price: ${foodViewModel.getFood().price}, \n" +
        "Food Variants: ${foodViewModel.getFood().foodVariants}, \n" +
        "Food Types: ${foodViewModel.getFood().foodTypes}"
    )
}

fun printAllFoodIds(dietDays: List<DietDayViewModel>, tag: String = "FoodIds") {
    dietDays.forEachIndexed { index, dayViewModel ->
        val foodIds = dayViewModel.getDiet().foodsId.joinToString(", ")
        Log.d(tag, "Día ${index + 1} - IDs de comidas: [$foodIds]")
    }
}

fun getDietViewModelId(dietViewModelList: List<DietViewModel>, dietId: String): DietViewModel? {
    return dietViewModelList.find { it.getDiet().dietId == dietId }
}