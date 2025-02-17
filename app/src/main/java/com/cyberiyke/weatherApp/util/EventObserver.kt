package com.cyberiyke.weatherApp.util

import androidx.lifecycle.Observer
import com.shashank.weatherapp.util.Event

/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been consumed.
 *
 * [onEventUnconsumedContent] is *only* called if the [Event]'s contents has not been consumed.
 */
class EventObserver<T>(private val onEventUnconsumedContent: (T) -> Unit) : Observer<Event<T>> {

    override fun onChanged(value: Event<T>) {
        value?.consume()?.run(onEventUnconsumedContent)

    }
}
