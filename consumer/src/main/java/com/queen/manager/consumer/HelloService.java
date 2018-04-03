package com.queen.manager.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;

@Service
public class HelloService {
    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "error")
    public String hello() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class);
        return responseEntity.getBody();
    }

    public String error() {
        return "error";
    }
    
    /**
     * 服务降级
     * 使用注解来定义服务降级逻辑时，服务降级函数和@HystrixCommand注解要处于同一个类中，同时，服务降级函数在执行过程中也有可能发生异常，所以也可以给服务降级函数添加‘备胎’
     * @return
     */
    @HystrixCommand(fallbackMethod = "error1")
    public Book test2() {
    	//产生异常
    	//int i = 1 / 0;
        return restTemplate.getForObject("http://HELLO-SERVICE/getbook1", Book.class);
    }
    
    /**
     * 如果有一个异常抛出后我不希望进入到服务降级方法中去处理，而是直接将异常抛给用户，那么我们可以在@HystrixCommand注解中添加忽略异常
     * 
     * 这里的实现原理很简单，因为有一个名叫HystrixBadRequestException的异常不会进入到服务降级方法中去，
     * 当我们定义了ignoreExceptions为ArithmeticException.class之后，当抛出ArithmeticException异常时，
     * Hystrix会将异常信息包装在HystrixBadRequestException里边然后再抛出，此时就不会触发服务降级方法了。
     */
    @HystrixCommand(fallbackMethod = "error1",ignoreExceptions = ArithmeticException.class)
    public Book test3() {
        int i = 1 / 0;
        return restTemplate.getForObject("http://HELLO-SERVICE/getbook1", Book.class);
    }
    
    /**
     * 注解的方式，只需要在服务降级方法中添加一个Throwable类型的参数就能够获取到抛出的异常的类型
     * @param throwable
     * @return
     */
    @HystrixCommand(fallbackMethod = "error2")
    public Book error1(Throwable throwable) {
    	System.out.println(throwable.getMessage());
        //发起某个网络请求（可能失败）
        //return null;
    	return new Book("百年孤独", 33, "马尔克斯", "人民文学出版社");
    }
    public Book error2() {
        return new Book();
    }
    
    //通过注解来开启缓存，和缓存相关的注解一共有三个，分别是@CacheResult、@CacheKey和@CacheRemove
    /**
     * @CacheResult方法，表示给该方法开启缓存，默认情况下方法的所有参数都将作为缓存的key
     * @param id
     * @param aa
     * @return
     */
    
    //@CacheResult(cacheKeyMethod = "getCacheKey2")
    @CacheResult
    @HystrixCommand
    public Book test6(Integer id,String aa) {
        return restTemplate.getForObject("http://HELLO-SERVICE/getbook5/{1}", Book.class, id);
    }
    
    ////@CacheResult中添加cacheKeyMethod属性来指定返回缓存key的方法，注意返回的key要是String类型的, 此时默认的规则失效
    @CacheResult(cacheKeyMethod = "getCacheKey2")
    @HystrixCommand
    public Book test6(Integer id) {
        return restTemplate.getForObject("http://HELLO-SERVICE/getbook5/{1}", Book.class, id);
    }
    public String getCacheKey2(Integer id) {
        return String.valueOf(id);
    }
    
    /**
     * 除了使用默认数据之外，我们也可以使用@CacheKey来指定缓存的key
     * 如果我们即使用了@CacheResult中cacheKeyMethod属性来指定key，又使用了@CacheKey注解来指定key，则后者失效
     */
   /* @CacheResult
    @HystrixCommand
    public Book test6(@CacheKey Integer id,String aa) {
        return restTemplate.getForObject("http://HELLO-SERVICE/getbook5/{1}", Book.class, id);
    }*/
    
    /**
     * 让缓存失效的注解
     * 这里必须指定commandKey，commandKey的值就为缓存的位置，配置了commandKey属性的值，Hystrix才能找到请求命令缓存的位置。
     * @param id
     * @return
     */
    @CacheRemove(commandKey = "test6")
    @HystrixCommand
    public Book test7(@CacheKey Integer id) {
        return null;
    }
}