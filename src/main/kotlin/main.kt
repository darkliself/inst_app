import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.brunocvcunha.instagram4j.Instagram4j
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowingRequest
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

val USER_ACCESS_TOKEN = ""


fun main(args: Array<String>) {
    val instagram: Instagram4j = Instagram4j.builder().username("enteryourlogin").password("enteryourpass").build()
    instagram.setup()
    instagram.login()
    val myPK = "5534295711"


    // получение информации о польователе
    val usernameResult = instagram.sendRequest(InstagramSearchUsernameRequest("facetofacelol"))
    var actualFollowers: Int = usernameResult.user.getFollower_count()
    var previousFollowers: Int = 0
    var backSheet = mutableListOf<List<String>>()
    println(usernameResult.user.biography)
    println(usernameResult.user.username)
    println(usernameResult.user.getFollowing_count())
    println(usernameResult.user.getPk())

    val listOfFollowers = mutableListOf<List<String>>()
    val listOfFollowing = mutableListOf<List<String>>()

    val followersResult = instagram.sendRequest(
        InstagramGetUserFollowersRequest(usernameResult.user.getPk())
    )
    for (user in followersResult.getUsers()) {
        //println(user.username + " " + user.getPk())
        listOfFollowers.add(listOf(user.getPk().toString(), user.username, user.full_name))
    }
    val followingResult = instagram.sendRequest(
        InstagramGetUserFollowingRequest(usernameResult.user.getPk())
    )
    for (user in followingResult.getUsers()) {
        //println(user.username + " " + user.getPk())
        listOfFollowing.add(listOf(user.getPk().toString(), user.username, user.full_name))
    }
    if (File("./Followers.xlsx").isFile) {
        previousFollowers = readFromExcelFile("./Followers.xlsx")?.count() ?: 0

    } else {
        writeToFile("./Followers.xlsx", createSheet(listOfFollowers))
    }
    if (previousFollowers != 0 && previousFollowers != actualFollowers) {
        backSheet = readFromExcelFile("./Followers.xlsx")
        println(previousFollowers)
        writeToFile("./Followers.xlsx", createSheet(listOfFollowers))
        writeToFile("./Followers${java.time.LocalDate.now()}backup.xlsx", createSheet(backSheet))
    } else {
        writeToFile("./Followers.xlsx", createSheet(listOfFollowers))
    }
    writeToFile("./Following.xlsx", createSheet(listOfFollowing))
}

fun writeToFile(filepath: String, workBook: XSSFWorkbook) {
    workBook.write(FileOutputStream(filepath))
}

fun createSheet(rowsToWrite: MutableList<List<String>>): XSSFWorkbook {
    val myWorkBook = XSSFWorkbook()
    val myWorkList = myWorkBook.createSheet("seed_request")
    var row = 0
    myWorkList.createRow(0)
    rowsToWrite.forEach {  list ->
        myWorkList.createRow(row)
        list.forEachIndexed { index, s ->
            myWorkList.getRow(row).createCell(index).setCellValue(s)
        }
        row++
    }
    return myWorkBook
}

fun readFromExcelFile(filepath: String): MutableList<List<String>> {
    val listOfValues = mutableListOf<List<String>>()
    val inputStream = FileInputStream(filepath)
    //Instantiate Excel workbook using existing file:
    val xlWb = WorkbookFactory.create(inputStream)
    val xlWs = xlWb.getSheetAt(0)

    xlWs.forEach {
        //println(it)
    }
    println(xlWs.count())
    xlWs.forEach { row ->
        listOfValues.add(listOf(row.getCell(0).stringCellValue, row.getCell(1).stringCellValue, row.getCell(2).stringCellValue))
    }
    //print(xlWs.getRow(0))
    return listOfValues
}

