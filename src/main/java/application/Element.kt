package application

import com.github.javaparser.ast.comments.JavadocComment
import com.github.javaparser.javadoc.Javadoc

/**
 * Element that represent single symbol
 * T is a real data
 * ptrData where is this symbol
 */
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

    override fun toString(): String {
        return elem.toString()
    }
}


/**
 * Represent comment
 */
data class UnifiedComment(val fileLocation: String, val text: Javadoc, val parentSignature: String){
}



/**
 * Element that represents single symbol from JavaDoc comment
 */
class CommentElement<T>(
    override var startPos: Int,
    override var endPos: Int,
    override var elem: T,
    override var ptrData: UnifiedComment
) : Element<T, UnifiedComment>()


class TextElement<T> (
    override var startPos: Int,
    override var endPos: Int,
    override var elem: T,
    override var ptrData: Int
) :Element<T,Int>()
