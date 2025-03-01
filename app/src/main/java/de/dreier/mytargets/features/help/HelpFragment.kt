/*
 * Copyright (C) 2018 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.help

import android.app.AlertDialog
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import android.view.*
import de.dreier.mytargets.R
import de.dreier.mytargets.base.navigation.NavigationController
import de.dreier.mytargets.databinding.FragmentWebBinding
import de.dreier.mytargets.features.help.licences.LicencesActivity
import de.dreier.mytargets.utils.ToolbarUtils
import java.io.IOException

/**
 * Shows all rounds of one training.
 */
class HelpFragment : Fragment() {

    private lateinit var navigationController: NavigationController
    private lateinit var binding: FragmentWebBinding

    private val helpHtmlPage: String
        get() {
            var prompt = ""
            try {
                val inputStream = resources.openRawResource(R.raw.help)
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                prompt = String(buffer)
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return prompt
        }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_web, container, false)
        val prompt = helpHtmlPage
        binding.webView
            .loadDataWithBaseURL("file:///android_asset/", prompt, "text/html", "utf-8", "")
        binding.webView.isHorizontalScrollBarEnabled = false
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigationController = NavigationController(this)
        ToolbarUtils.showHomeAsUp(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.help, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_source_licences -> {
                startActivity(Intent(context, LicencesActivity::class.java))
                true
            }

            R.id.action_about -> {
                navigationController.navigateToAbout()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
