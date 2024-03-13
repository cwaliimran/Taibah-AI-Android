package com.taibahai.utils

import android.content.Context
import android.os.Build
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu

fun Context.showOptionsMenu(
    view: View,
    menuResId: Int,
    hideMenuItemIds: List<Int>? = mutableListOf(), // pass id like this mutableListOf(R.id.delete)
    onMenuItemClickListener: (MenuItem) -> Boolean
) {
    val popupMenu = PopupMenu(this, view)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        popupMenu.setForceShowIcon(true)
    }
    val inflater: MenuInflater = popupMenu.menuInflater
    inflater.inflate(menuResId, popupMenu.menu)

    // Get a reference to the menu and loop through its items to hide the desired ones
    val menu = popupMenu.menu
    if (hideMenuItemIds != null) {
        for (id in hideMenuItemIds) {
            val menuItem = menu.findItem(id)
            menuItem?.isVisible = false
        }
    }

    popupMenu.setOnMenuItemClickListener { menuItem ->
        onMenuItemClickListener(menuItem)
    }

    popupMenu.show()
}