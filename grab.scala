import java.io._

import com.gargoylesoftware.htmlunit.BrowserVersion
import org.openqa.selenium.{By, WebDriver, WebElement}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

import scala.io.Source

object Application {

  val TIMEOUT: Int = 4000

  def main(args: Array[String]) {
    val driver: WebDriver = new HtmlUnitDriver(BrowserVersion.CHROME, true)

    val filename: String = "address.txt"
    val outFile: File = new File("data.csv")
    for (line <- Source.fromFile(filename).getLines()) {
      val zipCode = grab(driver, line)
      println(zipCode)
      println(line)
      val adr=zipCode._2
      val z1: String=adr.split(",").head;
      val z2= adr.stripSuffix(z1).trim;
      val z3=z2.replace("tänav", "tn")
      println(z3)

      if( z3 == line ){
        val finalZIP=zipCode._1;
        val p = new java.io.FileWriter(outFile, true)
        try { p.write(line + ";" + finalZIP + "\n") } finally { p.close() }
      }else{
        val finalZIP="";
        val p = new java.io.FileWriter(outFile, true)
        try { p.write(line + ";" + finalZIP + "\n") } finally { p.close() }
      }


    }

  }

  def grab(driver: WebDriver, query: String) = {
    driver.get("https://www.omniva.ee/eng")
    val wait: WebDriverWait = new WebDriverWait(driver, TIMEOUT)

    val tab: WebElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[.='Find a ZIP code'][@href='#search-zip']")))
    tab.click()

    val zipAddress: WebElement = driver.findElement(By.name("zip_address"))
    zipAddress.sendKeys(query)
    zipAddress.submit()

    wait.until(ExpectedConditions.elementToBeClickable(By.className("js_zip_search")))
    Thread.sleep(4000)

    if(driver.findElements(By.xpath("//*[@id='zip_container']/p")).size() > 0){
      val address: WebElement = driver.findElement(By.xpath("//*[@id='zip_container']/p"))
      println("...")
      println(address)
      println("...")

      val adr= address.getText().stripPrefix("Eesti Vabariik, ").trim
      val list: String=adr.split(",").last
      val adr2= adr.stripSuffix(list).trim
      val adr3=adr2.stripSuffix(",").trim

      val result: WebElement =driver.findElement(By.xpath("//*[@id='zip_container']/p/span"))
      println(result.getText())
      (result.getText(), adr3)


    }else{
      ("cat", "cat")
    }



  }

}
