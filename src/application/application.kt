package application


object Application {

    val processors = mutableListOf<IElementProcessor<String, UnifiedComment>>(
        TagRemovalTransformer(),
        TokenToLowerCaseTransformer(),
        StopWordsRemovalProcessor(),
        MinimalTokenLengthTransformer()
    )

    val nlpPipelineProcessor = StanfordNlpPipelineProcessor(processors)

    private fun parseDir(absFilePath: String) = collectAllJavaDoc(absFilePath)

    private fun processCommentsViaPipeline(comments: List<UnifiedComment>) =
        nlpPipelineProcessor.process(comments)


    @JvmStatic
    fun main(args: Array<String>) {

        TODO()
    }


    private fun constructJSONReport() {
        TODO()
    }
}