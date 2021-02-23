package com.ram.delivery

import org.junit.Assert.assertEquals
import org.junit.Test

/**
1463 Question 1로 만들기 분류
시간 제한	메모리 제한	제출	정답	맞은 사람	정답 비율
0.15 초 (하단 참고)	128 MB	138354	42970	27390	31.883%
문제
정수 X에 사용할 수 있는 연산은 다음과 같이 세 가지 이다.

X가 3으로 나누어 떨어지면, 3으로 나눈다.
X가 2로 나누어 떨어지면, 2로 나눈다.
1을 뺀다.
정수 N이 주어졌을 때, 위와 같은 연산 세 개를 적절히 사용해서 1을 만들려고 한다. 연산을 사용하는 횟수의 최솟값을 출력하시오.

입력
첫째 줄에 1보다 크거나 같고, 106보다 작거나 같은 정수 N이 주어진다.

출력
첫째 줄에 연산을 하는 횟수의 최솟값을 출력한다.

예제 입력 1
2
예제 출력 1
1
예제 입력 2
10
예제 출력 2
3
힌트
10의 경우에 10 -> 9 -> 3 -> 1 로 3번 만에 만들 수 있다.
 */
class BackJun1463 {

    val SIZE = 107

    val memoryHeep: MutableList<Int> = MutableList(SIZE) { -1 }

    val a = 3
    val b = 2

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        memoryHeep[1] = 0
        memoryHeep[2] = 1
        memoryHeep[3] = 1
        val index = 106

        for (i in 4..index) {
            memoryHeep[i] = Math.min(
                memoryHeep[i - 1],
                memoryHeep[
                        when {
                            i % a == 0 -> i / a
                            i % b == 0 -> i / b
                            else -> i - 1
                        }
                ]
            ) + 1
        }

        memoryHeep.forEachIndexed { index, i ->
            pt("index: $index - $i")
        }


    }

    fun solv(index: Int, divNum: Int = a) {

        val quotient = index / divNum
        val remainder = index % divNum

        if (remainder == 0) {
            memoryHeep[index] = quotient
            return
        } else {
            for (i in 0..quotient) {
                val curQuo = quotient - i
                val mem = memoryHeep[remainder + (divNum * (quotient - curQuo))]

                if (mem == -1) continue

                memoryHeep[index] = curQuo + mem
                return
            }

            val mem = memoryHeep[remainder]
            if (mem == -1) {
                if (divNum != b) solv(index, b)
                return
            }

            memoryHeep[index] = quotient + mem
        }
    }
}