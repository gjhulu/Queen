package com.queen.manager.hystrix;

import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommand;
import com.queen.pojo.entity.Book;
/**
 * 自定义HystrixCommand
 * 除了使用@HystrixCommand注解，也可以自定义类继承自HystrixCommand
 * @author zb
 *
 */
public class BookCommand extends HystrixCommand<Book>{
	 private RestTemplate restTemplate;

	@Override
    protected Book getFallback() {
        return new Book("宋诗选注", 88, "钱钟书", "三联书店");
    }

    public BookCommand(Setter setter, RestTemplate restTemplate) {
        super(setter);
        this.restTemplate = restTemplate;
    }

    @Override
    protected Book run() throws Exception {
        return restTemplate.getForObject("http://HELLO-SERVICE/getbook1", Book.class);
    }

}
