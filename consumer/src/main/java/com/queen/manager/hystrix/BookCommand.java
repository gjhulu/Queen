package com.queen.manager.hystrix;

import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommand;
import com.queen.manager.consumer.Book;
/**
 * 自定义HystrixCommand
 * 除了使用@HystrixCommand注解，也可以自定义类继承自HystrixCommand
 * @author zb
 *
 */
public class BookCommand extends HystrixCommand<Book>{
	 private RestTemplate restTemplate;
	 private Long id;

	@Override
    protected Book getFallback() {
		//Throwable executionException = getExecutionException();
	    //System.out.println(executionException.getMessage());
        return new Book("宋诗选注", 88, "钱钟书", "三联书店");
    }

    public BookCommand(Setter setter, RestTemplate restTemplate) {
        super(setter);
        this.restTemplate = restTemplate;
    }

    @Override
    protected Book run() throws Exception {
    	//抛出异常
    	//默认情况下方法抛了异常会自动进行服务降级，交给服务降级中的方法去处理
    	//在getFallback方法中调用getExecutionException方法来获取抛出的异常
    	//int i = 1 / 0;
        //return restTemplate.getForObject("http://HELLO-SERVICE/getbook1", Book.class);
    	return restTemplate.getForObject("http://HELLO-SERVICE/getbook5/{1}", Book.class,id);
    }
    
    public BookCommand(Setter setter, RestTemplate restTemplate,Long id) {
        super(setter);
        this.restTemplate = restTemplate;
        this.id = id;
    }
    
    /**
     * 通过方法重载开启缓存
     * 重写getCacheKey方法即可实现请求缓存
     */
    @Override
    protected String getCacheKey() {
        return String.valueOf(id);
    }
}
