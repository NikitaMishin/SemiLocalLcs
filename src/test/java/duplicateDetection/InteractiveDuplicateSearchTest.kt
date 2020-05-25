package duplicateDetection

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

internal class InteractiveDuplicateSearchTest {

    @Test
    fun equals() {
        val fast = InteractiveDuplicateSearchViaSemiLocal<Int>(0.7)
        val slow = InteractiveDuplicateSearchViaSemiLocal<Int>(0.7)

        val random = Random(42)
        for (i in 0 until 1000){
            val alphabetSize = random.nextInt(2,5)
            val p = 25
            val t = 1000
            val sequenceP =
                    (0 until p).toList().map { kotlin.math.abs(random.nextInt()) % alphabetSize }.toMutableList()
            val sequenceT =
                    (0 until t).toList().map { kotlin.math.abs(random.nextInt()) % alphabetSize }.toMutableList()
            val f = fast.find(sequenceP,sequenceT)
            val s = slow.find(sequenceP,sequenceT)
            if(!f.containsAll(s)){
                assertTrue(false)
            } else{
                println("size=${f.size}")
            }
        }


    }
}