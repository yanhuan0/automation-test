package com.qatest.functional.util;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedCondition;
import com.qa.framework.webdriver.WebPage;

public class PageSyncUtil {

	public static void waitForElementGone(WebDriver driver, String by, String name, int waitTime) {
		WebDriverWait wait = new WebDriverWait(driver, waitTime / 1000);

		final String element = name;
		final String byType = by;

		try {
			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driverObject) {
					boolean gone = true;
					WebElement elem = null;
					try {
						if (byType.equalsIgnoreCase("xpath"))
							elem = driverObject.findElement(By.xpath(element));
						if (elem != null)
							gone = false;
						else
							gone = false;
					} catch (Exception ex) {
						gone = false;
					}
					return !gone;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean waitForText(WebDriver driver, String xpath, String expectedText, int waitTime) {
		final String elemXpath = xpath;
		final String expText = expectedText.trim().replace("\n", "").replace("\r", "");

		WebDriverWait wait = new WebDriverWait(driver, waitTime / 1000);

		System.out.println("====Begin: waitForText");

		try {
			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driverObject) {
					System.out.println("====Waiting");

					WebElement elem = null;
					try {
						elem = driverObject.findElement(By.xpath(elemXpath));
					} catch (Exception ex) {
						System.out.println("====No element");
					}

					if (elem != null) {
						String actText = elem.getText().replace("\n", "").replace("\r", "");
						if (actText.equalsIgnoreCase(expText)) {
							System.out.println("====Got expected text");
						}
						return (Boolean) (actText.equalsIgnoreCase(expText));

					} else {
						System.out.println("====No expected element");
						return false;
					}
				}
			});
			Thread.sleep(1000);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("====End: waitForText");
		return false;
	}

	public static boolean waitForMatchedText(WebDriver driver, String xpath, String expectedMatchedText, int waitTime) {
		final String elemXpath = xpath;
		final String expText = expectedMatchedText;

		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		System.out.println("====Begin: waitForMatchedText");

		try {
			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driverObject) {
					System.out.println("====Waiting");
					WebElement elem = null;
					try {
						elem = driverObject.findElement(By.xpath(elemXpath));
					} catch (Exception ex) {
						System.out.println("====No element");
					}

					if (elem != null) {
						String actText = elem.getText();
						if (actText.indexOf(expText) > 0) {
							System.out.println("====Found matched text:");
							System.out.println(expText);
							return true;

						}
						return false;
					} else {
						System.out.println("====No expected element");
						return false;
					}
				}
			});
			Thread.sleep(1000);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("===End: waitForMatchedText");
		return false;
	}

	public static boolean waitForElementXpathGone(WebDriver driver, int waitTime, final String elementXpath) {
		WebDriverWait wait = new WebDriverWait(driver, waitTime / 1000);
		StringBuilder sb = new StringBuilder();
		WebPage webPage = new WebPage();
		try {
			webPage.waitForObject("xpath=" + elementXpath, QAConstants.SMALL_TIME);
		} catch (Exception ex) {

		}
		try {
			System.out.println("====Begin: waitForElementGone");

			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driverObject) {
					boolean processFlag = true;

					WebElement element = null;
					try {
						element = driverObject.findElement(By.xpath(elementXpath));
					} catch (Exception ex) {
						System.out.println("====No element");
					}
					System.out.println("====Waiting for the element gone");
					if (element != null) {
						processFlag = false;
						System.out.println("====The element is showing");
						return false;
					} else {
						processFlag = true;
						System.out.println("====The element is gone");
						return true;
					}

				}
			});
			Thread.sleep(2000);// Add sleep here to solve JET delay issue
		} catch (Exception e) {
			System.out.println("====No element");
			return true;
		}
		System.out.println("====End: waitForElementGone");
		return true;
	}

	public static boolean waitForProcessBar(WebDriver driver, int waitTime) {
		WebDriverWait wait = new WebDriverWait(driver, waitTime / 1000);
		StringBuilder sb = new StringBuilder();
		WebPage webPage = new WebPage();

		final String xpathProgressPane = "xpath=//div[contains(@id,'pane:contentpane')]//div[@class='bi_progressPaneMsg_mini_top']";
		final String xpathProgressPaneItem = "//div[contains(@id,'pane:contentpane') and @style='display: none;']//div[@class='bi_progressPaneMsg_mini_top']";

		try {
			// Determine whether process bar exists and ignore it is displayed or not,
			// return true if the page does not has processbar
			System.out.println("====Begin: waitForProcessBar");
			if (!webPage.waitForObject(xpathProgressPane, QAConstants.MINI_TIME)) {
				System.out.println("====No Processing bar");
				return true;
			}

			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driverObject) {
					boolean processFlag = true;
					WebElement element = null;
					try {
						element = driverObject.findElement(By.xpath(xpathProgressPaneItem));
					} catch (Exception ex) {
						System.out.println("====No element");
					}

					System.out.println("====Waiting for processing bar to be disappeared");
					if (element != null) {
						processFlag = true;
						System.out.println("====Processing bar is disappeared");
					} else {
						processFlag = false;
						return false;
					}
					return processFlag;
				}
			});
			Thread.sleep(2000);// Add sleep here to solve JET delay issue
		} catch (Exception e) {
			System.out.println("====No Processing bar");
			// System.out.println(e);
			// e.printStackTrace();
		}
		System.out.println("====End: waitForProcessBar");
		return true;
	}

	/**
	 * Wait the alert message gone.
	 * 
	 * @param driver
	 * @param waitTime
	 * @return
	 * @author liaochunxiang
	 */
	public static boolean waitForAlertGone(WebDriver driver, int waitTime) {
		String alertXpath = "//div[@role='alert']";
		boolean status = waitForElementXpathGone(driver, waitTime, alertXpath);
		return status;
	}

}
