/*
 * Copyright (C) 2013 Simon Marquis (http://www.simon-marquis.fr)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package fr.simon.marquis.preferencesmanager

import android.app.Application
import com.topjohnwu.superuser.Shell
import fr.simon.marquis.preferencesmanager.util.PrefManager
import timber.log.Timber

class App : Application() {

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setFlags(Shell.FLAG_MOUNT_MASTER) // Android R fix
                .setTimeout(10)
        )
    }

    override fun onCreate() {
        super.onCreate()

        PrefManager.init(applicationContext)

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}
