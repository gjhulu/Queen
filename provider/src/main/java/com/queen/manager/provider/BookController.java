package com.queen.manager.provider;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.queen.pojo.entity.Book;

@RestController
public class BookController {
	 private final Logger logger = Logger.getLogger(getClass());
	    @Autowired
	    private DiscoveryClient client;

	    @RequestMapping(value = "/hello", method = RequestMethod.GET)
	    public String hello() {
	        List<ServiceInstance> instances = client.getInstances("hello-service");
	        for (int i = 0; i < instances.size(); i++) {
	            ServiceInstance instance = instances.get(i);
	            logger.info(">>>>>>>>>>/hello,host:" + instance.getHost() + ",serivce_id:" + instance.getServiceId());
	        }
	        return "hello";
	    }

	    @RequestMapping(value = "/sayhello", method = RequestMethod.GET)
	    public String sayHello(String name) {
	        return "hello " + name;
	    }
	    
	    /**
		 * 服务提供者不仅可以返回String，也可以返回一个自定义类型的对象
		 */
	    @RequestMapping(value = "/getbook1", method = RequestMethod.GET)
	    public Book book1() {
	        return new Book("三国演义", 90, "罗贯中", "花城出版社");
	    }

	    @RequestMapping(value = "/getbook2", method = RequestMethod.POST)
	    public Book book2(@RequestBody Book book) {
	        System.out.println(book.getName());
	        book.setPrice(33);
	        book.setAuthor("曹雪芹");
	        book.setPublisher("人民文学出版社");
	        return book;
	    }

	    @RequestMapping(value = "/getbook3/{id}", method = RequestMethod.PUT)
	    public void book3(@RequestBody Book book, @PathVariable int id) {
	        logger.info(">>>>>>>>>>book:" + book);
	        logger.info(">>>>>>>>>>id:" + id);
	    }

	    @RequestMapping(value = "/getbook4/{id}", method = RequestMethod.DELETE)
	    public void book4(@PathVariable int id) {
	        logger.info(">>>>>>>>>>id:" + id);
	    }
}
