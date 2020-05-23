package application


interface IElementProcessor<T, P> {
    fun process(elemsList: List<List<Element<T, P>>>): List<List<Element<T, P>>>
}


/**
 * Stanford stop words in LOWER CASE
 */
val stanfordStopWords = hashSetOf(
    "!!",
    "?!",
    "??",
    "!?",
    "`",
    "``",
    "''",
    "-lrb-",
    "-rrb-",
    "-lsb-",
    "-rsb-",
    ",",
    ".",
    ":",
    ";",
    "\"",
    "'",
    "?",
    "<",
    ">",
    "{",
    "}",
    "[",
    "]",
    "+",
    "-",
    "(",
    ")",
    "&",
    "%",
    "$",
    "@",
    "!",
    "^",
    "#",
    "*",
    "..",
    "...",
    "'ll,",
    "'s",
    "'m",
    "a",
    "about",
    "above",
    "after",
    "again",
    "against",
    "all",
    "am",
    "an",
    "and",
    "any",
    "are",
    "aren't",
    "as",
    "at",
    "be",
    "because",
    "been",
    "before",
    "being",
    "below",
    "between",
    "both",
    "but",
    "by",
    "can", "can't",
    "cannot",
    "could",
    "couldn't",
    "did",
    "didn't",
    "do",
    "does",
    "doesn't",
    "doing"
    , "don't"
    , "down"
    , "during"
    , "each"
    , "few"
    , "for"
    , "from"
    , "further"
    , "had"
    , "hadn't"
    , "has"
    , "hasn't"
    , "have"
    , "haven't"
    , "having"
    , "he"
    , "he'd"
    , "he'll"
    , "he's"
    , "her"
    , "here"
    , "here's"
    , "hers"
    , "herself"
    , "him"
    , "himself"
    , "his"
    , "how"
    , "how's"
    , "i"
    , "i'd"
    , "i'll"
    , "i'm"
    , "i've"
    , "if"
    , "in"
    , "into"
    , "is"
    , "isn't"
    , "it",
    "it's"
    , "its"
    , "itself"
    , "let's"
    , "me"
    , "more"
    , "most"
    , "mustn't"
    , "my"
    , "myself"
    , "no"
    , "nor"
    , "not"
    , "of"
    , "off"
    , "on"
    , "once"
    , "only"
    , "or"
    , "other"
    , "ought"
    , "our"
    , "ours"
    , "ourselves"
    , "out"
    , "over"
    , "own"
    , "same"
    , "shan't"
    , "she"
    , "she'd"
    , "she'll"
    , "she's"
    , "should"
    , "shouldn't"
    , "so"
    , "some"
    , "such"
    , "than"
    , "that"
    , "that's"
    , "the"
    , "their"
    , "theirs"
    , "them"
    , "themselves"
    , "then"
    , "there",
    "there's",
    "these",
    "they",
    "they'd",
    "they'll",
    "they're",
    "they've",
    "this",
    "those",
    "through",
    "to",
    "too",
    "under",
    "until",
    "up",
    "very",
    "was",
    "wasn't",
    "we",
    "we'd",
    "we'll",
    "we're",
    "we've",
    "were",
    "weren't",
    "what",
    "what's",
    "when",
    "when's",
    "where",
    "where's",
    "which",
    "while",
    "who",
    "who's",
    "whom",
    "why",
    "why's",
    "with",
    "won't",
    "would",
    "wouldn't",
    "you",
    "you'd",
    "you'll",
    "you're",
    "you've",
    "your",
    "yours",
    "yourself",
    "yourselves",
    "###",
    "return", //TODO maybe comment
    "arent",
    "cant",
    "couldnt",
    "didnt",
    "doesnt",
    "dont",
    "hadnt",
    "hasnt",
    "havent",
    "hes",
    "heres",
    "hows",
    "im",
    "isnt",
    "its",
    "lets",
    "mustnt",
    "shant",
    "shes",
    "shouldnt",
    "thats",
    "theres",
    "theyll",
    "theyre",
    "theyve",
    "wasnt",
    "were",
    "werent",
    "whats",
    "whens",
    "wheres",
    "whos",
    "whys",
    "wont",
    "wouldnt",
    "youd",
    "youll",
    "youre",
    "youve"
)


val delimiters = hashSetOf(
    ",",
    ".",
    "....",
    "...",
    ":",
    "!!!",
    "!!",
    "!",
    "!?",
    "?!",
    "?",
    "???",
    ";",
    "-",
    "---",
    "--",
    "#",
    "//",
    "\\\\"
)


/**
 * Removes stop words from elemList
 */
class StopWordsRemovalProcessor<P>(val words: HashSet<String> = stanfordStopWords) : IElementProcessor<String, P> {
    override fun process(elemsList: List<List<Element<String, P>>>): List<List<Element<String, P>>> =
        elemsList.map { sentence -> sentence.filter { !words.contains(it.elem) } }.filter { it.isNotEmpty() }
}


/**
 * Removes all Delimiters
 */
class DelimiterRemovalProcessor<P>(val delimitersWords: HashSet<String> = delimiters) : IElementProcessor<String, P> {
    override fun process(elemsList: List<List<Element<String, P>>>): List<List<Element<String, P>>> =
        elemsList
            .map { sentence -> sentence.filter { !delimitersWords.contains(it.elem) } }
            .filter { it.isNotEmpty() }
}


/**
 * Removes tags  and other  <code> ... </code> and ...
 * See https://checkstyle.sourceforge.io/config_javadoc.html#JavadocStyle
 * and https://stackoverflow.com/questions/16481230/allowed-html-tags-in-javadoc
 * TODO NOt implemented
 */
class TagRemovalTransformer<P>() : IElementProcessor<String, P> {
    override fun process(elemsList: List<List<Element<String, P>>>): List<List<Element<String, P>>> = elemsList

}

/**
 * Filter for minimal length
 */
class MinimalTokenLengthTransformer<T, P>(val minLength: Int = 4) : IElementProcessor<T, P> {
    override fun process(elemsList: List<List<Element<T, P>>>): List<List<Element<T, P>>> =
        elemsList.filter { it.size >= minLength }
}

/**
 * To lower case
 */
class TokenToLowerCaseTransformer<P> : IElementProcessor<String, P> {
    override fun process(elemsList: List<List<Element<String, P>>>): List<List<Element<String, P>>> =
        elemsList.map { sentence ->
            sentence.map {
                it.elem = it.elem.toLowerCase()
                it
            }
        }
}