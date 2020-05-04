package application

import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import java.util.*


abstract class Element<T, D> {
    abstract var startPos: Int
    abstract var endPos: Int
    abstract var elem: T
    abstract var ptrData: D

    override fun equals(other: Any?): Boolean {
        if (other is Element<*, *>) return other.elem == this.elem
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return elem?.hashCode() ?: 0
    }
}

class CommentElement<T>(
    override var startPos: Int,
    override var endPos: Int,
    override var elem: T,
    override var ptrData: UnifiedComment
) : Element<T, UnifiedComment>()


interface IElementProcessor<T, P> {
    fun process(elemsList: List<Element<T, P>>): List<Element<T, P>>
}

/**
 * Removes stop words from elemList
 */
class StopWordsRemovalProcessor<T, P> : IElementProcessor<T, P> {
    override fun process(elemsList: List<Element<T, P>>): List<Element<T, P>> {
//        elemsList.filter { mapper(it.elem)=="." }
        TODO()
    }
}


/**
 * Removes all Delimiters
 */
class DelimiterRemovalProcessor<T, P> : IElementProcessor<T, P> {
    override fun process(elemsList: List<Element<T, P>>): List<Element<T, P>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


/**
 * Removes all <code> ... </code> and ...
 */
class TagRemovalTransformer<T, P> : IElementProcessor<T, P> {
    override fun process(elemsList: List<Element<T, P>>): List<Element<T, P>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


interface IPipeLinePreprocessor<T, P> {
    /**
     *
     */
    fun addProcessor(elementProcessor: IElementProcessor<T, P>)

    fun process(comments: List<UnifiedComment>): List<List<Element<Int, UnifiedComment>>>
}


class StanfordNlpPipelineProcessor(val processors: MutableList<IElementProcessor<String, UnifiedComment>> = mutableListOf()) :
    IPipeLinePreprocessor<String, UnifiedComment> {
    private var pipeline: StanfordCoreNLP

    override fun addProcessor(elementProcessor: IElementProcessor<String, UnifiedComment>) {
        processors.add(elementProcessor)
    }


    //TODO maybe add language tool ? grazie?
    init {
        val props = Properties()
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse,coref,kbp,quote")
        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
        props.setProperty("coref.algorithm", "neural")
        // build pipeline
        pipeline = StanfordCoreNLP(props)
    }

    //TODO named entitty ADD?
    override fun process(comments: List<UnifiedComment>): List<List<Element<Int, UnifiedComment>>> =
        mapToNumbers(
            comments.map { unifiedComment ->
                val annotatedText = CoreDocument(unifiedComment.javadocComment.parse().description.toText())
                pipeline.annotate(annotatedText)
                val elems: List<Element<String, UnifiedComment>> = annotatedText.tokens().map {
                    CommentElement(
                        it.beginPosition(),
                        it.endPosition(),
                        if (it.lemma().isNullOrBlank()) it.toString() else it.lemma(),
                        unifiedComment
                    )
                }
                processors.fold(elems, { acc, processor -> processor.process(acc) })
            }
        )


    private fun mapToNumbers(elems: List<List<Element<String, UnifiedComment>>>): List<List<Element<Int, UnifiedComment>>> {
        // lemma -> int
        val mapper = hashMapOf<String, Int>()
        var counter = 0
        return elems.map { comment ->
            comment.map { symbol ->
                if (mapper.containsKey(symbol.elem)) {
                    CommentElement(symbol.startPos, symbol.endPos, mapper[symbol.elem]!!, symbol.ptrData)
                } else {
                    counter++
                    mapper[symbol.elem] = counter
                    CommentElement(symbol.startPos, symbol.endPos, counter, symbol.ptrData)
                }
            }
        }
    }

}
