package application

/**
 * Transform from one language T to another language P,
 * K is where data Stored
 */
interface ITransformer<T, P, K> {
    fun transformMultiple(elems: List<List<Element<T, K>>>): List<List<Element<P, K>>>
}

/**
 * Transform Strings to Ints
 */
class StringToIntTransformerUnifiedComment : ITransformer<String, Int, UnifiedComment> {
    val mapper = hashMapOf<String, Int>()

    override fun transformMultiple(elems: List<List<Element<String, UnifiedComment>>>): List<List<Element<Int, UnifiedComment>>> {
        var counter = if (mapper.isEmpty()) 0 else mapper.maxBy { it.value }!!.value + 1
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


class StringToIntTransformerText : ITransformer<String, Int, Int> {
    val mapper = hashMapOf<String, Int>()

    override fun transformMultiple(elems: List<List<Element<String, Int>>>): List<List<Element<Int, Int>>> {
        var counter = if (mapper.isEmpty()) 0 else mapper.maxBy { it.value }!!.value + 1
        return elems.map { comment ->
            comment.map { symbol ->
                if (mapper.containsKey(symbol.elem)) {
                    TextElement(symbol.startPos, symbol.endPos, mapper[symbol.elem]!!, symbol.ptrData)
                } else {
                    counter++
                    mapper[symbol.elem] = counter
                    TextElement(symbol.startPos, symbol.endPos, counter, symbol.ptrData)
                }
            }
        }
    }
}