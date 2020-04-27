package application

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseProblemException
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.comments.Comment
import com.github.javaparser.ast.comments.JavadocComment
import com.github.javaparser.ast.expr.MarkerAnnotationExpr
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.javadoc.JavadocBlockTag
import com.github.javaparser.javadoc.description.JavadocDescription
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.streams.toList

/**
 * Represent comment
 */
data class UnifiedComment(val fileLocation: String, val javadocComment: JavadocComment, val parentSignature: String)


interface IJavaDocParser {
    fun parse(): List<UnifiedComment>
}

class JavaDocParser(private val compilationUnit: CompilationUnit, private val localPath: String) : IJavaDocParser {
    override fun parse(): List<UnifiedComment> = compilationUnit
        .javaDocToAnalyze().map { it as JavadocComment }
        .map {
            UnifiedComment(localPath, it, if (it.commentedNode.isPresent) it.commentedNode.get().getSignature() else "")
        }

    /**
     * We only analyze methods,classes/interfaces, enum classes ... see below
     */
    fun CompilationUnit.javaDocToAnalyze(): List<Comment> =
        this.comments.filter { it.isJavadocComment }.filter {
            it.commentedNode.isPresent && (
                    it.commentedNode.get() is ConstructorDeclaration ||
                            it.commentedNode.get() is MethodDeclaration ||
                            it.commentedNode.get() is AnnotationDeclaration ||
                            it.commentedNode.get() is EnumDeclaration ||
                            it.commentedNode.get() is ClassOrInterfaceDeclaration ||
                            it.commentedNode.get() is MarkerAnnotationExpr
                    )
        }

    /**
     * TO gEt signature of commented note
     */
    fun Node.getSignature(): String =
        when (this) {
            is ConstructorDeclaration -> this.declarationAsString
            is ClassOrInterfaceDeclaration -> (if (this.isInterface) "Interface" else "Class") + " " + this.name.toString()
            is MethodDeclaration -> this.declarationAsString
            is EnumDeclaration -> "Enum " + this.name.toString()
            is FieldDeclaration -> this.variables.joinToString(separator = ", ") { it.name.id }
            is AnnotationDeclaration -> "Annotation $name"
            is EnumConstantDeclaration -> this.name.toString()
            is PackageDeclaration -> "Package $name"
            is CompilationUnit -> "Package $this"
            is MarkerAnnotationExpr -> "AnnotationMember $name"
//        is SimpleName -> if (parentNode.isEmpty) "" else parentNode.get().getSignature()
            else -> ""
        }

}


fun collectAllJavaDoc(startLocation: String): List<UnifiedComment> {
    val relPath = Paths.get(startLocation)
    return Files.walk(relPath).use {
        it.toList().filter { it.toString().endsWith(".java") }.map { path ->
            JavaDocParser(JavaParser.parse(path), relPath.relativize(path).toString()).parse()
        }.flatten()

    }
}