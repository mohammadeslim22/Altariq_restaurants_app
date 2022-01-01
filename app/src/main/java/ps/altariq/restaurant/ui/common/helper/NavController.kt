package ps.altariq.restaurant.ui.common.helper

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import ps.altariq.restaurant.ui.common.navigation.NavigationRequest

fun NavController.navigate(
    request: NavigationRequest,
    activity: FragmentActivity? = null,
    fragmentManager: FragmentManager? = null
) {
    when (request) {
        is NavigationRequest.Pop -> {
            if (request.destinationId != null && (request.destinationId > 0)) popBackStack(
                request.destinationId,
                request.inclusive
            )
            else popBackStack()
        }
        is NavigationRequest.PopToRoot -> popBackStack(
            graph.startDestination,
            request.inclusive
        )
        is NavigationRequest.Push -> navigate(
            request.destinationId,
            request.args,
            request.options,
            request.extras
        )
        is NavigationRequest.Finish -> {
            if (activity == null) throw IllegalArgumentException("You cannot finish an Activity using NavController.navigate with a null activity argument")
            activity.finish()
        }
    }
}