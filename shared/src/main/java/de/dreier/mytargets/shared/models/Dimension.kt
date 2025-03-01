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
package de.dreier.mytargets.shared.models

import android.os.Parcelable
import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.SharedApplicationInstance
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dimension(val value: Float, val unit: Unit?) : IIdProvider, Comparable<Dimension>,
    Parcelable {

    override fun compareTo(other: Dimension) =
        compareBy({ unit?.abbreviation }, Dimension::value).compare(this, other)

    override fun toString(): String {
        val context = SharedApplicationInstance.context
        if (value == -1f) {
            return context.getString(R.string.unknown)
        } else if (unit == null) {
            return when (value.toInt()) {
                Diameter.MINI_VALUE -> context.getString(R.string.mini)
                Diameter.SMALL_VALUE -> context.getString(R.string.small)
                Diameter.MEDIUM_VALUE -> context.getString(R.string.medium)
                Diameter.LARGE_VALUE -> context.getString(R.string.large)
                Diameter.XLARGE_VALUE -> context.getString(R.string.xlarge)
                else -> ""
            }
        }
        return Integer.toString(value.toInt()) + unit.toString()
    }

    override val id: Long
        get() = hashCode().toLong()

    fun convertTo(unit: Unit): Dimension {
        if (this.unit == null) {
            return Dimension((8f - this.value) * 4f, Unit.CENTIMETER).convertTo(unit)
        }
        val newValue = value * unit.factor / this.unit.factor
        return Dimension(newValue, unit)
    }

    fun formatString(): String {
        return this.value.toString() + " " + unit.toString()
    }

    enum class Unit(
        internal val abbreviation: String,
        /* factor <units> = 1 meter */
        internal val factor: Float
    ) {
        CENTIMETER("cm", 100f),
        INCH("in", 39.3701f),
        METER("m", 1f),
        YARDS("yd", 1.093613f),
        FEET("ft", 3.28084f),
        MILLIMETER("mm", 1000f);

        override fun toString(): String {
            return abbreviation
        }

        companion object {
            fun from(unit: String?): Unit? {
                return when (unit) {
                    "cm" -> CENTIMETER
                    "in" -> INCH
                    "m" -> METER
                    "yd" -> YARDS
                    "ft" -> FEET
                    "mm" -> MILLIMETER
                    else -> null
                }
            }
        }
    }

    companion object {
        val UNKNOWN = Dimension(-1f, null as Dimension.Unit?)

        fun from(value: Float, unit: Unit?): Dimension {
            return Dimension(value, if (value < 0f) null else unit)
        }

        fun from(value: Float, unit: String?): Dimension {
            return from(value, Unit.from(unit))
        }

        fun parse(dimensionString: String): Dimension {
            val index = dimensionString.indexOf(' ')
            val value = dimensionString.substring(0, index)
            val unit = dimensionString.substring(index + 1)
            return Dimension.from(value.toFloat(), unit)
        }
    }
}

