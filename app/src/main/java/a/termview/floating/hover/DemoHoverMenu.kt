/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package a.termview.floating.hover

import a.termview.R
import android.content.Context
import android.content.res.Resources
import android.view.View
import io.mattcarroll.hover.HoverMenu
import utils.UiThread

import java.util.Collections

/**
 * A HoverMenu provides the Sections that are displayed in the HoverMenuView. Each Section has an
 * ID, tab View, and visual content.
 */
class DemoHoverMenu(UI: UiThread, private val mMenuId: String) : HoverMenu() { // DemoHoverMenuService

    private val mContext: Context
    private val mSection: HoverMenu.Section

    init {
        mContext = UI.context.applicationContext
        mSection = HoverMenu.Section(
            HoverMenu.SectionId("0"),
            createTabView(),
            NonFullscreenContent(UI)
        )
    }

    private fun createTabView(): View {
        return DemoTabView(mContext, mContext.resources.getDrawable(R.drawable.circle))
    }

    override fun getId(): String {
        return mMenuId
    }

    override fun getSectionCount(): Int {
        return 1
    }

    override fun getSection(index: Int): HoverMenu.Section? {
        return mSection
    }

    override fun getSection(sectionId: HoverMenu.SectionId): HoverMenu.Section? {
        return mSection
    }

    override fun getSections(): List<HoverMenu.Section> {
        return listOf(mSection)
    }
}
