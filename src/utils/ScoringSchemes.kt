package utils

import java.lang.IllegalArgumentException


interface IScoringScheme {
    fun getNormalizedMatchScore(): Fraction
    fun getNormalizedMismatchScore(): Fraction
    fun getNormalizedGapScore(): Fraction
    fun getMatchScore(): Fraction
    fun getMismatchScore(): Fraction
    fun getGapScore(): Fraction
    fun getOriginalScoreFunc(value: Double, m: Int, i: Int, j: Int): Double
}

/**
 * A regular scoring scheme for sequence alignment problem
 * regular i.e match equals to 1, gap equals to 0 and mismatch is a value from [0,1)
 */
class RegularScoringScheme(numerator: Int, denominator: Int) : IScoringScheme {

    private val match = Fraction(1, 1)
    private val mismatch = Fraction(numerator, denominator)
    private val gap = Fraction(0, 1)

    init {
        if (denominator == 0) throw IllegalArgumentException("denominator mustn't be equals to zero")
        if (numerator >= denominator) throw IllegalArgumentException("numerator should be less than denominator")
        if (!((numerator >= 0 && denominator >= 0) || (numerator <= 0 && denominator <= 0))) throw IllegalArgumentException(
            "numerator should be less than denominator"
        )
    }

    override fun getNormalizedMatchScore() = match

    override fun getNormalizedMismatchScore() = mismatch

    override fun getNormalizedGapScore() = gap

    override fun getMatchScore() = match

    override fun getMismatchScore() = mismatch

    override fun getGapScore() = gap

    override fun getOriginalScoreFunc(value: Double, m: Int, i: Int, j: Int): Double = value
}

//TODO not working ask Tiksin about normalization
class FixedScoringScheme(private val match: Fraction, private val mismatch: Fraction, private val gap: Fraction) :
    IScoringScheme {

    private val normalizedMismatch = (mismatch - 2 * gap) / (match - 2 * gap)

    override fun getNormalizedMatchScore(): Fraction = Fraction(0, 1)

    override fun getNormalizedMismatchScore(): Fraction = normalizedMismatch

    override fun getNormalizedGapScore(): Fraction = Fraction(0, 1)

    override fun getMatchScore(): Fraction = match

    override fun getMismatchScore() = mismatch

    override fun getGapScore() = gap

    override fun getOriginalScoreFunc(value: Double, m: Int, i: Int, j: Int): Double {
//        println((m + j - i) * gap.toDouble())
        //TODO ask tiskin
        return value * (match - 2 * gap).toDouble() + ( (m +j-i)) * gap.toDouble() //- ()//gap.toDouble()
    }

}