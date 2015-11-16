import java.io._

import com.gargoylesoftware.htmlunit.BrowserVersion
import org.openqa.selenium.{By, WebDriver, WebElement}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

import scala.io.Source


object Application {

  val TIMEOUT: Int = 1000

  def main(args: Array[String]) {
    val driver: WebDriver = new HtmlUnitDriver(BrowserVersion.CHROME, true)

    val filename: String = "address.txt"
    val outFile: File = new File("data.csv")
    for (line <- Source.fromFile(filename).getLines()) {
      val zipCode: String = grab(driver, line)
      val p = new java.io.FileWriter(outFile, true)
      try { p.write(line + ";" + zipCode + "\n") } finally { p.close() }
    }

  }

  def grab(driver: WebDriver, query: String): String = {
    driver.get("https://www.omniva.ee/eng")
    val wait: WebDriverWait = new WebDriverWait(driver, TIMEOUT)

    val tab: WebElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[.='Find a ZIP code'][@href='#search-zip']")))
    tab.click

    val zipAddress: WebElement = driver.findElement(By.name("zip_address"))
    zipAddress.sendKeys(query)
    zipAddress.submit

    val result: WebElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='zip_container']/p/span")))
    result.getText
  }

}