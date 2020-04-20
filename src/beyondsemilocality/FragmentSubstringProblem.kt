package beyondsemilocality

import sequenceAlignment.ISemiLocalCombined
import utils.IScoringScheme
import utils.Interval


/**
 * Given strings a, b, and a r fragment intervals in a, the fragment-substring  SA problem asks for the SA
 * score of every fragment in string a against every substring  in string b.
 */
interface IFragmentSubstringSA<T> {
    /**
     *
     */
    fun solve(
        a: List<T>,
        b: List<T>,
        fragments: List<Interval>,
        scoringScheme: IScoringScheme
    ): IFragmentSubstringSolution<T>
}

/**
 * Interface for accessing solution of fragment-substring problem
 */
interface IFragmentSubstringSolution<T> {

    fun getSolution(solutionNumber: Int): ISemiLocalCombined<T>

    fun getSolutions(): List<ISemiLocalCombined<T>>
}


class FragmentSubstringSA<T>(val provider: IFragmentSubstringProvider<T>) : IFragmentSubstringSA<T> {

    override fun solve(a: List<T>, b: List<T>, fragments: List<Interval>, scoringScheme: IScoringScheme):
            IFragmentSubstringSolution<T> =
        FragmentSubstringSolution(
            a,
            b,
            fragments.map { provider.getSolutionFor(it.startInclusive, it.endExclusive) }.toMutableList()
        )

}


class FragmentSubstringSolution<T>(a: List<T>, b: List<T>, val sol: List<ISemiLocalCombined<T>>) :
    IFragmentSubstringSolution<T> {
    override fun getSolution(solutionNumber: Int): ISemiLocalCombined<T> = sol[solutionNumber]

    override fun getSolutions(): List<ISemiLocalCombined<T>> = sol
}



