/* vim:set ts=4:sw=4:et:sts=4:ai:tw=80
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.sun.net.httpserver.*
import java.util.concurrent.Executors

/**
 * Simple HTTP server for namerepo.repo downloads
 *
 *
 * @author Javi Roman <javiroman@redoop.org>
 *
 */
class EmbeddedHTTPServer {
	def BDROOT
	def LOG
	def globalConfig

	def EmbeddedHTTPServer(buildoop) {
		LOG = buildoop.log
		BDROOT = buildoop.ROOT
		globalConfig = buildoop.globalConfig
        LOG.info "[EmbeddedHTTPServer] constructor"

		def HTTP_SERVER_PORT=8080
		println "Create server port " + HTTP_SERVER_PORT
		def server = HttpServer.create(new InetSocketAddress(HTTP_SERVER_PORT),0);

		server.createContext("/", new RepoHandler(server:server));
		server.setExecutor(Executors.newCachedThreadPool())
		println "Starting server"
		server.start();
		println "Server Started"
		//exchange.close();
		//server.stop(3) //max wait 3 second
	}

class RepoHandler implements HttpHandler {
    def server

    public void handle(HttpExchange exchange) throws IOException {
		println "getRequestMethod:"
		println exchange.getRequestMethod() 
		println "getRequestHeaders:"
		println exchange.getRequestHeaders()
		println "getRequestURI:"
		def fileName = exchange.getRequestURI()
		println fileName 

		def file = new File("." + fileName)
		def bytearray  = new byte [(int)file.length()]
		def fis = new FileInputStream(file)
		def bis = new BufferedInputStream(fis)
		bis.read(bytearray, 0, bytearray.length)

		// ok, we are ready to send the response.
		exchange.sendResponseHeaders(200, file.length())
		def os = exchange.getResponseBody()
		os.write(bytearray,0,bytearray.length)
		os.close()
	}
}

