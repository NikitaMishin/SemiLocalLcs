package utils

import java.lang.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

fun gcd(g: Int, e: Int): Int {
    var a = abs(g)
    var b = abs(e)
    if (a == 0 || b == 0) return max(a, b)

    while (b != 0) {
        val t = b
        b = a % b
        a = t
    }
    return a
}

/**
 * Standart fraction
 * Note denominator should not be zero
 **/
class Fraction(numerator: Int, denominator: Int) {
    val numerator: Int
    val denominator: Int
    val sign: Int = when {
        numerator.sign == denominator.sign -> 1
        numerator.sign == 0 || denominator.sign == 0 -> 0
        else -> -1
    }

    init {
        if (denominator == 0) throw IllegalArgumentException("Denominator mustn't equals to zero")

        val gcd = gcd(numerator, denominator)
        this.numerator = abs(numerator / gcd)
        this.denominator = abs(denominator / gcd)
    }

    fun toDouble(): Double = sign.toDouble() * numerator / denominator

    override fun equals(other: Any?): Boolean {
        if (other !is Fraction) return false
        return numerator == other.numerator && other.denominator == denominator
    }

    override fun hashCode(): Int {
        var hash = 7
        hash = 31 * hash + numerator.hashCode()
        hash = 31 * hash + denominator.hashCode()
        return hash
    }

    override fun toString(): String = when (sign) {
        1 -> "$}${numerator}/${denominator}"
        0 -> "0"
        else -> "-${numerator}/${denominator}"
    }


    operator fun plus(other: Fraction): Fraction = Fraction(
        this.sign * other.denominator * this.numerator + other.sign * other.numerator * this.denominator,
        this.denominator * other.denominator
    )


    operator fun times(other: Fraction): Fraction {
        if (this.numerator == 0 || other.numerator == 0) return Fraction(0, 1)
        return Fraction(this.sign * other.sign * this.numerator * other.numerator, this.denominator * other.denominator)

    }


    operator fun minus(other: Fraction): Fraction = Fraction(
        this.sign * other.denominator * this.numerator - other.sign * other.numerator * this.denominator,
        this.denominator * other.denominator
    )

    operator fun div(other: Fraction): Fraction {
        if (other.numerator == 0) throw  IllegalArgumentException("Division by zero")
        if (this.numerator == 0 || other.denominator == 0) return Fraction(0, 1)
        return Fraction(this.sign * other.sign * this.numerator * other.denominator, this.denominator * other.numerator)
    }

    operator fun plus(other: Int) = this + Fraction(other, 1)
    operator fun times(other: Int) = this * Fraction(other, 1)
}

operator fun Int.times(other: Fraction):Fraction = Fraction(this,1) * other
operator fun Int.plus(other: Fraction):Fraction = Fraction(this,1) + other
