package application

import duplicateDetection.*
import longestCommonSubsequence.ReducingKernelEvaluation
import sequenceAlignment.ImplicitSemiLocalProvider
import utils.FixedScoringScheme
import utils.Fraction
import utils.dummyPermutationMatrixTwoLists
import java.io.File
import kotlin.system.measureTimeMillis


//fun main(){
////    1, 2, 3, 1, 4, 3,
////       2, 3, 1,
//    val a = mutableListOf(5,5,44,5,  1,2,3,4,5)
//    val b = mutableListOf(5,42, 5, 46, 47, 48,1, 49,3,55,5,4,5, 50, 51)
//    val scheme = FixedScoringScheme(Fraction(1,1), Fraction(-1,1), Fraction(1,-1))
////    val res = BoundedLengthSmithWatermanAlignment(ImplicitFragmentSubstringProvider(a,b,scheme)).solve(a,b,scheme,3)
////    println(res.second.score)
////    println(a.subList(res.first.startInclusive,res.first.endExclusive))
////    println(b.subList(res.second.startInclusive,res.second.endExclusive))
//    print(BoundedLengthSWMeasureFunction<Int>(scheme,4).computeSimilarity(a,b,0.6))
//
//}


//"1,2" "1/1,-1/1,-1/1" "1" "1" "Started shortly for assured hearing expense" "/home/nikita/IdeaProjects/GeneralSemiLocalSubsequenceProblem/src/application/exampleTextForPatternMatching.txt" "0.8"
// "1,2" "1/1,-1/1,-1/1" "0" "0" "0" "/home/nikita/Apache/junit4/main/java" "0.8" mcl bounded legnth
// "1,2" "1/1,-1/1,-1/1" "0" "1" "0" "/home/nikita/Apache/junit4/main/java" "0.8" "1" branching bounded legnth
// "1,2" "1/1,-1/1,-1/1" "0" "1" "1" "/home/nikita/Apache/junit4/main/java" "0.8" "1" branching semi-local

/**
 * Setup for patternMatching
 * [0] preproc: "1,2"
 * [1] metric:  "1/1,-1/1,-1/1" --- aka edit distance
 * [2] taskType: "1" -- because pattern matching
 * [3] patternAlgo:  "0" -- tiskin, "1"- max cut, luciv - 2, lucivExplicit-3 lucivImplicti-4
 * [4] pattern: .....
 * [5] file location of text: path
 * [6] threshold: set i think 0.8
 * [7] unuised
 * [8] outputfilepath
 */

/**
 * Setup for clone detection
 * [0] prerpoc: "1,2"
 * [1] metric: "1/1,-1/1,-1/1"
 * [2] taskType: "0"
 * [3] grouppingAlgo: mcl--- "0" or branching "1"
 * [4] similarityFunc: boundedLengthSmithWaterman --- 0 , semi-local "1"
 * [5] directory: path to java project
 * [6] threshold: set from 0 to 1
 * [7] int (unsed here)
 * [8] outputfilepath
 */

/**
 * PatternMatching
 * preprocessorsIds after tokenization and lemmatization:
 *    0 == removes tags
 *    1 == all words and lemmas translated to lower case
 *    2 -> removes stop words
 *    3 -> removes comments with less then 3 words
 */

object Application {

    private val boundedLenghtConstant = 3

    private fun <P> getProcessor(id: Int): IElementProcessor<String, P> = when (id) {
        0 -> TagRemovalTransformer()
        1 -> TokenToLowerCaseTransformer()
        2 -> StopWordsRemovalProcessor()
        3 -> MinimalTokenLengthTransformer(3)
        else -> throw NotImplementedError("dfd")
    }

    @JvmStatic
    fun main(args: Array<String>) {

        val preprocessorsToApply = args[0].split(",")

        // scoring scheme
        val metricsN = args[1].split(',').map {
            val d = it.split('/')
            Fraction(d[0].toInt(), d[1].toInt())
        }
        val scheme = FixedScoringScheme(metricsN[0], metricsN[1], metricsN[2])

        // type of task
        val taskType = args[2].toInt().toTask()

        val task: ITaskDuplicateDetection

        if (taskType == Application.TASK.PATTERN) {
            val patternAlgo = args[3].toInt().toApproximateMatchingAlgo()
            val patternRaw = args[4]
            val file = args[5]
            val percent = args[6].toDouble()

            val textPreprocessor = StringPipeLineNLPStanford()

            val transformer = StringToIntTransformerText()
            textPreprocessor.addAllProcessors(preprocessorsToApply.map { getProcessor<Int>(it.toInt()) })
            val textRaw = FileParser(file).parse().joinToString("")
            val t = transformer.transformMultiple(
                    listOf(
                            textPreprocessor.processSingle(patternRaw),
                            textPreprocessor.processSingle(textRaw)
                    )
            )
            task = getPatternMatchingTask(patternAlgo, t[0], t[1], textRaw, patternRaw, scheme, percent)

        } else {
            val grouppingAlgo = args[3].toInt().toGrouppingAlgo()
            val metric = args[4].toInt().toFuncMetricAlgo()
            val directory = args[5]
            val percent1 = args[6].toDouble()
            val percent2 = args[7].toDouble()
            val javaDocParser: IParser<UnifiedComment> = JavaDocParser(directory)
            val preprocessor = JavaDocPipeLineNLPStanford()
            preprocessor.addAllProcessors(preprocessorsToApply.map { getProcessor<UnifiedComment>(it.toInt()) })
            val parsed = javaDocParser.parse()
            val transformer: ITransformer<String, Int, UnifiedComment> = StringToIntTransformerUnifiedComment()
            val comments: List<List<Element<Int, UnifiedComment>>> =
                    transformer.transformMultiple(preprocessor.processMultiple(parsed))
            task = getGroupMatchingTaskProvider(grouppingAlgo, metric, scheme, comments, percent1, percent2)
        }

        val t =
        measureTimeMillis {
            task.processTask()
        }
        val json = task.buildJSONReport()

        println(t)
//        write to output file
        File(args[8]).bufferedWriter().use { out ->
            out.write(json)
        }

    }

    private fun <T> getPatternMatchingTask(
            id: PATTERNALGO,
            pattern: List<Element<T, Int>>,
            text: List<Element<T, Int>>,
            rawText: String,
            rawPattern: String,
            scheme: FixedScoringScheme,
            percent: Double
    ): ITaskDuplicateDetection =
            when (id) {
                Application.PATTERNALGO.TISKIN ->
                    TaskApproximateMatchingViaSemiLocal(
                            ApproximateMatchingViaThresholdAMatch<Element<T, Int>>(
                                    ImplicitSemiLocalProvider(ReducingKernelEvaluation({ dummyPermutationMatrixTwoLists }, scheme)),
                                    scheme, percent
                            ), pattern, text, rawText, rawPattern, scheme
                    )
                Application.PATTERNALGO.NAIVEMAXIMUM2020 ->
                    TaskApproximateMatchingViaSemiLocal(

                            ApproximateMatchingViaCut<Element<T, Int>>(
                                    ImplicitSemiLocalProvider(ReducingKernelEvaluation({ dummyPermutationMatrixTwoLists }, scheme)),
                                    scheme, percent
                            ), pattern, text, rawText, rawPattern, scheme
                    )
                Application.PATTERNALGO.LUCIV ->
                    TaskApproximateMatchingViaSemiLocal(
                            InteractiveDuplicateSearch(0.77), pattern, text, rawText, rawPattern, scheme)
                Application.PATTERNALGO.LUCIVSmartSlow ->
                    TaskApproximateMatchingViaSemiLocal(
                            InteractiveDuplicateSearchViaSemiLocal(0.77, true),
                            pattern, text, rawText, rawPattern, scheme)
                Application.PATTERNALGO.LUCIVNaiveFast ->
                    TaskApproximateMatchingViaSemiLocal(
                            InteractiveDuplicateSearchViaSemiLocal(0.77, false),
                            pattern, text, rawText, rawPattern, scheme)

                else -> throw NotImplementedError("dfdf")
            }

    private fun <T> getGroupMatchingTaskProvider(
            id: GROUPALGO,
            metric: METRIC,
            scheme: FixedScoringScheme,
            comments: List<List<Element<T, UnifiedComment>>>,
            percent1: Double,
            percent2: Double
    ): ITaskDuplicateDetection = when {
        id == Application.GROUPALGO.MCL && metric == Application.METRIC.MAXIMUMLOCAL ->
            TreeGroupDuplicate(
                    comments,
                    BoundedLengthSWMeasureFunction(scheme, boundedLenghtConstant),
                    MCLWithSpanningTree(),
                    percent1
            )
        id == Application.GROUPALGO.BRANCHING && metric == Application.METRIC.MAXIMUMLOCAL ->
            TreeGroupDuplicate(
                    comments,
                    BoundedLengthSWMeasureFunction(scheme, boundedLenghtConstant),
                    TarjanTree(),
                    percent1
            )
        id == Application.GROUPALGO.BRANCHING && metric == Application.METRIC.MAXIMUMSEMI ->
            TreeGroupDuplicate(comments, StringSubstringMeasureFunction(scheme), TarjanTree(), percent1)
        id == Application.GROUPALGO.MCL && metric == Application.METRIC.MAXIMUMSEMI ->
            TreeGroupDuplicate(comments, StringSubstringMeasureFunction(scheme), MCLWithSpanningTree(), percent1)

        else -> throw NotImplementedError("ddd")
    }


    private enum class TASK {
        GROUPDUP,
        PATTERN,
    }

    private enum class GROUPALGO {
        MCL,
        HIERARHICALCLUSTERING,
        BRANCHING

    }

    private enum class METRIC {
        MAXIMUMLOCAL,
        MAXIMUMSEMI,
        MAXGLOBAL
    }

    private enum class PATTERNALGO {
        LUCIV,
        LUCIVSmartSlow,
        LUCIVNaiveFast,
        TISKIN,
        NAIVEMAXIMUM2020,
        UPDATEDLUCIV,
//        MAXIMUM2020
    }

    private fun Int.toTask() = when (this) {
        0 -> TASK.GROUPDUP
        1 -> TASK.PATTERN
        else -> throw NotImplementedError("No task for this id")
    }

    private fun Int.toApproximateMatchingAlgo() = when (this) {
        0 -> Application.PATTERNALGO.TISKIN
        1 -> Application.PATTERNALGO.NAIVEMAXIMUM2020
        2 -> Application.PATTERNALGO.LUCIV
        3 -> Application.PATTERNALGO.LUCIVSmartSlow
        4 -> Application.PATTERNALGO.LUCIVNaiveFast
        else -> throw NotImplementedError("No task for this id")
    }

    private fun Int.toFuncMetricAlgo() = when (this) {
        0 -> METRIC.MAXIMUMLOCAL
        1 -> METRIC.MAXIMUMSEMI
        else -> throw NotImplementedError("No task for this id")
    }

    private fun Int.toGrouppingAlgo() = when (this) {
        0 -> GROUPALGO.MCL
        1 -> GROUPALGO.BRANCHING
        else -> throw NotImplementedError("No task for this id")
    }


}





