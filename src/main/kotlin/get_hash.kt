fun main() {
    getHash("zxcvbvcb")
    getHash("bbb")
    getHash("abc")
}


fun getHash (str: String) {
    var ans = 0
    for (i in 0 until str.count()) {
        ans += (str[i] - 'a')
    }
    println(ans)
}