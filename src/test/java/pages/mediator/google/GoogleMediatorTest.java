package pages.mediator.google;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.colleague.google.GoogleColleague;
import support.GenericUtils;
import support.IPowerMockRun;
import support.ProcessBuilderUtils;
import support.js.TagBean;

import javax.swing.text.html.HTML;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static support.js.JsUtils.addElement;

@PrepareForTest(GoogleMediator.class)
public class GoogleMediatorTest implements IPowerMockRun {
	private static Logger LOG = LoggerFactory.getLogger(GoogleMediatorTest.class);
	@SuppressWarnings("ConstantConditions")
	private static String TEST_PAGE = GoogleMediatorTest.class.getClassLoader().getResource("testPage.html").toString();
	private WebDriver dri;
	private GoogleMediator mediMock;

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ProcessBuilderUtils.killProcessIe();
	}

	@Before
	public void setUp() throws Exception {
		//for @Spy
		//MockitoAnnotations.initMocks(this);

		mediMock = PowerMockito.spy(new GoogleMediator());
		PowerMockito.doReturn(TEST_PAGE).when(mediMock, "initializeUrl");

		dri = GenericUtils.getWebDriverList().get(0);
		mediMock.initialize(dri);

		//private取得
		//WebDriver dri = (WebDriver) Whitebox.getInternalState(mediMock, "driver");
	}

	@After
	public void tearDown() throws Exception {
		Thread.sleep(2000);
		dri.quit();
	}

	/**
	 * 1件取得
	 */
	@Ignore
	@Test
	public void testFindElement001() {
		LOG.info("start");

		final HTML.Tag TAG = HTML.Tag.DIV;
		final String ID1 = "id1";
		final String INNER1 = "inner1";

		//test用element初期化
		{
			TagBean bean1 = new TagBean(TAG);
			bean1.putAttributeMap(HTML.Attribute.ID, ID1);
			bean1.setInnerHTML(INNER1);

			mediMock.exeJs(addElement(bean1));
		}

		GoogleColleague collMock = spy(mediMock.getColleague(GoogleColleague.class));
		doReturn(mediMock.findElement(By.xpath("//" + TAG + "[@id='" + ID1 + "']"))).when(collMock).aGmail();
		WebElement ida = collMock.aGmail();
		String text = ida.getAttribute("innerHTML");

		assertThat(INNER1, is(text));

		LOG.info("end");
	}

	/**
	 * 2件存在NoSuchElementExceptionエラー
	 */
	@Test
	public void testFindElement002() {
		LOG.info("start");

		final HTML.Tag TAG = HTML.Tag.INPUT;
		final String TAG_TYPE = "text";
		final String NAME1 = "name1";
		final String VALUE1 = "text1";
		final String VALUE2 = "text2";

		//test用element初期化
		{
			TagBean bean1 = new TagBean(TAG);
			bean1.putAttributeMap(HTML.Attribute.TYPE, TAG_TYPE);
			bean1.putAttributeMap(HTML.Attribute.NAME, NAME1);
			bean1.putAttributeMap(HTML.Attribute.VALUE, VALUE1);

			TagBean bean2 = new TagBean(TAG);
			bean2.putAttributeMap(HTML.Attribute.TYPE, TAG_TYPE);
			bean2.putAttributeMap(HTML.Attribute.NAME, NAME1);
			bean2.putAttributeMap(HTML.Attribute.VALUE, VALUE2);

			mediMock.exeJs(addElement(bean1, bean2));
		}

		try {
			mediMock.findElement(By.xpath("//" + TAG + "[@name='" + NAME1 + "']"));
			fail();
		} catch (Exception e) {
			LOG.info(e.getMessage());
			assertThat(e, is(instanceOf(NoSuchElementException.class)));
			assertThat(e.getMessage().startsWith("element複数件エラー"), is(true));
		}

		LOG.info("end");
	}
}