package com.queen.manager.consumer;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.queen.pojo.entity.Book;

@RestController
public class ConsumerBookController {
	 @Autowired
	    RestTemplate restTemplate;
	 
	 @Autowired
    private HelloService helloService;
	 
    @RequestMapping(value = "/ribbon-consumer",method = RequestMethod.GET)
    public String helloController() {
        return helloService.hello();
    }
	 
	 /**
	 * getForEntity方法的返回值是一个ResponseEntity<T>，ResponseEntity<T>是Spring对HTTP请求响应的封装，包括了几个重要的元素，如响应码、contentType、contentLength、响应消息体等
	 * 案例地址： https://github.com/lenve/SimpleSpringCloud/tree/master/RestTemplate
	 * @return
	 */
	@RequestMapping("/gethello")
	public String getHello() {
	    ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class);
	    String body = responseEntity.getBody();
	    HttpStatus statusCode = responseEntity.getStatusCode();
	    int statusCodeValue = responseEntity.getStatusCodeValue();
	    HttpHeaders headers = responseEntity.getHeaders();
	    StringBuffer result = new StringBuffer();
	    result.append("responseEntity.getBody()：").append(body).append("<hr>")
	            .append("responseEntity.getStatusCode()：").append(statusCode).append("<hr>")
	            .append("responseEntity.getStatusCodeValue()：").append(statusCodeValue).append("<hr>")
	            .append("responseEntity.getHeaders()：").append(headers).append("<hr>");
	    return result.toString();
	}
	
	/**
	 * 有时候我在调用服务提供者提供的接口时，可能需要传递参数，有两种不同的方式，如下
	 * @return
	 */
	@RequestMapping("/sayhello")
	public String sayHello() {
	    ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/sayhello?name={1}", String.class, "张三");
	    return responseEntity.getBody();
	}
	@RequestMapping("/sayhello2")
	public String sayHello2() {
	    Map<String, String> map = new HashMap<>();
	    map.put("name", "李四");
	    ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/sayhello?name={name}", String.class, map);
	    return responseEntity.getBody();
	}
	
	/**
	 * 第一个调用地址也可以是一个URI而不是字符串，这个时候我们构建一个URI即可，参数神马的都包含在URI中了
	 * @return
	 */
	@RequestMapping("/sayhello3")
	public String sayHello3() {
	    UriComponents uriComponents = UriComponentsBuilder.fromUriString("http://HELLO-SERVICE/sayhello?name={name}").build().expand("王五").encode();
	    URI uri = uriComponents.toUri();
	    ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
	    return responseEntity.getBody();
	}
	
	/**
	 * 服务提供者不仅可以返回String，也可以返回一个自定义类型的对象,在服务消费者中通过如下方式来调用
	 */
	@RequestMapping("/book1")
	public Book book1() {
	    ResponseEntity<Book> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/getbook1", Book.class);
	    return responseEntity.getBody();
	}
	
	/**
	 * getForObject函数实际上是对getForEntity函数的进一步封装，如果你只关注返回的消息体的内容，对其他信息都不关注，此时可以使用getForObject
	 */
	@RequestMapping("/book2")
	public Book book2() {
	    Book book = restTemplate.getForObject("http://HELLO-SERVICE/getbook1", Book.class);
	    return book;
	}
	
	/**
	 * 在RestTemplate中，POST请求可以通过如下三个方法来发起:postForEntity、postForObject、postForLocation
	 */
	@RequestMapping("/book3")
	public Book book3() {
	    Book book = new Book();
	    book.setName("红楼梦");
	    ResponseEntity<Book> responseEntity = restTemplate.postForEntity("http://HELLO-SERVICE/getbook2", book, Book.class);
	    return responseEntity.getBody();
	}
	
	/**
	 * 在RestTemplate中，PUT请求可以通过put方法调用，put方法的参数和前面介绍的postForEntity方法的参数基本一致，只是put方法没有返回值而已
	 */
	@RequestMapping("/put")
	public void put() {
	    Book book = new Book();
	    book.setName("红楼梦");
	    restTemplate.put("http://HELLO-SERVICE/getbook3/{1}", book, 99);
	}
	
	/**
	 * delete请求我们可以通过delete方法调用来实现
	 */
	@RequestMapping("/delete")
	public void delete() {
	    restTemplate.delete("http://HELLO-SERVICE/getbook4/{1}", 100);
	}
}
