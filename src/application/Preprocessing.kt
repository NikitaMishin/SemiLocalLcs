package application

import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import java.util.*


/**
 * Pipeline preprocessor that preprocess data for the given type of data source P and concreteTODO
 */
interface IPipeLinePreprocessor<T, P,R> {
    /**
     *
     */
    fun addProcessor(elementProcessor: IElementProcessor<T, P>)

    fun removeAllProcessors()

    fun addAllProcessors(elementProcessor: List<IElementProcessor<T, P>>)


    fun processMultiple(comments: List<R>): List<List<Element<T, P>>>

    fun processSingle(comment: R): List<Element<T, P>>
}


class JavaDocPipeLineNLPStanford : IPipeLinePreprocessor<String, UnifiedComment,UnifiedComment> {
    private var pipeline: StanfordCoreNLP
    private val processors: MutableList<IElementProcessor<String, UnifiedComment>> = mutableListOf()

    init {
        val props = Properties()
        props.setProperty("annotators", "tokenize," + "ssplit," + "pos," + "lemma")
        pipeline = StanfordCoreNLP(props)

    }

    override fun addProcessor(elementProcessor: IElementProcessor<String, UnifiedComment>) {
        processors.add(elementProcessor)
    }

    override fun addAllProcessors(elementProcessor: List<IElementProcessor<String, UnifiedComment>>) {
        for (proc in elementProcessor) processors.add(proc)
    }

    override fun removeAllProcessors() {
        processors.clear()
    }

    override fun processSingle(comment: UnifiedComment): List<Element<String, UnifiedComment>> =
        process(listOf(comment)).first()

    override fun processMultiple(comments: List<UnifiedComment>): List<List<Element<String, UnifiedComment>>> =
        process(comments)


    private fun process(comments: List<UnifiedComment>): List<List<Element<String, UnifiedComment>>> =
        processors.fold(comments.map { unifiedComment ->
            val annotatedText = CoreDocument(unifiedComment.text.description.toText())
            pipeline.annotate(annotatedText)
            val elems: List<Element<String, UnifiedComment>> = annotatedText.tokens().map {
                CommentElement(
                    it.beginPosition(),
                    it.endPosition(),
                    if (it.lemma().isNullOrBlank()) it.word() else it.lemma(),
                    unifiedComment
                )
            }
            elems
        }, { acc, processor -> processor.process(acc) })

}


class StringPipeLineNLPStanford : IPipeLinePreprocessor<String,Int,String>{
    private var pipeline: StanfordCoreNLP
    private val processors: MutableList<IElementProcessor<String, Int>> = mutableListOf()

    init {
        val props = Properties()
        props.setProperty("annotators", "tokenize," + "ssplit," + "pos," + "lemma")
        pipeline = StanfordCoreNLP(props)

    }

    override fun addProcessor(elementProcessor: IElementProcessor<String, Int>) {
        processors.add(elementProcessor)
    }

    override fun addAllProcessors(elementProcessor: List<IElementProcessor<String, Int>>) {
        for (proc in elementProcessor) processors.add(proc)
    }

    override fun removeAllProcessors() {
        processors.clear()
    }

    override fun processSingle(comment: String): List<Element<String, Int>> =
        process(listOf(comment)).first()

    override fun processMultiple(comments: List<String>): List<List<Element<String, Int>>> =
        TODO()
//        process(comments)



    private fun process(comments: List<String>): List<List<Element<String, Int>>> =
        processors.fold(comments.map { text ->
            val annotatedText = CoreDocument(text)
            pipeline.annotate(annotatedText)
            val elems: List<Element<String, Int>> = annotatedText.tokens().map {
                TextElement(
                    it.beginPosition(),
                    it.endPosition(),
                    if (it.lemma().isNullOrBlank()) it.word() else it.lemma(),
                    0
                )
            }
            elems
        }, { acc, processor -> processor.process(acc) })


}

