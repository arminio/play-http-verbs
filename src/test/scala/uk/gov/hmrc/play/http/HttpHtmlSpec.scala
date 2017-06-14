/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play.http

import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpecLike}
import play.api.libs.json.Writes
import play.twirl.api.Html
import uk.gov.hmrc.play.http.hooks.HttpHook
import uk.gov.hmrc.play.http.ws.HtmlHttpReads

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HttpHtmlSpec extends WordSpecLike with Matchers with MockitoSugar with CommonHttpBehaviour {

  "HttpDelete" should {
    class StubbedHttpDelete(response: Future[HttpResponse]) extends HttpDelete with ConnectionTracingCapturing {
      val testHook1 = mock[HttpHook]
      val testHook2 = mock[HttpHook]
      val hooks = Seq(testHook1, testHook2)

      def appName: String = ???
      def doDelete(url: String)(implicit hc: HeaderCarrier) = response
    }

    "be able to return HTML responses" in new HtmlHttpReads {
      val testDelete = new StubbedHttpDelete(Future.successful(new DummyHttpResponse(testBody, 200)))
      testDelete.DELETE(url).futureValue should be (an [Html])
    }
  }

  "HttpGet" should {

    class StubbedHttpGet(doGetResult: Future[HttpResponse] = defaultHttpResponse) extends HttpGet with ConnectionTracingCapturing {
      val testHook1 = mock[HttpHook]
      val testHook2 = mock[HttpHook]
      val hooks = Seq(testHook1, testHook2)

      override def doGet(url: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = doGetResult
    }

    "be able to return HTML responses" in new HtmlHttpReads {
      val testGet = new StubbedHttpGet(Future.successful(new DummyHttpResponse(testBody, 200)))
      testGet.GET(url).futureValue should be(an[Html])
    }
  }

  "HttpPatch" should {
    class StubbedHttpPatch(doPatchResult: Future[HttpResponse]) extends HttpPatch with ConnectionTracingCapturing with MockitoSugar {
      val testHook1 = mock[HttpHook]
      val testHook2 = mock[HttpHook]
      val hooks = Seq(testHook1, testHook2)

      def doPatch[A](url: String, body: A)(implicit rds: Writes[A], hc: HeaderCarrier)= doPatchResult
    }

    val testObject = TestRequestClass("a", 1)
    
    "be able to return HTML responses" in new HtmlHttpReads {
      val testPatch = new StubbedHttpPatch(Future.successful(new DummyHttpResponse(testBody, 200)))
      testPatch.PATCH(url, testObject).futureValue should be(an[Html])
    }
  }

  "HttpPost.POST" should {
    val testObject = TestRequestClass("a", 1)

    class StubbedHttpPost(doPostResult: Future[HttpResponse]) extends HttpPost with MockitoSugar with ConnectionTracingCapturing {
      val testHook1 = mock[HttpHook]
      val testHook2 = mock[HttpHook]
      val hooks = Seq(testHook1, testHook2)

      def doPost[A](url: String, body: A, headers: Seq[(String,String)])(implicit rds: Writes[A], hc: HeaderCarrier) = doPostResult
      def doFormPost(url: String, body: Map[String, Seq[String]])(implicit hc: HeaderCarrier) = doPostResult
      def doPostString(url: String, body: String, headers: Seq[(String, String)])(implicit hc: HeaderCarrier) = doPostResult
      def doEmptyPost[A](url: String)(implicit hc: HeaderCarrier) = doPostResult
    }

    "be able to return HTML responses for POST" in new HtmlHttpReads {
      val testPOST = new StubbedHttpPost(Future.successful(new DummyHttpResponse(testBody, 200)))
      testPOST.POST(url, testObject).futureValue should be(an[Html])
    }

    "be able to return HTML responses for POSTForm" in new HtmlHttpReads {
      val testPOST = new StubbedHttpPost(Future.successful(new DummyHttpResponse(testBody, 200)))
      testPOST.POSTForm(url, Map()).futureValue should be (an [Html])
    }

    "be able to return HTML responses for PostString" in new HtmlHttpReads {
      val testPOST = new StubbedHttpPost(Future.successful(new DummyHttpResponse(testBody, 200)))
      testPOST.POSTString(url, testRequestBody).futureValue should be (an [Html])
    }

    "be able to return HTML responses for postEmtpy" in new HtmlHttpReads {
      val testPOST = new StubbedHttpPost(Future.successful(new DummyHttpResponse(testBody, 200)))
      testPOST.POSTEmpty(url).futureValue should be (an [Html])
    }

  }


}
