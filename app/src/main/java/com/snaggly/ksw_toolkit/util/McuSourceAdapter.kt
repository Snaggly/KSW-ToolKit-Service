package com.snaggly.ksw_toolkit.util

import android.content.Context
import com.snaggly.ksw_toolkit.R

class McuSourceAdapter(context: Context) {
    private val mcuSourcesList = context.resources.getStringArray(R.array.mcuSourcesList)

    fun getSourceString(sourceInt: Int) : String{
        return mcuSourcesList[sourceInt]
    }
}