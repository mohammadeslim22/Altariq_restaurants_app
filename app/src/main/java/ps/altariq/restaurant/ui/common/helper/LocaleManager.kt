package ps.altariq.restaurant.ui.common.helper

import android.content.Context
import android.content.res.Configuration
import java.util.*

class LocaleManager() {

    fun updateResources(context: Context, language: String?): Context {
        var contextTemp = context
        val locale = Locale(language)
        Locale.setDefault(locale)

        val res = context.resources
        val config = Configuration(res.configuration)

        config.setLocale(locale)
        contextTemp = context.createConfigurationContext(config)


        config.locale = locale
        res.updateConfiguration(config, res.displayMetrics)

        return contextTemp
    }

    companion object {

        val LANGUAGE_KEY = "language_key"
    }
}