package com.bandit.came.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bandit.came.ui.history.HistoryScreen
import com.bandit.came.ui.home.HomeScreen
import com.bandit.came.ui.permission.PermissionScreen
import com.bandit.came.ui.rule.RuleEditScreen

object Routes {
    const val HOME = "home"
    const val RULE_EDIT = "rule_edit?ruleId={ruleId}"
    const val HISTORY = "history"
    const val PERMISSION = "permission"

    fun ruleEdit(ruleId: Long = -1L) = "rule_edit?ruleId=$ruleId"
}

@Composable
fun BanditNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }
        composable(
            route = Routes.RULE_EDIT,
            arguments = listOf(navArgument("ruleId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getLong("ruleId") ?: -1L
            RuleEditScreen(navController = navController, ruleId = ruleId)
        }
        composable(Routes.HISTORY) {
            HistoryScreen(navController = navController)
        }
        composable(Routes.PERMISSION) {
            PermissionScreen(navController = navController)
        }
    }
}
