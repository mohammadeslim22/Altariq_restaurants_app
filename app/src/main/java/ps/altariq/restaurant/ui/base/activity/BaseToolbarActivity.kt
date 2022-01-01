package ps.altariq.restaurant.ui.base.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

abstract class BaseToolbarActivity : BaseActivity() {
     abstract val toolbar: Toolbar


     abstract var toolbarTitle: TextView?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        //setupActionBarWithNavController(navController)
    }


     fun showTitle() =
        toolbar
            .animate()
            ?.alpha(1.0f)

     fun hideTitle() =
        toolbar
            .animate()
            ?.alpha(0.0f)
}