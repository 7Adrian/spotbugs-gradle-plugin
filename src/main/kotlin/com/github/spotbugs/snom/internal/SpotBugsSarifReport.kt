/*
 * Copyright 2021 SpotBugs team
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.spotbugs.snom.internal

import com.github.spotbugs.snom.SpotBugsReport
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class SpotBugsSarifReport
    @Inject
    constructor(objects: ObjectFactory, task: SpotBugsTask) :
    SpotBugsReport(objects, task) {
        init {
            // the default reportsDir is "$buildDir/reports/spotbugs/${baseName}.sarif"
            outputLocation.convention(task.reportsDir.file(task.getBaseName() + ".sarif"))
        }

        override fun toCommandLineOption(): String {
            return "-sarif"
        }
    }