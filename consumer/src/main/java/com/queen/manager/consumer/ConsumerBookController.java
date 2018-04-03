package com.queen.manager.consumer;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.queen.manager.hystrix.BookCommand;

@RestController
public class ConsumerBookController {
	 @Autowired
	    RestTemplate restTemplate;
	 
	 @Autowired
    private HelloService helloService;
	
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
	
	/**
	 * 断路由
	 */
    @RequestMapping(value = "/ribbon-consumer",method = RequestMethod.GET)
    public String helloController() {
        return helloService.hello();
    }
	
	/**
	 * 同步调用和异步调用
	 * 当BookCommand创建成功之后，我们就可以在我们的Controller中调用它了
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@RequestMapping("/test1")
	public Book test1() throws ExecutionException, InterruptedException {
	    BookCommand bookCommand = new BookCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("")), restTemplate);
	    //同步调用
	    //Book book1 = bookCommand.execute();
	    //异步调用 异步请求中我们需要通过get方法来获取请求结果，在调用get方法的时候也可以传入超时时长。
	    Future<Book> queue = bookCommand.queue();
	    Book book = queue.get();
	    return book;
	}
	
	//http://HELLO-SERVICE/getbook5/{1}
	@RequestMapping("/test5")
	public Book test5() {
	    HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("commandKey");
	    HystrixRequestContext.initializeContext();
	    BookCommand bc1 = new BookCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("")).andCommandKey(commandKey), restTemplate, 1l);
	    Book e1 = bc1.execute();
	    //如果我将服务提供者的数据修改了，那么缓存的数据就应该被清除，否则用户在读取的时候就有可能获取到一个错误的数据，缓存数据的清除也很容易，也是根据id来清除
	    //HystrixRequestCache.getInstance(commandKey, HystrixConcurrencyStrategyDefault.getInstance()).clear(String.valueOf(1l));
	    BookCommand bc2 = new BookCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("")).andCommandKey(commandKey), restTemplate, 1l);
	    Book e2 = bc2.execute();
	    BookCommand bc3 = new BookCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("")).andCommandKey(commandKey), restTemplate, 1l);
	    Book e3 = bc3.execute();
	    System.out.println("e1:" + e1);
	    System.out.println("e2:" + e2);
	    System.out.println("e3:" + e3);
	    return e1;
	}
	
	@RequestMapping("/test6")
	public Book test6() {
	    HystrixRequestContext.initializeContext();
	    //第一次发起请求
	    Book b1 = helloService.test6(2, "");
	    //参数和上次一致，使用缓存数据
	    Book b2 = helloService.test6(2, "");
	    //参数不一致，发起新请求
	    Book b3 = helloService.test6(2, "aa");
	    return b1;
	}
	
	@RequestMapping("/test6")
	public Book test7() {
	    HystrixRequestContext.initializeContext();
	    //第一次发起请求
	    Book b1 = helloService.test6(2);
	    //清除缓存
	    helloService.test7(2);
	    //缓存被清除，重新发起请求
	    Book b2 = helloService.test6(2);
	    //参数一致，使用缓存数据
	    Book b3 = helloService.test6(2);
	    return b1;
	}
	
}
