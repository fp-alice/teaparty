package wtf.shekels.alice.teaparty.client

import java.util.concurrent.TimeUnit

import okhttp3.{OkHttpClient, Request, WebSocket, WebSocketListener}
import okio.ByteString
import scodec.bits.BitVector
import wtf.shekels.alice.teaparty.common.messages.{Incoming, Outgoing}

class TeaPartyClient(
    user: String,
    address: String,
    callback: Outgoing => Unit,
) extends WebSocketListener {

  private var client: OkHttpClient = _
  private var webSocket: WebSocket = _

  def init(): Unit = {
    client = new OkHttpClient.Builder()
      .readTimeout(1, TimeUnit.MINUTES)
      .build()
    val request = new Request.Builder()
      .url(s"ws://$address/ws/$user")
      .build()
    webSocket = client.newWebSocket(request, this)
  }

  override def onMessage(webSocket: WebSocket, bytes: ByteString): Unit = {
    val bitVector: BitVector = BitVector(bytes.asByteBuffer())
    val out: Outgoing        = Outgoing.codec.decode(bitVector).require.value
    callback(out)
  }

  override def onClosing(webSocket: WebSocket, code: Int, reason: String): Unit = {
    webSocket.close(code, null)
    client.dispatcher().executorService().shutdown()
  }

  def sendMessage(incoming: Incoming): Unit = {
    webSocket.send(new ByteString(Incoming.codec.encode(incoming).require.toByteArray))
  }
}

object TeaPartyClient {
  def apply(user: String, address: String, callback: Outgoing => Unit): TeaPartyClient = {
    val tpc = new TeaPartyClient(user, address, callback)
    tpc.init()
    tpc
  }
}
