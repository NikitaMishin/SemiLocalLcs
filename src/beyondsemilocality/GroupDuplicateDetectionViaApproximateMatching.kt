package beyondsemilocality

import approximateMatching.CompleteAMatchViaSemiLocalTotallyMonotone
import approximateMatching.ThresholdAMathViaSemiLocal
import longestCommonSubsequence.ReducingKernelEvaluation
import sequenceAlignment.ISemiLocalProvider
import sequenceAlignment.ImplicitSemiLocalSA
import utils.*


/**
 *
 */
data class Group<T>(val head: Fragment<T>, val duplicates: List<TextInterval<T>>)


/**
 *
 */
data class Fragment<T>(val text: List<T>, var startInclusive: Int, var endExclusive: Int) {
    fun size() = endExclusive - startInclusive
}


//TODO need choose specific threshold  /// maybe normalized?
class GroupDuplicateSearching(var semilocalBuilder: ISemiLocalProvider, var scheme: IScoringScheme) {


    /**
     * Find all clones of pattern p in all fragments with length of the duplicate >= minLen.
     * Note that returns Group  with rest fragments with length >= minLen
     * if no found --- returns initial fragments and empty Group
     */
    fun <T> duplicateViaApproximateMatching(
        p: Fragment<T>, fragments: List<Fragment<T>>, h: Double, w: Int): Pair<Group<T>, MutableList<Fragment<T>>> {

        val restFragments: MutableList<Fragment<T>> = mutableListOf()
        val duplicates = mutableListOf<TextInterval<T>>()


        for (fragment in fragments) {
            val solution = semilocalBuilder.buildSolution(
                p.text.subList(p.startInclusive, p.endExclusive),
                fragment.text.subList(fragment.startInclusive, fragment.endExclusive),
                scheme
            )

            val aMatch = CompleteAMatchViaSemiLocalTotallyMonotone(solution)
//            TODO set h to possible minimum? cause user Double.NEGATIVE_INFINITY
            val clones = ThresholdAMathViaSemiLocal(aMatch).solve(h)
                .filter { it.endExclusive - it.startInclusive >= w }

            if (clones.isEmpty()) {
                restFragments.add(fragment)
                continue
            }

            //local
            var last = 0
            // add intervals that not covered by clones with len > minlen
            for (cl in clones) {
                if (cl.startInclusive - last >= w)
                    restFragments.add(
                        Fragment(
                            fragment.text,
                            fragment.startInclusive + last,
                            fragment.startInclusive + cl.startInclusive
                        )
                    )
                last = cl.endExclusive
            }
            //last
            if (fragment.endExclusive - (last + fragment.startInclusive) >= w)
                restFragments.add(
                    Fragment(fragment.text, last + fragment.startInclusive, fragment.endExclusive)
                )

            duplicates.addAll(clones.map {
                TextInterval(
                    it.startInclusive + fragment.startInclusive,
                    it.endExclusive + fragment.startInclusive,
                    fragment.text,
                    it.score
                )
            })

        }

        return Pair(Group(p, duplicates), restFragments)


    }

    fun <T> findGroups(fragments: List<Fragment<T>>, groups: MutableList<Group<T>>, h: Double, min: Int, max: Int): MutableList<Group<T>> {

       if (groups.size==2){
           println()
       }

        if (max < min) return groups

        val start = fragments.maxBy { it.size() }
        if (start == null || start.size() < min) return groups

        if (start.size() < max) return findGroups(fragments, groups, h, min, start.size())


        for (p in fragments) {
            for (offset in 0..p.size() - max) {

                val rightEdge = p.startInclusive + max + offset
                val leftEdge = p.startInclusive + offset
                val pattern = Fragment(p.text, leftEdge, rightEdge)
                val tmp = mutableListOf<Fragment<T>>()

                if (offset >= min) tmp += Fragment(p.text, p.startInclusive, p.startInclusive + offset)

                if (p.endExclusive - rightEdge >= min) tmp += Fragment(p.text, rightEdge, p.endExclusive)

                for (fr in fragments) {
                    if (p == fr) continue
                    tmp.add(fr)
                }
                val res = duplicateViaApproximateMatching(pattern, tmp, h, min)
                if (res.first.duplicates.isNotEmpty()) {

                    groups.add(res.first)
                    return findGroups(res.second, groups, h, min, max)
                }
            }
        }

        // not found with specified length
        return findGroups(fragments, groups, h, min, max - 1)


    }

}
