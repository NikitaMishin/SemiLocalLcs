package utils

import kotlin.math.abs

val epsilon = 1E-5
fun Double.isEquals(d: Double) = abs(this - d) < epsilon

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

/**
 * Extensions for the
 */
operator fun Int.times(other: Fraction):Fraction = Fraction(this,1) * other
operator fun Int.plus(other: Fraction):Fraction = Fraction(this,1) + other


/**
 *
 */
fun <T : Comparable<T>> T.isInside(interval: IntervalQuery<T>) =
    this >= interval.leftInclusive && this <= interval.rightInclusive
