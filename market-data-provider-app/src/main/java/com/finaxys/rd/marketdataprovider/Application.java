/*
 * 
 */
package com.finaxys.rd.marketdataprovider;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class Application.
 */
@Configuration
public class Application {

	/** The rabbit host. */
	@Value("${rabbitmq.host}")
	private String rabbitHost;

	/**
	 * Connection.
	 *
	 * @return the connection
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Bean
	Connection connection() throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(rabbitHost);
		return factory.newConnection();
	}
	
	/**
	 * Channel.
	 *
	 * @return the channel
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Bean
	@Scope("prototype")
	Channel channel() throws IOException{
		return connection().createChannel();
	}

	/**
	 * H connection.
	 *
	 * @return the h connection
	 * @throws ZooKeeperConnectionException the zoo keeper connection exception
	 */
	@Bean
	HConnection hConnection() throws ZooKeeperConnectionException {
		return HConnectionManager.createConnection(HBaseConfiguration.create());
	}

	/**
	 * Client.
	 *
	 * @return the closeable http async client
	 * @throws IOReactorException the IO reactor exception
	 */
	@Bean
	CloseableHttpAsyncClient client() throws IOReactorException {
		// return HttpAsyncClients.createDefault();
		ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
		PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor);
		cm.setMaxTotal(100);
		return HttpAsyncClients.custom().setConnectionManager(cm).build();

	}


	public static void main(String[] args)   {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/spring-config.xml");

	}

}
